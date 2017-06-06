package com.example.niquelesstup.rtt.Classes;

/**
 * Created by Niquelesstup on 06/06/2017.
 */

public class InfosMango {
    private int id;
    private int mangoUserId;
    private Membres membre;
    private int mangoWalletId;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getMangoUserId() {
        return mangoUserId;
    }
    public void setMangoUserId(int mangoUserId) {
        this.mangoUserId = mangoUserId;
    }

    public Membres getMembre() {
        return membre;
    }
    public void setMembre(Membres membre) {
        this.membre = membre;
    }

    public int getMangoWalletId() {
        return mangoWalletId;
    }
    public void setMangoWalletId(int mangoWalletId) {
        this.mangoWalletId = mangoWalletId;
    }
}
