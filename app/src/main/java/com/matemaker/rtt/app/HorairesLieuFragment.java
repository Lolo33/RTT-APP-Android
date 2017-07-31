package com.matemaker.rtt.app;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.matemaker.rtt.app.Classes.Adapter.CreneauAdapter;
import com.matemaker.rtt.app.Classes.Adapter.LieuSpinnerAdapter;
import com.matemaker.rtt.app.Classes.Api.JsonConverter;
import com.matemaker.rtt.app.Classes.Creneaux;
import com.matemaker.rtt.app.Classes.CreneauxJoueurs;
import com.matemaker.rtt.app.Classes.ErrorDialog;
import com.matemaker.rtt.app.Classes.EvenementPrive;
import com.matemaker.rtt.app.Classes.Globals;
import com.matemaker.rtt.app.Classes.Lieu;
import com.matemaker.rtt.app.Classes.Mates;
import com.matemaker.rtt.app.Classes.Reservation;
import com.matemaker.rtt.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Niquelesstup on 17/07/2017.
 */

public class HorairesLieuFragment extends Fragment {

    View myView;
    Lieu leLieu;
    private Calendar ajd;
    private Button btnReserver;
    private TextView tvChargement;
    private Button btnOrga;
    private Spinner spinner;
    private String descriptif = "Pas de description";
    private ArrayList<String> listeCreneauxSelected;
    private ArrayList<Calendar> liste7ProchainsJours;
    private ArrayList<Lieu> listeLieux;
    private String nbJoueurs = "10";
    private ArrayList<TextView> listeTv7ProchainsJours;
    private ArrayList<ListView> listeListView;
    private String tarif = "null";
    private RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Définition des variables importantes
        ajd = Calendar.getInstance();
        myView = inflater.inflate(R.layout.horaires_fragment, container, false);
        tvChargement = (TextView) myView.findViewById(R.id.tvChargement);
        listeLieux = new ArrayList<>();
        listeListView = new ArrayList<>();
        listeTv7ProchainsJours = new ArrayList<>();
        listeCreneauxSelected = new ArrayList<>();
        btnReserver = (Button) myView.findViewById(R.id.btnReservHoraire);
        btnOrga = (Button) myView.findViewById(R.id.btnOrgaHor);
        // Ajoute les ListView des créneaux à la liste
        listeListView.add(0, (ListView) myView.findViewById(R.id.lvJour1));
        listeListView.add(1, (ListView) myView.findViewById(R.id.lvJour2));
        listeListView.add(2, (ListView) myView.findViewById(R.id.lvJour3));
        listeListView.add(3, (ListView) myView.findViewById(R.id.lvJour4));
        listeListView.add(4, (ListView) myView.findViewById(R.id.lvJour5));
        listeListView.add(5, (ListView) myView.findViewById(R.id.lvJour6));
        listeListView.add(6, (ListView) myView.findViewById(R.id.lvJour7));

        // Ajoute les Textviews des jours à la liste
        listeTv7ProchainsJours.add(0, (TextView) myView.findViewById(R.id.tvJour1));
        listeTv7ProchainsJours.add(1, (TextView) myView.findViewById(R.id.tvJour2));
        listeTv7ProchainsJours.add(2, (TextView) myView.findViewById(R.id.tvJour3));
        listeTv7ProchainsJours.add(3, (TextView) myView.findViewById(R.id.tvJour4));
        listeTv7ProchainsJours.add(4, (TextView) myView.findViewById(R.id.tvJour5));
        listeTv7ProchainsJours.add(5, (TextView) myView.findViewById(R.id.tvJour6));
        listeTv7ProchainsJours.add(6, (TextView) myView.findViewById(R.id.tvJour7));

        // Transmission des données des complexes proches à la liste déroulante
        for (int i = 0; i< Globals.getListeDpt().size(); i++)
            for (int j=0;j<Globals.getListeDpt().get(i).getListeLieux().size();j++)
                listeLieux.add(Globals.getListeDpt().get(i).getListeLieux().get(j));
        leLieu = new Lieu();
        leLieu.setId(1);
        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.start();
        Spinner spinListLieu = (Spinner) myView.findViewById(R.id.spinLieu);
        final LieuSpinnerAdapter adapterLieux = new LieuSpinnerAdapter(getActivity(), R.layout.liste_spinner_custom_lieux);
        spinListLieu.setAdapter(adapterLieux);
        adapterLieux.addAll(listeLieux);

        rafraichirListeHoraires();

