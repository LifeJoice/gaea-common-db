package org.gaea.data.dataset;

import org.apache.commons.lang3.StringUtils;
import org.gaea.cache.GaeaCacheOperator;
import org.gaea.data.convertor.XmlDataSetConvertor;
import org.gaea.data.dataset.domain.GaeaDataSet;
import org.gaea.data.xml.DataSetSchemaDefinition;
import org.gaea.event.CommonEventDefinition;
import org.gaea.event.GaeaEventPublisher;
import org.gaea.exception.InvalidDataException;
import org.gaea.exception.SysInitException;
import org.gaea.exception.ValidationFailedException;
import org.gaea.util.GaeaPropertiesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 负责Gaea框架的数据集DataSet的处理的核心。<p/>
 * 这个没有用注解让Spring自动扫描，因为初始化的时候会初始化数据集。因此构建bean的时候，需要DataSet的XML文件路径。
 * <p><b>配置:</b></p>
 * <p>只要配置了,就会给系统启用DataSet的功能.会把XML里面的DataSet获取并缓存.</p>
 * <p>
 * bean id="gaeaDataSetResolver" class="org.gaea.data.dataset.GaeaDataSetResolver"
 * constructor-arg name="filePath" value="classpath:system_datasets_config.xml"
 * /bean
 * </p>
 * Created by iverson on 2016/2/24.
 */
