package com.matemaker.rtt.app.Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Niquelesstup on 06/06/2017.
 */

public class Membre implements Serializable {

    private int id;

    public Membre(int id, String pseudo, String tel, String mail, boolean isOrga, Date dateInscription, Date dateDerniereConnexion, String ipInscription, String ipDerniereConnexion, String codeValidation, boolean isValid, String departement, Avatar avatar, ArrayList<Compte> listeComptes, ArrayList<InfosMango> listeInfosMango, String idFacebook) {
        this.id = id;
        this.pseudo = pseudo;
        this.tel = tel;
        this.mail = mail;
        this.isOrga = isOrga;
        this.dateInscription = dateInscription;
        this.dateDerniereConnexion = dateDerniereConnexion;
        this.ipInscription = ipInscription;
        this.ipDerniereConnexion = ipDerniereConnexion;
        this.codeValidation = codeValidation;
        this.isValid = isValid;
        this.departement = departement;
        this.avatar = avatar;
        this.listeComptes = listeComptes;
        this.listeInfosMango = listeInfosMango;
        this.idFacebook = idFacebook;
    }
    public Membre () {

    }

    private String pseudo;
    private String tel;
    private String mail;
    private boolean isOrga;
    private Date dateInscription;
    private Date dateDerniereConnexion;
    private String ipInscription;
    private String ipDerniereConnexion;
    private String codeValidation;
    private boolean isValid;
    private String departement;
    private ArrayList<Compte> listeComptes;
    private Avatar avatar;
    private ArrayList<InfosMango> listeInfosMango;
    private String idFacebook;
    private ArrayList<EvenementPrive> listeEventsPrives;
    private ArrayList<Mates> listeMates;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getTel() {
        return tel;
    }
    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }

    public boolean isOrga() {
        return isOrga;
    }

    public void setOrga(boolean orga) {
        isOrga = orga;
    }

    public Date getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(Date dateInscription) {
        this.dateInscription = dateInscription;
    }

    public Date getDateDerniereConnexion() {
        return dateDerniereConnexion;
    }

    public void setDateDerniereConnexion(Date dateDerniereConnexion) {
        this.dateDerniereConnexion = dateDerniereConnexion;
    }

    public String getIpInscription() {
        return ipInscription;
    }

    public void setIpInscription(String ipInscription) {
        this.ipInscription = ipInscription;
    }

    public String getIpDerniereConnexion() {
        return ipDerniereConnexion;
    }

    public void setIpDerniereConnexion(String ipDerniereConnexion) {
        this.ipDerniereConnexion = ipDerniereConnexion;
    }

    public String getCodeValidation() {
        return codeValidation;
    }
    public void setCodeValidation(String codeValidation) {
        this.codeValidation = codeValidation;
    }

    public boolean isValid() {
        return isValid;
    }
    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getDepartement() {
        return departement;
    }
    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public Avatar getAvatar() {
        return avatar;
    }
    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public ArrayList<Compte> getListeComptes() {
        return listeComptes;
    }
    public void setListeComptes(ArrayList<Compte> listeComptes) {
        this.listeComptes = listeComptes;
    }

    public ArrayList<InfosMango> getListeInfosMango() {
        return listeInfosMango;
    }

    public void setListeInfosMango(ArrayList<InfosMango> listeInfosMango) {
        this.listeInfosMango = listeInfosMango;
    }

    public String getIdFacebook() {
        return idFacebook;
    }

    public void setIdFacebook(String idFacebook) {
        this.idFacebook = idFacebook;
    }

    public ArrayList<Mates> getListeMates() {
        return listeMates;
    }

    public void setListeMates(ArrayList<Mates> listeMates) {
        this.listeMates = listeMates;
    }

    public ArrayList<EvenementPrive> getListeEventsPrives() {
        return listeEventsPrives;
    }

    public void setListeEventsPrives(ArrayList<EvenementPrive> listeEventsPrives) {
        this.listeEventsPrives = listeEventsPrives;
    }
}
