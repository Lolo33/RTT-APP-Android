package com.example.niquelesstup.rtt.Classes;

/**
 * Created by Niquelesstup on 06/06/2017.
 */

public class Lieu {
    private int id;
    private int cp;
    private String ville;
    private String nom;
    private String adresseL1;
    private String adresseL2;
    private Departement departement;
    private String logo;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getCp() {
        return cp;
    }
    public void setCp(int cp) {
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
}