public class GaeaDataSetResolver implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger logger = LoggerFactory.getLogger(GaeaDataSetResolver.class);
    @Autowired
    private XmlDataSetConvertor xmlDataSetConvertor;
    @Autowired
    private GaeaEventPublisher gaeaEventPublisher;
    @Autowired
    private ResourceLoader resourceLoader;
    // 标识是否已经在启动的时候，初始化过了数据集。一般只需要在系统启动的时候初始化一次。
    private static boolean hasInitDataSet = false;

    public GaeaDataSetResolver(String filePath) {
        this.filePath = filePath;
        this.dataSets = new ConcurrentHashMap<String, GaeaDataSet>();
    }

    // DataSet配置文件的路径。支持classpath:/com/**/*.xml这样的模糊匹配
    private String filePath;
    // 系统初始化后，所有配置的DataSet都会加载进来，并不再加载
    private final ConcurrentHashMap<String, GaeaDataSet> dataSets;
    @Autowired(required = false)
    @Qualifier("cachePropReader")
    private GaeaPropertiesReader cacheProperties;
    @Autowired(required = false)
    private GaeaCacheOperator gaeaCacheOperator;

    /**
     * 根据配置DataSet文件，读取XML中的DataSet配置，然后缓存（有配Redis就放在Redis，否则放在单例中）
     *
     * @throws ValidationFailedException
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (StringUtils.isEmpty(filePath)) {
            logger.warn("filePath为空！无法进行GaeaDataSet的提取操作。");
        }
        if (!hasInitDataSet) {
            hasInitDataSet = true;
            init();
        }
    }

    /**
     * 因为涉及一个实例变量, 基于也不会频繁调用，声明为同步的方法。
     */
    public synchronized void init() {
        try {
            // 读取配置的路径对应的文件。支持classpath:/com/**/*.xml这样的模糊匹配
            Resource[] arrayR = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(filePath);
            /* 读取XML文件，把DataSet读取和转换处理。 */
            if (arrayR != null) {
                for (Resource r : arrayR) {
                    readAndParseXmlDataSet(r);
                }
            }
            /* 先清空数据集缓存 */
            cleanCacheDataSets();
            /* 完成DataSet的XML的加载，接下来缓存 */
            cacheDataSets();
            /* 触发完成事件。让其他操作继续，例如：写入数据库。 */
            gaeaEventPublisher.publishSimpleEvent(CommonEventDefinition.EVENT_CODE_XML_DATASET_LOAD_FINISHED, null);
        } catch (ValidationFailedException e) {
            logger.error("初始化gaea xml dataSet失败。解析xml DataSet失败。", e);
        } catch (SysInitException e) {
            logger.error("初始化gaea xml dataSet失败。解析xml DataSet失败。", e);
        } catch (IOException e) {
            logger.error("初始化gaea xml dataSet失败。获取配置文件失败！filePath={}", filePath);
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 读取配置数据集的XML文件，解析成数据集GaeaDataSet列表并缓存。
     * <p>同时触发加载数据集完成事件。让其他功能，例如数据库数据集同步，继续后续操作。</p>
     *
     * @param r
     * @throws ValidationFailedException
     * @throws SysInitException
     */
    private void readAndParseXmlDataSet(Resource r) throws ValidationFailedException, SysInitException {
        DocumentBuilder db = null;
        Node document = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            File dsXmlFile = r.getFile();
            db = dbf.newDocumentBuilder();
            // document是整个XML schema
            document = db.parse(dsXmlFile);
            // 寻找根节点<dataset-definition>
            Node rootNode = getRootNode(document);

            NodeList nodes = rootNode.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node dataSetNode = nodes.item(i);
                // xml解析会把各种换行符等解析成元素。统统跳过。
                if (!(dataSetNode instanceof Element)) {
                    continue;
                }
                if (DataSetSchemaDefinition.DS_DATASET_NODE_NAME.equals(dataSetNode.getNodeName())) {
                    GaeaDataSet dataSet = xmlDataSetConvertor.convertDataSet(dataSetNode);
                    if (dataSet == null || StringUtils.isEmpty(dataSet.getId())) {
                        logger.warn("格式不正确。对应的DataSet为空或缺失id！" + dataSetNode.toString());
                        continue;
                    }
                    dataSets.put(dataSet.getId(), dataSet);
                } else {
                    logger.warn("Dataset Xml schema中包含错误数据。包含非dataset信息: <" + dataSetNode.getNodeName() + ">");
                }
            }
        } catch (FileNotFoundException e) {
            logger.error("加载Dataset的XML配置文件错误。File path:" + filePath, e);
        } catch (ParserConfigurationException e) {
            logger.error("解析Dataset的XML配置文件错误。File path:" + filePath, e);
        } catch (IOException e) {
            logger.error("解析Dataset的XML配置文件发生IO错误。File path:" + filePath, e);
        } catch (SAXException e) {
            logger.error("解析Dataset的XML配置文件错误。File path:" + filePath, e);
        } catch (InvalidDataException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 把从XML文件中读出来的DataSet缓存起来。
     */
    private void cacheDataSets() {
        if (dataSets != null && dataSets.size() > 0) {
            String rootKey = cacheProperties.get(GaeaDataSetDefinition.GAEA_DATASET_SCHEMA);
            gaeaCacheOperator.put(rootKey, dataSets, GaeaDataSet.class);
        }
    }

    /**
     * 把从XML文件中读出来的DataSet缓存起来。
     */
    private void cleanCacheDataSets() {
        String rootKey = cacheProperties.get(GaeaDataSetDefinition.GAEA_DATASET_SCHEMA);
        // 删除缓存所有数据集
        gaeaCacheOperator.delete(rootKey);
    }

    private Node getRootNode(Node document) throws ValidationFailedException {
        NodeList nodes = document.getChildNodes();
        Node rootNode = null; // 这个应该是<dataset-definition>
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            // xml解析会把各种换行符等解析成元素。统统跳过。
            if (!(node instanceof Element)) {
                continue;
            }
            if (DataSetSchemaDefinition.DS_ROOT_NODE.equals(node.getNodeName())) {
                rootNode = node;
                break;
            }
        }
        if (rootNode == null) {
            logger.warn("Dataset XML Schema根节点为空。加载Gaea dataset失败。");
        }
        return rootNode;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public GaeaDataSet getDataSet(String id) {
        return dataSets.get(id);
    }

    public void setDataSet(Map<String, GaeaDataSet> newDataSets) {
        dataSets.clear();
        dataSets.putAll(newDataSets);
    }
}
