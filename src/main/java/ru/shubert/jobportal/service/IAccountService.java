package ru.shubert.jobportal.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.shubert.jobportal.model.User;
import ru.shubert.jobportal.strategy.ILoginTokenGenerator;

import java.util.List;

/**
 * User: user
 * Date: 30.04.12 15:53
 */
public interface IAccountService extends IService {

    /* =========== USER ===========*/

    User newUser();

    User saveUser(@NotNull User user);

    User unproxyFields(@NotNull User id);

    @Nullable
    User getUser(@NotNull Long id);

    List<User> listUsers();

    void deleteUser(@NotNull User user);

    void deleteUser(@NotNull Long... id);

    @Nullable
    User findByToken(@NotNull String token);

    @Nullable
    User findByLogin(@NotNull String login);

    ILoginTokenGenerator getTokenGenerator();
}
