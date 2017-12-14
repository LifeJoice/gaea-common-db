package org.gaea.data.dataset.convertor;

import org.gaea.data.dataset.domain.XmlApiPage;
import org.gaea.data.dataset.domain.XmlDsParam;
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
 * 负责{@code <api-data-source> -> <paging>}的转换
 * Created by iverson on 2017年12月1日 星期五
 */
@Component
public class XmlApiDsPageConverter {
    private final Logger logger = LoggerFactory.getLogger(XmlApiDsPageConverter.class);
    @Autowired
    private XmlDsParamsConverter dsParamsConvertor;

    public XmlApiPage convert(Node node) throws InvalidDataException {
        // 解析<paging>
        XmlApiPage apiDsPage = new XmlApiPage();
        NodeList subNodes = node.getChildNodes();
        try {
            apiDsPage = GaeaXmlUtils.copyAttributesToBean(node, apiDsPage, XmlApiPage.class);
        } catch (Exception e) {
            String errorMsg = "自动转换XML元素<paging>的属性错误！";
            throw new InvalidDataException(errorMsg, e);
        }
        // 遍历<paging>子节点
        for (int i = 0; i < subNodes.getLength(); i++) {
            Node n = subNodes.item(i);
            // xml解析会把各种换行符等解析成元素。统统跳过。
            if (!(n instanceof Element)) {
                continue;
            }
            // <paging>支持<param>子元素（但暂时应该没用）
            if (DataSetSchemaDefinition.API_DS_PARAM_NODE.equals(n.getNodeName())) {
                // <param>的解析. 在<page>中的param不需要<params>父级
                XmlDsParam dsParam = dsParamsConvertor.convertParam(n);
                apiDsPage.getDsParamList().add(dsParam);
            } else {
                logger.warn("Dataset Xml schema中包含错误数据。包含非dataset信息: <" + node.getNodeName() + ">");
            }
        }
        return apiDsPage;
    }
}
