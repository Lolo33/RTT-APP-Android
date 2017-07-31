package com.matemaker.rtt.app.Classes.Api;

import android.util.Log;

import com.google.gson.JsonObject;
import com.matemaker.rtt.app.Classes.Avatar;
import com.matemaker.rtt.app.Classes.Compte;
import com.matemaker.rtt.app.Classes.CreneauStatut;
import com.matemaker.rtt.app.Classes.Creneaux;
import com.matemaker.rtt.app.Classes.CreneauxJoueurs;
import com.matemaker.rtt.app.Classes.Departement;
import com.matemaker.rtt.app.Classes.Equipe;
import com.matemaker.rtt.app.Classes.EquipeMembre;
import com.matemaker.rtt.app.Classes.Evenement;
import com.matemaker.rtt.app.Classes.EvenementPrive;
import com.matemaker.rtt.app.Classes.InfosMango;
import com.matemaker.rtt.app.Classes.Lieu;
import com.matemaker.rtt.app.Classes.Mates;
import com.matemaker.rtt.app.Classes.MatesStatut;
import com.matemaker.rtt.app.Classes.Membre;
import com.matemaker.rtt.app.Classes.MessageMur;
import com.matemaker.rtt.app.Classes.Reservation;
import com.matemaker.rtt.app.Classes.StatutJoueur;
import com.matemaker.rtt.app.Classes.TerrainFormat;
import com.matemaker.rtt.app.Classes.Terrains;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Niquelesstup on 12/06/2017.
 */

public class JsonConverter {

    // Conversion des DATE
    public static Date longJsonStringToDate(String date_a_format, boolean recup_time, boolean is_time){
        if (recup_time) {
            String[] dt_t = date_a_format.split(" ");
            String[] date_t = dt_t[0].split("-");
            String[] time_t = dt_t[1].split(":");
            //Log.e("date:", date_t[0] + "-" + date_t[1] + '-' + date_t[2]);
            return new Date(
                    Integer.valueOf(date_t[0]), Integer.valueOf(date_t[1]) - 1, Integer.valueOf(date_t[2]),
                    Integer.valueOf(time_t[0]), Integer.valueOf(time_t[1]), Integer.valueOf(time_t[2])
            );
        }else {
            String[] date_t = date_a_format.split("-");
            String[] time_t = date_a_format.split(":");
            if (is_time){
                //Log.e("Heure: ", "H: " + Integer.valueOf(time_t[0]) + " M: " + Integer.valueOf(time_t[1]) + " S: " + Integer.valueOf(time_t[2]));
                return new Date(0,0,0, Integer.valueOf(time_t[0]),
                        Integer.valueOf(time_t[1]),
                        Integer.valueOf(time_t[2]));
            }else{
                //Log.e("Date: ", "J: " + Integer.valueOf(date_t[2]) + " M: " + Integer.valueOf(date_t[1]) + " A: " + Integer.valueOf(date_t[0]));
                return new Date(
                        Integer.valueOf(date_t[0]),
                        Integer.valueOf(date_t[1]) - 1,
                        Integer.valueOf(date_t[2]),
                        0, 0, 0
                );
            }
        }
    }

    public static Avatar convertAvatar(JSONObject avatarJson){
        Avatar avatar;
        try {
            avatar = new Avatar(avatarJson.getInt("id"), avatarJson.getString("avatarUrl"), avatarJson.getInt("avatarStatut"));
        }catch (JSONException ex){
            avatar = new Avatar();
        }
        return avatar;
    }
    public static ArrayList<Avatar> convertListeAvatars(JSONArray arrayAvJson){
        ArrayList<Avatar> listeAvatars = new ArrayList<Avatar>();
        try {
            for (int i=0; i<arrayAvJson.length(); i++){
                JSONObject objAvJson = arrayAvJson.getJSONObject(i);
                Avatar unAvatar = convertAvatar(objAvJson);
                listeAvatars.add(unAvatar);
            }
        }catch (JSONException ex){
            Log.e("errParseAv:", ex.getMessage());
        }
        return listeAvatars;
    }

