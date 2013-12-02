package ru.shubert.jobportal.service;

import ru.shubert.jobportal.dao.QueryParams;
import ru.shubert.jobportal.model.EntityInstantiationException;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 *
 *
 *
 */
public interface IService {

    /**
     * returns Iterator for query result bounded by Example object and query params.
     * Object used only ass template due query building and params defines the
     * result frame and order clause. Null exaple object leads to empty list as result.
     * However params object may be null.
     *
     * @param t      example object
     * @param params of result frame
     * @param <T>    generic type, usually descendant of {@link ru.shubert.jobportal.model.prototype.AbstractEntity}
     * @return {@link java.util.Iterator} for result list
     */
    <T> List<T> find(T t, QueryParams params);

    /**
     * generick getById method
     *
     * @param tClass to be load
     * @param id     of entity
     * @param <T>    generic type
     * @return object or null
     */
    <T> T get(Class<T> tClass, Serializable id);

    /**
     * Load a list of requested entityes using supplied ID
     *
     * @param tClass of entityes
     * @param ids to be used in load query
     * @param <T> generic
     * @return list of results or empty list
     */
    <T> List<T>  get(Class<T> tClass, Collection<Serializable> ids);

    /**
     * Returns list of objects by class
     *
     * @param tClass to be load
     * @param <T>    generic type
     * @return object or null
     */
    <T> List<T> loadAll(Class<T> tClass);

    /**
     * Returns result row count for query built on example object
     *
     * @param t   example object
     * @param <T> generic type, usually descendant of {@link ru.shubert.jobportal.model.prototype.AbstractEntity}
     * @return row count
     */
    <T> Long countByExample(T t);

    /**
     * removes mapped object from datastorage
     *
     * @param entity to be removed
     * @param <T> just wildcart
     */
    <T> void delete(T entity);

    /**
     * Removes mapped object from datastorage by it's identifier
     *
     * @param tClass of entity
     * @param id identifier
     */
    <T> void delete(Class<T> tClass, Serializable... id);

    /**
     * Generic save method
     *
     * @param object to save
     * @param <T> IEntity descendant
     * @return saved instance with set ID
     */
    <T> T save (T object);

    /**
     * Pure reflection IEntity objet instantiation. It may fail if there are no default constructor or by any another
     * cause. If failed {@link ru.shubert.jobportal.model.EntityInstantiationException } returns.
     *
     * @param tClass to be instantiated
     * @param <T> generic class
     * @return newly created class object
     * @throws ru.shubert.jobportal.model.EntityInstantiationException if failed
     */
    <T> T instantiate(Class<T> tClass) throws EntityInstantiationException;


    /**
     * Reconnects given entity with a current session. Typical usecase is to iterate entity's collectino field
     * when entity was loaded with another session
     * @param entity to be reconnected
     * @param <T> entity type
     * @return same entity after reconnect
     */
    <T> T reconnect(T entity);

    <T> T merge(T entity);
}
