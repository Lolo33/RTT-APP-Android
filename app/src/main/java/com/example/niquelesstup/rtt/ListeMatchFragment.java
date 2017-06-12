package com.example.niquelesstup.rtt;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.niquelesstup.rtt.Classes.Api.ConfApi;
import com.example.niquelesstup.rtt.Classes.Api.JsonConverter;
import com.example.niquelesstup.rtt.Classes.Evenement;
import com.example.niquelesstup.rtt.Classes.Globals;
import com.example.niquelesstup.rtt.Classes.Lieu;
import com.example.niquelesstup.rtt.Classes.Membre;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Niquelesstup on 12/06/2017.
 */

public class ListeMatchFragment extends Fragment {

    View myView;
    private RequestQueue requestQueue;
    //private ConfApi confApi;
    final String url = Globals.getApiUrl() + "/departements/" + Globals.getMembreConnecte().getDepartement() + "/lieux";

    public void setFont(TextView textView, String fontName) {
        if(fontName != null){
            try {
                Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/" + fontName);
                textView.setTypeface(typeface);
            } catch (Exception e) {
                Log.e("FONT", fontName + " not found", e);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.liste_matchs, container, false);
        final ListView listViewEvents = (ListView) myView.findViewById(R.id.listViewEvents);
        ArrayList<Evenement> listeEvents = new ArrayList<Evenement>();
        //final TextView textAccueil = (TextView) myView.findViewById(R.id.textView2);

        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.start();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray liste_lieux_json = new JSONArray(response);
                            ArrayList<Lieu> listeLieux = JsonConverter.convertListeLieux(liste_lieux_json);
                            Iterator<Lieu> iterator = listeLieux.iterator();
                            //textAccueil.setText(listeLieux.get(0).getDepartement().toString());

                            ArrayAdapter<Lieu> adapter = new ArrayAdapter<Lieu>(getActivity(), android.R.layout.simple_list_item_1, listeLieux);
                            listViewEvents.setAdapter(adapter);

                        }catch (JSONException ex){
                            //textAccueil.setText("Une erreur s'est produite.");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //textAccueil.setText(url);
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("X-Auth-Token", Globals.getTokenApi().getValue());
                return headers;
            }
        };
        requestQueue.add(stringRequest);

        //
        // setFont(textAccueil, "Champagne & Limousines Bold.ttf");
        return myView;
    }

}
