package ru.shubert.jobportal.dao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.shubert.jobportal.model.User;

/**
 * Describes methods for account management
 * Account consists from {@link ru.shubert.jobportal.model.User}
 */
public interface IAccountDao extends IDao{

    /**
     * Finds user by login.
     *
     * @param login user login.
     * @return user with given login or {@code null} if no such user found.
     */
    @Nullable
    User findUserByLogin(@NotNull final String login);

    /**
     * Finds user by login token.
     *
     * @param token user login token, aquired from cookie
     * @return user with given login or {@code null} if no such user found.
     */
    @Nullable
    User findUserByToken(@NotNull final String token);

    /**
     * Persists User object
     *
     * @param user to be persisted
     * @return user object with hopefully initialized ID
     */
    @Nullable
    User saveUser(@NotNull User user);

    /**
     * Initialization of user proxy with all permission collections.
     *
     * @param user to initialize
     * @return version with unproxied collections
     */
    User unlazyUserFileds(@NotNull User user);
}