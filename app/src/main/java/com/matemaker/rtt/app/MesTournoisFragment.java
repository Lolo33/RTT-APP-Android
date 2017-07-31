package com.matemaker.rtt.app;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.matemaker.rtt.app.Classes.Adapter.EventAdapter;
import com.matemaker.rtt.app.Classes.Adapter.EventPrivesAdapter;
import com.matemaker.rtt.app.Classes.Api.JsonConverter;
import com.matemaker.rtt.app.Classes.CreneauxJoueurs;
import com.matemaker.rtt.app.Classes.ErrorDialog;
import com.matemaker.rtt.app.Classes.Evenement;
import com.matemaker.rtt.app.Classes.EvenementPrive;
import com.matemaker.rtt.app.Classes.Globals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Niquelesstup on 20/06/2017.
 */

public class MesTournoisFragment extends Fragment {

    View myView;
    private RequestQueue requestQueue;
    private ListView lvMesEvents;
    private ListView lvMesEventsPrives;
    private ListView lvMesInvits;
    private ErrorDialog errorDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Récupération de la vue (liste_mes_tournois)
        myView = inflater.inflate(R.layout.liste_mes_tournois, container, false);

        // Mise à jour de la police du titre
        TextView tvTitre = (TextView) myView.findViewById(R.id.tvTitreMesTournois);
        Globals.setFont(getActivity(), tvTitre, "Champagne & Limousines Bold.ttf");

        // Initialisation des variables utiles à la requete de récupération
        errorDialog = new ErrorDialog(getActivity());
        lvMesEvents = (ListView) myView.findViewById(R.id.lvMesTournois);
        lvMesEventsPrives = (ListView) myView.findViewById(R.id.lvMesEventsPrives);
        lvMesInvits = (ListView) myView.findViewById(R.id.lvMesInvits);

        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.start();

        // Lancement de la requete
        new RecupMesEvenementsAsynchTask().execute();
        new RecupMesEvenementsPrivesAsynchTask().execute();
        new RecupInvitationsEventsPrivesAsynchTask().execute();

