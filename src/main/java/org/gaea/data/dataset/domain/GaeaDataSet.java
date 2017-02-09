package org.gaea.data.dataset.domain;

import org.gaea.data.domain.GaeaDataSource;

import java.io.Serializable;
import java.util.List;

/**
 * 数据集实体。一个数据集对象，其实就是一句SQL和对应的结果集。
 * Created by iverson on 2016/2/23.
 */
public class GaeaDataSet<T> implements Serializable {
    private String id;// 类似code。自命名，不是无意义的系统生成。
    private String sql;
    private List<T> dsResults;// SQL查出来的对象的结果集
    /**
     * 最简单的结果集。键值对。适用于静态的。
     * (扩展一下，支持多值，不止键值对 by Iverson 2016-5-30 11:37:39)
     * 例如：性别（男女）、业务的一些状态（未审、已审待批、已批）、季节（春夏秋冬）……之类的<p/>
     * Key：应该对应的是页面下拉框的value；<br/>
     * Value：对应下拉框显示的值（但一般不是真实的值）
     */
    private List<DataItem> staticResults;
    /**
     * 缓存类型，具体参考下面的定义。
     * 当前简单分两种：不缓存和静态缓存。
     * 未来扩展了事件机制后，可以事件触发刷新，则可以对一般SQL结果集进行缓存（例如产品分类等）
     */
    private String cacheType;
    public static final String CACHE_TYPE_NONE = "none";// 不缓存
    public static final String CACHE_TYPE_AUTO = "auto";// 自动缓存。刷新时机由系统决定。
    public static final String CACHE_TYPE_STATIC = "static";// 静态的，系统启动时缓存，并不再刷新。
    //    private boolean isCache;// 是否缓存结果。对于一些静态的、变得比较少的可以缓存。
    private GaeaDataSource dataSource;
    private String expireTime;// 结果集缓存保留时间。1ms|s|min|d
    private Where where; // where条件组合
    private String primaryTable; // 本DataSet的主表。可能用它的id作为排序依据等。
    /**
     * 校验方式。 0：不校验 1：校验,无对应的当没权限. 2：校验,无对应的当有权限.
     */
    private Integer authorityType;
    private List<DsAuthority> dsAuthorities;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<T> getDsResults() {
        return dsResults;
    }

    public void setDsResults(List<T> dsResults) {
        this.dsResults = dsResults;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public GaeaDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(GaeaDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<DataItem> getStaticResults() {
        return staticResults;
    }

    public void setStaticResults(List<DataItem> staticResults) {
        this.staticResults = staticResults;
    }

    public String getCacheType() {
        return cacheType;
    }

    public void setCacheType(String cacheType) {
        this.cacheType = cacheType;
    }

    public Where getWhere() {
        return where;
    }

    public void setWhere(Where where) {
        this.where = where;
    }

    public String getPrimaryTable() {
        return primaryTable;
    }

    public void setPrimaryTable(String primaryTable) {
        this.primaryTable = primaryTable;
    }

    public Integer getAuthorityType() {
        return authorityType;
    }

    public void setAuthorityType(Integer authorityType) {
        this.authorityType = authorityType;
    }

    public List<DsAuthority> getDsAuthorities() {
        return dsAuthorities;
    }

    public void setDsAuthorities(List<DsAuthority> dsAuthorities) {
        this.dsAuthorities = dsAuthorities;
    }
}
