package com.example.niquelesstup.rtt;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.niquelesstup.rtt.Classes.Adapter.LieuAdapter;
import com.example.niquelesstup.rtt.Classes.Api.JsonConverter;
import com.example.niquelesstup.rtt.Classes.Departement;
import com.example.niquelesstup.rtt.Classes.Globals;
import com.example.niquelesstup.rtt.Classes.Lieu;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Niquelesstup on 12/06/2017.
 */

public class ListeComplexeFragment extends Fragment
        implements View.OnClickListener, AdapterView.OnItemClickListener {

    View myView;
    private FragmentManager manager = getFragmentManager();
    private RequestQueue requestQueue;
    private ListView listViewEvents;
    private ArrayList<Lieu> listeLieux;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.liste_complexes, container, false);
        String dpt_select;
        if (Globals.getDptSelect() != null)
            dpt_select = Globals.getDptSelect();
        else
            dpt_select = "33";
        actualiserListViewLieux(dpt_select);

        // Reset le focus et empeche l'apparition du clavier a la création de l'activité
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Button btnChange = (Button) myView.findViewById(R.id.buttonChangeDpt);
        btnChange.setOnClickListener(this);

        // setFont(textAccueil, "Champagne & Limousines Bold.ttf");
        return myView;
    }

    /// Méthode qui actualise la listView des lieux
    public void actualiserListViewLieux(String dpt){
        listViewEvents = (ListView) myView.findViewById(R.id.listViewEvents);
        final String url = Globals.getApiUrl() + "/departements/" + dpt + "/lieux";
        //final TextView textAccueil = (TextView) myView.findViewById(R.id.textView2);

        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.start();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray liste_lieux_json = new JSONArray(response);
                            listeLieux = JsonConverter.convertListeLieux(liste_lieux_json);
                            if (listeLieux != null) {
                                listViewEvents.setClickable(true);
                                LieuAdapter adapter = new LieuAdapter(getActivity(), R.layout.list_custom_lieu); // the adapter is a member field in the activity
                                listViewEvents.setAdapter(adapter);
                                adapter.addAll(listeLieux);
                            }else {
                                TextView tvVide = (TextView) myView.findViewById(R.id.textViewVide);
                                tvVide.setText("Ce département ne contient aucun complexe. Une erreur de saisie ?");
                            }
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
        listViewEvents.setOnItemClickListener(this);
    }

    /// OnClick : Se produit lors d'un clic sur le bouton "changer de département"
    public void onClick(View v) {
        EditText inputDpt = (EditText) myView.findViewById(R.id.inputDpt);
        String dpt = inputDpt.getText().toString();
        Globals.setDptSelect(dpt);

        // Reset le focus et empeche l'apparition du clavier a la création de l'activité
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Modification du texte titre
        TextView tv = (TextView) myView.findViewById(R.id.textViewTitreList);
        ArrayList<Departement> listeDpt = Globals.getListeDpt();
        //tv.setText("Les tournois en ");
        for (int i=0; i<listeDpt.size(); i++) {
            if (listeDpt.get(i).getCode().equals(dpt)){
                tv.setText("Les complexes en " + listeDpt.get(i).getNom());
            }
        }
        inputDpt.setText(Globals.getDptSelect());
        inputDpt.setSelection(dpt.length());
        actualiserListViewLieux(dpt);
    }

    /// OnItemClick : Se produit lors d'un clic sur un item de la listView
    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
        Lieu lieuSelected = listeLieux.get(position);
        //String str=(String)o;//As you are using Default String Adapter
        Globals.setLieuSelected(lieuSelected);
        Intent intentListMatch = new Intent(getActivity(), ListeMatchActivity.class);
        intentListMatch.putExtra("lieu", lieuSelected);
        startActivity(intentListMatch);
    }

}
