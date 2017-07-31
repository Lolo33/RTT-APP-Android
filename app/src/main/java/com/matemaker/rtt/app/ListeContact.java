package com.matemaker.rtt.app;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.matemaker.rtt.app.Classes.Adapter.ListeTelephoneContactAdapter;
import com.matemaker.rtt.app.Classes.Api.JsonConverter;
import com.matemaker.rtt.app.Classes.Api.Token;
import com.matemaker.rtt.app.Classes.CreneauxJoueurs;
import com.matemaker.rtt.app.Classes.ErrorDialog;
import com.matemaker.rtt.app.Classes.Evenement;
import com.matemaker.rtt.app.Classes.EvenementPrive;
import com.matemaker.rtt.app.Classes.Globals;
import com.matemaker.rtt.app.Classes.Membre;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListeContact extends AppCompatActivity {
    //tableau dans lequel je range mes contacts
    private ArrayList Mescontacts;
    static final int PICK_CONTACT_REQUEST = 1;  // The request code
    private EvenementPrive lEventPrive = null;
    private Evenement lEvenement = null;
    private ArrayList<ArrayList<String>> listeContactsPhoneSelected;
    private Button btnInvite;
    private RequestQueue requestQueue;
    private Activity activity;
    private ListeTelephoneContactAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_contact);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.start();

        activity = this;

        listeContactsPhoneSelected = new ArrayList<>();

        final ListView list = (ListView) findViewById(R.id.listContacts);
        final ArrayList<ArrayList<String>> contacts = retrieveContacts(getContentResolver());

        lEventPrive = (EvenementPrive) getIntent().getSerializableExtra("lEvenement");

        if (contacts != null)
        {
            adapter = new ListeTelephoneContactAdapter(getApplicationContext(), R.layout.liste_custom_contacts);
            list.setAdapter(adapter);
            for (int i=0;i<contacts.size();i++){
                if (contacts.get(i).get(1).length() > 5)
                adapter.add(contacts.get(i));
                Log.e("phone: ", "nom: " + contacts.get(i).get(0) + " num: " + contacts.get(i).get(1));
            }
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ArrayList<String> liste = (ArrayList<String>)  list.getItemAtPosition(position);
                    if (liste.get(2) == "selected"){
                        liste.set(2, "no-selected");
                        listeContactsPhoneSelected.remove(listeContactsPhoneSelected.indexOf(liste));
                    }else{
                        liste.set(2, "selected");
                        listeContactsPhoneSelected.add(liste);
                    }
                    adapter.notifyDataSetChanged();
                }
            });
            btnInvite = (Button) findViewById(R.id.btnInviterSMS);
            btnInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CreneauxJoueurs creneauInvite = new CreneauxJoueurs();
                    creneauInvite.setDateDebut(new Date(2016,10,10,10,10,10));
                    creneauInvite.setDateFin(new Date(2016,10,10,10,10,10));
                    creneauInvite.setInvite(true);
                    new InviterAmisAsynchTask(creneauInvite).execute();
                }
            });


        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private class InviterAmisAsynchTask extends AsyncTask<Void, String, Void>{

        CreneauxJoueurs leCreneau;
        Membre leMembre;
        private int j =0;

        private InviterAmisAsynchTask(CreneauxJoueurs creneauxJoueurs){this.leCreneau = creneauxJoueurs;}

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values[0] == "err"){
                ErrorDialog.showWithActivity(activity, "Une erreur est survenue pendant l'invitation, Vériifier votre connexion à internet et réessayez");
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (j = 0;j < listeContactsPhoneSelected.size(); j++) {
                SmsManager.getDefault().sendTextMessage(listeContactsPhoneSelected.get(j).get(1), null, "Inscris-toi sur RTT pour rejoindre mon Evenement ! https://play.google.com/store/apps/details?id=com.matemaker.rtt.app", null, null);
                final String strNum = Globals.replace(listeContactsPhoneSelected.get(j).get(1), " ", "");
                Log.e("strNum: ", ":" + strNum);
                final String pseudo = listeContactsPhoneSelected.get(j).get(0);
                String url = Globals.getApiUrl() + "/membres-tel";
                StringRequest requestMembre = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            leMembre = JsonConverter.convertMembre(new JSONObject(response), false);
                            new GenererCreneauInviteAsynchTask(leCreneau, leMembre).execute();
                        } catch (JSONException ex) {
                            Log.e("errJsonMembreTel", "err: " + ex.getMessage());
                            publishProgress("err");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse.statusCode == 404) {
                            String url_add_membre = Globals.getApiUrl() + "/membres";
                            StringRequest requestAddMembre = new StringRequest(Request.Method.POST, url_add_membre, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        leMembre = JsonConverter.convertMembre(new JSONObject(response), false);
                                        new GenererCreneauInviteAsynchTask(leCreneau, leMembre).execute();
                                    }catch (JSONException ex){
                                        Log.e("errJsonAddMembreInvite", "err: " + ex.getMessage());
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("errResAddMembreInvite", "err: " + error.getMessage());
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("membrePseudo", pseudo + Globals.generate(5));
                                    params.put("membreTel", strNum);
                                    params.put("membreValidation", "0");
                                    params.put("membrePass", "AjoutéParTel");
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
                            requestQueue.add(requestAddMembre);
                        } else {
                            publishProgress("err");
                        }
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("membreTel", strNum);
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
                requestQueue.add(requestMembre);

            }
            return null;
        }
    }

    private class GenererCreneauInviteAsynchTask extends AsyncTask<Void, String, Void>{

        CreneauxJoueurs leCreneau;
        Membre leMembre;

        private GenererCreneauInviteAsynchTask(CreneauxJoueurs creneauxJoueurs, Membre membre){
            this.leCreneau = creneauxJoueurs;
            this.leMembre = membre;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values[0] == "err"){
                ErrorDialog.showWithActivity(activity, "Une erreur est survenue pendant l'invitation, vérifiez votre connexion à internet et réessayez !");
            } else if (values[0] == "invite"){
                Toast.makeText(getApplicationContext(), "Vous avez bien invité vos amis ! ", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(activity, EvenementMatchActivity.class);
                i.putExtra("Evenement", lEventPrive);
                i.putExtra("from", getIntent().getStringExtra("from"));
                i.putExtra("prive", true);
                startActivity(i);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
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
                    params.put("idMembre", String.valueOf(leMembre.getId()));
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
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(this, EvenementMatchActivity.class);
        i.putExtra("Evenement", lEventPrive);
        i.putExtra("from", getIntent().getStringExtra("from"));
        i.putExtra("prive", true);
        startActivity(i);
        return true;
    }

    private String getContactPhone(String contactID) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                ContactsContract.CommonDataKinds.Event.START_DATE,
        };
        String where = ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?";
        String[] selectionArgs = new String[] { contactID };
        String sortOrder = null;
        Cursor result = managedQuery(uri, projection, where, selectionArgs, sortOrder);
        if (result.moveToFirst()) {
            String phone = result.getString(result.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (phone == null) {
                result.close();
                return null;
            }
            result.close();
            return phone;
        }
        result.close();
        return null;
    }



    private ArrayList<ArrayList<String>> retrieveContacts(ContentResolver contentResolver) {
        ArrayList<ArrayList<String>> listeContactsPhone = new ArrayList<>();
        final Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, new String[] { ContactsContract.Data.DISPLAY_NAME, ContactsContract.Data._ID, ContactsContract.Contacts.HAS_PHONE_NUMBER }, null, null, null);

        if (cursor == null)
        {
            Log.e("retrieveContacts", "Cannot retrieve the contacts");
            return null;
        }

        if (cursor.moveToFirst())
        {
            do
            {
                final long id = Long.parseLong(cursor.getString(cursor.getColumnIndex(ContactsContract.Data._ID)));
                final String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                final int hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Data.HAS_PHONE_NUMBER));
                final String numTelephone = getContactPhone(String.valueOf(id));

                if (hasPhoneNumber > 0)
                {
                    ArrayList<String> contactPhone = new ArrayList<>();
                    contactPhone.add(name);
                    contactPhone.add(numTelephone);
                    contactPhone.add("no-selected");
                    listeContactsPhone.add(contactPhone);
                }


            }
            while (cursor.moveToNext());
        }

        if (!cursor.isClosed())
        {
            cursor.close();
        }

        Collections.sort(listeContactsPhone, new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> a, ArrayList<String> b) {
                return a.get(0).compareTo(b.get(0));
            }
        });

        return listeContactsPhone;
    }
}
