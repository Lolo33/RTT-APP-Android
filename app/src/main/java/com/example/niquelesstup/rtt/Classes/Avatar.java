package com.example.niquelesstup.rtt.Classes;

/**
 * Created by Niquelesstup on 06/06/2017.
 */

public class Avatar {

    public Avatar(int id, String url, int statut) {
        this.id = id;
        this.url = url;
        this.statut = statut;
    }
    public Avatar() {
    }

    private int id;
    private String url;
    private int statut;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatut() {
        return statut;
    }
    public void setStatut(int statut) {
        this.statut = statut;
    }
}
