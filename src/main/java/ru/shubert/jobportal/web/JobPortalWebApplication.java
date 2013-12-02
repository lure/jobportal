package ru.shubert.jobportal.web;

import org.apache.wicket.Page;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.handler.ListenerInterfaceRequestHandler;
import org.apache.wicket.request.mapper.MountedMapper;
import org.apache.wicket.request.mapper.info.PageComponentInfo;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

/**
 * User: user
 * Date: 29.04.12 15:47
 */

public class JobPortalWebApplication extends AuthenticatedWebApplication {

    /**
     * keep constructor empty for the sake of testing. All initialisation and setup must be completed in {@link #init()}
     *
     */
    public JobPortalWebApplication() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init() {
        super.init();
        getComponentInstantiationListeners().add(getContextInjector());
        Injector.get().inject(this);

        // removing wicket tags, comments and restricts markup with UTF-8 only
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
        getMarkupSettings().setStripComments(true);
        getMarkupSettings().setStripWicketTags(true);

        getDebugSettings().setDevelopmentUtilitiesEnabled(true);

        // if page expired -- redirect user to homepage
        getApplicationSettings().setPageExpiredErrorPage(HomePage.class);
        //mount(new QueryStringUrlCodingStrategy("error404", HomePage.class));
        mountPage("/login", LoginPage.class);
        mountPage("/home", HomePage.class);
        mountPage("/registration", RegistrationPage.class);
        mountPage("/person", PersonPage.class);
        mountPage("/employer", EmployerPage.class);
        mountPage("/control", ControlPanelPage.class);
        mountPage("/vacancy", VacancyListPage.class);
        mountPage("/resume", ResumeListPage.class);
        mount(new MountedMapperWithoutPageComponentInfo("/show", ShowItemPage.class));
        //mountPage("/show", ShowItemPage.class);   http://stackoverflow.com/questions/8602489/delete-version-number-in-url
    }

    /**
     * Extracted injection setup for testCase overriding
     *
     * @return prepared injector
     */
    protected SpringComponentInjector getContextInjector() {
        return new SpringComponentInjector(this);
    }

    @Override
    protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
        return JobPortalSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class;
    }

    @Override
    public Class<HomePage> getHomePage() {
        return HomePage.class;
    }

    /**
     * Called when an AUTHENTICATED user tries to navigate to a page that they are not authorized to
     * access. Overrided to redirect user to login page in case if he has no access to home page. In other cases
     * redirects user to {@link org.apache.wicket.markup.html.pages.AccessDeniedPage}
     *
     * @param page The page
     */
    @Override
    protected void onUnauthorizedPage(Page page) {
        // if this is home page and user unauthorized to see it, that
        // he must be logged out at all and redirected to login page;
        if (page instanceof HomePage) {
            JobPortalSession.get().invalidate();
        } else
            super.onUnauthorizedPage(page);
    }

    /**
     * Covariant get method.
     *
     * @return JobPortalWebApplication
     */
    public static JobPortalWebApplication get() {
        return (JobPortalWebApplication) WebApplication.get();
    }
}

//http://stackoverflow.com/questions/8602489/delete-version-number-in-url
class MountedMapperWithoutPageComponentInfo extends MountedMapper {

    public MountedMapperWithoutPageComponentInfo(String mountPath, Class<? extends IRequestablePage> pageClass) {
        super(mountPath, pageClass, new PageParametersEncoder());
    }

    @Override
    protected void encodePageComponentInfo(Url url, PageComponentInfo info) {
        // do nothing so that component info does not get rendered in url
    }

    @Override
    public Url mapHandler(IRequestHandler requestHandler)
    {
        if (requestHandler instanceof ListenerInterfaceRequestHandler) {
            return null;
        } else {
            return super.mapHandler(requestHandler);
        }
    }
}