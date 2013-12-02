package ru.shubert.jobportal.dao;

import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.shubert.jobportal.model.prototype.IEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * User: user
 * Date: 29.04.12 18:35
 */
public interface IDao {
    @Nullable
    <T> T get(Class<T> eclass, Serializable id);

    <T> List<T> get(Class<T> eclass, Collection<Serializable> ids);

    <T> List<T> loadAll(Class<T> eclass);

    <T> void delete(T obj);

    public <T> void delete(Class<T> tClass, Serializable[] ids);

    /**
     * Finds object by the given example
     *
     * @param t      example
     * @param params search parameters
     * @param <T>    object type
     * @return list of find objects
     */
    <T> List<T> find(T t, @Nullable QueryParams params);

    <T> Long countByExample(T t);

    /**
     * Common findby method for http-authentication
     *
     * @param criteria by to find
     * @return user or null
     */
    List<? extends IEntity> findByCriteria(DetachedCriteria criteria);

    /**
     * May schedule updates for later execution that is diffirent from {@link #simpleSave(Object)}
     *
     * @param entity to be saved
     * @param <T>    class of entity
     * @return saved entity
     */
    <T> T simpleSaveOrUpdate(@NotNull T entity);

    /**
     * Executes sql statement right after call was made in order to return generated id that is different from
     * {@link #simpleSaveOrUpdate(Object)}
     *
     * @param entity to be saved
     * @param <T>    entity class
     * @return generated identifier
     */
    <T> Serializable simpleSave(@NotNull T entity);

    /**
     * Reconnects given entity with a current session. Typical usecase is to iterate entity's collection field
     * when entity was loaded with another session
     *
     * @param entity to be reconnected
     * @param <T>    entity type
     * @return same entity after reconnect
     */
    <T> T reconnect(@NotNull T entity);

    /**
     * merges instead of forced saving
     *
     * @param entity to be merged into cache
     * @param <T> entity generic type
     * @return same object
     */
    <T> T merge(@NotNull T entity);

    /**
     * Shortcut for a sessionFactory.getCurrentSession()
     *
     * @return Hibernate session bound to current thread
     */
    Session getSession();
}
