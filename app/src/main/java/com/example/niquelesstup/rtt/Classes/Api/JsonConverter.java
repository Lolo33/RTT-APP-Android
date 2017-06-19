package com.example.niquelesstup.rtt.Classes.Api;

import android.util.Log;

import com.example.niquelesstup.rtt.Classes.Avatar;
import com.example.niquelesstup.rtt.Classes.Compte;
import com.example.niquelesstup.rtt.Classes.Departement;
import com.example.niquelesstup.rtt.Classes.Equipe;
import com.example.niquelesstup.rtt.Classes.EquipeMembre;
import com.example.niquelesstup.rtt.Classes.Evenement;
import com.example.niquelesstup.rtt.Classes.InfosMango;
import com.example.niquelesstup.rtt.Classes.Lieu;
import com.example.niquelesstup.rtt.Classes.Membre;
import com.example.niquelesstup.rtt.Classes.MessageMur;
import com.example.niquelesstup.rtt.Classes.StatutJoueur;

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
            leMembre = new Membre();
            Log.e("membreBug:", ex.getMessage());
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
            Log.e("errDpt:", ex.getMessage());
        }
        return listeDpt;
    }
    public static Departement convertDepartement(JSONObject dptJson){
        Departement unDpt;
        try {
            unDpt = new Departement(dptJson.getInt("id"), dptJson.getString("dptCode"), dptJson.getString("dptNom"));
        }catch (JSONException ex){
            unDpt = new Departement();
            Log.e("errDptOne:", ex.getMessage());
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
            Log.e("errLieu:", ex.getMessage());
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
            unLieu = new Lieu();
            Log.e("errLieu:", ex.getMessage());
        }
        return unLieu;
    }

    // Conversion des EVENEMENTS
    public static ArrayList<Evenement> convertListeEvenements(JSONArray arrayJson, boolean set_liste_membre){
        ArrayList<Evenement> listeEvents = new ArrayList<Evenement>();
        try {
            for (int i=0; i<arrayJson.length(); i++){
                JSONObject objetJson = arrayJson.getJSONObject(i);
                Evenement unEvenement = convertEvenement(objetJson, set_liste_membre);
                listeEvents.add(unEvenement);
            }
        }catch (JSONException ex){
            Log.e("errEvent:", ex.getMessage());
        }
        return listeEvents;
    }
    public static Evenement convertEvenement(JSONObject eventJson, boolean set_liste_equipe){
        Evenement unEvenement;
        try {
            Lieu leLieu = convertLieu(eventJson.getJSONObject("eventLieu"), false);
            Membre orga1 = convertMembre(eventJson.getJSONObject("eventOrga"));
            Membre orga2 = new Membre();
            Compte leCompte = convertCompte(eventJson.getJSONObject("eventCompte"));
            InfosMango im = convertIm(eventJson.getJSONObject("eventMango"));
            JSONArray arrayMsg = eventJson.getJSONArray("eventListeMsg");
            ArrayList<MessageMur> listeMsg = convertListeMsgMur(arrayMsg);
            //float tarif = 12.2F; /*eventJson.getInt("eventTarif");*/
            unEvenement = new Evenement(eventJson.getInt("id"), eventJson.getString("eventTitre"), eventJson.getInt("eventNbEquipes"),
                    eventJson.getInt("eventJoueursMax"), eventJson.getInt("eventJoueursMin"), 12, leLieu, longJsonStringToDate(eventJson.getString("eventDate")), new Time(10,0,0), new Time(10,0,0)
                    /*new Time(longJsonStringToDate("eventHeureDebut").getTime()), new Time(longJsonStringToDate("eventHeureFin").getTime())*/, eventJson.getBoolean("eventPrive"),
                    eventJson.getString("eventPass"), eventJson.getBoolean("eventPaiement"), leCompte, im, eventJson.getBoolean("eventTarificationEquipe"),
                    orga1, orga2, eventJson.getString("eventImg"), eventJson.getString("eventDescriptif"), eventJson.getBoolean("eventTournoi"), listeMsg);
            if (set_liste_equipe){
                JSONArray listeEquipesJson = eventJson.getJSONArray("eventListeEquipes");
                ArrayList<Equipe> listeEquipes = convertListeEquipes(listeEquipesJson, true);
                unEvenement.setListeEquipes(listeEquipes);
                unEvenement.initialiserListeMembres();
            }
        }catch (JSONException ex){
            unEvenement = new Evenement();
            Log.e("errEvent:", ex.getMessage());
        }
        return unEvenement;
    }

    // Conversion messages du mur
    public static MessageMur convertMsgMur(JSONObject msgJson){
        MessageMur unMsg;
        try {
            unMsg = new MessageMur(msgJson.getInt("id"), msgJson.getString("murDate"), msgJson.getString("murContenu"),
                    convertMembre(msgJson.getJSONObject("murMembre")));
        }catch (JSONException ex){
            unMsg = null;
            Log.e("errMsg", ex.getMessage());
        }
        return unMsg;
    }
    public static ArrayList<MessageMur> convertListeMsgMur(JSONArray arrayMsg){
        ArrayList<MessageMur> listeMsg = new ArrayList<MessageMur>();
        try {
            for (int i = 0; i < arrayMsg.length(); i++) {
                JSONObject msgJson = arrayMsg.getJSONObject(i);
                MessageMur unMsg = convertMsgMur(msgJson);
                listeMsg.add(unMsg);
            }
        }catch (JSONException ex){
            listeMsg = null;
            Log.e("errMsg", ex.getMessage());
        }
        return listeMsg;
    }

    // Conversion des EQUIPES
    public static ArrayList<Equipe> convertListeEquipes(JSONArray listeEquipesJson, boolean set_liste_membre){
        ArrayList<Equipe> listeEquipes = new ArrayList<Equipe>();
        try {
            for (int i=0; i<listeEquipesJson.length(); i++){
                JSONObject objetEquipeJson = listeEquipesJson.getJSONObject(i);
                Equipe uneEquipe = new Equipe(objetEquipeJson.getInt("id"), objetEquipeJson.getString("teamNom"), objetEquipeJson.getString("teamCode"),
                        objetEquipeJson.getBoolean("teamPrive"), objetEquipeJson.getString("teamPass"));
                if (set_liste_membre){
                    ArrayList<EquipeMembre> listeMembresEquipe = new ArrayList<EquipeMembre>();
                    JSONArray arrayMembresJson = objetEquipeJson.getJSONArray("teamListeMembres");
                    for (int j=0; j<arrayMembresJson.length(); j++){
                        JSONObject objEmJson = arrayMembresJson.getJSONObject(j);
                        EquipeMembre membreEquipe = convertMembreTeam(objEmJson);
                        listeMembresEquipe.add(membreEquipe);
                    }
                    uneEquipe.setListeMembres(listeMembresEquipe);
                }
                listeEquipes.add(uneEquipe);
            }
        }catch (JSONException ex){
            //listeEquipes = null;
            Log.e("errTeam:", ex.getMessage());
        }
        return listeEquipes;
    }

    public static EquipeMembre convertMembreTeam(JSONObject objEmJson){
        EquipeMembre membreEquipe = new EquipeMembre();
        try {
            JSONObject objetMembreJson = objEmJson.getJSONObject("emMembre");
            Membre unMembre = convertMembre(objetMembreJson);
            membreEquipe.setId(objEmJson.getInt("id"));
            membreEquipe.setMembre(unMembre);
            membreEquipe.setPayId(objEmJson.getString("emPayId"));
            JSONObject jsonStatut = objEmJson.getJSONObject("emStatutJoueur");
            membreEquipe.setStatutJoueur(new StatutJoueur(jsonStatut.getInt("id"), jsonStatut.getString("statutNom")));
            membreEquipe.setPay(objEmJson.getBoolean("emMembrePaye"));
        }catch (JSONException ex){
            Log.e("errEm:", ex.getMessage());
        }
        return membreEquipe;
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
            Log.e("errCompte:", ex.getMessage());
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
            unCompte = new Compte();
            Log.e("errCompte:", ex.getMessage());
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
            Log.e("errMango:", ex.getMessage());
        }
        return listeIm;
    }
    public static InfosMango convertIm(JSONObject imJson){
        InfosMango im;
        try {
            im = new InfosMango(imJson.getInt("id"), imJson.getString("imMangoId"), imJson.getString("imWalletId"));
        }catch (JSONException ex){
            im = new InfosMango();
            Log.e("errMango:", ex.getMessage());
        }
        return im;
    }



}
