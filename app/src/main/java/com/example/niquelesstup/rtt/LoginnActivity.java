package com.example.niquelesstup.rtt;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.example.niquelesstup.rtt.Classes.Api.ConfApi;
import com.example.niquelesstup.rtt.Classes.Api.JsonConverter;
import com.example.niquelesstup.rtt.Classes.Avatar;
import com.example.niquelesstup.rtt.Classes.Compte;
import com.example.niquelesstup.rtt.Classes.Globals;
import com.example.niquelesstup.rtt.Classes.InfosMango;
import com.example.niquelesstup.rtt.Classes.Membre;
import com.example.niquelesstup.rtt.Classes.Api.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoginnActivity extends AppCompatActivity {

    private static final String url = Globals.getApiUrl() + "/connexion";
    private RequestQueue requestQueue;
    ConfApi confApi;
    Token tokenApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginn);

        if (Globals.getMembreConnecte() != null){
            Intent IntentAccueil = new Intent(this, MenuActivity.class);
            startActivity(IntentAccueil);
        }

        confApi = new ConfApi();
        confApi.newToken(getApplicationContext());
    }

    public RequestQueue getVolleyRequestQueue() {
        return requestQueue;
    }

    public void ClicBoutonConnexion(View view){

        final TextView textView = (TextView) findViewById(R.id.textView4);
        tokenApi = confApi.getToken();
        //textView.setText(.toString());
        EditText inputPseudo = (EditText) findViewById(R.id.inputPseudo);
        EditText inputPass = (EditText) findViewById(R.id.inputPass);
        final String pseudoTv = inputPseudo.getText().toString();
        final String passTv = inputPass.getText().toString();
        final Intent IntentAccueil = new Intent(this, MenuActivity.class);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.start();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject membreAConnecter = new JSONObject(response);
                            String pseudo = membreAConnecter.getString("membrePseudo");
                            if (pseudo != null) {
                                textView.setTextColor(Color.parseColor("#228B22"));
                                textView.setText("Vous êtes maintenant connecté");
                                Membre membreConnecte = JsonConverter.convertMembre(membreAConnecter);
                                //IntentAccueil.
                                Globals.setMembreConnecte(membreConnecte);
                                startActivity(IntentAccueil);
                            }
                        }catch (JSONException ex){
                            textView.setText("Une erreur s'est produite.");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText("Pseudo / mot de passe incorrects");
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("membrePseudo", pseudoTv);
                params.put("membrePass", passTv);
                return params;
            }
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Token token = confApi.getToken();
            Globals.setTokenApi(token);
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("X-Auth-Token", token.getValue());
            return headers;
        }
    };
    requestQueue.add(stringRequest);

    }

    public void onTerminate(){
        requestQueue.stop();
    }

}
