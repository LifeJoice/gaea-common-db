package org.gaea.data.dataset.convertor;

import org.apache.commons.lang3.StringUtils;
import org.gaea.data.dataset.domain.*;
import org.gaea.data.xml.DataSetSchemaDefinition;
import org.gaea.exception.InvalidDataException;
import org.gaea.util.GaeaStringUtils;
import org.gaea.util.GaeaXmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 负责{@code <api-data-source>}的转换
 * Created by iverson on 2017年12月1日 星期五
 */
@Component
public class XmlApiDataSourceConverter {
    private final Logger logger = LoggerFactory.getLogger(XmlApiDataSourceConverter.class);
    @Autowired
    private XmlApiDsPageConverter apiDsPageConverter;

    public XmlApiDataSource convert(Node node) throws InvalidDataException {
        XmlApiDataSource apiDataSource = new XmlApiDataSource();
        Element dataSetElement = (Element) node;
        NodeList nodes = dataSetElement.getChildNodes();
        // 先自动填充元素属性
        try {
            apiDataSource = GaeaXmlUtils.copyAttributesToBean(node, apiDataSource, XmlApiDataSource.class);
        } catch (Exception e) {
            String errorMsg = "自动转换XML元素<api-data-source>的属性错误！";
            throw new InvalidDataException(errorMsg, e);
        }
        // 遍历<api-data-source>子节点
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            // xml解析会把各种换行符等解析成元素。统统跳过。
            if (!(n instanceof Element)) {
                continue;
            }
            if (DataSetSchemaDefinition.API_DS_REQUEST_NODE.equals(n.getNodeName())) {
                // <request>的解析
                XmlApiRequest apiDsRequest = convertRequest(n);
                apiDataSource.setRequest(apiDsRequest);
            } else if (DataSetSchemaDefinition.API_DS_RESPONSE_NODE.equals(n.getNodeName())) {
                XmlApiResponse apiDsResponse = convertResponse(n);
                apiDataSource.setResponse(apiDsResponse);
            } else {
                logger.warn("Dataset Xml schema中包含错误数据。包含非dataset信息: <" + node.getNodeName() + ">");
            }
        }
        return apiDataSource;
    }

    /**
     * 对{@code <response>}的转换
     *
     * @param xmlNode
     * @return
     * @throws InvalidDataException
     */
    private XmlApiResponse convertResponse(Node xmlNode) throws InvalidDataException {
        XmlApiResponse apiDsResponse = new XmlApiResponse();

        NodeList list = xmlNode.getChildNodes();
        // 先自动填充元素属性
        try {
            apiDsResponse = GaeaXmlUtils.copyAttributesToBean(xmlNode, apiDsResponse, XmlApiResponse.class);
        } catch (Exception e) {
            String errorMsg = "自动转换XML元素<response>的属性错误！";
            throw new InvalidDataException(errorMsg, e);
        }
        // 遍历子节点
        for (int i = 0; i < list.getLength(); i++) {
            Node subNode = list.item(i);
            // xml解析会把各种换行符等解析成元素。统统跳过。
            if (!(subNode instanceof Element)) {
                continue;
            }
            if (DataSetSchemaDefinition.API_DS_RESPONSE_DATA_EXTRACT_NODE.equals(subNode.getNodeName())) {
                // 解析<data-extract>
                XmlApiResponseDataExtract responseDataExtract = convertDataExtract(subNode);
                apiDsResponse.setDataExtract(responseDataExtract);
            }
        }

        return apiDsResponse;
    }

    /**
     * 解析{@code <data-extract>}数据提取定义。
     *
     * @param xmlNode
     * @return
     * @throws InvalidDataException
     */
    private XmlApiResponseDataExtract convertDataExtract(Node xmlNode) throws InvalidDataException {
        XmlApiResponseDataExtract responseDataExtract = new XmlApiResponseDataExtract();

        NodeList list = xmlNode.getChildNodes();
        // 先自动填充元素的属性
        try {
            responseDataExtract = GaeaXmlUtils.copyAttributesToBean(xmlNode, responseDataExtract, XmlApiResponseDataExtract.class);
        } catch (Exception e) {
            String errorMsg = "自动转换XML元素<data-extract>的属性错误！";
            throw new InvalidDataException(errorMsg, e);
        }
        // 遍历子节点
        for (int i = 0; i < list.getLength(); i++) {
            Node subNode = list.item(i);
            // xml解析会把各种换行符等解析成元素。统统跳过。
            if (!(subNode instanceof Element)) {
                continue;
            }
            if (DataSetSchemaDefinition.API_DS_RESPONSE_DATA_NODE.equals(subNode.getNodeName())) {
                // 解析<response-data>
                XmlApiResponseData apiResponseData = convertResponseData(subNode);
                responseDataExtract.setApiResponseData(apiResponseData);
            } else if (DataSetSchemaDefinition.API_DS_PAGING_NODE.equals(subNode.getNodeName())) {
                // 解析<paging>。response只有部分属性值，例如"总记录数"。
                XmlApiPage apiDsPage = apiDsPageConverter.convert(subNode);
                responseDataExtract.setApiPage(apiDsPage);
            }
        }

        return responseDataExtract;
    }

    private XmlApiResponseData convertResponseData(Node xmlNode) throws InvalidDataException {
        XmlApiResponseData apiResponseData = new XmlApiResponseData();
        // 先自动填充元素的属性
        try {
            apiResponseData = GaeaXmlUtils.copyAttributesToBean(xmlNode, apiResponseData, XmlApiResponseData.class);
        } catch (Exception e) {
            String errorMsg = "自动转换XML元素<response-data>的属性错误！";
            throw new InvalidDataException(errorMsg, e);
        }
        return apiResponseData;
    }

    /**
     * 转换{@code <request>}
     *
     * @param xmlNode
     * @return
     * @throws InvalidDataException
     */
    private XmlApiRequest convertRequest(Node xmlNode) throws InvalidDataException {

        XmlApiRequest apiDsRequest = new XmlApiRequest();
        NodeList list = xmlNode.getChildNodes();
        // 先自动填充元素属性
        try {
            apiDsRequest = GaeaXmlUtils.copyAttributesToBean(xmlNode, apiDsRequest, XmlApiRequest.class);
        } catch (Exception e) {
            String errorMsg = "自动转换XML元素<request>的属性错误！";
            throw new InvalidDataException(errorMsg, e);
        }
        // 遍历子节点（<url>、<paing>等）
        for (int i = 0; i < list.getLength(); i++) {
            Node subNode = list.item(i);
            // xml解析会把各种换行符等解析成元素。统统跳过。
            if (!(subNode instanceof Element)) {
                continue;
            }
            if (DataSetSchemaDefinition.API_DS_REQUEST_URL_NODE.equals(subNode.getNodeName())) {
                // 解析<url>
                NodeList urlList = subNode.getChildNodes();
                for (int j = 0; j < urlList.getLength(); j++) {
                    Node urlNode = urlList.item(j);
                    // xml解析会把各种换行符等解析成元素。统统跳过。
                    if (StringUtils.isBlank(GaeaStringUtils.cleanFormatChar(urlNode.getTextContent()))) {
                        continue;
                    }
                    if (!(urlNode instanceof CharacterData)) {
                        continue;
                    }
                    CharacterData urlData = (CharacterData) urlNode;
                    String url = urlData.getData();
                    // 清理首尾换行符等
                    url = GaeaStringUtils.cleanFormatChar(url).trim();
                    apiDsRequest.setUrl(url);
                }
            } else if (DataSetSchemaDefinition.API_DS_PAGING_NODE.equals(subNode.getNodeName())) {
                // 解析<paging>
                XmlApiPage apiDsPage = apiDsPageConverter.convert(subNode);
                apiDsRequest.setApiPage(apiDsPage);
            }
        }
        return apiDsRequest;
    }

//    public static void main(String[] args) {
//        boolean whoIam;
//        System.out.println(whoIam);
//    }
}
