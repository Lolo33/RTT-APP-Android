package com.example.niquelesstup.rtt.Classes;

import com.example.niquelesstup.rtt.Classes.Api.Token;

/**
 * Created by Niquelesstup on 12/06/2017.
 */

public class Globals {

    private static Membre membreConnecte = null;
    private static final String API_URL = "http://mate-maker.fr";
    private static Token tokenApi;

    public static Membre getMembreConnecte() {
        return membreConnecte;
    }
    public static void setMembreConnecte(Membre membreConnecte) {
        Globals.membreConnecte = membreConnecte;
    }

    public static String getApiUrl() {
        return API_URL;
    }

    public static Token getTokenApi() {
        return tokenApi;
    }
    public static void setTokenApi(Token tokenApi) {
        Globals.tokenApi = tokenApi;
    }

}
