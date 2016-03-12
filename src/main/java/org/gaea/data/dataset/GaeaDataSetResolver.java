package org.gaea.data.dataset;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.gaea.cache.GaeaCacheProcessor;
import org.gaea.data.dataset.domain.GaeaDataSet;
import org.gaea.data.domain.GaeaDataSource;
import org.gaea.data.xml.DataSetSchemaDefinition;
import org.gaea.exception.InvalidDataException;
import org.gaea.exception.ValidationFailedException;
import org.gaea.util.GaeaPropertiesReader;
import org.gaea.util.GaeaStringUtils;
import org.gaea.util.GaeaXmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.CharacterData;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 负责Gaea框架的数据集DataSet的处理的核心。<p/>
 * 这个没有用注解让Spring自动扫描，因为初始化的时候会初始化数据集。因此构建bean的时候，需要DataSet的XML文件路径。
 * Created by iverson on 2016/2/24.
 */
public class GaeaDataSetResolver {

    private final Logger logger = LoggerFactory.getLogger(getClass());

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

//    public GaeaDataSetResolver() {
//        this.dataSets = new ConcurrentHashMap<String, GaeaDataSet>();
//    }

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
////        List<GaeaDataSet> results = new ArrayList<GaeaDataSet>();
//        DocumentBuilder db = null;
//        Node document = null;
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
////        Resource resource = springApplicationContext.getResource(viewSchemaPath);
//        try {
//            File dsXmlFile = ResourceUtils.getFile(filePath);
//            db = dbf.newDocumentBuilder();
//            // document是整个XML schema
//            document = db.parse(dsXmlFile);
//            // 寻找根节点<ur-schema>
//            Node rootNode = getRootNode(document);
//
//            NodeList nodes = rootNode.getChildNodes();
//            for (int i = 0; i < nodes.getLength(); i++) {
//                Node dataSetNode = nodes.item(i);
//                // xml解析会把各种换行符等解析成元素。统统跳过。
//                if (!(dataSetNode instanceof Element)) {
//                    continue;
//                }
//                if (DataSetSchemaDefinition.DS_DATASET_NODE_NAME.equals(dataSetNode.getNodeName())) {
//                    GaeaDataSet dataSet = convertDataSet(dataSetNode);
//                    if (dataSet == null || StringUtils.isEmpty(dataSet.getId())) {
//                        logger.warn("格式不正确。对应的DataSet为空或缺失id！" + dataSetNode.toString());
//                        continue;
//                    }
//                    dataSets.put(dataSet.getId(), dataSet);
//                } else {
//                    logger.warn("Dataset Xml schema中包含错误数据。包含非dataset信息: <" + dataSetNode.getNodeName() + ">");
//                }
//            }
//        } catch (FileNotFoundException e) {
//            logger.error("加载Dataset的XML配置文件错误。File path:" + filePath, e);
//        } catch (ParserConfigurationException e) {
//            logger.error("解析Dataset的XML配置文件错误。File path:" + filePath, e);
//        } catch (IOException e) {
//            logger.error("解析Dataset的XML配置文件发生IO错误。File path:" + filePath, e);
//        } catch (SAXException e) {
//            logger.error("解析Dataset的XML配置文件错误。File path:" + filePath, e);
//        } catch (InvalidDataException e) {
//            logger.error(e.getMessage(), e);
//        }
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
                    GaeaDataSet dataSet = convertDataSet(dataSetNode);
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
            String rootKey = cacheProperties.get("gaea.dataset") + cacheProperties.get("gaea.dataset.schema");
//            for(GaeaDataSet ds:dataSets.values()){
//                dsMap.put(ds.getId(),ds.getSql());
//            }
            gaeaCacheProcessor.put(rootKey, dataSets);
        }
    }

    /**
     * 转换XML文件中的单个DataSet
     * @param dataSetNode
     * @return
     * @throws InvalidDataException
     */
    private GaeaDataSet convertDataSet(Node dataSetNode) throws InvalidDataException {
        GaeaDataSet dataSet = new GaeaDataSet();
        GaeaDataSource dataSource = new GaeaDataSource();
        Element dataSetElement = (Element) dataSetNode;
        NodeList nodes = dataSetElement.getChildNodes();
        // 先自动填充<dataset>的属性
        try {
            dataSet = GaeaXmlUtils.copyAttributesToBean(dataSetNode, dataSet, GaeaDataSet.class);
        } catch (Exception e) {
            String errorMsg = "自动转换XML元素<dataset>的属性错误！";
            throw new InvalidDataException(errorMsg, e);
        }
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            // xml解析会把各种换行符等解析成元素。统统跳过。
            if (!(n instanceof Element)) {
                continue;
            }
            if (DataSetSchemaDefinition.DS_DATASET_DATASOURCE_NODE_NAME.equals(n.getNodeName())) {
                // <data-source>的解析
                Element dsElement = (Element) n;
                String dsCode = dsElement.getAttribute(DataSetSchemaDefinition.DS_DATASET_DATASOURCE_CODE_NODE_NAME);
                dataSource.setCode(dsCode);
            } else if (DataSetSchemaDefinition.DS_DATASET_DATASQL_NODE_NAME.equals(n.getNodeName())) {
                // <data-sql>的解析
                NodeList list = n.getChildNodes();
                for (int j = 0; j < list.getLength(); j++) {
                    Node sqlNode = list.item(j);
                    // xml解析会把各种换行符等解析成元素。统统跳过。
                    if (StringUtils.isBlank(GaeaStringUtils.cleanFormatChar(sqlNode.getTextContent()))) {
                        continue;
                    }
                    if (!(sqlNode instanceof CharacterData)) {
                        continue;
                    }
                    CharacterData sqlData = (CharacterData) sqlNode;
                    String sql = sqlData.getData();
                    dataSet.setSql(sql);
                }
            } else if (DataSetSchemaDefinition.DS_DATASET_DATA_NODE_NAME.equals(n.getNodeName())) {
                // <data>的解析
                NodeList list = n.getChildNodes();
                Map<String,String> data = new HashMap<String, String>();// <data-element>转换出来的map. key=value,value=text
                for (int j = 0; j < list.getLength(); j++) {
                    Node dataNode = list.item(j);
                    // xml解析会把各种换行符等解析成元素。统统跳过。
//                    if (StringUtils.isBlank(GaeaStringUtils.cleanFormatChar(dataNode.getTextContent()))) {
//                        continue;
//                    }
//                    if (!(dataNode instanceof CharacterData)) {
//                        continue;
//                    }
                    if(DataSetSchemaDefinition.DS_DATASET_DATA_ELEMENT_NODE_NAME.equals(dataNode.getNodeName())){
                        // 获取node的属性列表
                        Map<String, String> attributes = GaeaXmlUtils.getAttributes(dataNode);
                        // 遍历node的属性名
                        String key=null,value=null;
                        for (String attrName : attributes.keySet()) {
                            if(DataSetSchemaDefinition.DATA_ELEMENT_ATTR_VALUE.equalsIgnoreCase(attrName)){
                                key=attributes.get(attrName);
                            }else if(DataSetSchemaDefinition.DATA_ELEMENT_ATTR_TEXT.equalsIgnoreCase(attrName)){
                                value=attributes.get(attrName);
                            }
                        }
                        // 键值都有，才放入
                        if(StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)){
                            data.put(key,value);
                        }
                    }
                }
                // 放入DataSet
                dataSet.setSimpleResults(data);
            } else {
                logger.warn("Dataset Xml schema中包含错误数据。包含非dataset信息: <" + dataSetNode.getNodeName() + ">");
            }
        }
        return dataSet;
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
