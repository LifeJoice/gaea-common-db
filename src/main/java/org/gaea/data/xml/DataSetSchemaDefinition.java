package org.gaea.data.xml;

/**
 * XML的DataSet的element的名称定义
 * Created by iverson on 2016/2/26.
 */
public interface DataSetSchemaDefinition {
    public static String DS_ROOT_NODE = "dataset-definition";
    public static String DS_DATASET_NODE_NAME = "dataset";
    /* DataSet的data-sql */
    public static String DS_DATASET_DATASQL_NODE_NAME = "data-sql";
    /* DataSet的columns-define */
    public static String DS_DATASET_COLUMNS_DEFINE_NODE_NAME = "columns-define";
    public static String DS_DATASET_COLUMN_NODE_NAME = "column";
    /* DataSet的where */
    public static String DS_DATASET_WHERE_NODE_NAME = "where";
    public static String DS_DATASET_CONDITIONSET_NODE_NAME = "condition-set";
    public static String DS_DATASET_CONDITION_NODE_NAME = "condition";
    /* orderBy */
    public static String DS_DATASET_ORDER_BY_NODE_NAME = "order-by";
    /* groupBy */
    public static String DS_DATASET_GROUP_BY_NODE_NAME = "group-by";
    /* DataSet的DataSource相关 */
    public static String DS_DATASET_DATASOURCE_NODE_NAME = "data-source";
    public static String DS_API_DATA_SOURCE_NODE = "api-data-source";
    public static String API_DS_PAGING_NODE = "paging";
    public static String API_DS_PARAMS_NODE = "params";
    public static String API_DS_PARAM_NODE = "param"; // param应该是一个更高层级的通用的
    public static String API_DS_REQUEST_NODE = "request";
    public static String API_DS_REQUEST_URL_NODE = "url";
    public static String API_DS_RESPONSE_NODE = "response";
    public static String API_DS_RESPONSE_DATA_EXTRACT_NODE = "data-extract";
    public static String API_DS_RESPONSE_DATA_NODE = "response-data";
    //    public static String DS_DATASET_DATASOURCE_CODE_NODE_NAME = "data-source";
    /* DataSet的data-format */
    public static String DS_DATAFORMAT = "data-format";
    public static String DS_DATAFORMAT_NODE = "node";
    /* DataSet的data */
    public static String DS_DATASET_DATA_NODE_NAME = "data";
    public static String DS_DATASET_DATA_ELEMENT_NODE_NAME = "data-element";
    public static String DATA_ELEMENT_ATTR_VALUE = "value";
    public static String DATA_ELEMENT_ATTR_TEXT = "text";
}
