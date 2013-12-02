package ru.shubert.jobportal.model.person;

import ru.shubert.jobportal.model.prototype.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;

/**
 * Single education line. Collection of them is held by a person and may be used in a search
 */
@Entity
public class Education extends AbstractEntity {

    @Enumerated
    private EducationGrade grade;

    @Column
    private Integer end;

    @Column(length = LONG_STRING)
    private String place;

    @Column(length = LONG_STRING)
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
}
