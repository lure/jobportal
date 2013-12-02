package ru.shubert.jobportal;

import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.shubert.jobportal.preset.MockTestApplication;
import ru.shubert.jobportal.preset.TransactionalTestWithContext;
import ru.shubert.jobportal.web.HomePage;
import ru.shubert.jobportal.web.LoginPage;

import javax.annotation.Resource;

/**
 * Simple test using the WicketTester
 * <a href="https://forum.hibernate.org/viewtopic.php?t=929167"> osiv </a>
 * read the {@link org.springframework.test.context.testng.AbstractTestNGSpringContextTests} !!!!  <br/>
 * and <a href="https://cwiki.apache.org/WICKET/spring.html">https://cwiki.apache.org/WICKET/spring.html</a>
 * <p/>
 * UPDATE: let's inherit webApp and rock that floor
 */
public class HomePageTest extends TransactionalTestWithContext {

    private WicketTester tester;

    private FormTester form;

    @Resource(name = "sessionFactory")
    protected SessionFactory sessionFactory = null;

    @BeforeClass
    protected void setUp() {
        tester = new WicketTester(new MockTestApplication(applicationContext));

//        sessionFactory = (SessionFactory) applicationContext.getBean("sessionFactory");
        Session session = SessionFactoryUtils.openSession(sessionFactory);
        TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
    }

    @AfterClass
    public void tearDown() {
        SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
        SessionFactoryUtils.closeSession(sessionHolder.getSession());
    }

    @Test
    public void unauthenticatedRedirectedToLoginPage() {
        tester.startPage(HomePage.class);
        tester.assertRenderedPage(LoginPage.class);
    }

    @Test(dependsOnMethods = "unauthenticatedRedirectedToLoginPage")
    public void emptyUserCantLogin() {
        tester.startPage(LoginPage.class);
        form = tester.newFormTester("signInPanel:signInForm");
        // set the parameters for each component in the form
        // notice that the name is relative to the form - so it's 'username', not 'form:username' as in assertComponent
        form.setValue("username", "");
        // unset value is empty string (wicket binds this to null, so careful if your setter does not expect nulls)
        form.setValue("password", "123");
        // all set, submit
        form.submit();
        // check if the page is correct: in this case, I'm expecting an error to take me back to the same page
        tester.assertRenderedPage(LoginPage.class);
        // check if the error message is the one expected (you should use wicket's internationalization for this)
        // if you're not expecting an error (testing for submit successful) use assertNoErrorMessage() instead
        // Commented as there are no feedback panel in our markup
        //tester.assertErrorMessages(new String[]{"Не удалось войти"});
    }

    @Test(dependsOnMethods = "unauthenticatedRedirectedToLoginPage")
    public void passwordCanNotBeEmpty() {
        tester.startPage(LoginPage.class);
        form = tester.newFormTester("signInPanel:signInForm");
        // set the parameters for each component in the form
        // notice that the name is relative to the form - so it's 'username', not 'form:username' as in assertComponent
        form.setValue("username", "test");
        // unset value is empty string (wicket binds this to null, so careful if your setter does not expect nulls)
        form.setValue("password", "");
        // all set, submit
        form.submit();
        // check if the page is correct: in this case, I'm expecting an error to take me back to the same page
        tester.assertRenderedPage(LoginPage.class);
        // check if the error message is the one expected (you should use wicket's internationalization for this)
        // if you're not expecting an error (testing for submit successful) use assertNoErrorMessage() instead
        // Commented as there are no feedback panel in our markup
        //tester.assertErrorMessages(new String[]{"Поле 'password' обязательно для ввода."});
    }

    @Test(dependsOnMethods = "unauthenticatedRedirectedToLoginPage")
    public void rerenderSignInIfUserUnknown() {
        tester.startPage(LoginPage.class);
        form = tester.newFormTester("signInPanel:signInForm");
        // set the parameters for each component in the form
        // notice that the name is relative to the form - so it's 'username', not 'form:username' as in assertComponent
        form.setValue("username", "beta@beta.ru");
        // unset value is empty string (wicket binds this to null, so careful if your setter does not expect nulls)
        form.setValue("password", "abrakadabra!!!");
        // all set, submit
        form.submit();
        // check if the page is correct: in this case, I'm expecting an error to take me back to the same page
        tester.assertRenderedPage(LoginPage.class);
        // check if the error message is the one expected (you should use wicket's internationalization for this)
        // if you're not expecting an error (testing for submit successful) use assertNoErrorMessage() instead
        // Commented as there are no feedback panel in our markup
        //tester.assertErrorMessages(new String[]{"Не удалось войти"});
    }

    @Test(dependsOnMethods = "unauthenticatedRedirectedToLoginPage")
    public void successfullLoginWithCredentials() {
        tester.startPage(LoginPage.class);
        form = tester.newFormTester("signInPanel:signInForm");
        // set the parameters for each component in the form
        // notice that the name is relative to the form - so it's 'username', not 'form:username' as in assertComponent
        form.setValue("username", "admin");
        // unset value is empty string (wicket binds this to null, so careful if your setter does not expect nulls)
        form.setValue("password", "123");
        // all set, submit
        form.submit();
        // there must be no error messages
        tester.assertNoErrorMessage();
        // check if the page is correct: in this case, I'm expecting an error to take me back to the same page
        tester.assertRenderedPage(HomePage.class);
    }


}