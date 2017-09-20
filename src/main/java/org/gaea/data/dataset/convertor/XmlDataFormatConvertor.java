package org.gaea.data.dataset.convertor;

import org.apache.commons.lang3.StringUtils;
import org.gaea.data.convertor.DataConvertor;
import org.gaea.data.convertor.XmlDataSetConvertor;
import org.gaea.data.dataset.domain.ConditionSet;
import org.gaea.data.dataset.format.domain.GaeaDataFormat;
import org.gaea.data.dataset.format.domain.GaeaFormatNode;
import org.gaea.data.xml.DataSetSchemaDefinition;
import org.gaea.exception.InvalidDataException;
import org.gaea.util.GaeaXmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 负责{@code <data-format>}的转换
 * Created by iverson on 2017/9/5.
 */
@Component
public class XmlDataFormatConvertor {
    private final Logger logger = LoggerFactory.getLogger(XmlDataFormatConvertor.class);
    @Autowired
    private XmlDataSetConvertor xmlDataSetConvertor;

    public GaeaDataFormat convert(Node dataFormatNode) throws InvalidDataException {
        GaeaDataFormat dataFormat = new GaeaDataFormat();
        Element dataSetElement = (Element) dataFormatNode;
        NodeList nodes = dataSetElement.getChildNodes();
        // 先自动填充<dataset>的属性
        try {
            dataFormat = GaeaXmlUtils.copyAttributesToBean(dataFormatNode, dataFormat, GaeaDataFormat.class);
        } catch (Exception e) {
            String errorMsg = "自动转换XML元素<data-format>的属性错误！";
            throw new InvalidDataException(errorMsg, e);
        }
//        if (StringUtils.isEmpty(dataFormat.getId())) {
//            throw new InvalidDataException("XML数据集定义的id不允许为空！");
//        }
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            // xml解析会把各种换行符等解析成元素。统统跳过。
            if (!(n instanceof Element)) {
                continue;
            }
            if (DataSetSchemaDefinition.DS_DATAFORMAT_NODE.equals(n.getNodeName())) {
                // {@code <node>}的解析
                GaeaFormatNode formatNode = convertNode(n);
                dataFormat.setNode(formatNode);
            } else {
                logger.warn("Dataset Xml schema中包含错误数据。包含非dataset信息: <" + dataFormatNode.getNodeName() + ">");
            }
        }
        return dataFormat;
    }

    /**
     * 转换{@code <node>}
     *
     * @param xmlNode
     * @return
     * @throws InvalidDataException
     */
    private GaeaFormatNode convertNode(Node xmlNode) throws InvalidDataException {

        GaeaFormatNode gaeaFormatNode = new GaeaFormatNode();
        DataConvertor dataConvertor = new DataConvertor();
        NodeList list = xmlNode.getChildNodes();
        // 先自动填充<dataset>的属性
        try {
            gaeaFormatNode = GaeaXmlUtils.copyAttributesToBean(xmlNode, gaeaFormatNode, GaeaFormatNode.class);
            /**
             * 这里为了简化XML的语法，所以DataConvertor某些配置项和{@code <node>}放在一起。
             * 这部分分离出来。
             */
            dataConvertor = GaeaXmlUtils.copyAttributesToBean(xmlNode, dataConvertor, DataConvertor.class);
            // {@code <node name=''>}的name，简写，相当于dataConvertor的toName
            if (StringUtils.isNotEmpty(gaeaFormatNode.getName())) {
                dataConvertor.setToName(gaeaFormatNode.getName());
            }
            gaeaFormatNode.setDataConvertor(dataConvertor);
        } catch (Exception e) {
            String errorMsg = "自动转换XML元素<node>的属性错误！";
            throw new InvalidDataException(errorMsg, e);
        }
        // 遍历<condition-set>
        for (int i = 0; i < list.getLength(); i++) {
            Node subNode = list.item(i);
            // xml解析会把各种换行符等解析成元素。统统跳过。
            if (!(subNode instanceof Element)) {
                continue;
            }
            if (DataSetSchemaDefinition.DS_DATAFORMAT_NODE.equals(subNode.getNodeName())) {
                /**
                 * 递归调用、解析。
                 * {@code <node>}的解析
                 */
                GaeaFormatNode formatNode = convertNode(subNode);
                if (formatNode != null) {
                    gaeaFormatNode.getNodes().add(formatNode);
                }
//            }else if (DataSetSchemaDefinition.DS_DATASET_CONDITIONSET_NODE_NAME.equals(subNode.getNodeName())) {
//                // <node>里面的<condition-set>解析
//                ConditionSet conditionSet = xmlDataSetConvertor.convertConditionSet(subNode);
//                gaeaFormatNode.setConditionSet(conditionSet);
            }
        }
        return gaeaFormatNode;
    }
}
