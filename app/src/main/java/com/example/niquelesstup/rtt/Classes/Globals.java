package com.example.niquelesstup.rtt.Classes;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.niquelesstup.rtt.Classes.Api.Token;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Niquelesstup on 12/06/2017.
 */

public class Globals {

    private static Membre membreConnecte = null;
    private static final String API_URL = "http://mate-maker.fr";
    private static Token tokenApi;
    private static String dptSelect;
    private static ArrayList<Departement> listeDpt = null;
    private static Lieu lieuSelected = null;

    public static final String COULEUR_TOURNOI = "#2099cf";
    public static final String COULEUR_MATCH = "#4682B4";

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

    public static String getDptSelect() {
        return dptSelect;
    }
    public static void setDptSelect(String dptSelect) {
        Globals.dptSelect = dptSelect;
    }

    public static ArrayList<Departement> getListeDpt() {
        return listeDpt;
    }
    public static void setListeDpt(ArrayList<Departement> listeDpt) {
        Globals.listeDpt = listeDpt;
    }

    public static Lieu getLieuSelected() {
        return lieuSelected;
    }
    public static void setLieuSelected(Lieu lieuSelected) {
        Globals.lieuSelected = lieuSelected;
    }

    public static void setFont(Activity activity, TextView textView, String fontName) {
        if(fontName != null){
            try {
                Typeface typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/" + fontName);
                textView.setTypeface(typeface);
            } catch (Exception e) {
                Log.e("FONT", fontName + " not found", e);
            }
        }
    }

    public static void majImg(String url, ImageView img) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
            img.setImageBitmap(bitmap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
