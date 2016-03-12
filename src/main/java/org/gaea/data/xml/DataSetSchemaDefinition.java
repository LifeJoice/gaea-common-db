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
    /* DataSet的DataSource */
    public static String DS_DATASET_DATASOURCE_NODE_NAME = "data-source";
    public static String DS_DATASET_DATASOURCE_CODE_NODE_NAME = "data-source";
    /* DataSet的data */
    public static String DS_DATASET_DATA_NODE_NAME = "data";
    public static String DS_DATASET_DATA_ELEMENT_NODE_NAME = "data-element";
    public static String DATA_ELEMENT_ATTR_VALUE = "value";
    public static String DATA_ELEMENT_ATTR_TEXT = "text";
}
