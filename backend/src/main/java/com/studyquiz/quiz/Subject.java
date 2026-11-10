package com.studyquiz.quiz;
import jakarta.persistence.*;
@Entity @Table(name="subjects")
public class Subject {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(optional=false) @JoinColumn(name="filiere_id") public Filiere filiere;
    @Column(nullable=false,unique=true) public String name;
    protected Subject() {}
    public Subject(Filiere filiere,String name) { this.filiere=filiere;this.name=name; }
}
