package com.example.niquelesstup.rtt.Classes;

import java.util.ArrayList;

/**
 * Created by Niquelesstup on 06/06/2017.
 */

public class Equipe {
    private int id;
    private String nom;
    private String code;
    private boolean isPrive;
    private String pass;
    private ArrayList<Membre> ListeMembres;

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

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public boolean isPrive() {
        return isPrive;
    }
    public void setPrive(boolean prive) {
        isPrive = prive;
    }

    public String getPass() {
        return pass;
    }
    public void setPass(String pass) {
        this.pass = pass;
    }

    public ArrayList<Membre> getListeMembres() {
        return ListeMembres;
    }
    public void setListeMembres(ArrayList<Membre> listeMembres) {
        ListeMembres = listeMembres;
    }
}
