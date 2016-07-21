package org.gaea.data.dataset;

import org.apache.commons.lang3.StringUtils;
import org.gaea.cache.GaeaCacheProcessor;
import org.gaea.data.convertor.XmlDataSetConvertor;
import org.gaea.data.dataset.domain.GaeaDataSet;
import org.gaea.data.xml.DataSetSchemaDefinition;
import org.gaea.exception.InvalidDataException;
import org.gaea.exception.ValidationFailedException;
import org.gaea.util.GaeaPropertiesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.concurrent.ConcurrentHashMap;

/**
 * 负责Gaea框架的数据集DataSet的处理的核心。<p/>
 * 这个没有用注解让Spring自动扫描，因为初始化的时候会初始化数据集。因此构建bean的时候，需要DataSet的XML文件路径。
 * <p><b>配置:</b></p>
 * <p>只要配置了,就会给系统启用DataSet的功能.会把XML里面的DataSet获取并缓存.</p>
 * <p>
 *     bean id="gaeaDataSetResolver" class="org.gaea.data.dataset.GaeaDataSetResolver"
            constructor-arg name="filePath" value="classpath:system_datasets_config.xml"
       /bean
 * </p>
 * Created by iverson on 2016/2/24.
 */
public class GaeaDataSetResolver {

    private final Logger logger = LoggerFactory.getLogger(GaeaDataSetResolver.class);
    @Autowired
    private XmlDataSetConvertor xmlDataSetConvertor;

    public GaeaDataSetResolver(String filePath) {
        this.filePath = filePath;
        this.dataSets = new ConcurrentHashMap<String, GaeaDataSet>();
    }

    private String filePath;
    // 系统初始化后，所有配置的DataSet都会加载进来，并不再加载
    private final ConcurrentHashMap<String, GaeaDataSet> dataSets;
    @Autowired(required = false)
    @Qualifier("cachePropReader")
    private GaeaPropertiesReader cacheProperties;
    @Autowired(required = false)
    private GaeaCacheProcessor gaeaCacheProcessor;

    public GaeaDataSet getDataSet(String id) {
        return dataSets.get(id);
    }

    /**
     * 根据配置DataSet文件，读取XML中的DataSet配置，然后缓存（有配Redis就放在Redis，否则放在单例中）
     *
     * @throws ValidationFailedException
     */
    @PostConstruct
    public void init() throws ValidationFailedException {
        if (StringUtils.isEmpty(filePath)) {
            logger.warn("filePath为空！无法进行GaeaDataSet的提取操作。");
        }
        /* 读取XML文件，把DataSet读取和转换处理。 */
        readAndParseXmlDataSet();
        /* 完成DataSet的XML的加载，接下来缓存 */
        cacheDataSets();
    }

    private void readAndParseXmlDataSet() throws ValidationFailedException {
        DocumentBuilder db = null;
        Node document = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        Resource resource = springApplicationContext.getResource(viewSchemaPath);
        try {
            File dsXmlFile = ResourceUtils.getFile(filePath);
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
            /* 完成DataSet的XML的加载，接下来缓存 */
            cacheDataSets();
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
            gaeaCacheProcessor.put(rootKey, dataSets);
        }
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
}
