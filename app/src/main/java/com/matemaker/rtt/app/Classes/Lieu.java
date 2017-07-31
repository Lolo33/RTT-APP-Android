package com.matemaker.rtt.app.Classes;

import java.io.Serializable;

/**
 * Created by Niquelesstup on 06/06/2017.
 */

public class Lieu implements Serializable {
    private int id;
    private String cp;
    private String ville;
    private String nom;
    private String adresseL1;
    private String adresseL2;
    private Departement departement;
    private String logo;
    private double latitude;
    private double longitude;
    private int compteEvents;
    private String mail;
    private String tel;
    private String descriptif;

    public Lieu(int id, String cp, String ville, String nom, String adresseL1, String adresseL2, String tel, String mail, String descriptif, Departement departement, String logo, double latitude, double longitude) {
        this.id = id;
        this.cp = cp;
        this.ville = ville;
        this.nom = nom;
        this.adresseL1 = adresseL1;
        this.adresseL2 = adresseL2;
        this.departement = departement;
        this.logo = logo;
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.tel = tel;
        this.mail = mail;
        this.descriptif = descriptif;
    }
    public Lieu(int id, String cp, String ville, String nom, String adresseL1, String adresseL2, Departement departement, String logo, double latitude, double longitude) {
        this.id = id;
        this.cp = cp;
        this.ville = ville;
        this.nom = nom;
        this.adresseL1 = adresseL1;
        this.adresseL2 = adresseL2;
        this.departement = departement;
        this.logo = logo;
        this.setLatitude(latitude);
        this.setLongitude(longitude);
    }
    public Lieu() {
    }

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

    public int getCompteEvents() {
        return compteEvents;
    }
    public void setCompteEvents(int compteEvents) {
        this.compteEvents = compteEvents;
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getTel() {
        return tel;
    }
    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getDescriptif() {
        return descriptif;
    }
    public void setDescriptif(String descriptif) {
        this.descriptif = descriptif;
    }

    public static Lieu getLieuByNom(String nom){
        for (int i=0; i<Globals.getListeDpt().size();i++){
            for (int j=0;j<Globals.getListeDpt().get(i).getListeLieux().size(); j++){
                Lieu unLieu = Globals.getListeDpt().get(i).getListeLieux().get(j);
                if (unLieu.getNom().equals(nom))
                    return unLieu;
            }
        }
        return null;
    }
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return this.nom.toString();
    }
}
