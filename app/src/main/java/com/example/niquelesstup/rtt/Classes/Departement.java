package com.example.niquelesstup.rtt.Classes;

import java.io.Serializable;

/**
 * Created by Niquelesstup on 06/06/2017.
 */

public class Departement implements Serializable {

    public Departement() {
    }
    public Departement(int id, String code, String nom) {
        this.id = id;
        this.code = code;
        this.nom = nom;
    }

    private int id;
    private String code;
    private String nom;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
}
