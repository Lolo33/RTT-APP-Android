package com.matemaker.rtt.app.Classes;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Niquelesstup on 07/06/2017.
 */

public class MessagePrive implements Serializable {

    private int $id;
    private Membre expediteur;
    private Membre destinataire;
    private Date date;
    private String message;
    private boolean isVu;

    public int get$id() {
        return $id;
    }
    public void set$id(int $id) {
        this.$id = $id;
    }

    public Membre getExpediteur() {
        return expediteur;
    }
    public void setExpediteur(Membre expediteur) {
        this.expediteur = expediteur;
    }

    public Membre getDestinataire() {
        return destinataire;
    }
    public void setDestinataire(Membre destinataire) {
        this.destinataire = destinataire;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isVu() {
        return isVu;
    }
    public void setVu(boolean vu) {
        isVu = vu;
    }

}
