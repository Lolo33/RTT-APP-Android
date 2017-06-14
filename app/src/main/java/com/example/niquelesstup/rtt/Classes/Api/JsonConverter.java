package com.example.niquelesstup.rtt.Classes.Api;

import android.util.Log;

import com.example.niquelesstup.rtt.Classes.Avatar;
import com.example.niquelesstup.rtt.Classes.Compte;
import com.example.niquelesstup.rtt.Classes.Departement;
import com.example.niquelesstup.rtt.Classes.Equipe;
import com.example.niquelesstup.rtt.Classes.Evenement;
import com.example.niquelesstup.rtt.Classes.InfosMango;
import com.example.niquelesstup.rtt.Classes.Lieu;
import com.example.niquelesstup.rtt.Classes.Membre;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Niquelesstup on 12/06/2017.
 */

public class JsonConverter {

    // Conversion des DATE
    public static Date longJsonStringToDate(String date_a_format){
        String[] tab_date = date_a_format.split("T");
        String date = tab_date[0];
        String time = tab_date[1].split("\\+")[0];
        String[] date_t = date.split("-");
        String[] time_t = time.split(":");
        //2017-05-27T00:00:00+01:00
        return new Date(Long.valueOf(date_t[0] + (date_t[1] + date_t[2] + time_t[0] +time_t[1] + time_t[2])));
    }

    // Conversion des MEMBRES
    public static Membre convertMembre(JSONObject objetJson){
        Membre leMembre;
        try {
            // Conversion de l'objet Avatar de l'utilisateur
            JSONObject avatarJson = objetJson.getJSONObject("membreAvatar");
            Avatar avatar = new Avatar(avatarJson.getInt("id"), avatarJson.getString("avatarUrl"), avatarJson.getInt("avatarStatut"));
            // Conversion de la liste d'objets InfosMango de l'utilisateur
            JSONArray imListeJson = objetJson.getJSONArray("membreMango");
            ArrayList<InfosMango> listeIm = convertListeIm(imListeJson);
            // Conversion de la liste des objets Compte de l'utilisateur
            JSONArray comptes = objetJson.getJSONArray("membreComptes");
            ArrayList<Compte> listeComptes = convertListeComptes(comptes);
            // Création de l'objet utilisateurs
            leMembre = new Membre(objetJson.getInt("id"), objetJson.getString("membrePseudo"), objetJson.getString("membreTel"),
                    objetJson.getString("membreMail"), objetJson.getBoolean("membreOrga"), longJsonStringToDate(objetJson.getString("membreDateInscription")),
                    longJsonStringToDate(objetJson.getString("membreDerniereConnexion")), objetJson.getString("membreIpInscription"),
                    objetJson.getString("membreIpDerniereConnexion"), objetJson.getString("membreCodeValidation"),
                    objetJson.getBoolean("membreValidation"), objetJson.getString("membreDptCode"), avatar, listeComptes, listeIm);
        }catch (JSONException ex){
            leMembre = null;
        }
        return leMembre;
    }

    // Conversion des DEPARTEMENTS
    public static ArrayList<Departement> convertListeDepartements(JSONArray arrayJson){
        ArrayList<Departement> listeDpt = new ArrayList<Departement>();
        try {
            for (int i=0; i < arrayJson.length(); i++) {
                JSONObject objetJson = arrayJson.getJSONObject(i);
                Departement unDpt = convertDepartement(objetJson);
                listeDpt.add(unDpt);
            }
        } catch (JSONException ex) {
            listeDpt = null;
        }
        return listeDpt;
    }
    public static Departement convertDepartement(JSONObject dptJson){
        Departement unDpt;
        try {
            unDpt = new Departement(dptJson.getInt("id"), dptJson.getString("dptCode"), dptJson.getString("dptNom"));
        }catch (JSONException ex){
            unDpt = null;
        }
        return unDpt;
    }

    // Conversion des LIEUX
    public static ArrayList<Lieu> convertListeLieux(JSONArray arrayJson){
        //Lieu leLieu = new Lieu();
        ArrayList<Lieu> listeLieux = new ArrayList<Lieu>();
        try {
            for (int i=0; i < arrayJson.length(); i++) {
                JSONObject objetJson = arrayJson.getJSONObject(i);
                Lieu unLieu = convertLieu(objetJson, true);
                listeLieux.add(unLieu);
            }
        } catch (JSONException ex) {
            listeLieux = null;
        }
        return listeLieux;
    }
    public static Lieu convertLieu(JSONObject objetJson, boolean getCompteEvents){
        Lieu unLieu;
        try {
            JSONObject dptJson = objetJson.getJSONObject("lieuDpt");
            Departement dpt = convertDepartement(objetJson);
            unLieu = new Lieu(objetJson.getInt("id"), objetJson.getString("lieuCp"), objetJson.getString("lieuVille"),
                    objetJson.getString("lieuNom"), objetJson.getString("lieuAdresseL1"), objetJson.getString("lieuAdresseL2"),
                    dpt, objetJson.getString("lieuLogo"));
            if (getCompteEvents) {
                JSONArray listeEventJson = objetJson.getJSONArray("listeEvents");
                unLieu.setCompteEvents(listeEventJson.length());
            }
        }catch (JSONException ex){
            unLieu = null;
        }
        return unLieu;
    }

