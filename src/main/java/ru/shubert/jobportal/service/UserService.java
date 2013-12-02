package ru.shubert.jobportal.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import ru.shubert.jobportal.model.User;
import ru.shubert.jobportal.strategy.ILoginTokenGenerator;

/**
 * User: user
 * Date: 07.07.12 17:18
 */
@Service("userService")
public class UserService extends MongoService{

    @Autowired
    ILoginTokenGenerator generator;

    public User findByIdAndLoginToken(String id, String loginToken ){
        if ( ObjectId.isValid(id) ) {
            return template.findOne(new Query(
                    Criteria.where("loginToken").is(loginToken)
                    .and("id").is(id)
            ), User.class);
        }
        return null;
    }

    public User findByLoginAndPassword(String login, String password){
        return template.findOne(new Query(
                Criteria.where("password").is(password)
                        .and("login").is(login)
        ), User.class);

    }

    public ILoginTokenGenerator getGenerator() {
        return generator;
    }



}
