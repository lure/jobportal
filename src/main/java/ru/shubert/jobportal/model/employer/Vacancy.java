package ru.shubert.jobportal.model.employer;

import ru.shubert.jobportal.model.Currency;
import ru.shubert.jobportal.model.person.EducationGrade;
import ru.shubert.jobportal.model.prototype.AbstractEntity;

import javax.persistence.*;

/**
 * Требование к послужному списку - описывается просто опытом работы.
 */


@Entity
public class Vacancy extends AbstractEntity {
    @ManyToOne
    Employer employer;

    @Column(length = LONG_STRING)
    private String position;

    @Column(length = 2048)
    private String description;

    @Column
    private Integer salary;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private EducationGrade education;

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    public Vacancy() {
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public EducationGrade getEducation() {
        return education;
    }

    public void setEducation(EducationGrade education) {
        this.education = education;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
