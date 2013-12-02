package ru.shubert.jobportal.model.employer;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.shubert.jobportal.model.AbstractEntity;
import ru.shubert.jobportal.model.User;

import java.util.LinkedList;
import java.util.List;

/**
 * Embedded class describes Employer avatar of the user.
 * Holds list of {@link Vacancy} objects and information about company itself.
 * Linked with User using foreign key without join table
 */
@Document
public class Employer extends AbstractEntity {

    @Transient
    private User user;

    private String name;

    private String url;

    private String description;

    @DBRef
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
