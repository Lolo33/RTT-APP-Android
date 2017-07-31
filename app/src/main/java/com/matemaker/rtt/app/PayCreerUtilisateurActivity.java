package com.matemaker.rtt.app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.matemaker.rtt.app.Classes.Api.JsonConverter;
import com.matemaker.rtt.app.Classes.ErrorDialog;
import com.matemaker.rtt.app.Classes.Evenement;
import com.matemaker.rtt.app.Classes.EvenementPrive;
import com.matemaker.rtt.app.Classes.Globals;
import com.matemaker.rtt.app.Classes.InfosMango;
import com.mangopay.MangoPayApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PayCreerUtilisateurActivity extends AppCompatActivity {

    private MangoPayApi api;
    private TextView tvTitre;
    private RequestQueue requestQueue;
    private ErrorDialog errorDialog;
    private Evenement lEvenement;
    private EvenementPrive lEventPrive;
    private Activity activity;
    private ImageView ivCharg;
    private boolean is_prive;
    private RelativeLayout rlUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pay_creer_utilisateur);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvTitre = (TextView) findViewById(R.id.tvTitreUserMango);
        is_prive = getIntent().getBooleanExtra("prive", false);
        if (is_prive){
            lEventPrive = (EvenementPrive) getIntent().getSerializableExtra("Evenement");
        }else{
            lEvenement = (Evenement) getIntent().getSerializableExtra("Evenement");
        }
        if (Globals.getMembreConnecte().getListeInfosMango().size() > 0){
            Intent i = new Intent(this, PayCarteActivity.class);
            i.putExtra("idMango", Globals.getMembreConnecte().getListeInfosMango().get(0).getMangoUserId());
            i.putExtra("prive", is_prive);
            if (is_prive)
                i.putExtra("Evenement", lEventPrive);
            else
                i.putExtra("Evenement", lEvenement);
            startActivity(i);
        }

        rlUser = (RelativeLayout) findViewById(R.id.rlPayUser);
        ivCharg = (ImageView) findViewById(R.id.ivChargUserM);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.start();

        errorDialog = new ErrorDialog(this);

        activity = this;

    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent intentListe = new Intent(this, EvenementMatchActivity.class);
        if (!is_prive) {
            intentListe.putExtra("Evenement", lEvenement);
        }else {
            intentListe.putExtra("Evenement", lEventPrive);
        }
        intentListe.putExtra("from", "mes_tournois");
        intentListe.putExtra("prive", is_prive);
        startActivity(intentListe);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentListe = new Intent(this, EvenementMatchActivity.class);
        if (!is_prive) {
            intentListe.putExtra("Evenement", lEvenement);
        }else {
            intentListe.putExtra("Evenement", lEventPrive);
        }
        intentListe.putExtra("from", "mes_tournois");
        intentListe.putExtra("prive", is_prive);
        startActivity(intentListe);
    }

    public void ClicBoutonValider(View v){
        new AjouterMembreAsynchTask().execute();
    }

    /*private class TrouverImMembreAsynchTask extends AsyncTask<Void, String, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            String url = "";
            return null;
        }
    }*/

    private class AjouterMembreAsynchTask extends AsyncTask<Void, String, Void>{
        private String nom, prenom, mail;
        private int jour, mois, annee;
        private String userMangoId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            EditText inputNom = (EditText) findViewById(R.id.inputNomMango);
            EditText inputPrenom = (EditText) findViewById(R.id.inputPrenomMango);
            EditText inputMail = (EditText) findViewById(R.id.inputMailMango);
            EditText inputDDN = (EditText) findViewById(R.id.inputDDNMango);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            prenom = inputPrenom.getText().toString();
            mail = inputMail.getText().toString();
            String ddn = inputDDN.getText().toString();
            String ddn_tab[];
            if (ddn.split("-").length == 3 || ddn.split("/").length == 3) {
                if (ddn.split("-").length == 3)
                    ddn_tab = ddn.split("-");
                else
                    ddn_tab = ddn.split("/");
                try {
                    if (ddn_tab[0].length() == 2 && ddn_tab[1].length() == 2 && ddn_tab[2].length() == 4){
                        jour = Integer.valueOf(ddn_tab[0]);
                        mois = Integer.valueOf(ddn_tab[1]);
                        annee = Integer.valueOf(ddn_tab[2]);
                        //Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_rotate);
                        //rotation.setRepeatCount(Animation.INFINITE);
                        //ivCharg.setAnimation(rotation);
                        //ivCharg.setVisibility(View.VISIBLE);
                        Glide.with(activity).asGif().load(R.drawable.chargement).into(ivCharg);
                        rlUser.setVisibility(View.INVISIBLE);
                        nom = inputNom.getText().toString();
                    }else {
                        errorDialog.show("Le format de la date de naissance n'est pas correct," +
                                "Votre date de naissance doit être écrite sous la forme \"JJ/MM/AAAA\" ou \"JJ-MM-AAAA\"." +
                                "Veuillez réessayer.");
                    }
                }catch (Exception ex){
                    errorDialog.show("Votre date de naissance n'a pas pu etre converie, veillez à ne saisir que des chiffres séparés par un \"/\" ou un \"-\"");
                }
            }else{
                Log.e("errFormatDate", "Erreur du format de date de naissance");
                errorDialog.show("Le format de la date de naissance n'est pas correct, veuillez réessayer.");
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values[0].equals("err")){
                Toast.makeText(getApplicationContext(), "Une erreur est survenue pendant l'ajout de votre compte. Veuillez réessayer !", Toast.LENGTH_LONG).show();
                ivCharg.clearAnimation();
                ivCharg.setVisibility(View.INVISIBLE);
                rlUser.setVisibility(View.VISIBLE);
                this.cancel(false);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url = Globals.getRttUrl() + "/pay/add_user.php";
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        final JSONObject obj = new JSONObject(response);
                        String url_im = Globals.getApiUrl() + "/membres/" + Globals.getMembreConnecte().getId() + "/infos-mango";
                        StringRequest requestIm = new StringRequest(Request.Method.POST, url_im, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    // Ajout du nouveau jeu d'informations Mangopay au membre actuel
                                    JSONObject objIm = new JSONObject(response);
                                    InfosMango im = JsonConverter.convertIm(objIm);
                                    ArrayList<InfosMango> newListeIm = Globals.getMembreConnecte().getListeInfosMango();
                                    newListeIm.add(im);
                                    userMangoId = obj.getString("Id");
                                    Globals.getMembreConnecte().setListeInfosMango(newListeIm);
                                }catch (JSONException ex){
                                    publishProgress("err");
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                publishProgress("err");
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("X-Auth-Token", Globals.getTokenApi().getValue());
                                return headers;
                            }
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                try {
                                    params.put("imMangoId", obj.getString("Id"));
                                    params.put("imWalletId", obj.getString("WalletId"));
                                }catch (JSONException ex){
                                    publishProgress("err");
                                }
                                return params;
                            }
                        };
                        requestQueue.add(requestIm);
                    }catch (JSONException ex){
                        publishProgress("err");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("errRep", "e: " + error.getMessage());
                    publishProgress("err");
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("nom", nom);
                    params.put("prenom", prenom);
                    params.put("mail", mail);
                    params.put("jour", String.valueOf(jour));
                    params.put("mois", String.valueOf(mois));
                    params.put("annee", String.valueOf(annee));
                    params.put("residence", "FR");
                    params.put("nat", "FR");
                    params.put("from", "android");
                    params.put("idMembre", String.valueOf(Globals.getMembreConnecte().getId()));
                    return params;
                }
            };
            requestQueue.add(request);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //ivCharg.clearAnimation();
            Toast.makeText(getApplicationContext(), "Votre compte à bien été ajouté", Toast.LENGTH_LONG).show();
            Intent i = new Intent(activity, PayCarteActivity.class);
            i.putExtra("idMango", userMangoId);
            i.putExtra("Evenement", lEvenement);
            startActivity(i);
        }
    }

}
