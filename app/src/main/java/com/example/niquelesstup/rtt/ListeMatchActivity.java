package com.example.niquelesstup.rtt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.niquelesstup.rtt.Classes.Adapter.EventAdapter;
import com.example.niquelesstup.rtt.Classes.Api.JsonConverter;
import com.example.niquelesstup.rtt.Classes.Api.Token;
import com.example.niquelesstup.rtt.Classes.Evenement;
import com.example.niquelesstup.rtt.Classes.Globals;
import com.example.niquelesstup.rtt.Classes.Lieu;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListeMatchActivity extends AppCompatActivity {

    private Lieu leLieu;
    private RequestQueue requestQueue;
    private ArrayList<Evenement> listeEvenements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_match);
        // Barre cliquable, fleche retour
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Initialisation du lieu
        leLieu = (Lieu) getIntent().getSerializableExtra("lieu");

        // déclaration de l'adapter
        final EventAdapter adapter = new EventAdapter(this, R.layout.list_custom_events); // the adapter is a member field in the activity
        final ListView listViewEvents = (ListView) findViewById(R.id.listViewEvent);

        // Initialisation du texte
        final TextView tvTitre = (TextView) findViewById(R.id.textViewTitreListMatch);
        TextView tvVille = (TextView) findViewById(R.id.textViewVille);
        tvTitre.setText(leLieu.getNom());
        tvVille.setText("à " + leLieu.getVille());
        Globals.setFont(this, tvTitre, "Champagne & Limousines Bold.ttf");

        /// Requete de récupération de l'évenement
        String url = Globals.getApiUrl() + "/lieux/" + leLieu.getId() + "/evenements";
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.start();
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray arrayEventsJson = new JSONArray(response);
                    listViewEvents.setAdapter(adapter);
                    ArrayList<Evenement> listeEvents = JsonConverter.convertListeEvenements(arrayEventsJson);
                    listeEvenements = listeEvents;
                    adapter.addAll(listeEvents);
                }catch (JSONException ex){
                    tvTitre.setText("erreur lors du listing");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tvTitre.setText("erreur reponse");
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
        listViewEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Evenement eventSelected = listeEvenements.get(position);
                Intent intentEvent = new Intent(ListeMatchActivity.this, EvenementMatchActivity.class);
                intentEvent.putExtra("Evenement", eventSelected);
                startActivity(intentEvent);
            }
        });
    }
}
