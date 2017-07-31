package com.matemaker.rtt.app.Classes;

import java.io.Serializable;

/**
 * Created by Niquelesstup on 31/07/2017.
 */

public class Statistiques implements Serializable {

    public Statistiques() {
    }
    public Statistiques(int id, Niveaux niveau, TrancheAge age, int nbResa, int nbDesist, StatJeu statJeu, int noteFiabilite) {
        this.id = id;
        this.niveau = niveau;
        this.age = age;
        this.nbResa = nbResa;
        this.nbDesist = nbDesist;
        this.statJeu = statJeu;
        this.noteFiabilite = noteFiabilite;
    }

    private int id;
    private Niveaux niveau;
    private TrancheAge age;
    private int nbResa;
    private int nbDesist;
    private StatJeu statJeu;
    private int noteFiabilite;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public Niveaux getNiveau() {
        return niveau;
    }
    public void setNiveau(Niveaux niveau) {
        this.niveau = niveau;
    }

    public TrancheAge getAge() {
        return age;
    }
    public void setAge(TrancheAge age) {
        this.age = age;
    }

    public int getNbResa() {
        return nbResa;
    }
    public void setNbResa(int nbResa) {
        this.nbResa = nbResa;
    }

    public int getNbDesist() {
        return nbDesist;
    }
    public void setNbDesist(int nbDesist) {
        this.nbDesist = nbDesist;
    }

    public StatJeu getStatJeu() {
        return statJeu;
    }
    public void setStatJeu(StatJeu statJeu) {
        this.statJeu = statJeu;
    }

    public int getNoteFiabilite() {
        return noteFiabilite;
    }
    public void setNoteFiabilite(int noteFiabilite) {
        this.noteFiabilite = noteFiabilite;
    }

}
