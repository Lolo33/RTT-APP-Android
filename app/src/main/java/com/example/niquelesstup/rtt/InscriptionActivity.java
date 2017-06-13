package com.example.niquelesstup.rtt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import com.example.niquelesstup.rtt.Classes.Api.Token;
import com.example.niquelesstup.rtt.Classes.Globals;
import com.example.niquelesstup.rtt.Classes.Membre;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InscriptionActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    final String url = Globals.getApiUrl() + "/membres";
    ConfApi confApi;
    Token tokenApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        confApi = new ConfApi();
        confApi.newToken(getApplicationContext());
    }

    public void ClicBtnInscription(View view){
        // Récupération des saisies utilisateur
        EditText inputPseudo = (EditText) findViewById(R.id.inputPseudoInsc);
        EditText inputMail = (EditText) findViewById(R.id.inputMail);
        EditText inputTel = (EditText) findViewById(R.id.inputPhone);
        EditText inputPass = (EditText) findViewById(R.id.inputPassword);
        final String pseudo = inputPseudo.getText().toString();
        final String mail = inputMail.getText().toString();
        final String tel = inputTel.getText().toString();
        final String pass = inputPass.toString();
        final TextView tvReponse = (TextView) findViewById(R.id.textViewReponse);
        final Intent IntentAccueil = new Intent(this, MenuActivity.class);
        // Création de la requete HTTP avec volley
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.start();
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject membreJson = new JSONObject(response);
                    Membre leMembreInscrit = JsonConverter.convertMembre(membreJson);
                    Globals.setMembreConnecte(leMembreInscrit);
                    tvReponse.setText("Vous êtes maintenant connecté");
                    startActivity(IntentAccueil);
                }catch (JSONException ex){
                    tvReponse.setText("Nous n'avons pas pu récupérer votre compte");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tvReponse.setText("Une erreur s'est produite.");
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("membrePseudo", pseudo);
                params.put("membrePass", pass);
                params.put("membreTel", tel);
                params.put("membreMail", mail);
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
        requestQueue.add(request);
    }

}
