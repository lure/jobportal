package ru.shubert.jobportal.model.employer;

import org.hibernate.annotations.LazyToOne;
import ru.shubert.jobportal.model.User;
import ru.shubert.jobportal.model.prototype.AbstractEntity;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Embedded class describes Employer avatar of the user.
 * Holds list of {@link Vacancy} objects and information about company itself.
 * Linked with User using foreign key without join table
 */

@Entity
public class Employer extends AbstractEntity {

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "employer")
    User user;

    @Column(length = LONG_STRING)
    private String name;

    @Column(length = LONG_STRING)
    private String url;

    @Column(length = 2048)
    private String description;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "employer", fetch = FetchType.LAZY)
    private List<Vacancy> vacancies = new LinkedList<>();

    public Employer() {
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public List<Vacancy> getVacancies() {
        return vacancies;
    }

    public void setVacancies(final List<Vacancy> vacancies) {
        this.vacancies = vacancies;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
