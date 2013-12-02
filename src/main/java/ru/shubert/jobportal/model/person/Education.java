package ru.shubert.jobportal.model.person;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.shubert.jobportal.model.AbstractEntity;

/**
 * Single education line. Collection of them is held by a person and may be used in a search
 */
@Document
public class Education extends AbstractEntity{

    @Transient
    Person person;

    private EducationGrade grade;

    private Integer end;

    private String place;

    private String speciality;


    public EducationGrade getGrade() {
        return grade;
    }

    public void setGrade(EducationGrade grade) {
        this.grade = grade;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
