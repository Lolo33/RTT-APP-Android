package com.example.niquelesstup.rtt.Classes;

import java.io.Serializable;

/**
 * Created by Niquelesstup on 06/06/2017.
 */

public class Compte implements Serializable {

    public Compte(int id, String codeBic, String codeIban, String nom, String prenom, String adresseL1, String codePostal, String ville) {
        this.id = id;
        this.codeBic = codeBic;
        this.codeIban = codeIban;
        this.membre = membre;
        this.nom = nom;
        this.prenom = prenom;
        this.adresseL1 = adresseL1;
        this.codePostal = codePostal;
        this.ville = ville;
    }
    public Compte() {
    }
    public Compte(int id, String codeBic, String codeIban, String nom, String prenom, String adresseL1, String adresseL2, String codePostal, String ville) {
        this.id = id;
        this.codeBic = codeBic;
        this.codeIban = codeIban;
        this.membre = membre;
        this.nom = nom;
        this.prenom = prenom;
        this.adresseL1 = adresseL1;
        this.adresseL2 = adresseL2;
        this.codePostal = codePostal;
        this.ville = ville;
    }

    private int id;
    private String codeBic;
    private String codeIban;
    private Membre membre;
    private String nom;
    private String prenom;
    private String adresseL1;
    private String adresseL2;
    private String codePostal;
    private String ville;


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getCodeBic() {
        return codeBic;
    }
    public void setCodeBic(String codeBic) {
        this.codeBic = codeBic;
    }

    public String getCodeIban() {
        return codeIban;
    }
    public void setCodeIban(String codeIban) {
        this.codeIban = codeIban;
    }

    public Membre getMembre() {
        return membre;
    }
    public void setMembre(Membre membre) {
        this.membre = membre;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getAdresseL1() {
        return adresseL1;
    }
    public void setAdresseL1(String adresseL1) {
        this.adresseL1 = adresseL1;
    }

    public String getAdresseL2() {
        return adresseL2;
    }
    public void setAdresseL2(String adresseL2) {
        this.adresseL2 = adresseL2;
    }

    public String getCodePostal() {
        return codePostal;
    }
    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getVille() {
        return ville;
    }
    public void setVille(String ville) {
        this.ville = ville;
    }
}
