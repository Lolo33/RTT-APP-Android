package com.matemaker.rtt.app.Classes;

import java.io.Serializable;

/**
 * Created by Niquelesstup on 20/07/2017.
 */

public class Reservation implements Serializable {

    private int id;
    private String numero;
    private EvenementPrive event;
    private boolean confirme;

    public Reservation(int id, String numero, EvenementPrive event, boolean confirme) {
        this.id = id;
        this.numero = numero;
        this.event = event;
        this.confirme = confirme;
    }
    public Reservation() {
    }
    public Reservation(int id, String numero, boolean confirme) {
        this.id = id;
        this.numero = numero;
        this.confirme = confirme;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }
    public void setNumero(String numero) {
        this.numero = numero;
    }

    public EvenementPrive getEvent() {
        return event;
    }
    public void setEvent(EvenementPrive event) {
        this.event = event;
    }

    public boolean isConfirme() {
        return confirme;
    }
    public void setConfirme(boolean confirme) {
        this.confirme = confirme;
    }

}
