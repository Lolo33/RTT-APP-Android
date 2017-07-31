package com.matemaker.rtt.app.Classes;

import java.io.Serializable;

/**
 * Created by Niquelesstup on 31/07/2017.
 */

public class StatJeu implements Serializable {

    public StatJeu() {
    }
    public StatJeu(int id, int nbMatchs, int nbButs, int nbMatchsGagnes, int nbMinutesJeu) {
        this.id = id;
        this.nbMatchs = nbMatchs;
        this.nbButs = nbButs;
        this.nbMatchsGagnes = nbMatchsGagnes;
        this.nbMinutesJeu = nbMinutesJeu;
    }

    private int id;
    private int nbMatchs;
    private int nbButs;
    private int nbMatchsGagnes;
    private int nbMinutesJeu;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getNbMatchs() {
        return nbMatchs;
    }
    public void setNbMatchs(int nbMatchs) {
        this.nbMatchs = nbMatchs;
    }

    public int getNbButs() {
        return nbButs;
    }
    public void setNbButs(int nbButs) {
        this.nbButs = nbButs;
    }

    public int getNbMatchsGagnes() {
        return nbMatchsGagnes;
    }
    public void setNbMatchsGagnes(int nbMatchsGagnes) {
        this.nbMatchsGagnes = nbMatchsGagnes;
    }

    public int getNbMinutesJeu() {
        return nbMinutesJeu;
    }
    public void setNbMinutesJeu(int nbMinutesJeu) {
        this.nbMinutesJeu = nbMinutesJeu;
    }
}
