package com.matemaker.rtt.app.Classes;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Niquelesstup on 07/06/2017.
 */

public class MessageEquipe implements Serializable {

    private int $id;
    private Date date;
    private String contenu;
    private Membre membre;
    private Equipe equipe;

    public int get$id() {
        return $id;
    }
    public void set$id(int $id) {
        this.$id = $id;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
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

    public Equipe getEquipe() {
        return equipe;
    }
    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

}