    // Conversion des EVENEMENTS
    public static ArrayList<Evenement> convertListeEvenements(JSONArray arrayJson){
        ArrayList<Evenement> listeEvents = new ArrayList<Evenement>();
        try {
            for (int i=0; i<arrayJson.length(); i++){
                JSONObject objetJson = arrayJson.getJSONObject(i);
                Evenement unEvenement = convertEvenement(objetJson);
                listeEvents.add(unEvenement);
            }
        }catch (JSONException ex){
            //listeEvents = null;
        }
        return listeEvents;
    }

    public static Evenement convertEvenement(JSONObject eventJson){
        Evenement unEvenement;
        try {
            JSONArray listeEquipesJson = eventJson.getJSONArray("eventListeEquipes");
            ArrayList<Equipe> listeEquipes = convertListeEquipes(listeEquipesJson);
            Lieu leLieu = convertLieu(eventJson.getJSONObject("eventLieu"), false);
            Membre orga1 = convertMembre(eventJson.getJSONObject("eventOrga"));
            Membre orga2 = new Membre();
            Compte leCompte = convertCompte(eventJson.getJSONObject("eventCompte"));
            InfosMango im = convertIm(eventJson.getJSONObject("eventMango"));
            //float tarif = 12.2F; /*eventJson.getInt("eventTarif");*/
            unEvenement = new Evenement(eventJson.getInt("id"), eventJson.getString("eventTitre"), eventJson.getInt("eventNbEquipes"),
                    eventJson.getInt("eventJoueursMax"), eventJson.getInt("eventJoueursMin"), 12, leLieu, new Date(2016,10,10), new Time(10,0,0), new Time(10,0,0)
                    /*new Time(longJsonStringToDate("eventHeureDebut").getTime()), new Time(longJsonStringToDate("eventHeureFin").getTime())*/, eventJson.getBoolean("eventPrive"),
                    eventJson.getString("eventPass"), eventJson.getBoolean("eventPaiement"), leCompte, im, eventJson.getBoolean("eventTarificationEquipe"),
                    orga1, orga2, eventJson.getString("eventImg"), eventJson.getString("eventDescriptif"), eventJson.getBoolean("eventTournoi"), listeEquipes);
        }catch (JSONException ex){
            unEvenement = new Evenement();
        }
        return unEvenement;
    }

    // Conversion des EQUIPES
    public static ArrayList<Equipe> convertListeEquipes(JSONArray listeEquipesJson){
        ArrayList<Equipe> listeEquipes = new ArrayList<Equipe>();
        try {
            for (int i=0; i<listeEquipesJson.length(); i++){
                ArrayList<Membre> listeMembres = new ArrayList<Membre>();
                JSONObject objetEquipeJson = listeEquipesJson.getJSONObject(i);
                JSONArray arrayMembresJson = objetEquipeJson.getJSONArray("teamListeMembres");
                for (int j=0; j<arrayMembresJson.length(); j++){
                    JSONObject objetMembreJson = arrayMembresJson.getJSONObject(j);
                    Membre unMembre = convertMembre(objetMembreJson);
                    listeMembres.add(unMembre);
                }
                Equipe uneEquipe = new Equipe(objetEquipeJson.getInt("id"), objetEquipeJson.getString("teamNom"), objetEquipeJson.getString("teamCode"),
                        objetEquipeJson.getBoolean("teamPrive"), objetEquipeJson.getString("teamPass"), listeMembres);
                listeEquipes.add(uneEquipe);
            }
        }catch (JSONException ex){
            //listeEquipes = null;
        }
        return listeEquipes;
    }

    // Conversion des COMPTES
    public static ArrayList<Compte> convertListeComptes(JSONArray comptes){
        ArrayList<Compte> listeComptes = new ArrayList<Compte>();
        try {
            for (int i = 0; i < comptes.length(); i++) {
                JSONObject compteJson = comptes.getJSONObject(i);
                Compte unCompte = convertCompte(compteJson);
                listeComptes.add(unCompte);
            }
        }catch (JSONException ex){
            listeComptes = null;
        }
        return listeComptes;
    }
    public static Compte convertCompte(JSONObject compteJson){
        Compte unCompte;
        try {
            unCompte = new Compte(compteJson.getInt("id"), compteJson.getString("compteRibBic"), compteJson.getString("compteRibIban"),
                    compteJson.getString("compteNom"), compteJson.getString("comptePrenom"), compteJson.getString("compteAdresseL1"),
                    compteJson.getString("compteAdresseL2"), compteJson.getString("compteCp"), compteJson.getString("compteVille"));
        }catch (JSONException ex){
            unCompte = null;
        }
        return unCompte;
    }

    // Conversion des jeux d' INFOSMANGO
    public static ArrayList<InfosMango> convertListeIm(JSONArray imListeJson){
        ArrayList<InfosMango> listeIm = new ArrayList<InfosMango>();
        try {
            for (int i = 0; i < imListeJson.length(); i++) {
                JSONObject imJson = imListeJson.getJSONObject(i);
                InfosMango im = convertIm(imJson);
                listeIm.add(im);
            }
        }catch (JSONException ex){
            listeIm = null;
        }
        return listeIm;
    }
    public static InfosMango convertIm(JSONObject imJson){
        InfosMango im;
        try {
            im = new InfosMango(imJson.getInt("id"), imJson.getString("imMangoId"), imJson.getString("imWalletId"));
        }catch (JSONException ex){
            im = null;
        }
        return im;
    }



}