    // Conversion des MEMBRES
    public static Membre convertMembre(JSONObject objetJson, boolean create){
        Membre leMembre;
        try {
            // Conversion de l'objet Avatar de l'utilisateur
            JSONObject avatarJson = objetJson.getJSONObject("membreAvatar");
            Avatar avatar = convertAvatar(avatarJson);
            // Conversion de la liste d'objets InfosMango de l'utilisateur
            JSONArray imListeJson = objetJson.getJSONArray("membreMango");
            ArrayList<InfosMango> listeIm = convertListeIm(imListeJson);
            // Conversion de la liste des objets Compte de l'utilisateur
            JSONArray comptes = objetJson.getJSONArray("membreComptes");
            ArrayList<Compte> listeComptes = convertListeComptes(comptes);
            Date date_insc;
            Date date_last_conn;
            if (create){
                date_insc = new Date();
                date_last_conn = new Date();
            }else {
                date_insc = longJsonStringToDate(objetJson.getString("membreDateInscription"), true, false);
                date_last_conn = longJsonStringToDate(objetJson.getString("membreDerniereConnexion"), true, false);
            }
            // Création de l'objet utilisateurs
            leMembre = new Membre(objetJson.getInt("id"), objetJson.getString("membrePseudo"), objetJson.getString("membreTel"),
                    objetJson.getString("membreMail"), objetJson.getBoolean("membreOrga"), date_insc, date_last_conn,
                    objetJson.getString("membreIpInscription"), objetJson.getString("membreIpDerniereConnexion"),
                    objetJson.getString("membreCodeValidation"), objetJson.getBoolean("membreValidation"),
                    objetJson.getString("membreDptCode"), avatar, listeComptes, listeIm, objetJson.getString("membreFbId"));
            //Log.e("idFb: ", objetJson.getString("membreFbId"));
        }catch (JSONException ex){
            leMembre = new Membre();
            Log.e("membreBug:", ex.getMessage());
        }
        return leMembre;
    }

    public static MatesStatut convertMateStatut(JSONObject objStatut){
        MatesStatut statut;
        try {
            statut = new MatesStatut(objStatut.getInt("id"), objStatut.getString("statutNom"));
        }catch (JSONException ex){
            Log.e("errStatutMate", "err: " + ex.getMessage());
            statut = new MatesStatut();
        }
        return statut;
    }

