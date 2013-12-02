package ru.shubert.jobportal;

import org.apache.commons.io.FileUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.util.Assert;
import org.testng.annotations.Test;
import ru.shubert.jobportal.config.AppConfig;
import ru.shubert.jobportal.config.MongoConfig;
import ru.shubert.jobportal.config.WebConfig;
import ru.shubert.jobportal.model.User;
import ru.shubert.jobportal.model.employer.Vacancy;
import ru.shubert.jobportal.service.MongoService;

import javax.annotation.Resource;
import java.util.List;

@ContextConfiguration(classes = {WebConfig.class, AppConfig.class, MongoConfig.class})
public class AccountServiceTest extends AbstractTestNGSpringContextTests {

    @Resource(name = "baseService")
    private MongoService service;

    @Resource
    private MongoTemplate template;

    @Test
    public void checkInitialData() {
        List<User> users = template.findAll(User.class);
        Assert.notEmpty(users);
    }

    @Test
    public void findTest() {
        /*
        Criteria criteria = Criteria.where("position").regex(".*lish.*", "i");
        System.out.println("\nfindTest output");
        for (Vacancy o : template.find(new Query(criteria), Vacancy.class)) {
            System.out.println(o.getPosition());
        }
        */

        Criteria criteria = Criteria.where("position").is("стоматолог");
        for (Vacancy o : template.find(new Query(criteria), Vacancy.class)) {
            System.out.println(o.getDescription());
        }


//        DBCollection collection = template.getDb().getCollection("vacancy");
//        BasicDBObject query = new BasicDBObject();
//        pattern = Pattern.compile(".*мато.*", Pattern.UNICODE_CHARACTER_CLASS);
//        query.put("position", pattern);
//        query.put("position", "стоматолог");
//        System.out.println("--" + collection.find(query).count() + "--  " + collection.find(query));
    }
}
