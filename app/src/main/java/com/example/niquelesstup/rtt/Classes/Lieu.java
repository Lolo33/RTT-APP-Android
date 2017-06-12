package com.example.niquelesstup.rtt.Classes;

/**
 * Created by Niquelesstup on 06/06/2017.
 */

public class Lieu {
    private int id;
    private String cp;
    private String ville;
    private String nom;
    private String adresseL1;
    private String adresseL2;

    public Lieu(int id, String cp, String ville, String nom, String adresseL1, String adresseL2, Departement departement, String logo) {
        this.id = id;
        this.cp = cp;
        this.ville = ville;
        this.nom = nom;
        this.adresseL1 = adresseL1;
        this.adresseL2 = adresseL2;
        this.departement = departement;
        this.logo = logo;
    }
    public Lieu() {
    }

    private Departement departement;
    private String logo;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getCp() {
        return cp;
    }
    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getVille() {
        return ville;
    }
    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
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

    public Departement getDepartement() {
        return departement;
    }
    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public String getLogo() {
        return logo;
    }
    public void setLogo(String logo) {
        this.logo = logo;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return this.nom.toString();
    }

}
