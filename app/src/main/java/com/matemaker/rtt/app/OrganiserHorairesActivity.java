package com.matemaker.rtt.app;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.matemaker.rtt.app.Classes.Adapter.HorairePagerAdapter;
import com.matemaker.rtt.app.Classes.Api.JsonConverter;
import com.matemaker.rtt.app.Classes.CreneauxJoueurs;
import com.matemaker.rtt.app.Classes.ErrorDialog;
import com.matemaker.rtt.app.Classes.Evenement;
import com.matemaker.rtt.app.Classes.EvenementPrive;
import com.matemaker.rtt.app.Classes.Globals;
import com.matemaker.rtt.app.Classes.Lieu;
import com.matemaker.rtt.app.Classes.Membre;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class OrganiserHorairesActivity extends AppCompatActivity {

    private Calendar dateActuelle;
    HorairePagerAdapter mPagerAdapter;
    private final int NB_JOURS_AFFICHES = 7;
    private Lieu leLieu;
    private Activity activity;
    private String descriptif;
    private String nbJoueursMax;
    private ErrorDialog errorDialog;
    private String accepted;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organiser_horaires);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        activity = this;
        errorDialog = new ErrorDialog(this);
        dateActuelle = Calendar.getInstance();
        leLieu = (Lieu) getIntent().getSerializableExtra("lieu");
        descriptif = getIntent().getStringExtra("descriptif");
        nbJoueursMax = getIntent().getStringExtra("nbJoueurs").split(" ")[0];

        List fragments = new Vector();
        for (int i=0; i<NB_JOURS_AFFICHES; i++){
            Calendar date = Calendar.getInstance();
            date.add(Calendar.DATE, i);
            // Ajout des Fragments dans la liste
            fragments.add(OrganiserHorairesFragment.newInstance(date, leLieu));
        }

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.start();

        // Création de l'adapter qui s'occupera de l'affichage de la liste de Fragments
        this.mPagerAdapter = new HorairePagerAdapter(super.getSupportFragmentManager(), fragments);
        ViewPager pager = (ViewPager) super.findViewById(R.id.viewpager);
        pager.setAdapter(this.mPagerAdapter);

        // Evenement clic sur valider les horaires : Ajout d'un Event privé
        Button btnValider = (Button) findViewById(R.id.btnSelectCreneau);
        btnValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {new AjoutEvenementPriveAsynchTask().execute();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(activity, MenuActivity.class);
        i.putExtra("menuItem", "orga");
        startActivity(i);
        return true;
    }

    private class AjoutEvenementPriveAsynchTask extends AsyncTask<Void, String, Void> {

        private FrameLayout frameLayout;
        private ImageView ivCharg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ivCharg = (ImageView) findViewById(R.id.ivChargOrga);
            frameLayout = (FrameLayout) findViewById(R.id.contenuHoraireFrame);
            Glide.with(activity).asGif().load(R.drawable.chargement).into(ivCharg);
            ivCharg.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values[0] == "err"){
                errorDialog.show("Une erreur est survenue pendant l'ajout de votre évenement. Vérifiez votre connexion à internet." + values[1]);
                ivCharg.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                this.cancel(false);
            }else if (values[0] == "created"){
                Toast.makeText(getApplicationContext(), "Bravo! tu as ajouté l'évenement", Toast.LENGTH_SHORT).show();
                ivCharg.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                Intent i = new Intent(activity, MenuActivity.class);
                i.putExtra("menuItem", "mes_tournois");
                Globals.setDateSelected(new ArrayList<String>());
                startActivity(i);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            final String url_event = Globals.getApiUrl() + "/lieux/" + leLieu.getId() + "/event-prives";
            StringRequest request = new StringRequest(Request.Method.POST, url_event, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject objEvenement = new JSONObject(response);
                        final EvenementPrive event = JsonConverter.convertEvenementPrive(objEvenement, false);
                        String url_creneaux_joueurs = Globals.getApiUrl() + "/lieux/" + leLieu.getId() + "/event-prives/" + event.getId() + "/creneaux-joueurs";
                        final ArrayList<CreneauxJoueurs> listeCreneaux = new ArrayList<>();
                        accepted = "false";
                        if (Globals.getDateSelected().size() == 1)
                            accepted = "true";
                        /// AJOUT DES CRENEAUX
                        for (int i = 0; i < Globals.getDateSelected().size(); i++) {
                            String[] date_tab = Globals.getDateSelected().get(i).split(" # ");
                            String[] hTab = date_tab[1].split(" - ");
                            String heure_debut = hTab[0].split("h")[0] + ":" + hTab[0].split("h")[1] + ":00";
                            String heure_fin = hTab[1].split("h")[0] + ":" + hTab[1].split("h")[1] + ":00";
                            final String date_debut = date_tab[0] + " " + heure_debut;
                            final String date_fin = date_tab[0] + " " + heure_fin;
                            StringRequest requestCreneaux = new StringRequest(Request.Method.POST, url_creneaux_joueurs, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    publishProgress("created");
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    publishProgress("err", error.getMessage());
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("creneauDate", date_debut);
                                    params.put("creneauDateFin", date_fin);
                                    params.put("creneauAccepte", accepted);
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
                    }catch(JSONException ex){
                        Log.e("errJsonAjoutEP:", "err" + ex.getMessage());
                        publishProgress("err", ex.getMessage());
                    }
                }

            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("errReponseEventPrive:", "err: " + error.getMessage());
                    publishProgress("err");
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("eventDescriptif", descriptif);
                    params.put("eventJMax", nbJoueursMax);
                    params.put("eventMembre", String.valueOf(Globals.getMembreConnecte().getId()));
                    return params;
                }
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        ((TextView) findViewById(R.id.TV_PresentationHoraires)).setText("Mes créneaux (" + Globals.getDateSelected().size() + ")");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Globals.setDateSelected(new ArrayList<String>());
    }
}
