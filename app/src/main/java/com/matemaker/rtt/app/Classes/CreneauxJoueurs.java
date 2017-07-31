package com.matemaker.rtt.app.Classes;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Niquelesstup on 04/07/2017.
 */

public class CreneauxJoueurs implements Serializable {

    private int id;
    private boolean invite;
    private Membre membre;
    private Evenement evenement;
    private Date dateDebut;
    private Date dateFin;
    private Date dateSoumission;
    private boolean accepte;
    private boolean payed;
    private String payId;
    private String preAuthId;
    private Terrains terrain;

    public CreneauxJoueurs(int id, boolean invite, Membre membre, Date dateDebut, Date dateFin, Date dateSoumission, boolean accepte, Terrains terrain, boolean payed, String payId, String preAuthId) {
        this.id = id;
        this.invite = invite;
        this.membre = membre;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.dateSoumission = dateSoumission;
        this.accepte = accepte;
        this.terrain = terrain;
        this.payed = payed;
        this.payId = payId;
        this.preAuthId = preAuthId;
    }
    public CreneauxJoueurs() {
    }

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

    public Evenement getEvenement() {
        return evenement;
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public Date getDateSoumission() {
        return dateSoumission;
    }

    public void setDateSoumission(Date dateSoumission) {
        this.dateSoumission = dateSoumission;
    }

    public boolean isAccepte() {
        return accepte;
    }

    public void setAccepte(boolean accepte) {
        this.accepte = accepte;
    }

    public Terrains getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrains terrain) {
        this.terrain = terrain;
    }

    public boolean isPayed() {
        return payed;
    }

    public void setPayed(boolean payed) {
        this.payed = payed;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getPreAuthId() {
        return preAuthId;
    }

    public void setPreAuthId(String preAuthId) {
        this.preAuthId = preAuthId;
    }

    public boolean isInvite() {
        return invite;
    }

    public void setInvite(boolean invite) {
        this.invite = invite;
    }
}
