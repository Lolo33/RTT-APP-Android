package com.example.niquelesstup.rtt.Classes;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Niquelesstup on 06/06/2017.
 */

public class Evenement implements Serializable {

    public Evenement(int id, String titre, int nbEquipes, int nbJoueursMax, int nbJoueursMin, float tarif, Lieu lieu, Date date, Time heureDebut, Time heureFin, boolean isPrive, String pass, boolean isPayable, Compte compte, InfosMango infosMango, boolean isTarifEquipe, Membre organisateur1, Membre organisateur2, String img, String descriptif, boolean isTournoi, ArrayList<Equipe> listeEquipes) {
        this.id = id;
        this.titre = titre;
        this.nbEquipes = nbEquipes;
        this.nbJoueursMax = nbJoueursMax;
        this.nbJoueursMin = nbJoueursMin;
        this.tarif = tarif;
        this.lieu = lieu;
        this.date = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.isPrive = isPrive;
        this.pass = pass;
        this.isPayable = isPayable;
        this.compte = compte;
        this.infosMango = infosMango;
        this.isTarifEquipe = isTarifEquipe;
        this.organisateur1 = organisateur1;
        this.organisateur2 = organisateur2;
        this.img = img;
        this.descriptif = descriptif;
        this.isTournoi = isTournoi;
        this.listeEquipes = listeEquipes;
    }
    public Evenement() {
    }

    private int id;
    private String titre;
    private int nbEquipes;
    private int nbJoueursMax;
    private int nbJoueursMin;
    private float tarif;
    private Lieu lieu;
    private Date date;
    private Time heureDebut;
    private Time heureFin;
    private boolean isPrive;
    private String pass;
    private boolean isPayable;
    private Compte compte;
    private InfosMango infosMango;
    private boolean isTarifEquipe;
    private Membre organisateur1;
    private Membre organisateur2;
    private String img;
    private String descriptif;
    private boolean isTournoi;
    private ArrayList<Equipe> listeEquipes;

    public String getNombreEquipesString() {
        return this.getListeEquipes().size() + " / " + this.getNbEquipes()  + " équipes";
    }

    public String getNombreJoueursString(){
        int jMax = this.getNbJoueursMax() * this.getNbEquipes();
        int jInscrits = 0;
        for (int i=0; i<this.getListeEquipes().size(); i++){
            int nbJoueurs = this.getListeEquipes().get(i).getListeMembres().size();
            jInscrits += nbJoueurs;
        }
        return jInscrits + " / " + jMax + " joueurs";
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }
    public void setTitre(String titre) {
        this.titre = titre;
    }

    public int getNbEquipes() {
        return nbEquipes;
    }
    public void setNbEquipes(int nbEquipes) {
        this.nbEquipes = nbEquipes;
    }

    public int getNbJoueursMax() {
        return nbJoueursMax;
    }
    public void setNbJoueursMax(int nbJoueursMax) {
        this.nbJoueursMax = nbJoueursMax;
    }

    public int getNbJoueursMin() {
        return nbJoueursMin;
    }
    public void setNbJoueursMin(int nbJoueursMin) {
        this.nbJoueursMin = nbJoueursMin;
    }

    public float getTarif() {
        return tarif;
    }
    public void setTarif(float tarif) {
        this.tarif = tarif;
    }

    public Lieu getLieu() {
        return lieu;
    }
    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public Time getHeureDebut() {
        return heureDebut;
    }
    public void setHeureDebut(Time heureDebut) {
        this.heureDebut = heureDebut;
    }

    public Time getHeureFin() {
        return heureFin;
    }
    public void setHeureFin(Time heureFin) {
        this.heureFin = heureFin;
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

    public boolean isPayable() {
        return isPayable;
    }
    public void setPayable(boolean payable) {
        isPayable = payable;
    }

    public Compte getCompte() {
        return compte;
    }
    public void setCompte(Compte compte) {
        this.compte = compte;
    }

    public InfosMango getInfosMango() {
        return infosMango;
    }
    public void setInfosMango(InfosMango infosMango) {
        this.infosMango = infosMango;
    }

    public boolean isTarifEquipe() {
        return isTarifEquipe;
    }
    public void setTarifEquipe(boolean tarifEquipe) {
        isTarifEquipe = tarifEquipe;
    }

    public Membre getOrganisateur1() {
        return organisateur1;
    }
    public void setOrganisateur1(Membre organisateur1) {
        this.organisateur1 = organisateur1;
    }

    public Membre getOrganisateur2() {
        return organisateur2;
    }
    public void setOrganisateur2(Membre organisateur2) {
        this.organisateur2 = organisateur2;
    }

    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;
    }

    public String getDescriptif() {
        return descriptif;
    }
    public void setDescriptif(String descriptif) {
        this.descriptif = descriptif;
    }

    public boolean isTournoi() {
        return isTournoi;
    }
    public void setTournoi(boolean tournoi) {
        isTournoi = tournoi;
    }

    public ArrayList<Equipe> getListeEquipes() {
        return listeEquipes;
    }
    public void setListeEquipes(ArrayList<Equipe> listeEquipes) {
        this.listeEquipes = listeEquipes;
    }
}
