package com.matemaker.rtt.app.Classes;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Niquelesstup on 03/07/2017.
 */

public class EvenementPrive implements Serializable {

    public EvenementPrive(int id, String titre, Membre organisateur, boolean accepte, Lieu lieu, float tarif, int nbJoueursMax, String descriptif, Reservation resa) {
        this.id = id;
        this.setTitre(titre);
        this.accepte = accepte;
        this.tarif = tarif;
        this.lieu = lieu;
        this.organisateur = organisateur;
        this.nbJoueursMax = nbJoueursMax;
        this.descriptif = descriptif;
        this.reservation = resa;
    }
    public EvenementPrive(int id, String titre, Membre organisateur, boolean accepte, int nbJoueursMax, String descriptif, Reservation resa) {
        this.id = id;
        this.organisateur = organisateur;
        this.accepte = accepte;
        this.nbJoueursMax = nbJoueursMax;
        this.descriptif = descriptif;
        this.reservation = resa;
    }
    public EvenementPrive() {
    }

    private int id;
    private String titre;
    private Lieu lieu;
    private boolean accepte;
    private float tarif;
    private int nbJoueursMax;
    private String descriptif;
    private Membre organisateur;
    private Reservation reservation;
    private ArrayList<CreneauxJoueurs> listeCreneaux = new ArrayList<CreneauxJoueurs>();

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public Lieu getLieu() {
        return lieu;
    }
    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    public boolean isAccepte() {
        return accepte;
    }
    public void setAccepte(boolean accepte) {
        this.accepte = accepte;
    }

    public float getTarif() {
        return tarif;
    }
    public void setTarif(float tarif) {
        this.tarif = tarif;
    }

    public int getNbJoueursMax() {
        return nbJoueursMax;
    }
    public void setNbJoueursMax(int nbJoueursMax) {
        this.nbJoueursMax = nbJoueursMax;
    }

    public String getDescriptif() {
        return descriptif;
    }
    public void setDescriptif(String descriptif) {
        this.descriptif = descriptif;
    }

    public String getTitre() {
        return titre;
    }
    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Reservation getReservation() {
        return reservation;
    }
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public ArrayList<CreneauxJoueurs> getListeCreneaux() {
        return listeCreneaux;
    }
    public void setListeCreneaux(ArrayList<CreneauxJoueurs> listeCreneaux) {
        this.listeCreneaux = listeCreneaux;
    }

    public Membre getOrganisateur() {
        return organisateur;
    }
    public void setOrganisateur(Membre organisateur) {
        this.organisateur = organisateur;
    }

    /// METHODES UTILES
    public ArrayList<Membre> getMembresCreneau(CreneauxJoueurs leCreneau){
        ArrayList<Membre> lesMembres = new ArrayList<>();
        for (int i=0; i<this.listeCreneaux.size(); i++){
            if (this.listeCreneaux.get(i).getDateDebut().toString().equals(leCreneau.getDateDebut().toString()) &&
                    this.listeCreneaux.get(i).getDateFin().toString().equals(leCreneau.getDateFin().toString()) &&
                    !this.listeCreneaux.get(i).isInvite()) {
                lesMembres.add(listeCreneaux.get(i).getMembre());
            }
        }
        return lesMembres;
    }

    public ArrayList<Membre> getListeInvites() {
        ArrayList<Membre> listeInvites = new ArrayList<>();
        for(int i=0;i<this.listeCreneaux.size();i++){
            if (listeCreneaux.get(i).isInvite() && !listeInvites.contains(listeCreneaux.get(i).getMembre()))
                listeInvites.add(listeCreneaux.get(i).getMembre());
        }
        return listeInvites;
    }

    public boolean isParticipant(Membre leMembre){
        if (listeCreneaux.size() > 0)
            for (int i = 0; i < this.listeCreneaux.size(); i++) {
                Log.e("cren:", ":" + listeCreneaux.get(i).isInvite());
                if (!listeCreneaux.get(i).isInvite())
                    if (listeCreneaux.get(i).getMembre().getId() == leMembre.getId())
                        return true;
            }
        return false;
    }

    public ArrayList<Membre> getParticipants(){
        ArrayList<Membre> listeMembres = new ArrayList<>();
        ArrayList<Integer> listeId = new ArrayList<>();
        listeMembres.add(this.getOrganisateur());
        listeId.add(this.getOrganisateur().getId());
        if (listeCreneaux.size() > 0) {
            for (int i = 0; i < this.listeCreneaux.size(); i++) {
                if (!listeId.contains(listeCreneaux.get(i).getMembre().getId()) && !listeCreneaux.get(i).isInvite()) {
                    listeMembres.add(this.listeCreneaux.get(i).getMembre());
                    listeId.add(this.listeCreneaux.get(i).getMembre().getId());
                }
            }
        }
        return listeMembres;
    }

    public ArrayList<CreneauxJoueurs> getListeCreneauxMembre(Membre leMembre){
        ArrayList<CreneauxJoueurs> listeCreneauxMembre = new ArrayList<>();
        if (this.listeCreneaux.size() > 0) {
            for (int i = 0; i < this.listeCreneaux.size(); i++) {
                if (leMembre.getId() == this.listeCreneaux.get(i).getMembre().getId() && !this.listeCreneaux.get(i).isInvite())
                    listeCreneauxMembre.add(listeCreneaux.get(i));
            }
        }
        return listeCreneauxMembre;
    }

    public ArrayList<CreneauxJoueurs> getCreneauxEquivalents(CreneauxJoueurs leCreneau){
        ArrayList<CreneauxJoueurs> lesCreneaux = new ArrayList<>();
        if (listeCreneaux.size() > 0 && leCreneau != null) {
            for (int i = 0; i < this.listeCreneaux.size(); i++) {

                if (this.listeCreneaux.get(i).getDateDebut().toString().equals(leCreneau.getDateDebut().toString()) &&
                        this.listeCreneaux.get(i).getDateFin().toString().equals(leCreneau.getDateFin().toString()) &&
                        !this.listeCreneaux.get(i).isInvite()) {
                    lesCreneaux.add(listeCreneaux.get(i));
                }
            }
        }
        return lesCreneaux;
    }

    public CreneauxJoueurs getCreneauEvent(){
        for (int i=0; i<listeCreneaux.size(); i++){
            if (listeCreneaux.get(i).isAccepte())
                return listeCreneaux.get(i);
        }
        return null;
    }

}
