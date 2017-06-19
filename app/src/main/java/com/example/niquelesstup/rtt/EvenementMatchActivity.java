package com.example.niquelesstup.rtt;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.niquelesstup.rtt.Classes.Adapter.EquipeTournoiAdapter;
import com.example.niquelesstup.rtt.Classes.Adapter.MembreMatchAdapter;
import com.example.niquelesstup.rtt.Classes.Adapter.MsgMurAdapter;
import com.example.niquelesstup.rtt.Classes.Api.JsonConverter;
import com.example.niquelesstup.rtt.Classes.Api.Token;
import com.example.niquelesstup.rtt.Classes.Equipe;
import com.example.niquelesstup.rtt.Classes.EquipeMembre;
import com.example.niquelesstup.rtt.Classes.Evenement;
import com.example.niquelesstup.rtt.Classes.Globals;
import com.example.niquelesstup.rtt.Classes.MessageMur;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class EvenementMatchActivity extends AppCompatActivity {

    private Evenement lEvenement;
    private TextView tvTitreMatch;
    private ListView listViewJoueurs;
    private ListView listViewMsg;
    private RequestQueue requestQueue;
    private Button btnInscrire;
    private TextView tvNbJoueursMatchLV;
    private Button btnPay;
    private Button btnSendMsg;

    private static final int DIALOG_ALERT = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evenement_match);

        // Fleche retour
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Récupération de l'évenement
        lEvenement = (Evenement) getIntent().getSerializableExtra("Evenement");
        //lEvenement.initialiserListeMembres();

        /// REMPLISSAGE DU TEXTE DE LEVENEMENT - Ajustements graphiques
        tvTitreMatch = (TextView) findViewById(R.id.textViewTitreMatch);
        TextView tvLieuMatch = (TextView) findViewById(R.id.textViewLieuMatch);
        TextView tvDateMatch = (TextView) findViewById(R.id.TV_DATEMATCH);
        tvNbJoueursMatchLV = (TextView) findViewById(R.id.textViewNbJMatch);
        TextView tvOrgaMatch = (TextView) findViewById(R.id.TV_ORGAMATCH);
        TextView tvDesc = (TextView) findViewById(R.id.textViewDesc);
        // Définition des listViews
        listViewJoueurs = (ListView) findViewById(R.id.listViewJoueurs);
        listViewMsg = (ListView) findViewById(R.id.list);

        // Couleurs des boutons au toucher
        btnInscrire = (Button) findViewById(R.id.btnInscrire);
        //btnInscrire.setOnTouchListener(this);
        btnPay = (Button) findViewById(R.id.btnPay);
        //btnPay.setOnTouchListener(this);
        btnSendMsg = (Button) findViewById(R.id.btnSndMsg);
        //btnSendMsg.setOnTouchListener(this);

        String str_prive = "Paiement sur place";
        if (lEvenement.isPayable()) {
            str_prive = "Paiement en ligne";
        }
        tvTitreMatch.setText(lEvenement.getTitre());
        tvLieuMatch.setText(lEvenement.getLieu().getNom());
        tvOrgaMatch.setText(lEvenement.getOrganisateur1().getPseudo());
        if (lEvenement.isTournoi()) {
            findViewById(R.id.header_match).setBackgroundColor(Color.parseColor(Globals.COULEUR_TOURNOI));
            btnInscrire.setText("S'inscrire pour le tournoi");
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

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.start();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent i = new Intent(this, ListeMatchActivity.class);
        i.putExtra("lieu", lEvenement.getLieu());
        startActivity(i);
    }

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
        Button btnRej = (Button) dialog.findViewById(R.id.btnDialog);
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
                requestQueue.add(stringRequest);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /// Mise à jour de l'interface graphique (titre liste joueurs, liste joueurs, bouton s'inscrire)
    public void majGraph(Evenement lEvenement){
        btnInscrire.setBackgroundColor(Color.parseColor("#FF2E8B57"));
        if (lEvenement.isTournoi()){
            btnInscrire.setText("S'inscrire pour le tournoi");
            tvNbJoueursMatchLV.setText("Les équipes (" + lEvenement.getNombreEquipesString() + ')');
        }else {
            btnInscrire.setText("S'inscrire pour le match");
            tvNbJoueursMatchLV.setText("Les joueurs (" + lEvenement.getNombreJoueursString() + ')');
        }
        if (lEvenement.membreAppartientEvent(Globals.getMembreConnecte())) {
            btnInscrire.setBackgroundColor(Color.parseColor("#CD5C5C"));
            btnInscrire.setText("Se désinscrire");
        }
    }

    /// Inscription au match
    public void ClicSinscrire(View v){
            StringRequest stringRequest;
            if (lEvenement.membreAppartientEvent(Globals.getMembreConnecte())) {
                String url = Globals.getApiUrl() + "/equipes-membres/" + lEvenement.getOneEm(lEvenement.getListeEquipes().get(0), Globals.getMembreConnecte()).getId();
                stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!lEvenement.isTournoi()) {
                            ArrayList<EquipeMembre> newListe = lEvenement.getListeMembres();
                            newListe.remove(lEvenement.getIndexListeMembre(Globals.getMembreConnecte()));
                            lEvenement.setListeMembres(newListe);
                            ActualiserListeJoueurs(newListe);
                        }else{
                            ArrayList<Equipe> newListeEquipe = lEvenement.getListeEquipes();
                            ArrayList<EquipeMembre> newListeEmTeam = newListeEquipe.get(newListeEquipe.indexOf(lEvenement.getEquipeJoueur(Globals.getMembreConnecte()))).getListeMembres();
                            //newListeEmTeam.remove(newListeEmTeam.indexOf(lEvenement.getOneEm(lEvenement.getEquipeJoueur(Globals.getMembreConnecte()), Globals.getMembreConnecte())));
                            newListeEquipe.get(newListeEquipe.indexOf(lEvenement.getEquipeJoueur(Globals.getMembreConnecte()))).setListeMembres(newListeEmTeam);
                            lEvenement.setListeEquipes(newListeEquipe);
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
            }else {
                if (!lEvenement.isTournoi()) {
                    String url = Globals.getApiUrl() + "/equipes/" + lEvenement.getListeEquipes().get(0).getId() + "/membres/" + Globals.getMembreConnecte().getId();
                    stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject objEmJson = new JSONObject(response);
                                EquipeMembre newMembre = JsonConverter.convertMembreTeam(objEmJson);
                                ArrayList<EquipeMembre> newListe = lEvenement.getListeMembres();
                                newListe.add(newMembre);
                                lEvenement.setListeMembres(newListe);
                                ActualiserListeJoueurs(newListe);
                                majGraph(lEvenement);
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
                    requestQueue.add(stringRequest);
                }
            }
        }

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
            listViewJoueurs.setAdapter(adapter);
            listViewJoueurs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListView lj = (ListView) listViewJoueurs.getChildAt(position).findViewById(R.id.LvJoueurs + position);
                    Equipe equipeSelected = (Equipe) listViewJoueurs.getItemAtPosition(position);
                    onClicItem(view, equipeSelected);
                    adapter.notifyDataSetChanged();
                    adapter.addAll();
                    //adapter.getView(position, listViewJoueurs);
                }
            });
            adapter.addAll(listeEquipeMatch);
            //adapter.notifyDataSetChanged();
        } else {
            ArrayList<String> noJoueurs = new ArrayList<String>();
            noJoueurs.add("Pas encore d'équipes");
            ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, noJoueurs);
            listViewJoueurs.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            listViewJoueurs.setAdapter(adapter);
        }
    }

    /// Actualise la liste des joueurs du match
    public void ActualiserListeJoueurs(ArrayList<EquipeMembre> listeJoueursMatch){
        if (listeJoueursMatch.size() > 0) {
            MembreMatchAdapter adapter = new MembreMatchAdapter(getApplicationContext(), R.layout.liste_joueurs_match); // the adapter is a member field in the activity
            listViewJoueurs.setAdapter(adapter);
            adapter.addAll(listeJoueursMatch);
            adapter.notifyDataSetChanged();
        } else {
            ArrayList<String> noJoueurs = new ArrayList<String>();
            noJoueurs.add("Pas encore de joueurs");
            ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, noJoueurs);
            listViewJoueurs.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            listViewJoueurs.setAdapter(adapter);
        }
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

    /// Redirection clic sur la fleche retour
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intentListMatch = new Intent(getApplicationContext(), ListeMatchActivity.class);
        intentListMatch.putExtra("lieu", lEvenement.getLieu());
        startActivity(intentListMatch);
        return true;
    }




}
