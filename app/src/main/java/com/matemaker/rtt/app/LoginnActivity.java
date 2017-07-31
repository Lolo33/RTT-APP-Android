package com.matemaker.rtt.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.matemaker.rtt.app.Classes.ErrorDialog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.matemaker.rtt.app.Classes.Api.ConfApi;
import com.matemaker.rtt.app.Classes.Api.JsonConverter;
import com.matemaker.rtt.app.Classes.Globals;
import com.matemaker.rtt.app.Classes.Membre;
import com.matemaker.rtt.app.Classes.Api.Token;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
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
    Intent IntentAccueil;
    CallbackManager callbackManager;
    ErrorDialog errorDialog;
    RelativeLayout rlLogin;
    private Activity activity;

    public void redirectIfConnected() {
        // Redirection si le membre tente d'acceder à cette page en étant déja connecté
        if (Globals.getMembreConnecte() != null){
            startActivity(IntentAccueil);
        }
    }

    @Override
    public void onBackPressed(){
        //super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginn);

        activity = this;

        rlLogin = (RelativeLayout) findViewById(R.id.rlLogin);
        errorDialog = new ErrorDialog(this);

        confApi = new ConfApi();
        confApi.newToken(getApplicationContext());

        IntentAccueil = new Intent(this, MenuActivity.class);
        IntentAccueil.putExtra("menuItem", "accueil");

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.start();

        // Création du callback pour fb
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "user_friends"));
        // On récupère le token Facebook de l'utilisateur
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        // Si le token est null (l'utilisateur n'a pas pas son compte Facebook déja connecté à l'application)
        if (accessToken == null || accessToken.toString() == "null" || accessToken.toString() == null || accessToken.toString() == "" || accessToken.isExpired()) {

            // Retour de la connexion par facebook (succes : connexion ou ajout du membre
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    GraphRequest request = GraphRequest.newMeRequest(
                            accessToken,
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        final JSONObject object,
                                        GraphResponse response) {
                                    new RecupAjoutMembreFbAsynchTask(object).execute();
                                    Log.e("objFb", "fb: " + object.toString());
                                }
                            });
                    Bundle parameters = new Bundle();
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
                    errorDialog.show("Vous avez annulé l'opération.");
                }

                @Override
                public void onError(FacebookException exception) {
                    errorDialog.show("Une erreur est survenue lors de la connexion avec Facebook. Message : " + exception.getMessage());
                }
            });

            // Sinon (membre déja connecté à facbook sur l'application)
        }else {

            // Requete pour récupère l'utilisateur et le connecter
            //rlLogin.setVisibility(View.INVISIBLE);
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                final JSONObject object,
                                GraphResponse response) {
                            new RecupMembreAsynchTask(object).execute();
                        }

                    });
            Bundle parameters = new Bundle();
            request.setParameters(parameters);
            request.executeAsync();

        }

        redirectIfConnected();

    }

    // Requete pour l'ajout et/ou la connexion d'un membre par Facebook (à apeller au callback de la requete Facebook Connect
    private class RecupAjoutMembreFbAsynchTask extends AsyncTask<Void, String, Void> {

        private JSONObject object;
        private String tele;
        private RecupAjoutMembreFbAsynchTask(JSONObject object){
            this.object = object;
        }

        @Override
        protected void onProgressUpdate(String... params){
            if (params[0] == "err") {
                errorDialog.show("Erreur de conversion via Facebook, veuillez réessayer");
                rlLogin.setVisibility(View.VISIBLE);
            }else if (params[0] == "con" )
                Toast.makeText(getApplicationContext(), "Connexion...", Toast.LENGTH_SHORT).show();
            else if (params[0] == "new-connect")
                Toast.makeText(getApplicationContext(), "Bienvenue sur RTT, " + Globals.getMembreConnecte().getPseudo() + " !", Toast.LENGTH_LONG).show();
            else if (params[0] == "connect")
                Toast.makeText(getApplicationContext(), "Heureux de te revoir, " + Globals.getMembreConnecte().getPseudo() + " !", Toast.LENGTH_LONG).show();
            else if (params[0] == "wait-tel"){
                final Dialog dialogTel = new Dialog(activity);
                dialogTel.setContentView(R.layout.dialog_tel);
                final RecupAjoutMembreFbAsynchTask task = this;
                dialogTel.setTitle("Saisissez votre N° de tel.");
                dialogTel.show();
                Button btnValidTel = (Button) dialogTel.findViewById(R.id.btnValidTel);
                btnValidTel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String tel = ((EditText) dialogTel.findViewById(R.id.etNumTel)).getText().toString();
                        tele = tel;
                        final String code = Globals.generate(5);
                        SmsManager.getDefault().sendTextMessage(tel, null, "Votre code de validation: " + code, null, null);
                        final Dialog dialog = new Dialog(activity);
                        dialog.setContentView(R.layout.dialog_valider_tel);
                        dialog.setTitle("Validation du numéro");
                        dialog.show();
                        final Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirmTel);
                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TextView tvErr = (TextView) dialog.findViewById(R.id.tvErrCodeVerifTe);
                                tvErr.setVisibility(View.GONE);
                                String code_saisi = ((EditText) dialog.findViewById(R.id.etCodeVerifTel)).getText().toString();
                                if (code.equals(code_saisi)){
                                    new AjoutMembreFbAsynchTask(object, tele).execute();
                                }else {
                                    tvErr.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                });
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params){
            try {
                String url_find_membre = Globals.getApiUrl() + "/membres-fb/" + object.getString("id");
                StringRequest request_search = new StringRequest(Request.Method.GET, url_find_membre, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject objMembreFb = new JSONObject(response);
                            Membre leMembre = JsonConverter.convertMembre(objMembreFb, true);
                            Globals.setMembreConnecte(leMembre);
                            Globals.setMembreFbConnect(true);
                            publishProgress("connect");
                            startActivity(IntentAccueil);
                        } catch (JSONException ex) {
                            publishProgress("err");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse.statusCode == 404) {
                            publishProgress("wait-tel");
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Token token = confApi.getToken();
                        Globals.setTokenApi(token);
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Auth-Token", token.getValue());
                        return headers;
                    }
                };
                requestQueue.add(request_search);
            } catch (Exception ex) {
                Log.e("errFb", "err: " + ex.getMessage());
                publishProgress("err");
            }
            return null;
        }
    }

    private class AjoutMembreFbAsynchTask extends AsyncTask<Void, String, Void> {

        private JSONObject object;
        private String tele;

        private AjoutMembreFbAsynchTask(JSONObject obj, String tel){
            this.object = obj;
            this.tele = tel;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url_add = Globals.getApiUrl() + "/membres";
            publishProgress("con");
            StringRequest request = new StringRequest(Request.Method.POST, url_add, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject objMembreFb = new JSONObject(response);
                        Membre leMembre = JsonConverter.convertMembre(objMembreFb, true);
                        Globals.setMembreConnecte(leMembre);
                        Globals.setMembreFbConnect(true);
                        publishProgress("new-connect");
                        startActivity(IntentAccueil);
                    } catch (JSONException ex) {
                        publishProgress("err");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("errVollAjFb:", "err: " + error.getMessage());
                    publishProgress("err");
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    try {
                        params.put("membrePseudo", object.getString("name"));
                        params.put("membrePass", "facebook");
                        params.put("membreTel", tele);
                        params.put("membreFbId", object.getString("id"));
                        params.put("membreValidation", "1");
                    } catch (JSONException ex) {
                        publishProgress("err");
                    }
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
            return null;
        }
    }

    // Requete pour la connexion automatique d'un membre déja connecté par Facebook
    private class RecupMembreAsynchTask extends AsyncTask<Void, String, Void> {

        private JSONObject object;
        private RecupMembreAsynchTask(JSONObject object){
            this.object = object;
        }

        @Override
        protected void onProgressUpdate(String... params){
            if (params[0] == "err") {
                errorDialog.show("Erreur de connexion via Facebook, veuillez vous reconnecter.");
                AccessToken.setCurrentAccessToken(null);
                rlLogin.setVisibility(View.VISIBLE);
            }else if (params[0] == "connect")
                Toast.makeText(getApplicationContext(), "Connexion...", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(getApplicationContext(), "Heureux de te revoir, " + Globals.getMembreConnecte().getPseudo() + " !", Toast.LENGTH_LONG).show();
                startActivity(IntentAccueil);
            }
        }

        @Override
        protected void onCancelled() {
            errorDialog.show("L'opération de connexion a été annulée.");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                publishProgress("connect");
                String url_find_membre = Globals.getApiUrl() + "/membres-fb/" + this.object.getString("id");
                StringRequest request_search = new StringRequest(Request.Method.GET, url_find_membre, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject objMembreFb = new JSONObject(response);
                            Membre leMembre = JsonConverter.convertMembre(objMembreFb, true);
                            Globals.setMembreConnecte(leMembre);
                            Globals.setMembreFbConnect(true);
                            new RecupererMatesAsynchTask(leMembre).execute();
                            publishProgress("connected");
                        } catch (JSONException ex) {
                            Log.e("jsonEx.membre", "erreur parse json: " + ex.getMessage());
                            publishProgress("err");
                        }catch (Exception ex){
                            publishProgress("err");
                        }
                    }
                }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.e("errReponseVolley: ", "err:" + error.getMessage());
                        publishProgress("err");
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Token token = confApi.getToken();
                        Globals.setTokenApi(token);
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Auth-Token", confApi.newToken(getApplicationContext()).getValue());
                        return headers;
                    }
                };
                requestQueue.add(request_search);
            } catch (JSONException ex) {
                Log.e("errJson:", "err: " + ex.getMessage());
                publishProgress("err");
            }catch (NullPointerException ex){
                Log.e("exception réseau: ", "err: " + ex.getMessage());
                publishProgress("err");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class RecupererMatesAsynchTask extends AsyncTask<Void, String, Void>{

        private Membre membreConnecte;
        private RecupererMatesAsynchTask(Membre leMembre){
            this.membreConnecte = leMembre;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url_mates = Globals.getApiUrl() + "/membres/" + membreConnecte.getId() + "/amis";
            StringRequest requestMate = new StringRequest(Request.Method.GET, url_mates, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Membre leMembre = Globals.getMembreConnecte();
                        JSONArray arrayMates = new JSONArray(response);
                        leMembre.setListeMates(JsonConverter.convertListeMates(arrayMates));
                        Globals.setMembreConnecte(leMembre);
                        Log.e("mate", arrayMates.toString());
                    }catch (JSONException ex){
                        Log.e("errParseMates", "err " + ex.getMessage());
                        publishProgress("err");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("errConnectListeAmis", error.getMessage());
                    publishProgress("err");
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Token token = confApi.getToken();
                    Globals.setTokenApi(token);
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Auth-Token", token.getValue());
                    return headers;
                }
            };
            requestMate.setShouldCache(false);
            requestQueue.add(requestMate);
            return null;
        }
    }

    private class ConnectMembre extends AsyncTask<Void, String, Void>{

        private String pseudoTv;
        private String passTv;
        private ImageView ivCharg;

        @Override
        protected void onPreExecute(){
            ivCharg = (ImageView) findViewById(R.id.ivChargLogin);
            Glide.with(activity).asGif().load(R.drawable.chargement).into(ivCharg);
            ivCharg.setVisibility(View.VISIBLE);
            rlLogin.setVisibility(View.INVISIBLE);
            EditText inputPseudo = (EditText) findViewById(R.id.inputPseudoInsc);
            EditText inputPass = (EditText) findViewById(R.id.inputPass);
            this.pseudoTv = inputPseudo.getText().toString();
            this.passTv = inputPass.getText().toString();
            if (pseudoTv.isEmpty() || passTv.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Le pseudo / mot de passe est vide", Toast.LENGTH_SHORT).show();
                this.cancel(false);
                rlLogin.setVisibility(View.VISIBLE);
                ivCharg.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        protected void onProgressUpdate(String... params){
            if (params[0] == "err"){
                errorDialog.show("Erreur de conversion");
                this.cancel(false);
            }else if (params[0] == "err_mdp"){
                Toast.makeText(getApplicationContext(), "Pseudo ou mot de passe incorrects", Toast.LENGTH_SHORT).show();
                rlLogin.setVisibility(View.VISIBLE);
                ivCharg.setVisibility(View.INVISIBLE);
                this.cancel(false);
            }else if (params[0] == "errCo"){
                errorDialog.show("Une erreur innatendue s'est produite. Veuillez réessayer plus tard.");
                rlLogin.setVisibility(View.VISIBLE);
                ivCharg.setVisibility(View.INVISIBLE);
                this.cancel(false);
            }else{
                Toast.makeText(getApplicationContext(), "Connexion...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Void doInBackground(Void... params){
            try {
                tokenApi = confApi.getToken();
                publishProgress("connect");
                IntentAccueil.putExtra("menuItem", "accueil");
                requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.start();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject membreAConnecter = new JSONObject(response);
                                    Membre membreConnecte = JsonConverter.convertMembre(membreAConnecter, false);
                                    Globals.setMembreConnecte(membreConnecte);
                                    Globals.setDptSelect(membreConnecte.getDepartement());
                                    new RecupererMatesAsynchTask(membreConnecte).execute();
                                    startActivity(IntentAccueil);
                                } catch (JSONException ex) {
                                    //textView.setText("Une erreur s'est produite.");
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            if (error.networkResponse.statusCode == 403) {
                                publishProgress("err_mdp");
                            }
                        }else {
                            publishProgress("errCo");
                        }
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
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
            }catch (Exception ex){
                Toast.makeText(getApplicationContext(), "Une erreur innatendue s'est produite...", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    // Clic sur le bouton s'inscrire
    public void ClicBoutonInscription(View view){
        Intent intentInscription = new Intent(this, InscriptionActivity.class);
        startActivity(intentInscription);
    }

    // Clic sur le bouton connexion
    public void ClicBoutonConnexion(View view){
        new ConnectMembre().execute();
    }

    public void onTerminate(){
        requestQueue.stop();
    }


}
