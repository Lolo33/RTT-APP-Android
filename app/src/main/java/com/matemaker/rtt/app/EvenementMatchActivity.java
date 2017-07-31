package com.matemaker.rtt.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.matemaker.rtt.app.Classes.Adapter.CreneauxAdapter;
import com.matemaker.rtt.app.Classes.Adapter.EquipeTournoiAdapter;
import com.matemaker.rtt.app.Classes.Adapter.MatesAdapter;
import com.matemaker.rtt.app.Classes.Adapter.MembreMatchAdapter;
import com.matemaker.rtt.app.Classes.Adapter.MembreMatchPriveAdapter;
import com.matemaker.rtt.app.Classes.Adapter.MembreMatchPriveNDAdapter;
import com.matemaker.rtt.app.Classes.Adapter.MsgMurAdapter;
import com.matemaker.rtt.app.Classes.Api.JsonConverter;
import com.matemaker.rtt.app.Classes.Api.Token;
import com.matemaker.rtt.app.Classes.CreneauxJoueurs;
import com.matemaker.rtt.app.Classes.Equipe;
import com.matemaker.rtt.app.Classes.EquipeMembre;
import com.matemaker.rtt.app.Classes.ErrorDialog;
import com.matemaker.rtt.app.Classes.Evenement;
import com.matemaker.rtt.app.Classes.EvenementPrive;
import com.matemaker.rtt.app.Classes.Globals;
import com.matemaker.rtt.app.Classes.Mates;
import com.matemaker.rtt.app.Classes.Membre;
import com.matemaker.rtt.app.Classes.MessageMur;
import com.matemaker.rtt.app.Classes.Reservation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EvenementMatchActivity extends AppCompatActivity {

    /// ********** PARTIE COMMUNE EVENEMENT PUBLIC / PRIVE **********
    // Propriétés utiles au traitement
    private Evenement lEvenement;
    private EvenementPrive lEventPrive;
    private CreneauxJoueurs leCreneau;
    private RequestQueue requestQueue;
    private String from;
    private Activity activity;
    private ArrayList<String> datesSelects;
    private CreneauxJoueurs creneauxSelecteds;
    private ArrayList<Mates> matesSelecteds;
    // Propriétés de l'interface Graphique
    private TextView tvTitreMatch;
    private ListView listViewJoueurs;
    private ListView listViewMsg;
    private Button btnInscrire;
    private Button btnReserv;
    private TextView tvNbJoueursMatchLV;
    private TextView tvLieuMatch;
    private TextView tvDateMatch;
    private TextView tvPrixMatch;
    private TextView tvhorMatch;
    private TextView tvOrgaMatch;
    private TextView tvDesc;
    private Button btnPay;
    private Button btnSendMsg;
    private Button btnInvite;
    private ErrorDialog errorDialog;
    private static final int DIALOG_ALERT = 10;

    /// Initialisation des variables et de l'interface graphique
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Globals.redirectIfNotConnected(this);
        setContentView(R.layout.activity_evenement_match);

        from = getIntent().getStringExtra("from");
        activity = this;

        datesSelects = new ArrayList<>();
        matesSelecteds = new ArrayList<>();

        // Fleche retour
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /// REMPLISSAGE DU TEXTE DE LEVENEMENT - Ajustements graphiques
        tvTitreMatch = (TextView) findViewById(R.id.textViewTitreMatch);
        tvLieuMatch = (TextView) findViewById(R.id.textViewLieuMatch);
        tvDateMatch = (TextView) findViewById(R.id.TV_DATEMATCH);
        tvPrixMatch = (TextView) findViewById(R.id.TV_PRIXMATCH);
        tvhorMatch = (TextView) findViewById(R.id.TV_HORMATCH);
        tvNbJoueursMatchLV = (TextView) findViewById(R.id.textViewNbJMatch);
        tvOrgaMatch = (TextView) findViewById(R.id.TV_ORGAMATCH);
        tvDesc = (TextView) findViewById(R.id.textViewDesc);
        // Définition des listViews
        listViewJoueurs = (ListView) findViewById(R.id.listViewJoueurs);
        listViewMsg = (ListView) findViewById(R.id.list);
        btnInscrire = (Button) findViewById(R.id.btnInscrire);
        btnPay = (Button) findViewById(R.id.btnPay);
        btnInvite = (Button) findViewById(R.id.btnInviteAmis);
        btnSendMsg = (Button) findViewById(R.id.btnSndMsg);
        btnReserv = (Button) findViewById(R.id.btnReservTerrain);

        errorDialog = new ErrorDialog(this);

        if (!getIntent().getBooleanExtra("prive", false)) {
            // Récupération de l'évenement
            lEvenement = (Evenement) getIntent().getSerializableExtra("Evenement");
            String prix = "Non réservé";

            String s;
            if (lEvenement.isTarifEquipe()) {
                s = Float.valueOf(lEvenement.getTarif() / lEvenement.getNbJoueursMax()).toString();
            }else{
                s = String.valueOf(lEvenement.getTarif());
            }
            prix = s.substring(0,s.indexOf(".")+2);
            prix = String.valueOf(Globals.tarifToString(prix)) + " €";
            prix = Globals.tarifToString(prix);

            btnReserv.setVisibility(View.GONE);
            btnInvite.setVisibility(View.GONE);
            tvTitreMatch.setText(lEvenement.getTitre().toUpperCase());
            tvLieuMatch.setText(lEvenement.getLieu().getNom());
            tvOrgaMatch.setText(lEvenement.getOrganisateur1().getPseudo());
            tvPrixMatch.setText(prix);
            Calendar ajd = Calendar.getInstance();
            Calendar dateEvent = Calendar.getInstance();
            dateEvent.setTime(lEvenement.getDate());
            if (ajd.get(Calendar.DATE) == dateEvent.get(Calendar.DATE) && ajd.get(Calendar.MONTH) == dateEvent.get(Calendar.MONTH) && ajd.get(Calendar.YEAR) == dateEvent.get(Calendar.YEAR))
                tvDateMatch.setText("Aujourd'hui");
            else
                tvDateMatch.setText(Globals.dateToString(lEvenement.getDate()));
            tvhorMatch.setText(Globals.timeToString(lEvenement.getHeureDebut(), false) + " - " + Globals.timeToString(lEvenement.getHeureFin(), false));
            if (lEvenement.isTournoi()) {
                findViewById(R.id.header_match).setBackgroundColor(Color.parseColor(Globals.COULEUR_TOURNOI));
                btnInscrire.setText("Créer une équipe");
                // Remplissage listView des joueurs du match
                ArrayList<Equipe> listeEquipesMatch = lEvenement.getListeEquipes();
                ActualiserListeEquipes(listeEquipesMatch);
            } else {
                findViewById(R.id.header_match).setBackgroundColor(Color.parseColor(Globals.COULEUR_MATCH));
                // Remplissage listView des joueurs du match
                ArrayList<EquipeMembre> listeJoueursMatch = lEvenement.getListeMembres();
                ActualiserListeJoueurs(listeJoueursMatch);
            }

            majGraph(lEvenement);

            tvDesc.setText(lEvenement.getDescriptif());
            Globals.setFont(this, tvTitreMatch, "Champagne & Limousines Bold.ttf");

            // Remplissage listView des messages du mur
            ArrayList<MessageMur> listeMsg = lEvenement.getListeMsg();
            ActualiserListeMsg(listeMsg);
        }else {
            lEventPrive = (EvenementPrive) getIntent().getSerializableExtra("Evenement");
            lEvenement = null;
            findViewById(R.id.header_match).setBackgroundColor(Color.parseColor(Globals.COULEUR_EVENT_PRIVE));
            if (getIntent().getBooleanExtra("invite", false))
                onClicInviterAmis(btnInvite);
            majGraphPrive();
        }
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.start();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent i;
        if (from.equals("mes_tournois")){
            i = new Intent(this, MenuActivity.class);
            i.putExtra("menuItem", "mes_tournois");
        }else {
            i = new Intent(this, ListeMatchActivity.class);
            i.putExtra("lieu", lEvenement.getLieu());
        }
        startActivity(i);
    }

    @Override
    public void onResume(){
        super.onResume();
        Globals.redirectIfNotConnected(this);
    }

    /// Redirection clic sur la fleche retour
    public boolean onOptionsItemSelected(MenuItem item){
        Intent i;
        if (from.equals("mes_tournois")){
            i = new Intent(this, MenuActivity.class);
            i.putExtra("menuItem", "mes_tournois");
        }else {
            i = new Intent(this, ListeMatchActivity.class);
            i.putExtra("lieu", lEvenement.getLieu());
        }
        startActivity(i);
        return true;
    }





    /// ********** EVENEMENT PUBLIC - FONCTIONS **********
    /// Mise à jour de l'interface graphique de l'évenement public (titre liste joueurs, liste joueurs, bouton s'inscrire)
    public void majGraph(Evenement lEvenement){
        btnInscrire.setBackgroundColor(Color.parseColor("#FF2E8B57"));
        if (lEvenement.isTournoi()){
            btnInscrire.setText("Créer une équipe");
            tvNbJoueursMatchLV.setText("Les équipes (" + lEvenement.getNombreEquipesString() + ')');
        }else {
            btnInscrire.setText("S'inscrire pour le match");
            tvNbJoueursMatchLV.setText("Les joueurs (" + lEvenement.getNombreJoueursString() + ')');
        }
        if (lEvenement.membreAppartientEvent(Globals.getMembreConnecte())) {
            btnPay.setEnabled(true);
            btnPay.setBackgroundColor(Color.parseColor("#FF2E8B57"));
            btnInscrire.setBackgroundColor(Color.parseColor("#CD5C5C"));
            btnInscrire.setText("Se désinscrire");
            if (lEvenement.membrePayed(Globals.getMembreConnecte())){
                btnPay.setEnabled(false);
                btnPay.setBackgroundColor(Color.parseColor("#778899"));
            }
        } else {
            btnPay.setEnabled(false);
            btnPay.setBackgroundColor(Color.parseColor("#778899"));
        }
    }

    /// ***** FONCTIONS APELLEES AU CLIC *****
    /// Clic sur le bouton créer une équipé (Conditions: Tournoi + membre non inscrit)
    public void onClickCreerTeam(View v){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_create_team);
        dialog.setTitle("Créer une équipe");
        // set the custom dialog components - text, image and button
        final Button btnCreer = (Button) dialog.findViewById(R.id.btnCreerTeam);
        final Button btnRet = (Button) dialog.findViewById(R.id.btnExitDialog2);
        if (lEvenement.membreAppartientEvent(Globals.getMembreConnecte())){
            btnCreer.setVisibility(View.GONE);
        }
        // if button is clicked, close the custom dialog
        btnRet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnCreer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCreer.setEnabled(false);
                String url = Globals.getApiUrl() + "/evenements/" + lEvenement.getId() + "/equipes";
                final EditText etNomTeam = (EditText) dialog.findViewById(R.id.dialogInputNomTeam);
                final String nomTeam = etNomTeam.getText().toString();
                if (!nomTeam.equals("")) {
                    StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject objetTeamJson = new JSONObject(response);
                                final Equipe newTeam = JsonConverter.convertEquipe(objetTeamJson);
                                //tvTitreMatch.setText(uneEquipe.getNom());
                                String url_membre = Globals.getApiUrl() + "/equipes/" + newTeam.getId() + "/membres/" + Globals.getMembreConnecte().getId();
                                StringRequest requestMembre = new StringRequest(Request.Method.POST, url_membre, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject objEmJson = new JSONObject(response);
                                            EquipeMembre newMembre = JsonConverter.convertMembreTeam(objEmJson);
                                            ArrayList<Equipe> newListeEquipe = lEvenement.getListeEquipes();
                                            ArrayList<EquipeMembre> newListeEm = new ArrayList<EquipeMembre>();
                                            newListeEm.add(newMembre);
                                            newTeam.setListeMembres(newListeEm);
                                            newListeEquipe.add(newTeam);
                                            ActualiserListeEquipes(newListeEquipe);
                                            lEvenement.setListeEquipes(newListeEquipe);
                                            majGraph(lEvenement);
                                            etNomTeam.setText("");
                                            new SendPushTopicAsynchTask(Globals.getMembreConnecte().getPseudo() + " a crée une équipe pour le tournoi : " + lEvenement.getTitre(),
                                                    "match-" + lEvenement.getId()).execute();
                                            dialog.dismiss();
                                            btnCreer.setEnabled(true);
                                        }catch (JSONException ex){
                                            Log.e("errConvertMembreTeam", ex.getMessage());
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("Erreur volley Em:", error.getMessage());
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("emStatutJoueur", "1");
                                        params.put("emMembrePaye", "0");
                                        params.put("emPayId", "");
                                        return params;
                                    }
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        Token token = Globals.getTokenApi();
                                        Map<String, String> headers = new HashMap<String, String>();
                                        headers.put("X-Auth-Token", token.getValue());
                                        return headers;
                                    }
                                };
                                requestQueue.add(requestMembre);
                            } catch (JSONException ex) {
                                Log.e("errConvertTeam", ex.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Erreur volley:", error.getMessage());
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("teamNom", nomTeam);
                            params.put("teamPrive", "0");
                            params.put("teamPass", "");
                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Token token = Globals.getTokenApi();
                            Map<String, String> headers = new HashMap<String, String>();
                            headers.put("X-Auth-Token", token.getValue());
                            return headers;
                        }
                    };
                    requestQueue.add(request);
                }
            }
        });
        dialog.show();
    }

    /// Clic sur le bouton payer
    public void onClicBtnPay(View v){
        Intent intentCreerUser = new Intent(this, PayCreerUtilisateurActivity.class);
        if (getIntent().getBooleanExtra("prive", false))
            intentCreerUser.putExtra("Evenement", lEventPrive);
        else
            intentCreerUser.putExtra("Evenement", lEvenement);
        intentCreerUser.putExtra("prive", getIntent().getBooleanExtra("prive", false));
        startActivity(intentCreerUser);
    }

    /// Clic sur l'item d'une Equipe (Condition: Tournoi)
    public void onClicItem(View arg0, Equipe lEquipe) {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_team_layout);
        dialog.setTitle("La liste des joueurs");
        // set the custom dialog components - text, image and button
        ListView lvJoueursTeam = (ListView) dialog.findViewById(R.id.lvjteamevent);
        MembreMatchAdapter adapter = new MembreMatchAdapter(getApplicationContext(), R.layout.liste_joueurs_match);
        lvJoueursTeam.setAdapter(adapter);
        adapter.addAll(lEquipe.getListeMembres());
        final Button btnRej = (Button) dialog.findViewById(R.id.btnDialog);
        final Button btnRet = (Button) dialog.findViewById(R.id.btnExitDialog);
        if (lEvenement.membreAppartientEvent(Globals.getMembreConnecte())){
            btnRej.setVisibility(View.GONE);
        }
        final Equipe equipe = lEquipe;
        // if button is clicked, close the custom dialog
        btnRet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnRej.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRej.setEnabled(false);
                if (!lEvenement.membreAppartientEvent(Globals.getMembreConnecte())) {
                    String url = Globals.getApiUrl() + "/equipes/" + equipe.getId() + "/membres/" + Globals.getMembreConnecte().getId();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject objEmJson = new JSONObject(response);
                                EquipeMembre newMembre = JsonConverter.convertMembreTeam(objEmJson);
                                ArrayList<Equipe> newListeEquipe = lEvenement.getListeEquipes();
                                ArrayList<EquipeMembre> newListeEm = newListeEquipe.get(lEvenement.getListeEquipes().indexOf(equipe)).getListeMembres();
                                newListeEm.add(newMembre);
                                ActualiserListeEquipes(newListeEquipe);
                                lEvenement.setListeEquipes(newListeEquipe);
                                majGraph(lEvenement);
                                btnRej.setEnabled(true);
                            } catch (JSONException ex) {
                                Log.e("Erreur JSON:", ex.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Erreur volley:", error.getMessage());
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("emStatutJoueur", "3");
                            params.put("emMembrePaye", "0");
                            params.put("emPayId", "");
                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Token token = Globals.getTokenApi();
                            Map<String, String> headers = new HashMap<String, String>();
                            headers.put("X-Auth-Token", token.getValue());
                            return headers;
                        }
                    };
                    requestQueue.add(stringRequest);
                    dialog.dismiss();
                }
                btnRej.setEnabled(true);
            }
        });
        dialog.show();
    }

    /// Envoi d'un message
    public void ClicSendMessage(View v){
        tvTitreMatch.setText("clic btn msg");
        EditText inputContenu = (EditText) findViewById(R.id.editTextContenuMSG);
        final String contenu = inputContenu.getText().toString();
        if (!contenu.equals("")) {
            String url = Globals.getApiUrl() + "/evenements/" + lEvenement.getId() + "/membres/" + Globals.getMembreConnecte().getId() + "/messages";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //tvTitreMatch.setText(response);
                    try {
                        JSONObject msgJson = new JSONObject(response);
                        MessageMur msg = JsonConverter.convertMsgMur(msgJson);
                        ArrayList<MessageMur> listeMsg = lEvenement.getListeMsg();
                        listeMsg.add(msg);
                        lEvenement.setListeMsg(listeMsg);
                        ActualiserListeMsg(listeMsg);
                        new SendPushTopicAsynchTask(Globals.getMembreConnecte().getPseudo() + " a posté sur le mur du match : " + lEvenement.getTitre(),
                                "match-" + lEvenement.getId()).execute();
                        //tvTitreMatch.setText("Brvo");
                    } catch (JSONException ex) {
                        Log.e("errJson:", ex.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    tvTitreMatch.setText("errReponse");
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("msgContenu", contenu);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Token token = Globals.getTokenApi();
                    Globals.setTokenApi(token);
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Auth-Token", token.getValue());
                    return headers;
                }
            };
            inputContenu.setText("");
            requestQueue.add(stringRequest);
        }
    }

    /// Inscription au match + Désinscriptions tournoi / match (Gère aussi la suppression des équipes)
    public void ClicSinscrire(View v){
        if (lEvenement.membreAppartientEvent(Globals.getMembreConnecte())) {
            new DesinscrireAsynchTask().execute();
        }else {
            if (!lEvenement.isTournoi()) {
                String url = Globals.getApiUrl() + "/equipes/" + lEvenement.getListeEquipes().get(0).getId() + "/membres/" + Globals.getMembreConnecte().getId();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject objEmJson = new JSONObject(response);
                            EquipeMembre newMembre = JsonConverter.convertMembreTeam(objEmJson);
                            ArrayList<EquipeMembre> newListe = lEvenement.getListeMembres();
                            newListe.add(newMembre);
                            lEvenement.setListeMembres(newListe);
                            lEvenement.getListeEquipes().get(0).setListeMembres(newListe);
                            ActualiserListeJoueurs(newListe);
                            majGraph(lEvenement);
                            new SendPushTopicAsynchTask(Globals.getMembreConnecte().getPseudo() + " a rejoint le match : " + lEvenement.getTitre() + " auquel vous participez",
                                    "match-" + lEvenement.getId()).execute();
                            FirebaseMessaging.getInstance().subscribeToTopic("match-" + lEvenement.getId());
                            btnInscrire.setEnabled(true);
                        } catch (JSONException ex) {
                            Log.e("Erreur JSON:", ex.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Erreur volley:", error.getMessage());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("emStatutJoueur", "3");
                        params.put("emMembrePaye", "0");
                        params.put("emPayId", "");
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Token token = Globals.getTokenApi();
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Auth-Token", token.getValue());
                        return headers;
                    }
                };
                requestQueue.add(stringRequest);
            }else{
                onClickCreerTeam(v);
                btnInscrire.setEnabled(true);
            }
        }
    }

    /// ***** APPELS RESEAU *****
    /// Désinscrit un membre de l'évenement public
    private class DesinscrireAsynchTask extends AsyncTask<Void, String, Void>{
        private ArrayList<Equipe> newListeEquipe;
        private ArrayList<EquipeMembre> newListe;
        private Evenement levenement = lEvenement;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnInscrire.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Equipe equipeJoueur = lEvenement.getEquipeJoueur(Globals.getMembreConnecte());
            final EquipeMembre infoJoueurEquipe = lEvenement.getOneEm(equipeJoueur, Globals.getMembreConnecte());
            String url = Globals.getApiUrl() + "/equipes-membres/" + infoJoueurEquipe.getId();
            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (!levenement.isTournoi()) {
                        newListe = levenement.getListeMembres();
                        newListe.remove(levenement.getIndexListeMembre(Globals.getMembreConnecte()));
                        levenement.setListeMembres(newListe);
                        ActualiserListeJoueurs(newListe);
                    }else{
                        // Suppression du membre de l'equipe
                        ArrayList<EquipeMembre> listeMembres = levenement.getEquipeJoueur(Globals.getMembreConnecte()).getListeMembres();
                        newListeEquipe = levenement.getListeEquipes();
                        listeMembres.remove(listeMembres.indexOf(infoJoueurEquipe));
                        newListeEquipe.get(newListeEquipe.indexOf(equipeJoueur)).setListeMembres(listeMembres);
                        levenement.setListeEquipes(newListeEquipe);
                        // Si il est capitaine, suppression de l'equipe
                        if (infoJoueurEquipe.getStatutJoueur().getId() == 1) {
                            String url_membre = Globals.getApiUrl() + "/equipes/" + equipeJoueur.getId();
                            StringRequest request = new StringRequest(Request.Method.DELETE, url_membre, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    ArrayList<Equipe> newListeTeam = levenement.getListeEquipes();
                                    newListeTeam.remove(newListeTeam.indexOf(equipeJoueur));
                                    levenement.setListeEquipes(newListeTeam);
                                    newListeEquipe = newListeTeam;
                                    ActualiserListeEquipes(newListeTeam);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("Erreur volley:", error.getMessage());
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Token token = Globals.getTokenApi();
                                    Map<String, String> headers = new HashMap<String, String>();
                                    headers.put("X-Auth-Token", token.getValue());
                                    return headers;
                                }
                            };
                            requestQueue.add(request);
                        }
                        ActualiserListeEquipes(levenement.getListeEquipes());
                    }
                    majGraph(lEvenement);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Erreur volley: ", error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Token token = Globals.getTokenApi();
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Auth-Token", token.getValue());
                    return headers;
                }
            };
            requestQueue.add(stringRequest);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            btnInscrire.setEnabled(true);
            lEvenement = levenement;
            majGraph(lEvenement);
        }
    }

    // ***** ACTUALISATION DES LISTVIEWS *****
    /// Actualise la liste des messages du match
    public void ActualiserListeMsg(ArrayList<MessageMur> listeMsg){
        // Liste des messages
        if (listeMsg.size() > 0) {
            MsgMurAdapter adapter = new MsgMurAdapter(getApplicationContext(), R.layout.liste_msg_mur);
            listViewMsg.setAdapter(adapter);// the adapter is a member field in the activity
            adapter.addAll(listeMsg);
            adapter.notifyDataSetChanged();
        } else {
            ArrayList<String> noMsg = new ArrayList<String>();
            noMsg.add("Pas encore de messages");
            ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, noMsg);
            listViewMsg.setAdapter(adapter);
        }
    }

    /// Actualise la liste des equipes avec l'equipe passée en paramètre
    public void ActualiserListeEquipes(ArrayList<Equipe> listeEquipeMatch){
        if (listeEquipeMatch.size() > 0) {
            final EquipeTournoiAdapter adapter = new EquipeTournoiAdapter(getApplicationContext(), R.layout.list_equipes_tournoi); // the adapter is a member field in the activity
            listViewJoueurs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListView lj = (ListView) listViewJoueurs.getChildAt(position).findViewById(R.id.LvJoueurs + position);
                    Equipe equipeSelected = (Equipe) listViewJoueurs.getItemAtPosition(position);
                    onClicItem(view, equipeSelected);
                }
            });
            adapter.addAll(listeEquipeMatch);
            adapter.notifyDataSetChanged();
            listViewJoueurs.setAdapter(adapter);
        } else {
            ArrayList<String> noJoueurs = new ArrayList<String>();
            noJoueurs.add("Pas encore d'équipes");
            ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, noJoueurs);
            listViewJoueurs.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            listViewJoueurs.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    /// Actualise la liste des joueurs du match public
    public void ActualiserListeJoueurs(ArrayList<EquipeMembre> listeJoueursMatch){
        if (listeJoueursMatch.size() > 0) {
            MembreMatchAdapter adapter = new MembreMatchAdapter(getApplicationContext(), R.layout.liste_joueurs_match); // the adapter is a member field in the activity
            adapter.addAll(listeJoueursMatch);
            adapter.notifyDataSetChanged();
            listViewJoueurs.setAdapter(adapter);
        } else {
            ArrayList<String> noJoueurs = new ArrayList<String>();
            noJoueurs.add("Pas encore de joueurs");
            ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, noJoueurs);
            listViewJoueurs.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }





    /// ********** EVENEMENT PRIVE - FONCTIONS **********
    /// Mise à jour de l'interface graphique de l'évenement privé
    public void majGraphPrive(){
        tvLieuMatch.setText(lEventPrive.getLieu().getNom());
        tvTitreMatch.setText(lEventPrive.getTitre());
        tvOrgaMatch.setText(lEventPrive.getOrganisateur().getPseudo());
        tvDesc.setText(lEventPrive.getDescriptif());
        leCreneau = lEventPrive.getCreneauEvent();
        if (leCreneau != null){
            tvDateMatch.setText(Globals.dateToString(leCreneau.getDateDebut()));
            tvhorMatch.setText(Globals.timeToString(leCreneau.getDateDebut(), false) + " - " + Globals.timeToString(leCreneau.getDateFin(), false));
            tvNbJoueursMatchLV.setText(lEventPrive.getMembresCreneau(leCreneau).size() + " / " + lEventPrive.getNbJoueursMax() + " joueurs ( + " + lEventPrive.getListeInvites().size() + " invités )");
            ActualiserListeJoueursPrives(lEventPrive.getCreneauxEquivalents(leCreneau));
        }else {
            tvDateMatch.setText("Date non déterminée");
            if (lEventPrive.getOrganisateur().getId() == Globals.getMembreConnecte().getId()) {
                tvhorMatch.setText("Choisir un créneau pour l'évenement");
                tvhorMatch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { onClicJoueurCreneau(v, Globals.getMembreConnecte(), false); }
                });
            }else {
                tvhorMatch.setText("Horaires à déterminer");
                tvhorMatch.setOnClickListener(null);
            }
            tvNbJoueursMatchLV.setText("Les participants (" + lEventPrive.getNbJoueursMax() + "max.)");
            ActualiserListeJoueursPrivesND(lEventPrive.getParticipants());
        }
        if (lEventPrive.isParticipant(Globals.getMembreConnecte())){
            btnInscrire.setBackground(getResources().getDrawable(R.drawable.btn_custom_red));
            if (lEventPrive.getOrganisateur().getId() == Globals.getMembreConnecte().getId()) {
                btnInscrire.setText("Supprimer l'évenement");
                btnInscrire.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(activity);
                        dialog.setContentView(R.layout.dialog_alert_yes_no);
                        dialog.setTitle("Attention");
                        ((TextView) dialog.findViewById(R.id.tvContenuDialog)).setText("Etes vous certains de vouloir supprimer cet évenement?");
                        Button btnSuppr = (Button) dialog.findViewById(R.id.btnSupprEvent);
                        Button btnRetour = (Button) dialog.findViewById(R.id.btnNoSupprEvent);
                        btnSuppr.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new SupprimerEventAsynchTask(lEventPrive).execute();
                                dialog.dismiss();
                            }
                        });
                        btnRetour.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                dialog.cancel();;
                            }
                        });
                        dialog.show();
                    }
                });
            }else {
                btnInscrire.setText("Se désinscrire");
                btnInscrire.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { new DesinscrireEventPriveAsynchTas().execute(); }
                });
            }
        }else {
            btnInscrire.setText("S'inscrire");
            btnInscrire.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (leCreneau != null) {
                        new InscriptionEventPriveAsynchTask(leCreneau, Globals.getMembreConnecte()).execute();
                    } else {
                        onClicJoueurCreneau(v, Globals.getMembreConnecte(), true);
                    }
                }
            });
            btnInscrire.setBackground(getResources().getDrawable(R.drawable.btn_custom_success));
        }
        if (lEventPrive.getTarif() == 0) {
            tvPrixMatch.setText("Non Validé");
            tvPrixMatch.setTextColor(Color.parseColor("#B22222"));
            btnPay.setBackground(getResources().getDrawable(R.drawable.btn_custom_success));
            btnPay.setEnabled(false);
        } else {
            String s = Float.valueOf(lEventPrive.getTarif() / lEventPrive.getMembresCreneau(leCreneau).size()).toString();
            String prix = s.substring(0, s.indexOf(".") + 2);
            tvPrixMatch.setText(Globals.tarifToString(prix) + " €");
            btnPay.setEnabled(true);
            btnPay.setBackground(getResources().getDrawable(R.drawable.btn_custom_success));
        }
        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClicInviterAmis(v);
            }
        });
        if (lEventPrive.getReservation() != null || lEventPrive.getOrganisateur().getId() != Globals.getMembreConnecte().getId()){
            btnReserv.setVisibility(View.GONE);
        }else{
            btnReserv.setVisibility(View.VISIBLE);
            btnReserv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClicReserver();
                }
            });
        }

    }

    /// Clic sur le bouton réserver
    private void onClicReserver() {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_alert_yes_no);
        Button btnReservConf = (Button) dialog.findViewById(R.id.btnSupprEvent);
        Button btnFermer = (Button) dialog.findViewById(R.id.btnNoSupprEvent);
        TextView tvContenu = (TextView) dialog.findViewById(R.id.tvContenuDialog);
        tvContenu.setText("Le terrain sera définitivement réservé (à moins que vous ne supprimiez l'évenement plus d'une heure avant qu'il commence.)" +
                "\n" + "Etes vous vraiment sûr de vouloir réserver le terrain ? ");
        TextView tvPrev = (TextView) dialog.findViewById(R.id.tvPrevDialog);
        tvPrev.setText("(Si vous ne vous présentez pas au complexe, votre indice de fiabilité en sera affecté.)");
        btnFermer.setText("Non, annuler");
        btnReservConf.setText("Oui, Reserver");
        btnReservConf.setBackground(getResources().getDrawable(R.drawable.btn_custom_success));
        btnReservConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ReserverTerrainAsynchTask().execute();
            }
        });
        btnFermer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /// Applique la couleur en paramètre à tous les items d'une listView
    public void changerCouleursItemsLV(ListView listView, int color){
        for (int i=0;i<listView.getCount();i++)
            listView.getChildAt(i).setBackgroundColor(color);
    }

    /// Invitation d'amis à l'évenement
    public void onClicInviterAmis(View arg0){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_mode_invite_amis);
        dialog.setTitle("Inviter des amis");
        final TextView tvRTT = (TextView) dialog.findViewById(R.id.tvAmiRTT);
        TextView tvSMS = (TextView) dialog.findViewById(R.id.tvAmiSMS);
        TextView tvFB = (TextView) dialog.findViewById(R.id.tvAmiFB);
        Button btnFermer = (Button) dialog.findViewById(R.id.btnFermerAmis);
        btnFermer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // Inviter des amis de l'application
        tvRTT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.dialog_amis_rtt);
                dialog.setTitle("Mes Mates sur RTT");
                final ListView lvAmisRTT = (ListView) dialog.findViewById(R.id.lvAmisRTT);
                if (Globals.getMembreConnecte().getListeMates().size() > 0) {
                    final MatesAdapter adapter = new MatesAdapter(getApplicationContext(), R.layout.liste_custom_mates);
                    lvAmisRTT.setAdapter(adapter);
                    adapter.addAll(Globals.getMembreConnecte().getListeMates());
                    final TextView tvSelectAll = (TextView) dialog.findViewById(R.id.tvSelectAll);
                    lvAmisRTT.setTag(R.string.tag_horaire_checked, "null");
                    // Selection de tous les amis
                    tvSelectAll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (lvAmisRTT.getTag(R.string.tag_horaire_checked).toString() == "all_selected") {
                                lvAmisRTT.setTag(R.string.tag_horaire_checked, "null");
                                changerCouleursItemsLV(lvAmisRTT, Color.WHITE);
                                matesSelecteds.clear();
                                tvSelectAll.setText("Tout sélectionner");
                            } else {
                                lvAmisRTT.setTag(R.string.tag_horaire_checked, "all_selected");
                                changerCouleursItemsLV(lvAmisRTT, Color.parseColor(Globals.COULEUR_EVENT_PRIVE));
                                matesSelecteds.clear();
                                matesSelecteds.addAll(Globals.getMembreConnecte().getListeMates());
                                tvSelectAll.setText("Tout désélectionner");
                            }
                        }
                    });
                    // Sélection d'un ami
                    lvAmisRTT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (view.getTag(R.string.tag_horaire_checked).toString() == "selected") {
                                view.setTag(R.string.tag_horaire_checked, "null");
                                view.setBackgroundColor(Color.WHITE);
                                matesSelecteds.remove(matesSelecteds.indexOf(adapter.getItem(position)));
                            } else {
                                view.setTag(R.string.tag_horaire_checked, "selected");
                                view.setBackgroundColor(Color.parseColor(Globals.COULEUR_EVENT_PRIVE));
                                matesSelecteds.add(adapter.getItem(position));
                            }
                        }
                    });
                }else{
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);
                    lvAmisRTT.setAdapter(adapter);
                    adapter.add("Vous n'avez pas de mates pour l'instant");
                }
                // Invitation des amis
                final Button btnInviter = (Button) dialog.findViewById(R.id.btnInviteAmiRTT);
                btnInviter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CreneauxJoueurs creneauInvite = new CreneauxJoueurs();
                        creneauInvite.setDateDebut(new Date(2016,10,10,10,10,10));
                        creneauInvite.setDateFin(new Date(2016,10,10,10,10,10));
                        creneauInvite.setInvite(true);
                        btnInviter.setEnabled(false);
                        for (int i=0; i<matesSelecteds.size(); i++)
                            new InscriptionEventPriveAsynchTask(creneauInvite, matesSelecteds.get(i).getMate()).execute();
                    }
                });
                // Retour a l'affichage des modes d'invitation
                Button btnRetour = (Button) dialog.findViewById(R.id.btnRetourAmis);
                btnRetour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        onClicInviterAmis(v);
                    }
                });
            }
        });
        /// Inviter des amis par SMS
        tvSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, ListeContact.class);
                if (!getIntent().getBooleanExtra("prive", false)){
                    i.putExtra("lEvenement", lEvenement);
                }else {
                    i.putExtra("lEvenement", lEventPrive);
                }
                i.putExtra("prive", getIntent().getBooleanExtra("prive", false));
                i.putExtra("from", getIntent().getStringExtra("from"));
                startActivity(i);
            }
        });
        /// Inviter des amis Facebook
        if (Globals.getMembreConnecte().getIdFacebook() == "null" || Globals.getMembreConnecte().getIdFacebook() == null){
            tvFB.setVisibility(View.GONE);
        }
        tvFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, ListeAmisFacebook.class);
                i.putExtra("Evenement", lEventPrive);
                startActivity(i);
            }
        });
        dialog.show();
    }

    /// Clic sur un joueur de la liste ou sur "Choisir un créneau pour cet évenement"
    public void onClicJoueurCreneau(View arg0, Membre leMembre, boolean inscription) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_creneaux);
        // Définition de la liste des créneaux
        ListView lvCreneaux = (ListView) dialog.findViewById(R.id.lvCreneauxJoueurs);
        final CreneauxAdapter adapter = new CreneauxAdapter(getApplicationContext(), R.layout.list_custom_creneaux, lEventPrive);
        lvCreneaux.setAdapter(adapter);
        Button btnBas = (Button) dialog.findViewById(R.id.btnFermDialogCreneaux);
        // Validation du créneau au clic si le membre est bien l'organisateur de l'évenement
        if (inscription) {
            dialog.setTitle("Choisir mes créneau");
            adapter.addAll(lEventPrive.getListeCreneauxMembre(lEventPrive.getOrganisateur()));
            btnBas.setText("Sélectionner ces horaires");
            lvCreneaux.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (view.getTag(R.string.tag_horaire_checked).toString() == "selected") {
                        view.setTag(R.string.tag_horaire_checked, "null");
                        view.setBackgroundColor(Color.WHITE);
                        datesSelects.remove(datesSelects.indexOf(view.getTag(R.string.tag_horaire_obj).toString()));
                    } else {
                        view.setTag(R.string.tag_horaire_checked, "selected");
                        view.setBackgroundColor(Color.parseColor(Globals.COULEUR_EVENT_PRIVE));
                        datesSelects.add(view.getTag(R.string.tag_horaire_obj).toString());
                    }
                }
            });
            btnBas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ChoisirHorairesInscriptionAsynchTask().execute();
                    dialog.dismiss();
                }
            });
        } else {
            adapter.addAll(lEventPrive.getListeCreneauxMembre(leMembre));
            if (leMembre.getId() == lEventPrive.getOrganisateur().getId() && leMembre.getId() == Globals.getMembreConnecte().getId()) {
                dialog.setTitle("Choisir un créneau");
                creneauxSelecteds = null;
                lvCreneaux.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        for (int j = 0; j < parent.getChildCount(); j++)
                            parent.getChildAt(j).setBackgroundColor(Color.WHITE);
                        view.setBackgroundColor(Color.parseColor(Globals.COULEUR_EVENT_PRIVE));
                        creneauxSelecteds = adapter.getItem(position);
                    }
                });
                btnBas.setText("Sélectionner ce créneau");
                btnBas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (creneauxSelecteds != null) {
                            lEventPrive.getListeCreneaux().remove(lEventPrive.getListeCreneaux().indexOf(creneauxSelecteds));
                            new ValiderHoraireCreneauAsynchTask(creneauxSelecteds).execute();
                            dialog.dismiss();
                        }else {
                            errorDialog.show("Vous devez sélectionner un créneau pour valider votre évenement");
                        }
                    }
                });
            } else {
                dialog.setTitle("Créneaux de " + leMembre.getPseudo());
                btnBas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        }
        dialog.show();
    }


    /// ***** APPELS RESEAU *****
    /// Validation d'un créneau par l'organisateur
    private class ValiderHoraireCreneauAsynchTask extends AsyncTask<Void, String, Void>{

        private CreneauxJoueurs leCreneau;
        private ValiderHoraireCreneauAsynchTask(CreneauxJoueurs creneauAValider){
            this.leCreneau = creneauAValider;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values[0] == "err")
                errorDialog.show("Une erreur s'est produite pendant la validation de l'horaire. Vérifiez votre connexion Internet");
            else if (values[0] == "validated") {
                new SendPushTopicAsynchTask(Globals.getMembreConnecte().getPseudo() + " a validé les créneaux de son match : " + lEventPrive.getTitre(),
                        "match_prive-" + lEventPrive.getId()).execute();
                Toast.makeText(getApplicationContext(), "Vous avez bien validé ce créneau.", Toast.LENGTH_SHORT);
                majGraphPrive();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url_valider = Globals.getApiUrl() + "/event-prives/" + lEventPrive.getId() + "/creneaux-joueurs/" + leCreneau.getId();
            StringRequest requestValidCreneau = new StringRequest(Request.Method.PATCH, url_valider, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject objNewCreneau = new JSONObject(response);
                        CreneauxJoueurs newCreneau = JsonConverter.convertCreneauJoueur(objNewCreneau);
                        lEventPrive.getListeCreneaux().add(newCreneau);
                    }catch (JSONException ex){
                        Log.e("errJsonCreneau", "err: " + ex.getMessage());
                        publishProgress("err");
                    }
                    publishProgress("validated");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("errVolleyCreneau", "err: " + error.getMessage());
                    publishProgress("err");
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Token token = Globals.getTokenApi();
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Auth-Token", token.getValue());
                    return headers;
                }
            };
            requestQueue.add(requestValidCreneau);
            return null;
        }

    }

    /// Désinscrit un membre d'un évenement privé (Supprime tous ses créneaux)
    private class DesinscrireEventPriveAsynchTas extends AsyncTask<Void, String, Void>{

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values[0] == "err")
                errorDialog.show("Une erreur est survenue pendant la désinscription, vérifiez votre connexion internet et réessayez !");
            else if (values[0] == "deleted"){
                Toast.makeText(getApplicationContext(), "Vous n'êtes plus inscrit à cet évenement !", Toast.LENGTH_SHORT).show();
                majGraphPrive();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url = Globals.getApiUrl() + "/membres/" + Globals.getMembreConnecte().getId() + "/event-prives/" + lEventPrive.getId();
            StringRequest request = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    for (int i=0;i<lEventPrive.getListeCreneaux().size();i++){
                        if (lEventPrive.getListeCreneaux().get(i).getMembre().getId() == Globals.getMembreConnecte().getId())
                            lEventPrive.getListeCreneaux().remove(i);
                    }
                    publishProgress("deleted");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    publishProgress("err");
                    Log.e("errReseauDeleteEP", "err: " + error.getMessage());
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Auth-Token", Globals.getTokenApi().getValue());
                    return headers;
                }
            };
            requestQueue.add(request);
            return null;
        }
    }

    /// Inscription à l'évenement privé dont le créneau n'est pas déterminé
    private class ChoisirHorairesInscriptionAsynchTask extends AsyncTask<Void, String, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (datesSelects.size() == 0) {
                errorDialog.show("Vous n'avez pas sélectionné d'horaires");
                this.cancel(false);
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values[0] == "err"){
                errorDialog.show("Une erreur est survenue pendant la validation de vos horaires. Vérifiez votre connexion internet et réessayez.");
                this.cancel(false);
            }else if (values[0] == "inscrit"){
                new SendPushTopicAsynchTask(Globals.getMembreConnecte().getPseudo() + " s'est inscrit au match : " + lEventPrive.getTitre() + " de " + lEventPrive.getOrganisateur().getPseudo(),
                        "match_prive-" + lEventPrive.getId()).execute();
                FirebaseMessaging.getInstance().subscribeToTopic("match_prive-" + lEventPrive.getId());
                Toast.makeText(getApplicationContext(), "Vous avez bien sélectionné vos horaires.", Toast.LENGTH_SHORT).show();
                majGraphPrive();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url_creneaux_joueurs = Globals.getApiUrl() + "/lieux/" + lEventPrive.getLieu().getId() + "/event-prives/" + lEventPrive.getId() + "/creneaux-joueurs";
            for (int i = 0; i < datesSelects.size(); i++) {
                String[] date_tab = datesSelects.get(i).split(" # ");
                String[] hTab = date_tab[1].split(" - ");
                final String date_debut = date_tab[0] + " " + hTab[0];
                final String date_fin = date_tab[0] + " " + hTab[1];
                StringRequest requestCreneaux = new StringRequest(Request.Method.POST, url_creneaux_joueurs, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject objCreneau = new JSONObject(response);
                            CreneauxJoueurs unCreneau = JsonConverter.convertCreneauJoueur(objCreneau);
                            lEventPrive.getListeCreneaux().add(unCreneau);
                            publishProgress("inscrit");
                        }catch (JSONException ex){
                            publishProgress("err");
                            Log.e("errJsonNbHor", "err: " + ex.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("errVolleyInscNbHor", "err: " + error.getMessage());
                        publishProgress("err", error.getMessage());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("creneauDate", date_debut);
                        params.put("creneauDateFin", date_fin);
                        params.put("creneauAccepte", "false");
                        params.put("creneauInvite", "false");
                        params.put("idMembre", String.valueOf(Globals.getMembreConnecte().getId()));
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Auth-Token", Globals.getTokenApi().getValue());
                        return headers;
                    }
                };
                requestQueue.add(requestCreneaux);
            }
            return null;
        }

    }

    /// Réservation du terrain
    private class ReserverTerrainAsynchTask extends AsyncTask<Void, String, Void>{
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values[0] == "err"){
                errorDialog.show("Une erreur est survenue pendant la réservation, vérifiez votre connexion internet puis réessayez !");
            }else{
                new SendPushTopicAsynchTask("Le terrain a été réservé pour le match : " + lEventPrive.getTitre() + " de " + lEventPrive.getOrganisateur().getPseudo(),
                        "match_prive-" + lEventPrive.getId()).execute();
                majGraphPrive();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url_reserv = Globals.getApiUrl() + "/event-prives/" + lEventPrive.getId() + "/reservations";
            StringRequest req = new StringRequest(Request.Method.POST, url_reserv, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //TODO Ajouter la réservation à l'évenement
                    try {
                        Reservation resa = JsonConverter.convertReservation(new JSONObject(response), false);
                        lEventPrive.setReservation(resa);
                        publishProgress("reserved");
                    }catch (JSONException ex){
                        publishProgress("err");
                        Log.e("errReservTerrain", "err: " + ex.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    publishProgress("err");
                    Log.e("respResaFail: ", "err: " + error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Auth-Token", Globals.getTokenApi().getValue());
                    return headers;
                }
            };
            requestQueue.add(req);
            return null;
        }
    }

    /// Inscription à l'évenement privé dont le créneau est déterminée
    private class InscriptionEventPriveAsynchTask extends AsyncTask<Void, String, Void>{

        private CreneauxJoueurs leCreneau;
        private Membre leMembre;
        private InscriptionEventPriveAsynchTask(CreneauxJoueurs creneauJoueurs, Membre membre){
            this.leCreneau = creneauJoueurs;
            this.leMembre = membre;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values[0] == "err"){
                errorDialog.show("Une erreur est survenue pendant l'inscription, vérifiez votre connexion internet et réessayez.");
                datesSelects.clear();
            }else if (values[0] == "inscrit"){
                new SendPushTopicAsynchTask(Globals.getMembreConnecte().getPseudo() + " s'est inscrit au match : " + lEventPrive.getTitre() + " de " + lEventPrive.getOrganisateur().getPseudo(),
                        "match_prive-" + lEventPrive.getId()).execute();
                FirebaseMessaging.getInstance().subscribeToTopic("match_prive-" + lEventPrive.getId());
                Toast.makeText(getApplicationContext(), "Vous êtes bien inscrit", Toast.LENGTH_SHORT).show();
                majGraphPrive();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url = Globals.getApiUrl() + "/lieux/" + lEventPrive.getLieu().getId() + "/event-prives/" + lEventPrive.getId() + "/creneaux-joueurs";
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject objCreneau = new JSONObject(response);
                        CreneauxJoueurs leCreneau = JsonConverter.convertCreneauJoueur(objCreneau);
                        lEventPrive.getListeCreneaux().add(leCreneau);
                        publishProgress("inscrit");
                    }catch (JSONException ex){
                        Log.e("exJsonInscPriv", "err: " + ex.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("errResponseInscPrive: ", "err: " + error.getMessage());
                    publishProgress("err");
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("creneauDate", Globals.dateToString(leCreneau.getDateDebut()) + " " + Globals.timeToStringFormatBdd(leCreneau.getDateDebut()) );
                    params.put("creneauDateFin", Globals.dateToString(leCreneau.getDateFin()) + " " + Globals.timeToStringFormatBdd(leCreneau.getDateFin()));
                    params.put("creneauAccepte", "false");
                    params.put("creneauInvite", String.valueOf(leCreneau.isInvite()));
                    params.put("idMembre", String.valueOf(leMembre.getId()));
                    return params;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Token token = Globals.getTokenApi();
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Auth-Token", token.getValue());
                    return headers;
                }
            };
            requestQueue.add(request);
            return null;
        }

    }

    /// Suppression de l'évenement privé par l'organisateur
    private class SupprimerEventAsynchTask extends AsyncTask<Void, String, Void>{

        private EvenementPrive evenementPrive;
        private SupprimerEventAsynchTask(EvenementPrive eventPrive){
            this.evenementPrive = eventPrive;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values[0] == "err"){
                errorDialog.show("Une erreur est survenue pendant la suppression du créneau. Vérifiez votre connexion à Internet.");
            } else if (values[0] == "deleted") {
                Toast.makeText(getApplicationContext(), "Vous avez bien supprimé l'évenement.", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(activity, MenuActivity.class);
                i.putExtra("menuItem", "mes_tournois");
                startActivity(i);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url = Globals.getApiUrl() + "/event-prives/" + evenementPrive.getId();
            StringRequest requestDelete = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    publishProgress("deleted");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("errVolleyDelEvent", "err: " + error.getMessage());
                    publishProgress("err");
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Token token = Globals.getTokenApi();
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Auth-Token", token.getValue());
                    return headers;
                }
            };
            requestQueue.add(requestDelete);
            return null;
        }

    }

    private class SendPushTopicAsynchTask extends AsyncTask<Void, String, Void>{

        private String message;
        private String topic;
        private SendPushTopicAsynchTask(String message, String topic){
            this.message = message;
            this.topic = topic;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String type;
            String id;
            if (getIntent().getBooleanExtra("prive", false)) {
                type = "prive";
                id = String.valueOf(lEventPrive.getId());
            }else {
                type = "public";
                id = String.valueOf(lEvenement.getId());
            }
            try {
                JSONObject requestObject = new JSONObject();
                requestObject.put("to", "/topics/" + topic);
                JSONObject data = new JSONObject();
                data.put("message", message);
                data.put("type", type);
                data.put("event", id);
                requestObject.put("data", data);
                final String requestBody = requestObject.toString();

                String url = "https://fcm.googleapis.com/fcm/send";
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("bj", "topic: " + response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("errCreateTopic", "err: " + error.getMessage());
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Token token = Globals.getTokenApi();
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "key=AAAAGjxNrbM:APA91bFPudUAURXSOZXMrRr0C41B-3yEsQ9rbGAsUaqOSG5CdVpxQo0181Idr3Cf_gbnG8sOMmSlOCfGwGUFlCyrjVFmUBqcjE4NYgjrWvIhME4t8ZEjX3TDIUnBNGYGzcz9bmfdBGZD");
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return requestBody == null ? null : requestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            Log.e("Unsupported Encoding",
                                    "err: " + requestBody);
                            return null;
                        }
                    }
                };
                requestQueue.add(request);

            }catch (JSONException ex){
                Log.e("exJsonSonPush", "err: " + ex.getMessage());
            }
            return null;
        }
    }


    // ***** ACTUALISATION DES LISTVIEWS *****
    /// Actualise la liste des joueurs d'un match privé dont l'horaire est validée
    public void ActualiserListeJoueursPrives(ArrayList<CreneauxJoueurs> creneauxJoueurs){
        if (creneauxJoueurs.size() > 0){
            MembreMatchPriveAdapter adapter = new MembreMatchPriveAdapter(getApplicationContext(), R.layout.liste_joueurs_match); // the adapter is a member field in the activity
            adapter.addAll(creneauxJoueurs);
            adapter.notifyDataSetChanged();
            listViewJoueurs.setAdapter(adapter);
            listViewJoueurs.setOnItemClickListener(null);
        }else {
            ArrayList<String> noJoueurs = new ArrayList<String>();
            noJoueurs.add("Pas encore de joueurs");
            ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, noJoueurs);
            listViewJoueurs.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    /// Actualise la liste des joueurs d'un match privé dont l'horaire n'est pas validée
    public void ActualiserListeJoueursPrivesND(ArrayList<Membre> membres){
        if (membres.size() > 0){
            final MembreMatchPriveNDAdapter adapter = new MembreMatchPriveNDAdapter(getApplicationContext(), R.layout.liste_joueurs_match, lEventPrive); // the adapter is a member field in the activity
            adapter.addAll(membres);
            adapter.notifyDataSetChanged();
            listViewJoueurs.setAdapter(adapter);
            listViewJoueurs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Membre leMembre = (Membre) adapter.getItem(position);
                    onClicJoueurCreneau(view, leMembre, false);
                }
            });
        }else {
            ArrayList<String> noJoueurs = new ArrayList<String>();
            noJoueurs.add("Pas encore de joueurs");
            ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, noJoueurs);
            listViewJoueurs.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

}
