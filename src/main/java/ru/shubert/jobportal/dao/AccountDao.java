package ru.shubert.jobportal.dao;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;
import ru.shubert.jobportal.model.User;

import java.util.List;

/**
 * data manipulation object. Contains user management operation, such as save, find by login and webtoken.
 *  <br />
 * {@link #unlazyUserFileds} method requires special attention thought.
 */
@Repository(value = "accountDao")
public class AccountDao extends BaseDao implements IAccountDao {

    /**
     * Initialization of user employer/person proxy
     *
     * @param user to initialize
     * @return version with unproxied collections
     */
    @Nullable
    public User unlazyUserFileds(@NotNull User user) {
        Hibernate.initialize(user.getEmployer());
        Hibernate.initialize(user.getPerson());
        return user;
    }


    /**
     * Looking for one user with supplied login
     *
     * @param login user`s login field
     * @return found user or null
     */
    @Nullable
    public User findUserByLogin(@NotNull String login) {
        DetachedCriteria criteria = DetachedCriteria.forClass(User.class).add(Property.forName("login").eq(login));
        @SuppressWarnings({"unchecked"})
        List<User> list = (List<User>) findByCriteria(criteria);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Looking for one user with supplied login
     *
     * @param token user`s {@link User#loginToken}
     * @return found user or null
     */
    @Nullable
    public User findUserByToken(@NotNull String token) {
        DetachedCriteria criteria = DetachedCriteria.forClass(User.class).add(Property.forName("login").eq(token));
        @SuppressWarnings({"unchecked"})
        List<User> list = (List<User>) findByCriteria(criteria);
        return list.isEmpty() ? null : list.get(0);
    }


    /**
     * Persists user to datastorage
     *
     * @param user to be persisted
     * @return user object with hopefully assigned ID
     */
    public User saveUser(@NotNull User user) {
        getSession().saveOrUpdate(user);
        return user;
    }


}