        // Retourne la vue créee précédemment
        return myView;
    }

    private class RecupInvitationsEventsPrivesAsynchTask extends  AsyncTask<Void, String, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            String url = Globals.getApiUrl() + "/membres/" + Globals.getMembreConnecte().getId() + "/invitations";
            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray arrayEvents = new JSONArray(response);
                        ArrayList<EvenementPrive> listeEvents = JsonConverter.convertListeEvenementsPrives(arrayEvents);
                        if (listeEvents.size() >= 1) {
                            lvMesInvits.setClickable(true);
                            EventPrivesAdapter adapter = new EventPrivesAdapter(getActivity().getApplicationContext(), R.layout.list_custom_events);
                            lvMesInvits.setAdapter(adapter);
                            adapter.addAll(listeEvents);
                            adapter.notifyDataSetChanged();
                            lvMesInvits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    EvenementPrive lEvenement = (EvenementPrive) lvMesInvits.getItemAtPosition(position);
                                    Intent intentEvent = new Intent(getActivity(), EvenementMatchActivity.class);
                                    intentEvent.putExtra("Evenement", lEvenement);
                                    intentEvent.putExtra("from", "mes_tournois");
                                    intentEvent.putExtra("prive", true);
                                    startActivity(intentEvent);
                                }
                            });
                        }else {
                            ArrayList<String> notFound = new ArrayList<String>();
                            notFound.add("Vous n'etes inscrit à aucun évenement privé.");
                            ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, notFound);
                            lvMesInvits.setAdapter(adapter);
                            lvMesInvits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent i = new Intent(getActivity(), MenuActivity.class);
                                    i.putExtra("menuItem", "orga");
                                    startActivity(i);
                                }
                            });
                        }
                    }catch (JSONException ex){
                        Log.e("Erreur Json invit", "err: " + ex.getMessage());
                        publishProgress("err");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Erreur volley invit", "err" + error.getMessage());
                    publishProgress("err");
                }
            }){
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
    }

    private class RecupMesEvenementsPrivesAsynchTask extends AsyncTask<Void, String, Void>{

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values[0] == "err"){
                errorDialog.show("Une erreur s'est produite pendant la récupération des évenements, vérifiez votre connexion internet !");
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url = Globals.getApiUrl() + "/membres/" + Globals.getMembreConnecte().getId() + "/event-prives";
            final StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray arrayEvents = new JSONArray(response);
                        ArrayList<EvenementPrive> listeEvents = JsonConverter.convertListeEvenementsPrives(arrayEvents);
                        if (listeEvents.size() >= 1) {
                            lvMesEventsPrives.setClickable(true);
                            EventPrivesAdapter adapter = new EventPrivesAdapter(getActivity().getApplicationContext(), R.layout.list_custom_events);
                            lvMesEventsPrives.setAdapter(adapter);
                            adapter.addAll(listeEvents);
                            adapter.notifyDataSetChanged();
                            lvMesEventsPrives.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    EvenementPrive lEvenement = (EvenementPrive) lvMesEventsPrives.getItemAtPosition(position);
                                    Intent intentEvent = new Intent(getActivity(), EvenementMatchActivity.class);
                                    intentEvent.putExtra("Evenement", lEvenement);
                                    intentEvent.putExtra("from", "mes_tournois");
                                    intentEvent.putExtra("prive", true);
                                    startActivity(intentEvent);
                                }
                            });
                        }else {
                            ArrayList<String> notFound = new ArrayList<String>();
                            notFound.add("Vous n'etes inscrit à aucun évenement privé.");
                            ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, notFound);
                            lvMesEventsPrives.setAdapter(adapter);
                            lvMesEventsPrives.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent i = new Intent(getActivity(), MenuActivity.class);
                                    i.putExtra("menuItem", "orga");
                                    startActivity(i);
                                }
                            });
                        }
                    }catch (JSONException ex){
                        Log.e("errConvertListeEvents", ex.getMessage());
                        publishProgress("err");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Erreur volley eventMe", "err:" + error.getMessage());
                    publishProgress("err");
                }
            }){
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
    }

    public class RecupMesEvenementsAsynchTask extends AsyncTask<Void, String, Void>{

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values[0] == "err"){
                errorDialog.show("Une erreur s'est produite pendant la récupération des évenements, vérifiez votre connexion internet !");
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url = Globals.getApiUrl() + "/membres/" + Globals.getMembreConnecte().getId() + "/evenements";
            final StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray arrayEvents = new JSONArray(response);
                        ArrayList<Evenement> listeEvents = JsonConverter.convertListeEvenements(arrayEvents, true);
                        if (listeEvents.size() >= 1) {
                            lvMesEvents.setClickable(true);
                            EventAdapter adapter = new EventAdapter(getActivity().getApplicationContext(), R.layout.list_custom_events);
                            lvMesEvents.setAdapter(adapter);
                            adapter.addAll(listeEvents);
                            adapter.notifyDataSetChanged();
                            lvMesEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Evenement lEvenement = (Evenement) lvMesEvents.getItemAtPosition(position);
                                    Intent intentEvent = new Intent(getActivity(), EvenementMatchActivity.class);
                                    intentEvent.putExtra("Evenement", lEvenement);
                                    intentEvent.putExtra("from", "mes_tournois");
                                    intentEvent.putExtra("prive", false);
                                    startActivity(intentEvent);
                                }
                            });
                        }else {
                            ArrayList<String> notFound = new ArrayList<String>();
                            notFound.add("Vous n'etes inscrit à aucun évenement public.");
                            ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, notFound);
                            lvMesEvents.setAdapter(adapter);
                            lvMesEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent i = new Intent(getActivity(), MenuActivity.class);
                                    i.putExtra("menuItem", "liste_complexes");
                                    startActivity(i);
                                }
                            });
                        }
                    }catch (JSONException ex){
                        Log.e("errConvertListeEvents", ex.getMessage());
                        publishProgress("err");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Erreur volley eventMe", error.getMessage());
                }
            }){
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
    }
}
