package com.example.niquelesstup.rtt.Classes;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Niquelesstup on 07/06/2017.
 */

public class MessageMur implements Serializable{

    public MessageMur(int id, String date, String contenu, Membre membre) {
        this.id = id;
        this.date = date;
        this.contenu = contenu;
        this.membre = membre;
    }
    public MessageMur() {
    }

    private int id;
    private String date;
    private String contenu;
    private Membre membre;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getContenu() {
        return contenu;
    }
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Membre getMembre() {
        return membre;
    }
    public void setMembre(Membre membre) {
        this.membre = membre;
    }
}
