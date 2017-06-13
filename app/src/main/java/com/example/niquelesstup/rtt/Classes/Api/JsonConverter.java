package com.example.niquelesstup.rtt.Classes.Api;

import android.util.Log;

import com.example.niquelesstup.rtt.Classes.Avatar;
import com.example.niquelesstup.rtt.Classes.Compte;
import com.example.niquelesstup.rtt.Classes.Departement;
import com.example.niquelesstup.rtt.Classes.InfosMango;
import com.example.niquelesstup.rtt.Classes.Lieu;
import com.example.niquelesstup.rtt.Classes.Membre;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Niquelesstup on 12/06/2017.
 */

public class JsonConverter {

    public static Date stringToDate(String date_a_format){
        String[] tab_date = date_a_format.split("T");
        String date = tab_date[0];
        String time = tab_date[1].split("\\+")[0];
        String[] date_t = date.split("-");
        String[] time_t = time.split(":");
        return new Date(
                Integer.valueOf(date_t[2]), Integer.valueOf(date_t[1]), Integer.valueOf(date_t[0]),
                Integer.valueOf(time_t[0]), Integer.valueOf(time_t[1]), Integer.valueOf(time_t[2]));
    }

    public static Membre convertMembre(JSONObject objetJson){
        Membre leMembre;
        try {
            // Conversion de l'objet Avatar de l'utilisateur
            JSONObject avatarJson = objetJson.getJSONObject("membreAvatar");
            Avatar avatar = new Avatar(avatarJson.getInt("id"), avatarJson.getString("avatarUrl"), avatarJson.getInt("avatarStatut"));
            // Conversion de la liste d'objets InfosMango de l'utilisateur
            ArrayList<InfosMango> listeIm = new ArrayList<InfosMango>();
            JSONArray imListeJson = objetJson.getJSONArray("membreMango");
            for (int i=0; i < imListeJson.length(); i++) {
                JSONObject imJson = imListeJson.getJSONObject(i);
                InfosMango im = new InfosMango(imJson.getInt("id"), imJson.getString("imMangoId"), imJson.getString("imWalletId"));
                listeIm.add(im);
            }
            // Conversion de la liste des objets Compte de l'utilisateur
            JSONArray comptes = objetJson.getJSONArray("membreComptes");
            ArrayList<Compte> listeComptes = new ArrayList<Compte>();
            for (int i=0; i<comptes.length(); i++){
                JSONObject compteJson = comptes.getJSONObject(i);
                Compte unCompte = new Compte(compteJson.getInt("id"), compteJson.getString("compteRibBic"), compteJson.getString("compteRibIban"),
                        compteJson.getString("compteNom"), compteJson.getString("comptePrenom"), compteJson.getString("compteAdresseL1"),
                        compteJson.getString("compteAdresseL2"), compteJson.getString("compteCp"),compteJson.getString("compteVille"));
                listeComptes.add(unCompte);
            }
            // Création de l'objet utilisateurs
            leMembre = new Membre(objetJson.getInt("id"), objetJson.getString("membrePseudo"), objetJson.getString("membreTel"),
                    objetJson.getString("membreMail"), objetJson.getBoolean("membreOrga"), stringToDate(objetJson.getString("membreDateInscription")),
                    stringToDate(objetJson.getString("membreDerniereConnexion")), objetJson.getString("membreIpInscription"),
                    objetJson.getString("membreIpDerniereConnexion"), objetJson.getString("membreCodeValidation"),
                    objetJson.getBoolean("membreValidation"), objetJson.getString("membreDptCode"), avatar, listeComptes, listeIm);
        }catch (JSONException ex){
            leMembre = null;
        }
        return leMembre;
    }

    public static ArrayList<Lieu> convertListeLieux(JSONArray arrayJson){
        //Lieu leLieu = new Lieu();
        ArrayList<Lieu> listeLieux = new ArrayList<Lieu>();
        try {
            for (int i=0; i < arrayJson.length(); i++) {
                JSONObject objetJson = arrayJson.getJSONObject(i);
                // Conversion du département
                JSONObject dptJson = objetJson.getJSONObject("lieuDpt");
                Departement dpt = new Departement(dptJson.getInt("id"), dptJson.getString("dptNom"), dptJson.getString("dptCode"));
                Lieu unLieu = new Lieu(objetJson.getInt("id"), objetJson.getString("lieuCp"), objetJson.getString("lieuVille"),
                        objetJson.getString("lieuNom"), objetJson.getString("lieuAdresseL1"), objetJson.getString("lieuAdresseL2"),
                        dpt, objetJson.getString("lieuLogo"));
                JSONArray listeEventJson = objetJson.getJSONArray("listeEvents");
                unLieu.setCompteEvents(listeEventJson.length());
                listeLieux.add(unLieu);
            }
        } catch (JSONException ex) {

        }
        return listeLieux;
    }
}
