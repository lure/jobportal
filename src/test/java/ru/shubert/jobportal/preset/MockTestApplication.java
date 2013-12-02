package ru.shubert.jobportal.preset;

import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;
import ru.shubert.jobportal.web.JobPortalWebApplication;

/**
 * Mock of wicket web application.
 * We need it as a base for webtests
 *
 *
 */
public class MockTestApplication extends JobPortalWebApplication{
    protected ApplicationContext context;

    public MockTestApplication(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    @Override
    protected SpringComponentInjector getContextInjector() {
        return new SpringComponentInjector(this, context, true);
    }
}
