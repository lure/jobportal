package ru.shubert.jobportal.model.person;


import org.hibernate.annotations.IndexColumn;
import ru.shubert.jobportal.model.Currency;
import ru.shubert.jobportal.model.User;
import ru.shubert.jobportal.model.prototype.AbstractEntity;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

/**
 * update: Education and JobExperience by default uses join tables to connect with owner.
 * Specifying explicit joincolumn makes every Education or Expirience object directly know where their
 * master is
 */
@Entity
public class Person extends AbstractEntity {
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "person")
    User user;

    // does he look for a job
    private Boolean available;

    @Column(length = LONG_STRING)
    private String position;

    private Integer salary;

    @Enumerated(value = EnumType.STRING)
    private Currency currency;

    @Column(length = 2048)
    private String address;

    // schema is built by hibernate and for h2 database there are no way to determine column presence since h2 operates in memory
    @SuppressWarnings("JpaDataSourceORMInspection")
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderColumn(name="list_order")
    @JoinColumn(name="person_id")
    private List<Education> education;

    // schema is built by hibernate and for h2 database there are no way to determine column presence since h2 operates in memory
    @SuppressWarnings("JpaDataSourceORMInspection")
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderColumn(name="list_order")
    @JoinColumn(name="person_id")
    private List<JobExperience> experiences;

    @Column(length = 2048)
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
