package com.matemaker.rtt.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.matemaker.rtt.app.Classes.Adapter.EventAdapter;
import com.matemaker.rtt.app.Classes.Api.JsonConverter;
import com.matemaker.rtt.app.Classes.Api.Token;
import com.matemaker.rtt.app.Classes.DownloadImageTask;
import com.matemaker.rtt.app.Classes.ErrorDialog;
import com.matemaker.rtt.app.Classes.Evenement;
import com.matemaker.rtt.app.Classes.Globals;
import com.matemaker.rtt.app.Classes.Lieu;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListeMatchActivity extends AppCompatActivity {

    private Lieu leLieu;
    private RequestQueue requestQueue;
    private ArrayList<Evenement> listeEvenements;
    private ListView listViewEvents;
    private EventAdapter adapter;
    private ErrorDialog errorDialog;
    private ImageView ivCharg;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.redirectIfNotConnected(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_match);
        // Barre cliquable, fleche retour
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Initialisation du lieu
        leLieu = (Lieu) getIntent().getSerializableExtra("lieu");
        activity = this;
        errorDialog = new ErrorDialog(this);

        // déclaration de l'adapter
        adapter = new EventAdapter(this, R.layout.list_custom_events); // the adapter is a member field in the activity
        listViewEvents = (ListView) findViewById(R.id.listViewEvent);

        ivCharg = (ImageView) findViewById(R.id.ivChargListeMatch);

        ImageView ivLogoComplexe = (ImageView) findViewById(R.id.ivLogoComplexe);
        new DownloadImageTask(ivLogoComplexe).execute(Globals.getRttUrl() + "/" + leLieu.getLogo());

        // Initialisation du texte
        final TextView tvTitre = (TextView) findViewById(R.id.textViewTitreListMatch);
        TextView tvVille = (TextView) findViewById(R.id.textViewVille);
        tvTitre.setText(leLieu.getNom());
        tvVille.setText("à " + leLieu.getVille());
        Globals.setFont(this, tvTitre, "Champagne & Limousines Bold.ttf");
        /// Requete de récupération de l'évenement
        listEvents();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent intentListe = new Intent(this, MenuActivity.class);
        intentListe.putExtra("menuItem", "liste_complexes");
        startActivity(intentListe);
        return true;
    }

    public void afficherCarteLieu(View v){
        Intent i = new Intent(this, MapLieuActivity.class);
        i.putExtra("leLieu", leLieu);
        startActivity(i);
    }

    private class listeEventsAsynchTask extends AsyncTask<Void, String, Void>{

        private ArrayList<Evenement> listeEvents;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ivCharg.setVisibility(View.VISIBLE);
            Glide.with(activity).asGif().load(R.drawable.chargement).into(ivCharg);
            listViewEvents.setVisibility(View.INVISIBLE);
            //ivCharg.setAnimation(rotation);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values[0] == "err"){
                errorDialog.show("Une erreur s'est produite lors de la récupération des évenements. Veuillez réessayer.");
                this.cancel(false);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url = Globals.getApiUrl() + "/lieux/" + leLieu.getId() + "/evenements";
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.start();
            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray arrayEventsJson = new JSONArray(response);
                        listeEvents = JsonConverter.convertListeEvenements(arrayEventsJson, true);
                        if (listeEvents.size() >= 1) {
                            listViewEvents.setAdapter(adapter);
                            listeEvenements = listeEvents;
                            adapter.addAll(listeEvents);
                        }else {
                            ArrayList<String> notFound = new ArrayList<String>();
                            notFound.add("Ce complexe ne possède pas d'évenements pour l'instant.");
                            ArrayAdapter adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, notFound);
                            listViewEvents.setAdapter(adapter);
                            listViewEvents.setOnItemClickListener(null);
                            listViewEvents.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        }
                    }catch (JSONException ex){
                        publishProgress("err");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    publishProgress("err");
                }
            })
            {
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ivCharg.clearAnimation();
            ivCharg.setVisibility(View.INVISIBLE);

            listViewEvents.setVisibility(View.VISIBLE);
            listViewEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Evenement eventSelected = listeEvenements.get(position);
                    Intent intentEvent = new Intent(ListeMatchActivity.this, EvenementMatchActivity.class);
                    intentEvent.putExtra("Evenement", eventSelected);
                    intentEvent.putExtra("from", "liste_tournois");
                    startActivity(intentEvent);
                }
            });
            listViewEvents.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.setBackgroundColor(Color.parseColor("#f5f5f5"));
                    return false;
                }
            });
        }
    }

    public void listEvents(){
        new listeEventsAsynchTask().execute();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Globals.redirectIfNotConnected(this);
    }

    @Override
    public void onRestart(){
        super.onRestart();
        Globals.redirectIfNotConnected(this);
    }
}
