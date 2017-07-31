package com.matemaker.rtt.app.Classes;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Niquelesstup on 29/06/2017.
 */

public class Terrains implements Serializable {

    private int id;
    private String nom;
    private TerrainFormat format;
    private Evenement evenement;
    private Lieu lieu;
    private ArrayList<Creneaux> listeCreneaux;

    public Terrains(int id, String nom, TerrainFormat format) {
        this.id = id;
        this.nom = nom;
        this.format = format;
    }
    public Terrains() {
    }

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

    public TerrainFormat getFormat() {
        return format;
    }
    public void setFormat(TerrainFormat format) {
        this.format = format;
    }

    public Evenement getEvenement() {
        return evenement;
    }
    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
    }

    public Lieu getLieu() {
        return lieu;
    }
    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    public ArrayList<Creneaux> getListeCreneaux() {
        return listeCreneaux;
    }
    public void setListeCreneaux(ArrayList<Creneaux> listeCreneaux) {
        this.listeCreneaux = listeCreneaux;
    }
}
