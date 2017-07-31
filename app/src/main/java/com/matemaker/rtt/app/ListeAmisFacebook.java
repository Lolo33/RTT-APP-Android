package com.matemaker.rtt.app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.matemaker.rtt.app.Classes.Adapter.InfosFbAdapter;
import com.matemaker.rtt.app.Classes.Api.JsonConverter;
import com.matemaker.rtt.app.Classes.Api.Token;
import com.matemaker.rtt.app.Classes.Creneaux;
import com.matemaker.rtt.app.Classes.CreneauxJoueurs;
import com.matemaker.rtt.app.Classes.ErrorDialog;
import com.matemaker.rtt.app.Classes.EvenementPrive;
import com.matemaker.rtt.app.Classes.Globals;
import com.matemaker.rtt.app.LoginnActivity;
import com.matemaker.rtt.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ListeAmisFacebook extends AppCompatActivity {

    private ArrayList<ArrayList<String>> amisFbSelected;
    private ListView lvAmisFb;
    private InfosFbAdapter adapter;
    private EvenementPrive lEventPrive;
    private RequestQueue requestQueue;
    ArrayList<ArrayList<String>> listeInfosFb;
    Activity activity;
    ErrorDialog errorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_amis_facebook);

        errorDialog = new ErrorDialog(this);
        requestQueue = Volley.newRequestQueue(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity = this;

        lEventPrive = (EvenementPrive) getIntent().getSerializableExtra("Evenement");
        listeInfosFb = new ArrayList<>();
        amisFbSelected = new ArrayList<>();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        lvAmisFb = (ListView) findViewById(R.id.lvAmiFb);
        adapter = new InfosFbAdapter(getApplicationContext(), R.layout.liste_custom_amis_fb);
        GraphRequest request = GraphRequest.newMyFriendsRequest(
                accessToken,
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(
                            final JSONArray array,
                            GraphResponse response) {
                        for (int i=0; i<array.length();i++) {
                            try {
                                ArrayList<String> infosFb = new ArrayList<String>();
                                infosFb.add(array.getJSONObject(i).getString("name"));
                                infosFb.add(array.getJSONObject(i).getString("id"));
                                infosFb.add("no-selected");
                                listeInfosFb.add(infosFb);
                                adapter.add(infosFb);
                            }catch (JSONException ex){
                                Log.e("errFBJSONAmi", "err: " + ex.getMessage());
                            }
                        }
                        lvAmisFb.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                ArrayList<String> liste = adapter.getItem(position);
                                if (liste.get(2) == "selected"){
                                    adapter.getItem(position).set(2, "no-selected");
                                    amisFbSelected.remove(amisFbSelected.indexOf(liste));
                                }else{
                                    adapter.getItem(position).set(2, "selected");
                                    amisFbSelected.add(liste);
                                    Log.e("amiSelected: ", "ami: " + adapter.getItem(position));
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
        Bundle parameters = new Bundle();
        request.setParameters(parameters);
        request.executeAsync();
        Button btnInvite = (Button) findViewById(R.id.btnInviteAmiFb);
        lvAmisFb.setAdapter(adapter);
        for (int j=0;j<listeInfosFb.size();j++){
            Log.e("ami:", "ami: " + listeInfosFb.get(0).get(0));
        }
        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amisFbSelected.size() > 0){
                    Log.e("amiselect:", "ami: " + amisFbSelected.get(0).get(0));
                    new SendInvitAmiFbAsynchTask().execute();
                }else {
                    errorDialog.show("Vous devez sélectionner au moins un ami !");
                }
            }
        });
     }

     private class SendInvitAmiFbAsynchTask extends AsyncTask<Void, String, Void> {

         private CreneauxJoueurs leCreneau;

         @Override
         protected void onPreExecute() {
             super.onPreExecute();
             leCreneau = new CreneauxJoueurs();
             leCreneau.setInvite(true);
             leCreneau.setDateDebut(new Date(2016,10,10,10,10,10));
             leCreneau.setDateFin(new Date(2016,10,10,10,10,10));
         }

         @Override
         protected void onProgressUpdate(String... values) {
             super.onProgressUpdate(values);
             if (values[0] == "err"){
                errorDialog.show("Une erreur est survenue pendant l'invitation, veuillez vérifier votre connexion internet et réessayer.");
             }else if (values[0] == "invite"){
                 Toast.makeText(getApplicationContext(), "Vous avez bien invité vos amis !", Toast.LENGTH_SHORT).show();
                 Intent i = new Intent(activity, EvenementMatchActivity.class);
                 i.putExtra("from", "mes_tournois");
                 i.putExtra("Evenement", lEventPrive);
                 i.putExtra("prive", true);
                 startActivity(i);
             }
         }

         @Override
         protected Void doInBackground(Void... params) {
             for (int k=0;k<amisFbSelected.size(); k++) {
                 final String membre_fb_id = amisFbSelected.get(k).get(1);
                 String url_invite = Globals.getApiUrl() + "/lieux/" + lEventPrive.getLieu().getId() + "/event-prives/" + lEventPrive.getId() + "/creneaux-joueurs";
                 StringRequest request = new StringRequest(Request.Method.POST, url_invite, new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         try {
                             JSONObject objCreneau = new JSONObject(response);
                             CreneauxJoueurs leCreneau = JsonConverter.convertCreneauJoueur(objCreneau);
                             lEventPrive.getListeCreneaux().add(leCreneau);
                             publishProgress("invite");
                         } catch (JSONException ex) {
                             Log.e("exJsonInscPriv", "err: " + ex.getMessage());
                             publishProgress("err");
                         }
                     }
                 }, new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Log.e("errResponseInscPrive: ", "err: " + error.getMessage());
                         publishProgress("err");
                     }
                 }) {
                     @Override
                     protected Map<String, String> getParams() {
                         Map<String, String> params = new HashMap<String, String>();
                         params.put("creneauDate", Globals.dateToString(leCreneau.getDateDebut()) + " " + Globals.timeToStringFormatBdd(leCreneau.getDateDebut()));
                         params.put("creneauDateFin", Globals.dateToString(leCreneau.getDateFin()) + " " + Globals.timeToStringFormatBdd(leCreneau.getDateFin()));
                         params.put("creneauAccepte", "false");
                         params.put("creneauInvite", String.valueOf(leCreneau.isInvite()));
                         params.put("idMembreFb", membre_fb_id);
                         //Log.e("fbId", membre_fb_id);
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
                 requestQueue.add(request);
             }
             return null;
         }
     }

}
