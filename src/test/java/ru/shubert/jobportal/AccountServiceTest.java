package ru.shubert.jobportal;

import junit.framework.Assert;
import org.testng.annotations.Test;
import ru.shubert.jobportal.model.User;
import ru.shubert.jobportal.model.employer.Employer;
import ru.shubert.jobportal.model.person.Person;
import ru.shubert.jobportal.model.prototype.RoleEnum;
import ru.shubert.jobportal.preset.TransactionalTestWithContext;
import ru.shubert.jobportal.service.IAccountService;

import javax.annotation.Resource;

/**
 * User: user
 * Date: 29.04.12 17:49
 */


@Test
public class AccountServiceTest extends TransactionalTestWithContext {

    @Resource()
    private IAccountService service;


    public void loadingExistingUserMustSucceed() {
        Object p = service.get(User.class, 1L);
        Assert.assertNotNull(p);
    }

    public void loadingEmployerMustSucceed() {
        Employer p = service.get(Employer.class, 1L);
        Assert.assertNotNull(p);
    }

    public void getEmployerOnEmployerUserMustSucceed() {
        User p = service.get(User.class, 1L);
        Assert.assertNotNull(p);
        Assert.assertNotNull(p.getEmployer());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void userInitRoleMustThrowExceptionOnNullRole() {
        User user = new User();
        //noinspection ConstantConditions
        user.setRole(null);
        user.initRole();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void userInitRoleMustThrowExceptionOnAdminRole() {
        User user = new User();
        user.setRole(RoleEnum.ADMIN);
        user.initRole();
    }

    public void employerUserMustHaveEmployerObject() {
        User user = new User();
        user.setRole(RoleEnum.EMPLOYER);
        user.initRole();
        Assert.assertNotNull(user.getEmployer());
    }

    public void printVacancyListMustSucee(){
        System.out.println("===================================");
        Person e = service.get(Person.class, 1L);
        System.out.println(e.getEducation());
        System.out.println(e.getExperiences());
        System.out.println("===================================");
    }




}
