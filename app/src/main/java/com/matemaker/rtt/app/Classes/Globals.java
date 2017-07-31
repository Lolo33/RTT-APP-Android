package com.matemaker.rtt.app.Classes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.matemaker.rtt.app.Classes.Api.Token;
import com.matemaker.rtt.app.MenuActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Niquelesstup on 12/06/2017.
 */

public class Globals {

    private static Membre membreConnecte = null;
    private static final String API_URL = "https://mate-maker.fr";
    private static final String RTT_URL = "https://reservetonterrain.fr";
    private static Token tokenApi = new Token();
    private static String dptSelect;
    private static ArrayList<Departement> listeDpt = null;
    private static Lieu lieuSelected = null;
    private static boolean membreFbConnect = false;
    private static ArrayList<Lieu> listeLieuxJoueur = new ArrayList<Lieu>();
    private static ArrayList<String> dateSelected = new ArrayList<String>();
    private static ArrayList<Integer> listeIdChk;

    public static final String COULEUR_TOURNOI = "#16A086";
    //public static final String COULEUR_MATCH = "#557780";
    public static final String COULEUR_MATCH = "#16A086";
    public static final String COULEUR_EVENT_PRIVE = "#16a085";

    public static Membre getMembreConnecte() {
        return membreConnecte;
    }
    public static void setMembreConnecte(Membre membreConnecte) {
        Globals.membreConnecte = membreConnecte;
    }

    public static String getApiUrl() {
        return API_URL;
    }
    public static String getRttUrl() {
        return RTT_URL;
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

    public static boolean isMembreFbConnect() {
        return membreFbConnect;
    }
    public static void setMembreFbConnect(boolean membreFbConnect) {
        Globals.membreFbConnect = membreFbConnect;
    }

    public static void setListeLieuxJoueur(ArrayList<Lieu> listeLieuxJoueur) {
        Globals.listeLieuxJoueur = listeLieuxJoueur;
    }

    public static ArrayList<String> getDateSelected() {
        return dateSelected;
    }
    public static void setDateSelected(ArrayList<String> dateSelected) {
        Globals.dateSelected = dateSelected;
    }

    ///     ***** Opérations sur les Strings *****      ///
    public static String tarifToString(String tarifString){
        String[] tabTarif = tarifString.split("\\.");
        if (tabTarif.length > 1) {
            if (tabTarif[0].length() == 1) {
                tarifString = "0" + tarifString;
            }
            if (tabTarif[1].length() == 1) {
                tarifString = tarifString + "0";
            }
        }else {
            return tarifString;
        }
        return tarifString;
    }
    public static String replace(String originalText, String subStringToFind, String subStringToReplaceWith) {
        int s = 0;
        int e = 0;

        StringBuffer newText = new StringBuffer();

        while ((e = originalText.indexOf(subStringToFind, s)) >= 0) {

            newText.append(originalText.substring(s, e));
            newText.append(subStringToReplaceWith);
            s = e + subStringToFind.length();

        }

        newText.append(originalText.substring(s));
        return newText.toString();

    }
    public static String generate(int length) {
        String chars = "1234567890";
        StringBuffer pass = new StringBuffer();
        for(int x=0;x<length;x++)   {
            int i = (int)Math.floor(Math.random() * (chars.length() -1));
            pass.append(chars.charAt(i));
        }
        return pass.toString();
    }
    // Conversions dates en string
    public static String dateToString(Date date){
        String j = String.valueOf(date.getDate());
        String m = String.valueOf(date.getMonth() + 1);
        if (j.length() < 2) {
            j = "0" + j;
        }
        if (m.length() < 2){
            m = "0" + m;
        }
        return j + "-" + m + "-" + date.getYear();
    }
    public static String dateToStringFormatCourt(int dOw, int date){
        String strJourDate = String.valueOf(date);
        if (date < 10){
            strJourDate = "0" + strJourDate;
        }
        String jourCourt;
        switch (dOw){
            case Calendar.MONDAY:
                jourCourt = "Lun.";
                break;
            case Calendar.TUESDAY:
                jourCourt = "Mar.";
                break;
            case Calendar.WEDNESDAY:
                jourCourt = "Mer.";
                break;
            case Calendar.THURSDAY:
                jourCourt = "Jeu.";
                break;
            case Calendar.FRIDAY:
                jourCourt = "Ven.";
                break;
            case Calendar.SATURDAY:
                jourCourt = "Sam.";
                break;
            case Calendar.SUNDAY:
                jourCourt = "Dim.";
                break;
            default:
                jourCourt = "%j%";
        }
        return jourCourt + " " + strJourDate;
    }
    public static String timeToString(Date heure, boolean afficher_secondes){
        String h = String.valueOf(heure.getHours());
        String m = String.valueOf(heure.getMinutes());
        String s = String.valueOf(heure.getSeconds());
        if (h.length() < 2) {
            h = "0" + h;
        }
        if (m.length() < 2){
            m = "0" + m;
        }
        if (s.length() < 2){
            s = "0" + s;
        }
        String hToString = h + "h" + m;
        if (afficher_secondes)
            hToString += "m" + s;
        return hToString;
    }
    public static String timeToStringFormatBdd(Date heure){
        String h = String.valueOf(heure.getHours());
        String m = String.valueOf(heure.getMinutes());
        String s = String.valueOf(heure.getSeconds());
        if (h.length() < 2) {
            h = "0" + h;
        }
        if (m.length() < 2){
            m = "0" + m;
        }
        if (s.length() < 2){
            s = "0" + s;
        }
        return h + ":" + m + ":" + s;
    }
    public static String jourToString(int dOw){
        String jour;
        switch (dOw){
            case Calendar.MONDAY:
                jour = "Lundi";
                break;
            case Calendar.TUESDAY:
                jour = "Mardi";
                break;
            case Calendar.WEDNESDAY:
                jour = "Mercredi";
                break;
            case Calendar.THURSDAY:
                jour = "Jeudi";
                break;
            case Calendar.FRIDAY:
                jour = "Vendredi";
                break;
            case Calendar.SATURDAY:
                jour = "Samedi";
                break;
            case Calendar.SUNDAY:
                jour = "Dimanche";
                break;
            default:
                jour = "%jour%";
        }
        return jour;
    }
    public static String stringCalendar(int j, int m, int annee, String separateur){
        m += 1;
        String jour = String.valueOf(j);
        String mois = String.valueOf(m);
        if (jour.length() < 2) {
            jour = "0" + j;
        }
        if (mois.length() < 2){
            mois = "0" + m;
        }
        return jour + separateur + mois + separateur + annee;
    }

    ///     ****** Diversses opérations *****     ///
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
    public static void redirectIfNotConnected(Activity activity){
        if (Globals.getMembreConnecte() == null){
            Intent IntentAccueil = new Intent(activity, MenuActivity.class);
            activity.startActivity(IntentAccueil);
        }
    }

}
