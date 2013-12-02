package ru.shubert.jobportal.dao;

import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.shubert.jobportal.model.prototype.IEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Base DAO implementation.
 * <p>
 * Primary goal was to locate all transactional demarcations in one place . If any method would require
 * any custom demarcations it's always possible re-annotate such method in descendants.
 * ransactional by default with Propagation.REQUIRED
 * <p/>
 * <p>xThis implementation does not contain any methods specific to any Entity and as so may be used as a common dao</p>
 * http://stackoverflow.com/questions/5104765/hibernatedaosupport-is-not-recommended-why
 * @see DetachedCriteriaBuilder
 */
@Repository(value = "baseDao")
public class BaseDao implements IDao {
    //static final Logger LOGGER = LoggerFactory.getLogger(BaseDao.class);

    @Autowired
    SessionFactory factory;

    @Nullable
    public <T> T get(final Class<T> eclass, final Serializable id) {
        //noinspection unchecked
        return (T) getSession().get(eclass, id);
    }

    public <T> List<T> get(Class<T> eclass, Collection<Serializable> ids) {
        Criteria c = getSession().createCriteria(eclass);
        c.add(Restrictions.in("id", ids));
        //noinspection unchecked
        return (List<T>) c.list();
    }

    @Nullable
    public <T> List<T> loadAll(final Class<T> eclass) {
        Criteria criteria = getSession().createCriteria(eclass);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        //noinspection unchecked
        return criteria.list();

    }

    public <T> void delete(T obj) {
        getSession().delete(obj);
    }

    /**
     * JPA2 leaves us with
     * <pre>    4.10 Bulk Update and Delete Operations
     *          (...)
     *          A delete operation only applies to entities of the specified class and its subclasses.
     *          <b>It does not cascade to related entities.</b>
     *          (...)
     * </pre>
     * It means that executing queryes such as "delete from my.package.Customer c where c.id in (?) may be executed only
     * with preceding manual association clearing. E.g. "delete from Adress a where a.id in (?)". Note that Address may
     * have his own associations...
     * <p/>
     * So, 'correct' (lol)  way is to load all entityes and remove them one by one
     *
     * @param tClass entity class to be used in query
     * @param ids    of entity
     */
    public <T> void delete(Class<T> tClass, Serializable[] ids) {
        for (Serializable lid : ids) {
            delete(get(tClass, lid));
        }
    }


    @SuppressWarnings({"unchecked"})
    public <T> List<T> find(T t, QueryParams params) {
        DetachedCriteria criteria = DetachedCriteriaBuilder.getLikeNotNullExample(t, params);
        Criteria executableCriteria = criteria.getExecutableCriteria(getSession());
        //prepareCriteria(executableCriteria);
        if (params != null) {
            executableCriteria.setFirstResult(params.getFirst());
            executableCriteria.setMaxResults(params.getCount());
        }
        return executableCriteria.list();
    }

    public <T> Long countByExample(T t) {
        DetachedCriteria criteria = DetachedCriteriaBuilder.getLikeNotNullExample(t, null);
        criteria.setProjection(Projections.rowCount());
        Criteria executableCriteria = criteria.getExecutableCriteria(getSession());
        //prepareCriteria(executableCriteria);
        return (Long) executableCriteria.list().get(0);
    }

    public List<? extends IEntity> findByCriteria(DetachedCriteria criteria) {
        if (criteria == null) {
            return Collections.emptyList();
        }
        Criteria executableCriteria = criteria.getExecutableCriteria(factory.getCurrentSession());
        //noinspection unchecked
        return executableCriteria.list();
    }

    public <T> T simpleSaveOrUpdate(@NotNull T entity) {
        // https://forum.hibernate.org/viewtopic.php?t=951275&highlight=difference+persist+save
        getSession().saveOrUpdate(entity);
        return entity;
    }

    public <T> Serializable simpleSave(@NotNull T entity) {
        return getSession().save(entity);
    }

    public <T> T reconnect(@NotNull T entity) {
        getSession().buildLockRequest(LockOptions.NONE).lock(entity);
        return entity;
    }

    public <T> T merge(@NotNull T entity) {
        getSession().merge(entity);
        return entity;
    }

    @Override
    public Session getSession() {
        return factory.getCurrentSession();
    }
}