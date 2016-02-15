package com.ur.framework.util;

import org.gaea.paging.PagingBean;
import org.gaea.paging.PagingResultDataWrapper;
import org.gaea.system.factory.WebDataWrapperFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 基于Hibernate的BaseDAO。提供基于Hibernate的公用方法。
 * @author Iverson
 * @history 2014年9月4日 星期四
 */
public class HibernateBaseDAO<T> {
    @Autowired
    private SessionFactory sessionFactory;
    
    protected List<T> query(String hql) {
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        List<T> list = query.list();
        return list;
    }
    
    protected PagingResultDataWrapper pagingQuery(String hql, PagingBean paging) {
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setFirstResult(paging.getStart());
        query.setMaxResults(paging.getPageSize());
        List<T> list = query.list();
        PagingResultDataWrapper result = WebDataWrapperFactory.pagingData(list);
        return result;
    }
}
