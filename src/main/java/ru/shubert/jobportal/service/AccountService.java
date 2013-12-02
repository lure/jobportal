package ru.shubert.jobportal.service;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.shubert.jobportal.dao.IAccountDao;
import ru.shubert.jobportal.model.User;
import ru.shubert.jobportal.strategy.ILoginTokenGenerator;

import javax.annotation.Resource;
import java.util.List;

/**
 * User: user
 * Date: 30.04.12 16:03
 */
@Service("accountService")
public class AccountService extends BaseService implements IAccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

    @Resource
    private IAccountDao dao;

    @Resource
    private ILoginTokenGenerator tokenGenerator;


    public User newUser() {
        User user = new User();
        user.setLoginToken(tokenGenerator.generate());
        return user;
    }

    @Transactional
    public User getUser(@NotNull Long id) {
        return dao.get(User.class, id);
    }

    @Transactional
    public User saveUser(@NotNull User user) throws IllegalArgumentException {
        validateUser(user);
        return dao.saveUser(user);
    }


    @Transactional
    public List<User> listUsers() {
        return dao.loadAll(User.class);
    }

    @Transactional(readOnly = true)
    public User findByToken(@NotNull String token) {
        return dao.findUserByToken(token);
    }

    @Transactional(readOnly = true)
    public User findByLogin(@NotNull String login) {
        return dao.findUserByLogin(login);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public ILoginTokenGenerator getTokenGenerator() {
        return tokenGenerator;
    }

    /**
     * User validation based on common sense
     * {@link User#login}, {@link User#password}, {@link User#loginToken}
     * Depending on {@link User#role}, {@link User#employer} or {@link User#person} must not be null
     *
     * @param user to be checked
     */
    protected void validateUser(User user) {
        try {
            Assert.notNull(user);
            Assert.hasLength(user.getLogin());
            Assert.hasLength(user.getPassword());

            switch (user.getRole()){
                case ADMIN:
                    Assert.notNull(user.getPerson());
                    Assert.notNull(user.getEmployer());
                    break;
                case PERSON:
                    Assert.notNull(user.getPerson());
                    break;
                case EMPLOYER:
                    Assert.notNull(user.getEmployer());
                    break;
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warn("User validation failed for user {}", user);
            throw e;
        }
    }

    /**
     * TODO: replace with reflection variant (maybe)
     *
     * @param user whos properties to be fully loaded from datastorage
     * @return object with loaded fields
     */
    @Transactional(readOnly = true)
    public User unproxyFields(@NotNull User user) {
        dao.unlazyUserFileds(user);
        return null;
    }

    public void deleteUser(@NotNull User user) {
        dao.delete(user);
    }

    public void deleteUser(@NotNull Long[] id) {
        dao.delete(User.class, id);
    }

}
