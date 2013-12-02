package ru.shubert.jobportal.model.person;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.shubert.jobportal.model.AbstractEntity;
import ru.shubert.jobportal.model.Currency;

import java.util.Date;

/**
 * User: user
 * Date: 29.04.12 17:24
 */

@Document
public class JobExperience extends AbstractEntity{

    @Transient
    Person person;

    private String company;

    private Date start;

    private Date end;

    private String position;

    private Integer salary;

    private Currency currency;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
