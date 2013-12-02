package ru.shubert.jobportal.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.shubert.jobportal.model.employer.Employer;
import ru.shubert.jobportal.model.person.Person;

import java.util.EnumSet;


/**
 * Represent any application user. Contains login, password, and representation. Representation may be person e.g.
 * employee or employer - speak for itself.
 * May be Employer or Person depending on chosen role, or Admin - one in two faces.
 *
 * update: Employer abd Person mappings in User modified to be responsible part of association
 * <ol>
 *  <li> @MapsId is removed from Employer and Person and 'mappedBy' clause added instead</li>
 *  <li> @JoinColumn added in  User class, pointing on column that keeps foreign key for embedded class</li>
 *  Association with Employer and Person maybe null so and foreign key. Hence Hibernate knows exactly when there are no
 *  employer or person without loading them.
 * </ol>
 * @see <a href="http://stackoverflow.com/questions/1444227/making-a-onetoone-relation-lazy">stackoverflow</a> and
 * <a href="https://community.jboss.org/wiki/SomeExplanationsOnLazyLoadingone-to-one">Hibernate docs</a> for
 * information
 */

@Document
public class User extends AbstractEntity {

    @Indexed(unique = true)
    private String login;

    private String password;

    private String loginToken;

    private RoleEnum role = RoleEnum.PERSON;

    private String firstName;

    private String middleName;

    private String lastName;

    @DBRef
    private Employer employer;

    @DBRef
    private Person person;


    /* FUNCTIONS */

    public User() {
    }

    @PersistenceConstructor
    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(@Nullable String loginToken) {
        this.loginToken = loginToken;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(@NotNull RoleEnum role) {
        this.role = role;
    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(@Nullable Employer employer) {
        this.employer = employer;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(@Nullable Person person) {
        this.person = person;
    }

    public EnumSet<RoleEnum> getRoles() {
        return EnumSet.of(role);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void initRole() {
        if (role == null) {
            throw new NullPointerException("Can't initialize null role!");
        }
        switch (role) {
            case EMPLOYER:
                Employer e = new Employer();
                e.setUser(this);
                this.setEmployer(e);
                break;
            case PERSON:
                Person p = new Person();
                p.setUser(this);
                this.setPerson(p);
                break;
            case ADMIN :
                throw new IllegalArgumentException("Admin creation is not allowed!");
        }
    }
}
