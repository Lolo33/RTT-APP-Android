package com.example.niquelesstup.rtt.Classes;

import java.io.Serializable;

/**
 * Created by Niquelesstup on 06/06/2017.
 */

public class InfosMango implements Serializable {

    public InfosMango(){}
    public InfosMango(int id, String mangoUserId, String mangoWalletId) {
        this.id = id;
        this.mangoUserId = mangoUserId;
        this.mangoWalletId = mangoWalletId;
    }

    private int id;
    private String mangoUserId;
    private String mangoWalletId;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getMangoUserId() {
        return mangoUserId;
    }
    public void setMangoUserId(String mangoUserId) {
        this.mangoUserId = mangoUserId;
    }

    public String getMangoWalletId() {
        return mangoWalletId;
    }
    public void setMangoWalletId(String mangoWalletId) {
        this.mangoWalletId = mangoWalletId;
    }

}
