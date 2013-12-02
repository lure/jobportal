package ru.shubert.jobportal.model.person;

import ru.shubert.jobportal.model.Currency;
import ru.shubert.jobportal.model.prototype.AbstractEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * User: user
 * Date: 29.04.12 17:24
 */

@Entity
public class JobExperience extends AbstractEntity {

    @Column(length = LONG_STRING)
    private String company;

    private Date start;

    private Date end;

    @Column(length = LONG_STRING)
    private String position;

    private Integer salary;

    @Enumerated(value = EnumType.STRING)
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
}
