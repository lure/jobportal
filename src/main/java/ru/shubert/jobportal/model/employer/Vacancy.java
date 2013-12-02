package ru.shubert.jobportal.model.employer;

import org.springframework.data.annotation.Persistent;
import org.springframework.data.annotation.Transient;
import ru.shubert.jobportal.model.AbstractEntity;
import ru.shubert.jobportal.model.Currency;
import ru.shubert.jobportal.model.person.EducationGrade;


/**
 * Требование к послужному списку - описывается просто опытом работы.
 */

@Persistent
public class Vacancy extends AbstractEntity {

    @Transient
    Employer employer;

    private String position;

    private String description;

    private Integer salary;

    private Currency currency;

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
