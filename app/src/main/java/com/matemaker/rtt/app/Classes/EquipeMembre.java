package com.matemaker.rtt.app.Classes;

import java.io.Serializable;

/**
 * Created by Niquelesstup on 07/06/2017.
 */

public class EquipeMembre implements Serializable {

    private int id;
    private Equipe equipe;
    private Membre membre;
    private StatutJoueur statutJoueur;
    private boolean pay;
    private String payId;

    public int getId() {
        return id;
    }
    public void setId(int $id) {
        this.id = $id;
    }

    public Equipe getEquipe() {
        return equipe;
    }
    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    public Membre getMembre() {
        return membre;
    }
    public void setMembre(Membre membre) {
        this.membre = membre;
    }

    public StatutJoueur getStatutJoueur() {
        return statutJoueur;
    }
    public void setStatutJoueur(StatutJoueur statutJoueur) {
        this.statutJoueur = statutJoueur;
    }

    public boolean isPay() {
        return pay;
    }
    public void setPay(boolean pay) {
        this.pay = pay;
    }

    public String getPayId() {
        return payId;
    }
    public void setPayId(String payId) {
        this.payId = payId;
    }
}
