package ru.shubert.jobportal.config;

import com.mongodb.Mongo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import ru.shubert.jobportal.dao.MongoDataSeeder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * User: user
 * Date: 06.07.12 20:28
 */
@Configuration
public class MongoConfig extends AbstractMongoConfiguration {



    @Override
    public String getDatabaseName() {
        return "jobportal";
    }

    @Override
    public Mongo mongo() throws Exception {
        return new Mongo("localhost");
    }

    @Override
    public UserCredentials getUserCredentials() {
        return super.getUserCredentials();
    }


    @Bean
    public MongoDataSeeder mongoDataSeeder() throws Exception {
        MongoDataSeeder seeder = new MongoDataSeeder();
        seeder.setMongoTemplate(mongoTemplate());

        ClassPathResource resource = new ClassPathResource("initialData.json");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), "UTF-8"));
        StringBuilder readData = new StringBuilder();
        String readLine;
        while((readLine = bufferedReader.readLine()) != null) {
            readData.append(readLine);
        }

        seeder.setData(readData.toString());
        return seeder;
    }

}