    public static Mates convertMate(JSONObject objMate){
        Mates leMate;
        try {
            Membre leMembre = convertMembre(objMate.getJSONObject("mateAmi"), false);
            MatesStatut statut = convertMateStatut(objMate.getJSONObject("mateStatut"));
            leMate = new Mates(objMate.getInt("id"), leMembre, statut);
        }catch (JSONException ex){
            Log.e("errJsonMate", "err: " + ex.getMessage());
            leMate = new Mates();
        }
        return leMate;
    }
    public static ArrayList<Mates> convertListeMates(JSONArray arrayMates){
        ArrayList<Mates> listeMates = new ArrayList<>();
        try {
            for (int i = 0; i < arrayMates.length(); i++) {
                JSONObject objMate = arrayMates.getJSONObject(i);
                Mates unMate = convertMate(objMate);
                listeMates.add(unMate);
            }
        }catch (JSONException ex){
            Log.e("errParseMate", "err: " + ex.getMessage());
        }
        return listeMates;
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
            JSONArray arrayLieux = dptJson.getJSONArray("lieux");
            unDpt = new Departement(dptJson.getInt("id"), dptJson.getString("dptCode"), dptJson.getString("dptNom"));
            ArrayList<Lieu> listeLieux = convertListeLieux(arrayLieux);
            unDpt.setListeLieux(listeLieux);
        }catch (JSONException ex){
            unDpt = new Departement();
            Log.e("errDptOne:", ex.getMessage());
        }
        return unDpt;
    }

    public static TerrainFormat convertTerrainFormat(JSONObject objFormat){
        TerrainFormat leFormat;
        try {
            leFormat = new TerrainFormat(objFormat.getInt("id"), objFormat.getString("formatNom"));
        }catch (JSONException ex){
            Log.e("errJsonDormatTerr", "err: " + ex.getMessage());
            leFormat = new TerrainFormat();
        }
        return leFormat;
    }

    public static ArrayList<Terrains> convertListeTerrains(JSONArray arrayTerr){
        ArrayList<Terrains> listeTerrains = new ArrayList<Terrains>();
        try {
            for (int i=0; i<arrayTerr.length();i++){
                JSONObject objTerrain = arrayTerr.getJSONObject(i);
                Terrains unTerrain = convertTerrain(objTerrain);
                listeTerrains.add(unTerrain);
            }
        }catch (JSONException ex){
            Log.e("errJsonListeTerr", "err" + ex.getMessage());
        }
        return listeTerrains;
    }
    public static Terrains convertTerrain(JSONObject objTerr){
        Terrains leTerrain;
        try {
            JSONObject objFormat = objTerr.getJSONObject("terrainFormat");
            TerrainFormat format = convertTerrainFormat(objFormat);
            leTerrain = new Terrains(objTerr.getInt("id"), objTerr.getString("terrainNom"), format);
            JSONArray arrayCreneau = objTerr.getJSONArray("listeCreneaux");
            leTerrain.setListeCreneaux(convertListeCreneaux(arrayCreneau));
        }catch (JSONException ex){
            Log.e("errJsonTerr", "err: " + ex.getMessage());
            leTerrain = new Terrains();
        }
        return leTerrain;
    }

    public static ArrayList<Creneaux> convertListeCreneaux(JSONArray arrayCreneaux){
        ArrayList<Creneaux> listeCreneaux = new ArrayList<Creneaux>();
        try {
            for (int i=0; i<arrayCreneaux.length(); i++){
                Creneaux unCreneau = convertCreneau(arrayCreneaux.getJSONObject(i));
                listeCreneaux.add(unCreneau);
            }
        }catch (JSONException ex){
            Log.e("errJsonListeCreneau", "err:" + ex.getMessage());
        }
        return listeCreneaux;
    }
    public static Creneaux convertCreneau(JSONObject objCreneau){
        Creneaux unCreneau;
        try {
            float tarif;
            if (objCreneau.getString("creneauTarif") == null || objCreneau.getString("creneauTarif") == "null")
                tarif = 0;
            else
                tarif = objCreneau.getInt("creneauTarif");
            JSONObject statutJson = objCreneau.getJSONObject("creneauStatut");
             unCreneau = new Creneaux(objCreneau.getInt("id"), tarif, convertCreneauStatut(statutJson),
                    longJsonStringToDate(objCreneau.getString("creneauDatetime"), true, false),
                    longJsonStringToDate(objCreneau.getString("creneauDatetimeFin"), true, false));
        }catch (JSONException ex){
            Log.e("errJsonCreneau", "err: " + ex.getMessage());
            unCreneau = new Creneaux();
        }
        return  unCreneau;
    }

    public static CreneauStatut convertCreneauStatut(JSONObject objStatut){
        CreneauStatut statut;
        try {
            statut = new CreneauStatut(objStatut.getInt("id"), objStatut.getString("statutNom"));
        }catch (JSONException ex){
            statut = new CreneauStatut();
        }
        return statut;
    }

    // CONVERSION DES CRENEAUX DES JOUEURS
    public static ArrayList<CreneauxJoueurs> convertListeCreneauxJoueurs(JSONArray arrayCreneaux){
        ArrayList<CreneauxJoueurs> listeCreneaux = new ArrayList<CreneauxJoueurs>();
        try {
            for (int i=0;i<arrayCreneaux.length();i++){
                CreneauxJoueurs unCreneau = convertCreneauJoueur(arrayCreneaux.getJSONObject(i));
                listeCreneaux.add(unCreneau);
            }
        }catch (JSONException ex){
            Log.e("errListeCreneauxJ:", "err: " + ex.getMessage());
        }
        return listeCreneaux;
    }
    public static CreneauxJoueurs convertCreneauJoueur(JSONObject objCreneau){
        CreneauxJoueurs leCreneau;
        try {
            Membre leMembre = convertMembre(objCreneau.getJSONObject("creneauMembre"), false);
            Terrains leTerrain = convertTerrain(objCreneau.getJSONObject("creneauTerrain"));
            leCreneau = new CreneauxJoueurs(objCreneau.getInt("id"), objCreneau.getBoolean("creneauInvite"), leMembre,
                    longJsonStringToDate(objCreneau.getString("creneauDateDebut"), true, false),
                    longJsonStringToDate(objCreneau.getString("creneauDateFin"), true, false),
                    longJsonStringToDate(objCreneau.getString("creneauDateSoumission"), true, false),
                    objCreneau.getBoolean("creneauAccepte"), leTerrain, objCreneau.getBoolean("creneauPaye"),
                    objCreneau.getString("creneauPayId"), objCreneau.getString("creneauPreAuthId"));
            //Log.e("invite: ", "inv: " + leCreneau.isInvite() + " " + objCreneau.getBoolean("creneauInvite"));
        }catch (JSONException ex){
            Log.e("errJsonCreneauxJoueur", "err: " + ex.getMessage());
            leCreneau = new CreneauxJoueurs();
        }
        return leCreneau;
    }

    // CONVERSION DES RESERVATIONS
    public static Reservation convertReservation(JSONObject objetResa, boolean get_event){
        Reservation reservation;
        try {
            reservation = new Reservation(objetResa.getInt("id"), objetResa.getString("resNumero"), objetResa.getBoolean("resConfirme"));
            if (get_event)
                reservation.setEvent(convertEvenementPrive(objetResa.getJSONObject("resEvent"), true));
        }catch (JSONException ex){
            reservation = new Reservation();
            Log.e("errJsonResa:", "err: " + ex.getMessage());
        }
        return reservation;
    }

    // CONVERSION DES EVENEMENTS-PRIVES
    public static EvenementPrive convertEvenementPrive(JSONObject objEvent, boolean list_creneau){
        EvenementPrive lEvent;
        try {
            Membre leMembre = convertMembre(objEvent.getJSONObject("eventMembre"), false);
            Reservation resa;
            float tarif;
            if (objEvent.getString("eventTarif") == null || objEvent.getString("eventTarif") == "null")
                tarif = 0;
            else
                tarif = objEvent.getInt("eventTarif");
            if (objEvent.getString("eventReservation") == null || objEvent.getString("eventReservation") == "null" || objEvent.getString("eventReservation") == "[]")
                resa = null;
            else
                resa = convertReservation(objEvent.getJSONObject("eventReservation"), false);
            lEvent = new EvenementPrive(objEvent.getInt("id"), objEvent.getString("eventTitre"), leMembre, objEvent.getBoolean("eventAccepte"),
                    convertLieu(objEvent.getJSONObject("eventLieu"), false), tarif,
                    objEvent.getInt("eventJoueursMax"), objEvent.getString("eventDescriptif"),
                    resa);
            if (list_creneau){
                lEvent.setListeCreneaux(convertListeCreneauxJoueurs(objEvent.getJSONArray("eventListeCreneaux")));
            }
        }catch (JSONException ex){
            Log.e("errJsonEventPriv: ", "err: " + ex.getMessage());
            lEvent = new EvenementPrive();
        }
        return lEvent;
    }
    public static ArrayList<EvenementPrive> convertListeEvenementsPrives(JSONArray array){
        ArrayList<EvenementPrive> listeEvents = new ArrayList<>();
        try {
            for (int i=0; i<array.length(); i++){
                EvenementPrive unEvent = convertEvenementPrive(array.getJSONObject(i), true);
                listeEvents.add(unEvent);
            }
        }catch (JSONException ex){
            Log.e("errJsonListeEp:", "err: " + ex.getMessage());
        }
        return listeEvents;
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
            Departement dpt = convertDepartement(objetJson);
            unLieu = new Lieu(objetJson.getInt("id"), objetJson.getString("lieuCp"), objetJson.getString("lieuVille"),
                    objetJson.getString("lieuNom"), objetJson.getString("lieuAdresseL1"), objetJson.getString("lieuAdresseL2"),
                    dpt, objetJson.getString("lieuLogo"), objetJson.getDouble("lieuLatitude"), objetJson.getDouble("lieuLongitude"));
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
            Membre orga1 = convertMembre(eventJson.getJSONObject("eventOrga"), false);
            Membre orga2 = new Membre();
            Compte leCompte = convertCompte(eventJson.getJSONObject("eventCompte"));
            InfosMango im = convertIm(eventJson.getJSONObject("eventMango"));
            JSONArray arrayMsg = eventJson.getJSONArray("eventListeMsg");
            ArrayList<MessageMur> listeMsg = convertListeMsgMur(arrayMsg);
            unEvenement = new Evenement(eventJson.getInt("id"), eventJson.getString("eventTitre"), eventJson.getInt("eventNbEquipes"),
                    eventJson.getInt("eventJoueursMax"), eventJson.getInt("eventJoueursMin"), eventJson.getInt("eventTarif"), leLieu, longJsonStringToDate(eventJson.getString("eventDate"), false, false),
                    longJsonStringToDate(eventJson.getString("eventHeureDebut"), false, true), longJsonStringToDate(eventJson.getString("eventHeureFin"), false, true), eventJson.getBoolean("eventPrive"),
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
            unMsg = new MessageMur(msgJson.getInt("id"), longJsonStringToDate(msgJson.getString("murDate"), true, false), msgJson.getString("murContenu"),
                    convertMembre(msgJson.getJSONObject("murMembre"), false));
            //Log.e("MSGDATE" , unMsg.getDate().toString());
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
                Equipe uneEquipe = convertEquipe(objetEquipeJson);
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
    public static Equipe convertEquipe(JSONObject objetEquipeJson){
        Equipe uneEquipe;
        try {
            uneEquipe = new Equipe(objetEquipeJson.getInt("id"), objetEquipeJson.getString("teamNom"), objetEquipeJson.getString("teamCode"),
                    objetEquipeJson.getBoolean("teamPrive"), objetEquipeJson.getString("teamPass"));
        }catch (JSONException ex){
            uneEquipe = new Equipe();
            Log.e("errTeam", ex.getMessage());
        }
        return uneEquipe;
    }

    public static EquipeMembre convertMembreTeam(JSONObject objEmJson){
        EquipeMembre membreEquipe = new EquipeMembre();
        try {
            JSONObject objetMembreJson = objEmJson.getJSONObject("emMembre");
            Membre unMembre = convertMembre(objetMembreJson, false);
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
            if (imListeJson.length() > 0) {
                for (int i = 0; i < imListeJson.length(); i++) {
                    JSONObject imJson = imListeJson.getJSONObject(i);
                    InfosMango im = convertIm(imJson);
                    listeIm.add(im);
                }
            }
        }catch (JSONException ex){
            Log.e("errArrayMango:", ex.getMessage());
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
