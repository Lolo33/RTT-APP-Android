package com.matemaker.rtt.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.matemaker.rtt.app.Classes.Adapter.LieuAdapter;
import com.matemaker.rtt.app.Classes.Api.JsonConverter;
import com.matemaker.rtt.app.Classes.Departement;
import com.matemaker.rtt.app.Classes.ErrorDialog;
import com.matemaker.rtt.app.Classes.Globals;
import com.matemaker.rtt.app.Classes.Lieu;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Niquelesstup on 12/06/2017.
 */

public class ListeComplexeFragment extends Fragment
        implements View.OnClickListener {

    View myView;
    private FragmentManager manager = getFragmentManager();
    private RequestQueue requestQueue;
    private ListView listViewEvents;
    private ArrayList<Lieu> listeLieux;
    private EditText etDpt;

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

        etDpt = (EditText) myView.findViewById(R.id.inputDpt);

        // Reset le focus et empeche l'apparition du clavier a la création de l'activité
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Button btnChange = (Button) myView.findViewById(R.id.buttonChangeDpt);
        btnChange.setOnClickListener(this);
        //btnChange.setText(Globals.getMembreConnecte().getListeMates().get(0).getPseudo());

        listViewEvents = (ListView) myView.findViewById(R.id.listViewEvents);

        return myView;
    }

    private class ActualiserListeDepartementsAsynchTask extends AsyncTask<Void, String, Void>{

        private String dpt;
        ErrorDialog errorDialog = new ErrorDialog(getActivity());

        public ActualiserListeDepartementsAsynchTask(String dpt){
            this.dpt = dpt;
        }
        private ImageView iv;
        private Animation rotation;

        @Override
        protected void onPreExecute() {
            iv = (ImageView) myView.findViewById(R.id.ivChargement);
            listViewEvents = (ListView) myView.findViewById(R.id.listViewEvents);
            Glide.with(getActivity()).asGif().load(R.drawable.chargement).into(iv);
            listViewEvents.setVisibility(View.INVISIBLE);
            //iv.setAnimation(rotation);
        }

        @Override
        protected void onProgressUpdate(String... string) {
            if (string[0] == "find") {
                if (listeLieux.size() >= 1) {
                    listViewEvents.setClickable(true);
                    LieuAdapter adapter = new LieuAdapter(getActivity(), R.layout.list_custom_lieu); // the adapter is a member field in the activity
                    listViewEvents.setAdapter(adapter);
                    adapter.addAll(listeLieux);
                    listViewEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                            Lieu lieuSelected = listeLieux.get(position);
                            //String str=(String)o;//As you are using Default String Adapter
                            Globals.setLieuSelected(lieuSelected);
                            Intent intentListMatch = new Intent(getActivity(), ListeMatchActivity.class);
                            intentListMatch.putExtra("lieu", lieuSelected);
                            intentListMatch.putExtra("from", "liste_complexes");
                            startActivity(intentListMatch);
                        }
                    });
                }else {
                    ArrayList<String> notFound = new ArrayList<String>();
                    notFound.add("Ce département ne contient pas de complexes sportifs pour l'instant.");
                    ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, notFound);
                    listViewEvents.setAdapter(adapter);
                    listViewEvents.setOnItemClickListener(null);
                    listViewEvents.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
            } else if (string[0] == "errNotFound") {
                errorDialog.show("Ce département n'existe pas");
            } else {
                this.errorDialog.show("Une erreur est survenue avec le message suivant : " + string[1]);
            }
        }

        @Override
        protected Void doInBackground(Void... params){
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
                                Globals.setListeLieuxJoueur(listeLieux);
                                publishProgress("find");
                            }catch (JSONException ex){
                                publishProgress("err", "Erreur de conversion des lieux : " + ex.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 404){
                        publishProgress("errNotFound");
                    }else {
                        publishProgress("err", "Erreur de connexion aux données, veuillez vérifier votre connexion Internet.");
                    }
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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //iv.clearAnimation();
            iv.setVisibility(View.INVISIBLE);
            listViewEvents.setVisibility(View.VISIBLE);
        }

    }

    /// Méthode qui actualise la listView des lieux
    public void actualiserListViewLieux(String dpt){
        new ActualiserListeDepartementsAsynchTask(dpt).execute();
    }

    public void hideKeyboard(Context ctx, View v) {
        InputMethodManager imm = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /// OnClick : Se produit lors d'un clic sur le bouton "changer de département"
    public void onClick(View v) {
        EditText inputDpt = (EditText) myView.findViewById(R.id.inputDpt);
        String dpt = inputDpt.getText().toString();
        Globals.setDptSelect(dpt);

        hideKeyboard(getActivity().getApplicationContext(), inputDpt);

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


}
