package com.matemaker.rtt.app.Classes;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Niquelesstup on 29/06/2017.
 */

public class Creneaux implements Serializable {

    public Creneaux() {
    }
    public Creneaux(int id, CreneauStatut statut, Date date, Date dateFin) {
        this.id = id;
        this.statut = statut;
        this.date = date;
        this.dateFin = dateFin;
    }
    public Creneaux(int id, float tar, CreneauStatut statut, Date date, Date dateFin) {
        this.id = id;
        this.setTarif(tar);
        this.statut = statut;
        this.date = date;
        this.dateFin = dateFin;
    }

    private int id;
    private CreneauStatut statut;
    private float tarif;
    private Date date;
    private Date dateFin;
    private Evenement evenement;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CreneauStatut getStatut() {
        return statut;
    }

    public void setStatut(CreneauStatut statut) {
        this.statut = statut;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public Evenement getEvenement() {
        return evenement;
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
    }

    public float getTarif() {
        return tarif;
    }

    public void setTarif(float tarif) {
        this.tarif = tarif;
    }
}
