package ru.shubert.jobportal.dao;

import org.apache.wicket.util.string.Strings;
import org.hibernate.Hibernate;
import org.hibernate.criterion.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.Assert;
import ru.shubert.jobportal.model.employer.Vacancy;
import ru.shubert.jobportal.model.person.Person;

import java.lang.reflect.Field;

/**
 * Hibernate example builder. Queries it makes are based on {@link QueryParams} and
 * target object example. Object serves as a filter i.e. his property values are used as query parameters.
 * It made abstract so no one decide to inherit.
 * <p/>
 * It may be refactored to Builder.getBuilder(..customBuilderObject..) but it's enough for current task
 */
public abstract class DetachedCriteriaBuilder {

    /**
     * factory method for Example initialized with entity fields.
     * It builds examples where properties queries with like, wildcards implicitly added, all null and zero fields
     * are ignored and case are ignored. Also it reads configuration properties
     * from {@link QueryParams}
     * @see <a href="http://docs.jboss.org/hibernate/orm/3.3/reference/en/html/querycriteria.html">Hibernate Criteria</a>
     *
     * @param entity with partially set properties
     * @param params of sorting and result view frame. May be null, then order clause will be empty
     * @param <T>    anu suitable class
     * @return {@link org.hibernate.criterion.DetachedCriteria} ready to be used in query
     */
    public static <T> DetachedCriteria getLikeNotNullExample(@NotNull T entity, @Nullable QueryParams params) {
        Assert.notNull(entity);
        Integer salary = null;

        Field m;
        try {
            m = entity.getClass().getDeclaredField("salary");
            if (null != m ){
                m.setAccessible(true);
                salary = (Integer) m.get(entity);
                m.set(entity, 0);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // here goes object who serves as example
        Example example = Example.create(entity);
        // Announcing we want to look with %like% clauses
        example.enableLike(MatchMode.ANYWHERE);
        // all properties set to null or zero must be not included in result query
        example.excludeZeroes();
        // and ignore case
        example.ignoreCase();

        DetachedCriteria criteria = DetachedCriteria.forClass(entity.getClass());
        criteria.add(example);
        if (null != salary && (! salary.equals(0))) {
            criteria.add(Restrictions.ge("salary", salary));
        }

        // VACANCY
        if (Hibernate.getClass(entity).equals(Vacancy.class)) {
            Example emp = Example.create(((Vacancy) entity).getEmployer())
                    .enableLike(MatchMode.ANYWHERE)
                    .excludeZeroes()
                    .ignoreCase();
            criteria.add(example).createCriteria("employer").add(emp);
        }

        // PERSON
        if (Hibernate.getClass(entity).equals(Person.class)) {
            Person person = (Person) entity;
            if (null != person.getEducation().get(0).getGrade()){
                criteria
                        .createCriteria("education")
                        .add(Restrictions.eq("grade", person.getEducation().get(0).getGrade()));
            }

            if (!Strings.isEmpty(person.getExperiences().get(0).getCompany())) {
                criteria
                        .createCriteria("experiences")
                        .add(Restrictions.ilike("company", person.getExperiences().get(0).getCompany()));
            }
        }

        // ORDER AND PAGINATION
        if (params != null && params.getOrderField() != null) {
            criteria.addOrder(params.getOrderAsc() ? Order.asc(params.getOrderField()) : Order.desc(params.getOrderField()));
        }

        return criteria;
    }
}