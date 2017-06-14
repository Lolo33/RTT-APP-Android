package com.example.niquelesstup.rtt.Classes;

import java.io.Serializable;

/**
 * Created by Niquelesstup on 07/06/2017.
 */

public class EquipeMembre implements Serializable {

    private int $id;
    private int membre_id;
    private Equipe equipe;
    private Membre membre;
    private StatutJoueur statutJoueur;

    public int get$id() {
        return $id;
    }
    public void set$id(int $id) {
        this.$id = $id;
    }

    public int getMembre_id() {
        return membre_id;
    }
    public void setMembre_id(int membre_id) {
        this.membre_id = membre_id;
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

}
