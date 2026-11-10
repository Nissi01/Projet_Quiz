package com.studyquiz.quiz;
import jakarta.persistence.*;
@Entity @Table(name="filieres")
public class Filiere {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(nullable=false,unique=true,length=160) public String name;
    protected Filiere() {}
    public Filiere(String name) { this.name=name; }
}
