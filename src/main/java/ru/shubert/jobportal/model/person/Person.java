package ru.shubert.jobportal.model.person;


import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.shubert.jobportal.model.AbstractEntity;
import ru.shubert.jobportal.model.Currency;
import ru.shubert.jobportal.model.User;

import java.util.LinkedList;
import java.util.List;

/**
 * update: Education and JobExperience by default uses join tables to connect with owner.
 * Specifying explicit joincolumn makes every Education or Expirience object directly know where their
 * master is
 */
@Document
public class Person extends AbstractEntity{

    @Transient
    private User user;

    // does he look for a job
    private Boolean available;

    private String position;

    private Integer salary;

    private Currency currency;

    private String address;

    private List<Education> education;

    private List<JobExperience> experiences;

    private String description;

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Education> getEducation() {
        if (education == null) {
            education = new LinkedList<>();
        }
        return education;
    }

    public void setEducation(final List<Education> education) {
        this.education = education;
    }

    public List<JobExperience> getExperiences() {
        if (experiences == null) {
            experiences = new LinkedList<>();
        }
        return experiences;
    }

    public void setExperiences(final List<JobExperience> experiences) {
        this.experiences = experiences;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(final Currency currency) {
        this.currency = currency;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
