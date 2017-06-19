package com.example.niquelesstup.rtt.Classes;

import java.io.Serializable;

/**
 * Created by Niquelesstup on 07/06/2017.
 */

public class StatutJoueur implements Serializable {

    public StatutJoueur(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }
    public StatutJoueur() {
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
