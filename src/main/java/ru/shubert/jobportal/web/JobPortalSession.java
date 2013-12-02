package ru.shubert.jobportal.web;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.WebUtils;
import ru.shubert.jobportal.model.User;
import ru.shubert.jobportal.model.prototype.RoleEnum;
import ru.shubert.jobportal.service.IAccountService;
import ru.shubert.jobportal.web.strategy.DetachableEntityModel;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Authenticated HTTP session may log users in by stored cookies with login token inside.
 * On logout every cookies are purged. Login token are the same for every computer and is not
 * bound to IP because there are a lot of sub-nets hidden behind one public ip.
 * <p/>
 * On login we always storing cookies. May be, we have to drop this functionality at all preferring
 * pure http session with expiration and login. Also, it's likely SpringFramework already got
 * convenient way to remember users.
 *
 * @see "org.springframework.security.ui.rememberme"
 */
public class JobPortalSession extends AuthenticatedWebSession {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobPortalSession.class);

    // cookie constant names: ID and LoginToken
    final static private String COOKIE_USERID = "id";
    final static private String COOKIE_LOGINTOKEN = "token";

    // dunno why I prefer this one instead of covariant getRequest/getResponse
    final static private CookieGenerator TOKENCOOKIE_GENERATOR;
    final static private CookieGenerator USERIDCOOKIE_GENERATOR;

    @SpringBean
    static private IAccountService service;

    static {
        TOKENCOOKIE_GENERATOR = new CookieGenerator();
        TOKENCOOKIE_GENERATOR.setCookieName(COOKIE_LOGINTOKEN);


        USERIDCOOKIE_GENERATOR = new CookieGenerator();
        USERIDCOOKIE_GENERATOR.setCookieName(COOKIE_USERID);
    }

    // currently logged user
    private IModel<User> userModel;

    public JobPortalSession(Request request) {
        super(request);
        Injector.get().inject(this);
        userModel = new DetachableEntityModel<>(service);
        signInByCookie();
    }

    /**
     * Authentication with stored in cookies session. If login token exists and id is suitable
     * for any stored {@link User} information then that user is automatically logged in bypassing
     * SignIn page.
     * <b>NOTE:</b> It's possible to store user credentials just stealing cookie for loginToken+userid
     * works as login+password pair!
     */
    private void signInByCookie() {
        // TODO: consider unsafety of such method.
        Cookie tokenCookie = WebUtils.getCookie(getHttpRequest(), COOKIE_LOGINTOKEN);
        Cookie userIdCookie = WebUtils.getCookie(getHttpRequest(), COOKIE_USERID);

        if (tokenCookie != null && userIdCookie != null) {
            Long userId;
            final String loginToken = tokenCookie.getValue();
            try {
                userId = Long.valueOf(userIdCookie.getValue());
            } catch (NumberFormatException e) {
                LOGGER.debug("signInByCookie cannot convert loginId:" + e.getMessage());
                userId = null;
            }

            if (loginToken != null && userId != null) {
                final User user = service.findByToken(loginToken);
                if (user != null && user.getId().equals(userId)) {
                    signIn(true);
                    setUser(user);
                    return;
                }
            }
            clearCookie();
        }

    }

    /**
     * TODO: It strongly necessary to rewrite that pretty simple code with salted password
     *
     * @param username received from http request
     * @param password received from http request
     * @return true if user with such credentials exists
     */
    @Override
    public boolean authenticate(final String username, final String password) {
        User user = service.findByLogin(username);
        if (user != null && user.getPassword().equals(password)) {
            user.setLoginToken(service.getTokenGenerator().generate());
            service.save(user);

            USERIDCOOKIE_GENERATOR.addCookie(getHttpResponse(), user.getId().toString());
            TOKENCOOKIE_GENERATOR.addCookie(getHttpResponse(), user.getLoginToken());
            setUser(user);
            return true;
        }
        clearCookie();
        return false;
    }

    public void forceAuthenticate(final User user) {
        invalidate();
        LOGGER.info("signing in {}", user);
        USERIDCOOKIE_GENERATOR.addCookie(getHttpResponse(), user.getId().toString());
        TOKENCOOKIE_GENERATOR.addCookie(getHttpResponse(), user.getLoginToken());
        setUser(user);
        signIn(true);
    }

    @Override
    public Roles getRoles() {
        User user = userModel.getObject();
        if (user != null) {
            Roles roles = new Roles();
            for (RoleEnum userRole : user.getRoles()) {
                if (userRole != null) roles.add(userRole.name());
            }
            return roles;
        }
        return null;
    }

    @Override
    public void signOut() {
        super.signOut();
        User user = getUser();
        if (user != null) {
            LOGGER.info("signing out {}", user);
            user.setLoginToken(null);
            service.save(user);
        }
        setUser(null);
        clearCookie();
    }

    public void setUser(@Nullable User userObj) {
        userModel.setObject(userObj);
    }

    /**
     * @return User object, acquired from data storage by {@link #authenticate(String, String)} method
     */
    @SuppressWarnings("UnusedDeclaration")
    public User getUser() {
        return userModel.getObject();
    }

    /**
     * Convenient shortcut method for casting Wicket's {@link Request} to
     * javax {@link javax.servlet.http.HttpServletRequest} as only that type may be passed to
     * SpringFramework's {@link org.springframework.web.util.WebUtils}
     *
     * @return request casted to {@link javax.servlet.http.HttpServletRequest}
     */
    private static HttpServletRequest getHttpRequest() {
        return (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest();
    }

    /**
     * Convenient shortcut method for casting Wicket's {@link org.apache.wicket.request.Response} to
     * javax {@link javax.servlet.http.HttpServletResponse} as only that type may be passed to
     * SpringFramework's {@link org.springframework.web.util.WebUtils}
     *
     * @return request casted to {@link javax.servlet.http.HttpServletResponse}
     */
    private static HttpServletResponse getHttpResponse() {
        return (HttpServletResponse) RequestCycle.get().getResponse().getContainerResponse();
    }

    /**
     * @return Current authenticated web session
     */
    public static JobPortalSession get() {
        return (JobPortalSession) Session.get();
    }

    public IModel<User> getUserModel() {
        return userModel;
    }

    protected void clearCookie() {
        USERIDCOOKIE_GENERATOR.removeCookie(getHttpResponse());
        TOKENCOOKIE_GENERATOR.removeCookie(getHttpResponse());
    }
}

