package com.studyquiz.quiz;
import jakarta.persistence.*;
@Entity @Table(name="subjects")
public class Subject {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(nullable=false,unique=true) public String name;
    protected Subject() {}
    public Subject(String name) { this.name=name; }
}
