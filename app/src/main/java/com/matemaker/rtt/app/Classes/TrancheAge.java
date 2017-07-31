package com.matemaker.rtt.app.Classes;

import java.io.Serializable;

/**
 * Created by Niquelesstup on 31/07/2017.
 */

public class TrancheAge implements Serializable {

    public TrancheAge() {
    }
    public TrancheAge(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    private int id;
    private String nom;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
}
