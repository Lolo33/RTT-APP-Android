package com.matemaker.rtt.app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.matemaker.rtt.app.Classes.ErrorDialog;
import com.matemaker.rtt.app.Classes.Evenement;
import com.matemaker.rtt.app.Classes.EvenementPrive;
import com.matemaker.rtt.app.Classes.Globals;
import com.mangopay.android.sdk.Callback;
import com.mangopay.android.sdk.MangoPay;
import com.mangopay.android.sdk.model.CardRegistration;
import com.mangopay.android.sdk.model.MangoCard;
import com.mangopay.android.sdk.model.MangoSettings;
import com.mangopay.android.sdk.model.exception.MangoException;
import com.mangopay.android.sdk.util.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class PayCarteActivity extends AppCompatActivity {

    private String idMango;
    private Evenement lEvenement;
    private EvenementPrive lEventPrive;
    private RequestQueue requestQueue;
    private static final String TAG = PayCarteActivity.class.getSimpleName();
    private Activity activity;
    private ImageView ivChargement;
    private RelativeLayout rlPay;
    private Animation rotation;
    private ErrorDialog errorDialog;
    private boolean is_prive;
    private String prix;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_carte);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        idMango = getIntent().getStringExtra("idMango");
        is_prive = getIntent().getBooleanExtra("prive", false);
        if (is_prive)
            lEventPrive = (EvenementPrive) getIntent().getSerializableExtra("Evenement");
        else
            lEvenement = (Evenement) getIntent().getSerializableExtra("Evenement");
        Button btnPayer = (Button) findViewById(R.id.btnPayer);
        activity = this;
        rlPay = (RelativeLayout) findViewById(R.id.rlFormPay);
        ivChargement = (ImageView) findViewById(R.id.ivChargPay);
        String s;
        if (is_prive)
            s=Float.valueOf(lEventPrive.getTarif() / lEventPrive.getNbJoueursMax()).toString();
        else
            s=Float.valueOf(lEvenement.getTarif() / lEvenement.getNbJoueursMin()).toString();
        prix = s.substring(0,s.indexOf(".")+2);
        errorDialog = new ErrorDialog(this);

        //ivChargement.setVisibility(View.INVISIBLE);
        btnPayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                getCardRegistration();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentListe = new Intent(this, EvenementMatchActivity.class);
        if (is_prive)
            intentListe.putExtra("Evenement", lEventPrive);
        else
            intentListe.putExtra("Evenement", lEvenement);
        intentListe.putExtra("prive", is_prive);
        intentListe.putExtra("from", "mes_tournois");
        startActivity(intentListe);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent intentListe = new Intent(this, EvenementMatchActivity.class);
        if (is_prive)
            intentListe.putExtra("Evenement", lEventPrive);
        else
            intentListe.putExtra("Evenement", lEvenement);
        intentListe.putExtra("from", "mes_tournois");
        intentListe.putExtra("prive", is_prive);
        startActivity(intentListe);
        return true;
    }

    /*
    * Get card pre-registration data needed for card registration
    * */
    private void getCardRegistration() {
        new AsyncTask<Void, String, String>() {

            private String codeCarte;
            private String dateExpir;
            private String codeCVC;

            @Override
            protected void onPreExecute() {
                EditText inputCodeCarte = (EditText) findViewById(R.id.inputCodeCarte);
                EditText inputDateCarte = (EditText) findViewById(R.id.inputDateCarte);
                EditText inputCVC = (EditText) findViewById(R.id.inputCVC);
                this.codeCarte = inputCodeCarte.getText().toString();
                this.codeCVC = inputCVC.getText().toString();
                this.dateExpir = inputDateCarte.getText().toString();
                if (codeCarte == "null" || codeCarte == "" || codeCarte == null || codeCVC == "" || codeCVC == "null" || codeCVC == null || dateExpir == "" || dateExpir == "null" || dateExpir == null){
                    errorDialog.show("Vous devez remplir tous les champs");
                }else {
                    if (codeCarte.length() == 16) {
                        if (dateExpir.length() == 4){
                            if (codeCVC.length() == 3){
                                try {
                                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                    //Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_rotate);
                                    //rotation.setRepeatCount(Animation.INFINITE);
                                    //ivChargement.setAnimation(rotation);
                                    Glide.with(activity).asGif().load(R.drawable.chargement).into(ivChargement);
                                    ivChargement.setVisibility(View.VISIBLE);
                                    rlPay.setVisibility(View.INVISIBLE);
                                }catch (Exception ex){
                                    errorDialog.show("Vos codes de carte ne peuvent contenir que des chiffres." + ex.getMessage());
                                    this.cancel(false);
                                }
                            }else {
                                errorDialog.show("Le cryptogramme visuel doit être composé de 3 chiffres");
                                this.cancel(false);
                            }
                        }else {
                            errorDialog.show("La date d'expiration doit etre ccmposé de 4 chiffres");
                            this.cancel(false);
                        }
                    }else {
                        errorDialog.show("Le code de la carte doit être composé de 16 chiffres");
                        this.cancel(false);
                    }
                }
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
            }

            @Override protected String doInBackground(Void... voids) {
                try {
                    String url = Globals.getRttUrl() + "/pay/reg_card.php?id=" + idMango; //+ Globals.getMembreConnecte().getId();

                    URL obj = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

                    // optional default is GET
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Accept", "application/json");

                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        connection.disconnect();
                        return response.toString();
                    } else {
                        connection.disconnect();
                        return "";
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
                return null;
            }

            @Override protected void onPostExecute(String response) {
                super.onPostExecute(response);
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject object = new JSONObject(response);
                        String accessKey = JsonUtil.getValue(object, "accessKey");
                        String baseURL = JsonUtil.getValue(object, "baseURL");
                        String cardPreregistrationId = JsonUtil.getValue(object, "cardPreregistrationId");
                        final String cardRegistrationURL = JsonUtil.getValue(object, "cardRegistrationURL");
                        String clientId = JsonUtil.getValue(object, "clientId");
                        String preregistrationData = JsonUtil.getValue(object, "preregistrationData");

                        MangoSettings mSettings = new MangoSettings(baseURL, clientId, cardPreregistrationId,
                                cardRegistrationURL, preregistrationData, accessKey);
                        final MangoCard mCard = new MangoCard(codeCarte, dateExpir, codeCVC);

                        MangoPay mangopay = new MangoPay(PayCarteActivity.this, mSettings);

                        mangopay.registerCard(mCard, new Callback() {
                            @Override public void success(final CardRegistration cardRegistration) {
                                Log.e(PayCarteActivity.class.getSimpleName(), cardRegistration.toString());
                                requestQueue = Volley.newRequestQueue(getApplicationContext());
                                requestQueue.start();
                                String url = Globals.getRttUrl() + "/pay/preAuth.php";
                                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject obj = new JSONObject(response);
                                            if (obj.getString("Status").equals("SUCCEEDED")){
                                                Toast.makeText(getApplicationContext(), "Votre paiement a bien été enregistré", Toast.LENGTH_LONG).show();
                                                Intent i = new Intent(activity, EvenementMatchActivity.class);
                                                if (is_prive)
                                                    i.putExtra("Evenement", lEventPrive);
                                                else
                                                    i.putExtra("Evenement", lEvenement);
                                                i.putExtra("from", "mes_tournois");
                                                i.putExtra("prive", is_prive);
                                                startActivity(i);
                                            }else{
                                                errorDialog.show("Une erreur est survenue pendant le paiement, vérifiez vos informations de carte bancaire");
                                            }
                                            //ivChargement.clearAnimation();
                                            ivChargement.setVisibility(View.INVISIBLE);
                                            rlPay.setVisibility(View.VISIBLE);
                                        }catch (JSONException ex){
                                            errorDialog.show("errjson" + ex.getMessage());
                                            ivChargement.clearAnimation();
                                            ivChargement.setVisibility(View.INVISIBLE);
                                            rlPay.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(), "Une erreur s'est produite: " + error.getMessage(), Toast.LENGTH_LONG).show();
                                        ivChargement.clearAnimation();
                                        ivChargement.setVisibility(View.INVISIBLE);
                                        rlPay.setVisibility(View.VISIBLE);
                                    }
                                }){
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("walletId", lEvenement.getInfosMango().getMangoWalletId());
                                        params.put("cardId", cardRegistration.getCardId());
                                        params.put("authorId", cardRegistration.getUserId());
                                        params.put("userId", String.valueOf(Globals.getMembreConnecte().getId()));
                                        params.put("teamId", String.valueOf(lEvenement.getEquipeJoueur(Globals.getMembreConnecte()).getId()));
                                        params.put("amount", prix);
                                        return params;
                                    }
                                };
                                requestQueue.add(request);
                            }

                            @Override public void failure(MangoException error) {
                                Toast.makeText(PayCarteActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                ivChargement.clearAnimation();
                                ivChargement.setVisibility(View.INVISIBLE);
                                rlPay.setVisibility(View.VISIBLE);
                            }
                        });

            /*
            MangoPayBuilder builder = new MangoPayBuilder(MainActivity.this);
            builder.baseURL(baseURL)
                    .clientId(clientId).accessKey(accessKey)
                    .cardRegistrationURL(cardRegistrationURL)
                    .preregistrationData(preregistrationData)
                    .cardPreregistrationId(cardPreregistrationId)
                    .cardNumber("3569990000000157")
                     //.cardExpirationDate("0920")
                    .cardExpirationYear(2017)
                    .cardExpirationMonth(2)
                    .cardCvx("123")
                    .logLevel(LogLevel.FULL)
                    .callback(new Callback() {
                      @Override public void success(CardRegistration cardRegistration) {
                        Log.d(MainActivity.class.getSimpleName(), cardRegistration.toString());
                      }
                      @Override public void failure(MangoException error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                      }
                    }).start();
              */

                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

        }.execute();
    }
}