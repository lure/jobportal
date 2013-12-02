package ru.shubert.jobportal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.shubert.jobportal.dao.IDao;
import ru.shubert.jobportal.dao.QueryParams;
import ru.shubert.jobportal.model.EntityInstantiationException;
import ru.shubert.jobportal.model.prototype.IEntity;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * serves as a holder for common values and configurations.
 * Carryes {@link org.springframework.transaction.annotation.Transactional} annotation with
 * default {@link org.springframework.transaction.annotation.Propagation#REQUIRED} and
 * {@link org.springframework.transaction.annotation.Isolation#READ_COMMITTED} parameters
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
public class BaseService implements IService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseService.class);

    @Resource(name = "baseDao")
    private IDao dao;


    public <T> List<T> find(T example, QueryParams params) {
        if (example == null) {
            return Collections.emptyList();
        }
        return getDao().find(example, params);
    }

    public <T> Long countByExample(T example) {
        return getDao().countByExample(example);
    }

    public <T> T get(Class<T> tClass, Serializable id) {
        return getDao().get(tClass, id);
    }

    public <T> List<T> get(Class<T> tClass, Collection<Serializable> ids) {
        return getDao().get(tClass, ids);
    }

    public <T> List<T> loadAll(Class<T> tClass) {
        return getDao().loadAll(tClass);
    }

    public <T> void delete(T entity) {
        try {
            Assert.isAssignable(IEntity.class, entity.getClass());
        } catch (IllegalArgumentException e) {
            LOGGER.warn("cant remove object:", e.getMessage());
        }

        getDao().delete(entity);
    }

    public <T> void delete(Class<T> tClass, Serializable... id) {
        try {
            Assert.isAssignable(IEntity.class, tClass);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("cant remove object: ", e.getMessage());
        }

        getDao().delete(tClass, id);
    }

    public <T> T save(T object) {
        return getDao().simpleSaveOrUpdate(object);
    }

    public <T> T instantiate(Class<T> tClass) throws EntityInstantiationException {
        try {
            return tClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new EntityInstantiationException(e);
        }
    }

    public <T> T reconnect(T entity) {
        getDao().reconnect(entity);
        return entity;
    }

    public <T> T merge(T entity) {
        getDao().merge(entity);
        return entity;
    }

    public IDao getDao() {
        return dao;
    }
}
