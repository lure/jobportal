package ru.shubert.jobportal.strategy;

import org.hibernate.proxy.HibernateProxyHelper;

/**
 * Orm helper
 * Hides particular orm implemetation details. 
 *
 *
 */
public class ORMHelper {
    
    private ORMHelper() { }

    public static Class getClass(Object tClass) {
        return HibernateProxyHelper.getClassWithoutInitializingProxy(tClass);
    }

}
