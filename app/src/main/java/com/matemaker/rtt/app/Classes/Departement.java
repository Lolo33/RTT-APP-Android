package com.matemaker.rtt.app.Classes;

import java.io.Serializable;
import java.util.ArrayList;

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
    private ArrayList<Lieu> listeLieux;

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

    public ArrayList<Lieu> getListeLieux() {
        return listeLieux;
    }

    public void setListeLieux(ArrayList<Lieu> listeLieux) {
        this.listeLieux = listeLieux;
    }
}
