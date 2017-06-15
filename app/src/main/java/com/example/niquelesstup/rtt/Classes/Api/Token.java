package com.example.niquelesstup.rtt.Classes.Api;

import java.util.Date;

/**
 * Created by Niquelesstup on 12/06/2017.
 */

public class Token {

    public Token(String value, String createdAt) {
        this.value = value;
        this.createdAt = createdAt;
    }

    private String value;
    private String createdAt;

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
