package com.matemaker.rtt.app.Classes.Api;

/**
 * Created by Niquelesstup on 12/06/2017.
 */

public class Token {

    public Token(String value, String createdAt) {
        this.value = value;
        this.createdAt = createdAt;
    }

    public Token(){}

    private String value = "ZwbyKi5TGaXT5q9tflMO73iXxHyrE0XNuZJiRC61pmW49rCA3WeAfqx9NpilI2jx6iw=";
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
