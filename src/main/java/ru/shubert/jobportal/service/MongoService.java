package ru.shubert.jobportal.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.shubert.jobportal.dao.QueryParams;
import ru.shubert.jobportal.model.Currency;
import ru.shubert.jobportal.model.User;
import ru.shubert.jobportal.model.employer.Employer;
import ru.shubert.jobportal.model.employer.Vacancy;
import ru.shubert.jobportal.model.person.EducationGrade;
import ru.shubert.jobportal.model.person.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: user
 * Date: 10.07.12 23:18
 */

@Service("baseService")
public class MongoService implements IService {

    static final String LIKESTR = ".*%s.*";

    @Autowired
    protected MongoTemplate template;


    @Override
    public <T> T findOne(String id, Class<T> tClass) {
        if (ObjectId.isValid(id)) {
            return template.findById(id, tClass);
        }
        return null;
    }

    @Override
    public <T> T findOne(ObjectId id, Class<T> tClass) {
        return template.findById(id, tClass);
    }

    @Override
    public <T> T save(T t) {
        if (t != null) {
            template.save(t);
        }
        return t;
    }

    @Override
    public <T> void delete(T t) {
        template.remove(t);
    }

    public <T> long count(final T example) {
        Criteria criteria = null;

        if (example.getClass().equals(Vacancy.class)) {
            criteria = findVacancy((Vacancy) example);
        }

        if (example.getClass().equals(Person.class)) {
        }

        if (criteria != null) {
            return template.count(new Query(criteria), example.getClass());
        }
        return template.count(null, example.getClass());
    }


    @SuppressWarnings("unchecked")
    public <T> List<T> find(final T example, QueryParams params) {

        if (example.getClass().equals(Vacancy.class)) {
            Query query = new Query(findVacancy((Vacancy) example));
            List<Vacancy> list = template.find(query, Vacancy.class);

            if (list.size() > 0){
                Map<ObjectId, Vacancy> vacMap = new HashMap<>(list.size());
                List<ObjectId> empList = new ArrayList<>();
                for(Vacancy v: list){
                    vacMap.put(v.getId(), v);
                    empList.add(v.getId());
                }
                Query empQuery = new Query(Criteria.where("vacancies.$id").in(empList));
                List<Employer> employers = template.find(empQuery, Employer.class);

                for(Employer employer : employers){
                    for (Vacancy subVac: employer.getVacancies()){
                        vacMap.get(subVac.getId()).setEmployer(employer);
                    }
                }
            }
            return (List<T>) list;
        }


        if (example.getClass().equals(Person.class)) {
            Query query = new Query(findPerson((Person) example));
            List<Person> list = template.find(query, Person.class);

            if (list.size() > 0){
                Map<ObjectId, Person> persMap = new HashMap<>(list.size());
                List<ObjectId> empList = new ArrayList<>();
                for(Person o: list){
                    persMap.put(o.getId(), o);
                    empList.add(o.getId());
                }
                Query empQuery = new Query(Criteria.where("person.$id").in(empList));
                List<User> users = template.find(empQuery, User.class);

                for(User user : users){
                    persMap.get(user.getPerson().getId()).setUser(user);
                }
            }
            return (List<T>) list;

        }

        //noinspection unchecked
        return template.findAll((Class<T>) example.getClass());
    }

    public Criteria findVacancy(final Vacancy o) {

        //  reducing tuple by employers
        List<ObjectId> employerList = new ArrayList<>();
        String empName = o.getEmployer().getName();
        if (StringUtils.hasText(empName)) {
            Criteria criteria = Criteria.where("name").regex(like(empName), "i");
            Query query = new Query(criteria);
            query.fields().include("vacancies");
            for (Employer e : template.find(query, Employer.class)) {
                for (Vacancy vacancy : e.getVacancies()) {
                    employerList.add(vacancy.getId());
                }
            }
        }

        Criteria criteria = new Criteria();

        // filter by employers id
        if (employerList.size() > 0) {
            criteria = criteria.and("id").in(employerList);
        }

        String position = o.getPosition();
        if (StringUtils.hasText(position)) {
            criteria = criteria.and("position").regex(like(position), "i");
        }

        Integer salary = o.getSalary();
        if (salary != null && salary > 0) {
            criteria = criteria.and("salary").gte(salary);
        }

        Currency currency = o.getCurrency();
        if (currency != null) {
            criteria = criteria.and("currency").is(currency.toString());
        }

        return criteria;
    }

    public Criteria findPerson(final Person o) {
        Criteria criteria = new Criteria();

        if (StringUtils.hasText(o.getPosition())) {
            criteria = criteria.and("position").regex(like(o.getPosition()), "i");
        }

        Integer salary = o.getSalary();
        if (salary != null && salary > 0) {
            criteria = criteria.and("salary").gte(salary);
        }

        Currency currency = o.getCurrency();
        if (currency != null) {
            criteria = criteria.and("currency").is(currency.toString());
        }

        EducationGrade grade = o.getEducation().get(0).getGrade();
        if (grade != null) {
            criteria = criteria.and("education.grade").is(grade.toString());
        }

        String emp = o.getExperiences().get(0).getCompany();
        if (StringUtils.hasText(emp)) {
            criteria = criteria.and("experiences.company").regex(like(emp), "i");
        }

        return criteria;
    }



    protected String like(String root){
        return String.format(LIKESTR, root);
    }
}
