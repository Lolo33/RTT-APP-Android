package com.matemaker.rtt.app.Classes;

import java.io.Serializable;

/**
 * Created by Niquelesstup on 10/07/2017.
 */

public class Mates implements Serializable {

    public Mates(){}
    public Mates(int id, Membre mate, MatesStatut statut) {
        this.id = id;
        this.mate = mate;
        this.statut = statut;
    }

    private int id;
    private Membre membre;
    private Membre mate;
    private MatesStatut statut;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public Membre getMembre() {
        return membre;
    }
    public void setMembre(Membre membre) {
        this.membre = membre;
    }

    public Membre getMate() {
        return mate;
    }
    public void setMate(Membre mate) {
        this.mate = mate;
    }

    public MatesStatut getStatut() {
        return statut;
    }
    public void setStatut(MatesStatut statut) {
        this.statut = statut;
    }

}
