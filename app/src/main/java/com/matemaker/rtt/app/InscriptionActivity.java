package com.matemaker.rtt.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.matemaker.rtt.app.Classes.Api.ConfApi;
import com.matemaker.rtt.app.Classes.Api.JsonConverter;
import com.matemaker.rtt.app.Classes.Api.Token;
import com.matemaker.rtt.app.Classes.ErrorDialog;
import com.matemaker.rtt.app.Classes.Globals;
import com.matemaker.rtt.app.Classes.Membre;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InscriptionActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    final String url = Globals.getApiUrl() + "/membres";
    ConfApi confApi;
    Activity activity;
    Token tokenApi;
    EditText inputPseudo;
    EditText inputMail;
    EditText inputTel;
    EditText inputPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        confApi = new ConfApi();
        confApi.newToken(getApplicationContext());

        activity = this;

        TelephonyManager tel=(TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        inputPseudo = (EditText) findViewById(R.id.inputPseudoInsc);
        inputMail = (EditText) findViewById(R.id.inputMail);
        inputTel = (EditText) findViewById(R.id.inputPhone);
        inputPass = (EditText) findViewById(R.id.inputPassword);

        //inputTel.setText(tel.getLine1Number());
        /*SmsReceiver receiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver, filter);*/

    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), LoginnActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    public void ClicBtnInscription(View view){
        final String code = Globals.generate(5);
        final String pseudo, mail, tel, pass;
        pseudo = inputPseudo.getText().toString();
        mail = inputMail.getText().toString();
        tel = inputTel.getText().toString();
        pass = inputPass.getText().toString();
        SmsManager.getDefault().sendTextMessage(tel, null, "Votre code de validation: " + code, null, null);
        final Dialog dialog = new Dialog(this);
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
                    new InscriptionCompteRTTAsynchTask(pseudo, mail, tel, pass).execute();
                }else {
                    //ErrorDialog.showWithActivity(activity, "code: " + code + "saisi: " + code_saisi);
                    tvErr.setVisibility(View.VISIBLE);
                }
            }
        });
        //RelativeLayout rlInsc = (RelativeLayout) findViewById(R.id.rlInscription);
        // Récupération des saisies utilisateur
        //
    }

    private class InscriptionCompteRTTAsynchTask extends AsyncTask<Void, String, Void>{

        String pseudo, mail, tel, pass;
        private InscriptionCompteRTTAsynchTask(String pse, String maile, String tele, String pas){
            this.pseudo = pse;
            this.mail = maile;
            this.tel = tele;
            this.pass = pas;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            final TextView tvReponse = (TextView) findViewById(R.id.textViewReponse);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values[0] == "err"){
                ErrorDialog.showWithActivity(activity, "Une erreur s'est produite. Vérifiez votre connexion à internet et réessayez.");
            }else if (values[0] == "add"){
                Toast.makeText(getApplicationContext(),"Bienvenue sur RTT, " + Globals.getMembreConnecte().getPseudo() + " !",Toast.LENGTH_LONG).show();
                final Intent IntentAccueil = new Intent(activity, MenuActivity.class);
                IntentAccueil.putExtra("menuItem", "accueil");
                startActivity(IntentAccueil);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Création de la requete HTTP avec volley
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.start();
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject membreJson = new JSONObject(response);
                        Membre leMembreInscrit = JsonConverter.convertMembre(membreJson, true);
                        Globals.setMembreConnecte(leMembreInscrit);
                        publishProgress("add");
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
                protected Map<String, String> getParams() {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("membrePseudo", pseudo);
                    params.put("membrePass", pass);
                    params.put("membreTel", tel);
                    params.put("membreMail", mail);
                    params.put("membreValidation", "1");
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