        spinListLieu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                leLieu = (Lieu) adapterLieux.getItem(position);
                rafraichirListeHoraires();
                listeCreneauxSelected.clear();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnOrga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                organiserReserver(true);
            }
        });
        btnReserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                organiserReserver(false);
            }
        });

        return myView;
    }

    private void organiserReserver(boolean isOrga){
        if (isOrga)
            new AjoutEvenementPriveAsynchTask().execute();
        else
            new ReserverTerrainAsynchTask().execute();

    }

    public void rafraichirListeHoraires(){
        btnOrga.setEnabled(false);
        btnReserver.setEnabled(false);
        tvChargement.setVisibility(View.VISIBLE);
        liste7ProchainsJours = new ArrayList<>();
        for (int i=0; i<7;i++){
            Calendar jPlus1 = Calendar.getInstance();
            if (i > 0)
                jPlus1.set(Calendar.DATE, ajd.get(Calendar.DATE) + i);
            listeTv7ProchainsJours.get(i).setText(Globals.dateToStringFormatCourt(jPlus1.get(Calendar.DAY_OF_WEEK), jPlus1.get(Calendar.DATE)));
            liste7ProchainsJours.add(jPlus1);
        }
        for (int h=0;h<liste7ProchainsJours.size();h++)
            new RecupCreneauxAsynchTask(listeListView.get(h), liste7ProchainsJours.get(h)).execute();
    }

    private class ReserverTerrainAsynchTask extends AsyncTask<Void, String, Void>{

        private LinearLayout frameLayout;
        private ImageView ivCharg;
        private EvenementPrive event;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ivCharg = (ImageView) myView.findViewById(R.id.ivChargHoraires);
            frameLayout = (LinearLayout) myView.findViewById(R.id.llLvHoraires);
            Glide.with(getActivity()).asGif().load(R.drawable.chargement).into(ivCharg);
            ivCharg.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values[0] == "err"){
                ErrorDialog.showWithActivity(getActivity(), "Une erreur est survenue pendant l'ajout de votre évenement. Vérifiez votre connexion à internet.");
                ivCharg.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                this.cancel(false);
            }else {
                Toast.makeText(getActivity().getApplicationContext(), "Bravo! tu as bien réservé le terrain; Tu dois maintenant attendre la confirmation du complexe !", Toast.LENGTH_SHORT).show();
                ivCharg.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                FirebaseMessaging.getInstance().subscribeToTopic("match_prive-" + event.getId());
                Intent i = new Intent(getActivity(), EvenementMatchActivity.class);
                i.putExtra("Evenement", event);
                i.putExtra("prive", true);
                i.putExtra("from", "mes_tournois");
                i.putExtra("invite", true);
                startActivity(i);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            final String url_event = Globals.getApiUrl() + "/lieux/" + leLieu.getId() + "/event-prives";
            StringRequest request = new StringRequest(Request.Method.POST, url_event, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject objEvenement = new JSONObject(response);
                        event = JsonConverter.convertEvenementPrive(objEvenement, false);
                        String url_creneaux_joueurs = Globals.getApiUrl() + "/lieux/" + leLieu.getId() + "/event-prives/" + event.getId() + "/creneaux-joueurs";
                        final ArrayList<CreneauxJoueurs> listeCreneaux = new ArrayList<>();
                        /// AJOUT DES CRENEAUX
                            String[] date_tab = listeCreneauxSelected.get(0).split(" # ");
                            String[] hTab = date_tab[1].split(" - ");
                            String heure_debut = hTab[0].split("h")[0] + ":" + hTab[0].split("h")[1] + ":00";
                            String heure_fin = hTab[1].split("h")[0] + ":" + hTab[1].split("h")[1] + ":00";
                            final String date_debut = date_tab[0] + " " + heure_debut;
                            final String date_fin = date_tab[0] + " " + heure_fin;
                            StringRequest requestCreneaux = new StringRequest(Request.Method.POST, url_creneaux_joueurs, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        CreneauxJoueurs leCreneauReserve = JsonConverter.convertCreneauJoueur(new JSONObject(response));
                                        event.getListeCreneaux().add(leCreneauReserve);
                                        String url_reserv = Globals.getApiUrl() + "/event-prives/" + event.getId() + "/reservations";
                                        StringRequest req = new StringRequest(Request.Method.POST, url_reserv, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                //TODO Ajouter la réservation à l'évenement
                                                try {
                                                    Reservation resa = JsonConverter.convertReservation(new JSONObject(response), false);
                                                    event.setReservation(resa);
                                                    publishProgress("reserved");
                                                }catch (JSONException ex){
                                                    publishProgress("err");
                                                    Log.e("errReservTerrain", "err: " + ex.getMessage());
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                publishProgress("err");
                                            }
                                        }) {
                                            @Override
                                            public Map<String, String> getHeaders() throws AuthFailureError {
                                                Map<String, String> headers = new HashMap<String, String>();
                                                headers.put("X-Auth-Token", Globals.getTokenApi().getValue());
                                                return headers;
                                            }
                                        };
                                        requestQueue.add(req);
                                    }catch (JSONException ex){
                                        Log.e("JsonErrCrenau:", "err: " + ex.getMessage());
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    publishProgress("err", error.getMessage());
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("creneauDate", date_debut);
                                    params.put("creneauDateFin", date_fin);
                                    params.put("creneauAccepte", "true");
                                    params.put("creneauInvite", "false");
                                    params.put("idMembre", String.valueOf(Globals.getMembreConnecte().getId()));
                                    return params;
                                }

                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> headers = new HashMap<String, String>();
                                    headers.put("X-Auth-Token", Globals.getTokenApi().getValue());
                                    return headers;
                                }
                            };
                            requestQueue.add(requestCreneaux);
                    }catch(JSONException ex){
                        Log.e("errJsonAjoutEP:", "err" + ex.getMessage());
                        publishProgress("err", ex.getMessage());
                    }
                }

            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("errReponseEventPrive:", "err: " + error.getMessage());
                    publishProgress("err");
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("eventDescriptif", descriptif);
                    params.put("eventJMax", nbJoueurs);
                    params.put("eventTarif", tarif);
                    params.put("eventMembre", String.valueOf(Globals.getMembreConnecte().getId()));
                    return params;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Auth-Token", Globals.getTokenApi().getValue());
                    return headers;
                }
            };
            Log.e("tarif: ", "t: " + tarif);
            requestQueue.add(request);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class AjoutEvenementPriveAsynchTask extends AsyncTask<Void, String, Void> {

        private LinearLayout frameLayout;
        private ImageView ivCharg;
        private String accepted;
        private EvenementPrive event;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ivCharg = (ImageView) myView.findViewById(R.id.ivChargHoraires);
            frameLayout = (LinearLayout) myView.findViewById(R.id.llLvHoraires);
            Glide.with(getActivity()).asGif().load(R.drawable.chargement).into(ivCharg);
            ivCharg.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values[0] == "err"){
                ErrorDialog.showWithActivity(getActivity(), "Une erreur est survenue pendant l'ajout de votre évenement. Vérifiez votre connexion à internet." + values[1]);
                ivCharg.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                this.cancel(false);
            }else if (values[0] == "created"){
                Toast.makeText(getActivity().getApplicationContext(), "Bravo! tu as ajouté l'évenement", Toast.LENGTH_SHORT).show();
                ivCharg.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                Intent i = new Intent(getActivity(), EvenementMatchActivity.class);
                FirebaseMessaging.getInstance().subscribeToTopic("match_prive-" + event.getId());
                i.putExtra("Evenement", event);
                i.putExtra("prive", true);
                i.putExtra("from", "mes_tournois");
                i.putExtra("invite", true);
                startActivity(i);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            final String url_event = Globals.getApiUrl() + "/lieux/" + leLieu.getId() + "/event-prives";
            StringRequest request = new StringRequest(Request.Method.POST, url_event, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        final ArrayList<CreneauxJoueurs> creneauxJoueurs = new ArrayList<>();
                        JSONObject objEvenement = new JSONObject(response);
                        event = JsonConverter.convertEvenementPrive(objEvenement, false);
                        String url_creneaux_joueurs = Globals.getApiUrl() + "/lieux/" + leLieu.getId() + "/event-prives/" + event.getId() + "/creneaux-joueurs";
                        accepted = "false";
                        if (listeCreneauxSelected.size() == 1)
                            accepted = "true";
                        /// AJOUT DES CRENEAUX
                        for (int i = 0; i < listeCreneauxSelected.size(); i++) {
                            String[] date_tab = listeCreneauxSelected.get(i).split(" # ");
                            String[] hTab = date_tab[1].split(" - ");
                            String heure_debut = hTab[0].split("h")[0] + ":" + hTab[0].split("h")[1] + ":00";
                            String heure_fin = hTab[1].split("h")[0] + ":" + hTab[1].split("h")[1] + ":00";
                            final String date_debut = date_tab[0] + " " + heure_debut;
                            final String date_fin = date_tab[0] + " " + heure_fin;
                            final int compte = i;
                            StringRequest requestCreneaux = new StringRequest(Request.Method.POST, url_creneaux_joueurs, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        creneauxJoueurs.add(JsonConverter.convertCreneauJoueur(new JSONObject(response)));
                                        if (compte == listeCreneauxSelected.size() - 1) {
                                            publishProgress("created");
                                            event.setListeCreneaux(creneauxJoueurs);
                                        }
                                    }catch (JSONException ex){
                                        Log.e("errCreneauOrga", "err: " + ex.getMessage());
                                        publishProgress("err", ex.getMessage());
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    publishProgress("err", error.getMessage());
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("creneauDate", date_debut);
                                    params.put("creneauDateFin", date_fin);
                                    params.put("creneauAccepte", accepted);
                                    params.put("creneauInvite", "false");
                                    params.put("idMembre", String.valueOf(Globals.getMembreConnecte().getId()));
                                    return params;
                                }

                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> headers = new HashMap<String, String>();
                                    headers.put("X-Auth-Token", Globals.getTokenApi().getValue());
                                    return headers;
                                }
                            };
                            requestQueue.add(requestCreneaux);
                        }
                    }catch(JSONException ex){
                        Log.e("errJsonAjoutEP:", "err" + ex.getMessage());
                        publishProgress("err", ex.getMessage());
                    }
                }

            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("errReponseEventPrive:", "err: " + error.getMessage());
                    publishProgress("err");
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("eventDescriptif", descriptif);
                    params.put("eventJMax", nbJoueurs);
                    params.put("eventTarif", tarif);
                    params.put("eventMembre", String.valueOf(Globals.getMembreConnecte().getId()));
                    return params;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Auth-Token", Globals.getTokenApi().getValue());
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

    private class RecupCreneauxAsynchTask extends AsyncTask<Void, String, Void> {

        private ListView listView;
        private Calendar date;
        CreneauAdapter adapter;
        private RecupCreneauxAsynchTask(ListView lv, Calendar dte) {
            this.listView = lv;
            this.date = dte;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.adapter = new CreneauAdapter(getActivity().getApplicationContext(), R.layout.liste_custom_creneau);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ArrayList<String> listCreneau = adapter.getItem(position);
                    if (listCreneau.get(1) == "no-selected") {
                        adapter.getItem(position).set(1, "selected");
                        listeCreneauxSelected.add(adapter.getItem(position).get(2));
                        Log.e("cr:", adapter.getItem(position).get(2));
                    } else {
                        adapter.getItem(position).set(1, "no-selected");
                        listeCreneauxSelected.remove(listeCreneauxSelected.indexOf(adapter.getItem(position).get(2)));
                    }
                    adapter.notifyDataSetChanged();
                    if (listeCreneauxSelected.size() >= 1 && !listCreneau.get(0).equals("no")) {
                        if (listeCreneauxSelected.size() == 1) {
                            tarif = adapter.getItem(position).get(3);
                            btnReserver.setEnabled(true);
                        } else {
                            tarif = "null";
                            btnReserver.setEnabled(false);
                        }
                        btnOrga.setEnabled(true);
                    } else {
                        tarif = "null";
                        btnOrga.setEnabled(false);
                        btnReserver.setEnabled(false);
                    }
                }
            });
        }

        @Override
        protected void onProgressUpdate(String... creneaux) {
            ArrayList<String> listCreneaux = new ArrayList<String>();
            listCreneaux.add(creneaux[0]);
            listCreneaux.add("no-selected");
            String dateChk = Globals.stringCalendar(date.get(Calendar.DATE), date.get(Calendar.MONTH), date.get(Calendar.YEAR), "-");
            listCreneaux.add(dateChk + " # " + creneaux[0]);
            listCreneaux.add(creneaux[1]);
            adapter.add(listCreneaux);
        }

        @Override
        protected Void doInBackground(Void... params) {
            final String dateStringForRequest = Globals.stringCalendar(date.get(Calendar.DATE), date.get(Calendar.MONTH), date.get(Calendar.YEAR), "-");
            String url_dates = Globals.getApiUrl() + "/lieux/" + leLieu.getId() + "/creneaux/" + dateStringForRequest;
            StringRequest request = new StringRequest(Request.Method.GET, url_dates, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray arrayCreneaux = new JSONArray(response);
                        ArrayList<Creneaux> listeCreneaux = JsonConverter.convertListeCreneaux(arrayCreneaux);
                        ArrayList<String> creneaux = new ArrayList<String>();
                        if (listeCreneaux.size() > 0) {
                            for (int i = 0; i < listeCreneaux.size(); i++) {
                                String creneau_string = Globals.timeToString(listeCreneaux.get(i).getDate(), false) + " - " + Globals.timeToString(listeCreneaux.get(i).getDateFin(), false);
                                publishProgress(creneau_string, String.valueOf(listeCreneaux.get(i).getTarif()));
                            }
                        }else{
                            publishProgress("no", "no");
                        }
                        if (date == liste7ProchainsJours.get(liste7ProchainsJours.size() - 1))
                            tvChargement.setVisibility(View.GONE);
                    } catch (JSONException ex) {
                        Log.e("errJsonCreneau:", "err " + ex.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("errVolley:", "err " + error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Auth-Token", Globals.getTokenApi().getValue());
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
