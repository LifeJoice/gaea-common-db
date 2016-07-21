package org.gaea.data.convertor;

import org.apache.commons.lang3.StringUtils;
import org.gaea.data.dataset.domain.Condition;
import org.gaea.data.dataset.domain.ConditionSet;
import org.gaea.data.dataset.domain.GaeaDataSet;
import org.gaea.data.dataset.domain.Where;
import org.gaea.data.domain.GaeaDataSource;
import org.gaea.data.xml.DataSetSchemaDefinition;
import org.gaea.exception.InvalidDataException;
import org.gaea.exception.ValidationFailedException;
import org.gaea.util.GaeaStringUtils;
import org.gaea.util.GaeaXmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 负责XML DataSet的转换。
 * Created by iverson on 2016/7/19.
 */
@Component
public class XmlDataSetConvertor {
    private final Logger logger = LoggerFactory.getLogger(XmlDataSetConvertor.class);

    /**
     * 转换XML文件中的单个DataSet
     *
     * @param dataSetNode
     * @return
     * @throws InvalidDataException
     */
    public GaeaDataSet convertDataSet(Node dataSetNode) throws InvalidDataException {
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
            } else if (DataSetSchemaDefinition.DS_DATASET_WHERE_NODE_NAME.equals(n.getNodeName())) {
                // <where>的解析
                Where whereCondition = convertWhere(n);
                dataSet.setWhere(whereCondition);
            } else if (DataSetSchemaDefinition.DS_DATASET_DATA_NODE_NAME.equals(n.getNodeName())) {
                // <data>的解析
                NodeList list = n.getChildNodes();
                List<Map<String, String>> data = new ArrayList<Map<String, String>>();// <data-element>转换出来的map. key=value,value=text
                for (int j = 0; j < list.getLength(); j++) {
                    Node dataNode = list.item(j);
                    if (DataSetSchemaDefinition.DS_DATASET_DATA_ELEMENT_NODE_NAME.equals(dataNode.getNodeName())) {
                        // 获取node的属性列表
                        Map<String, String> attributes = GaeaXmlUtils.getAttributes(dataNode);
                        Map<String, String> newAttributes = new HashMap<String, String>();
                        // 遍历node的属性名
                        // 去掉空值
                        String key = null, value = null;
                        for (String attrName : attributes.keySet()) {
                            value = attributes.get(attrName);
                            if (StringUtils.isNotEmpty(value)) {
                                newAttributes.put(attrName, value);
                            }
                        }
                        data.add(newAttributes);
                    }
                }
                // 放入DataSet
                dataSet.setStaticResults(data);
            } else {
                logger.warn("Dataset Xml schema中包含错误数据。包含非dataset信息: <" + dataSetNode.getNodeName() + ">");
            }
        }
        return dataSet;
    }

    /**
     * 转换XML SCHEMA -> dataset -> where的内容。
     *
     * @param whereNode
     * @return
     * @throws InvalidDataException
     */
    private Where convertWhere(Node whereNode) throws InvalidDataException {
        Where whereCondition = new Where();
        Map<String, ConditionSet> conditionSetsMap = new HashMap<String, ConditionSet>();
        NodeList list = whereNode.getChildNodes();
        // 遍历<condition-set>
        for (int i = 0; i < list.getLength(); i++) {
            Node conditionSetNode = list.item(i);
            // xml解析会把各种换行符等解析成元素。统统跳过。
            if (!(conditionSetNode instanceof Element)) {
                continue;
            }
            if (DataSetSchemaDefinition.DS_DATASET_CONDITIONSET_NODE_NAME.equals(conditionSetNode.getNodeName())) {
                ConditionSet conditionSet = new ConditionSet();
                List<Condition> conditionsList = new ArrayList<Condition>();
                // 获取<condition-set>的属性
                conditionSet = GaeaXmlUtils.copyAttributesToBean(conditionSetNode, conditionSet, ConditionSet.class);
                NodeList conditionNodes = conditionSetNode.getChildNodes();

                // 遍历condition( <and>,<or>等 )
                for (int j = 0; j < conditionNodes.getLength(); j++) {
                    Condition condition = new Condition();
                    Node conditionNode = conditionNodes.item(j);
                    // xml解析会把各种换行符等解析成元素。统统跳过。
                    if (!(conditionNode instanceof Element)) {
                        continue;
                    }
                    // 读取<and>,<or>...等元素属性到bean中
                    condition = GaeaXmlUtils.copyAttributesToBean(conditionNode, condition, Condition.class);
                    String name = conditionNode.getNodeName();
                    /**
                     * 【重要】 <condition-set>子元素的名(例如<and>)，其实就是条件间的关系操作符
                     */
                    condition.setCondOp(name);
                    conditionsList.add(condition);
                }
                conditionSet.setConditions(conditionsList);
                conditionSetsMap.put(conditionSet.getId(), conditionSet);
            }
        }
        whereCondition.setConditionSets(conditionSetsMap);
        return whereCondition;
    }
}
