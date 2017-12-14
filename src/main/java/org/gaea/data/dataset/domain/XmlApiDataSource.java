package org.gaea.data.dataset.domain;

import java.io.Serializable;

/**
 * 这个是基于接口调用获取数据的数据集的配置。
 * Created by iverson on 2017年11月30日16:09:23
 */
public class XmlApiDataSource implements Serializable {
    //    private String name;
//    private String code;
    /* 通过接口请求数据的方式，默认POST。value=post|get */
    private String requestType = REQUEST_TYPE_POST;
    public static final String REQUEST_TYPE_POST = "post";
    public static final String REQUEST_TYPE_GET = "get";
    /* Api请求的定义，包括请求参数、请求方式等 */
    private XmlApiRequest request;
    /* Api响应的定义，包括响应数据的抽取等 */
    private XmlApiResponse response;

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public XmlApiRequest getRequest() {
        return request;
    }

    public void setRequest(XmlApiRequest request) {
        this.request = request;
    }

    public XmlApiResponse getResponse() {
        return response;
    }

    public void setResponse(XmlApiResponse response) {
        this.response = response;
    }
}
