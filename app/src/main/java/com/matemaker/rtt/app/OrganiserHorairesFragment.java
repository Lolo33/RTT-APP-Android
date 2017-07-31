package com.matemaker.rtt.app;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.matemaker.rtt.app.Classes.Api.JsonConverter;
import com.matemaker.rtt.app.Classes.Creneaux;
import com.matemaker.rtt.app.Classes.ErrorDialog;
import com.matemaker.rtt.app.Classes.Globals;
import com.matemaker.rtt.app.Classes.Lieu;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Niquelesstup on 29/06/2017.
 */

public class OrganiserHorairesFragment extends Fragment {

    private View myView;
    private Calendar maDate;
    private Lieu leLieu;
    private String dateChk;
    private RequestQueue requestQueue;
    private String descriptifEvent;
    private String nbJoueursMaxEvent;

    public static OrganiserHorairesFragment newInstance(Calendar maDate, Lieu lieu) {
        OrganiserHorairesFragment fragment = new OrganiserHorairesFragment();
        Bundle args = new Bundle();
        args.putSerializable("date", maDate);
        args.putSerializable("lieu", lieu);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        maDate = (Calendar) getArguments().getSerializable("date");
        leLieu = (Lieu) getArguments().getSerializable("lieu");
        nbJoueursMaxEvent = getArguments().getString("nbJoueurs");
        myView = inflater.inflate(R.layout.fragment_horaires, container, false);

        String dateString = Globals.stringCalendar(maDate.get(Calendar.DATE),maDate.get(Calendar.MONTH), maDate.get(Calendar.YEAR), "/");
        TextView tvDate = (TextView) myView.findViewById(R.id.tvDateHoraires);
        tvDate.setText(Globals.jourToString(maDate.get(Calendar.DAY_OF_WEEK)) + " " + dateString);
        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.start();

        dateChk = Globals.stringCalendar(maDate.get(Calendar.DATE), maDate.get(Calendar.MONTH), maDate.get(Calendar.YEAR), "-");

        /*myView.findViewById(R.id.tv_fleche_droite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar datePlusUn = maDate;
                datePlusUn.set(Calendar.DATE, datePlusUn.get(Calendar.DATE)  + 1);
                getFragmentManager().beginTransaction().replace(R.id.contenuHoraireFrame, newInstance(datePlusUn, leLieu)).commit();
            }
        });

        myView.findViewById(R.id.tv_fleche_gauche).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar dateMoinsUn = maDate;
                dateMoinsUn.set(Calendar.DATE, dateMoinsUn.get(Calendar.DATE) - 1);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contenuHoraireFrame, newInstance(dateMoinsUn, leLieu))
                        .commit();
            }
        });*/

        TextView flecheRetour = (TextView) myView.findViewById(R.id.tv_fleche_gauche);
        flecheRetour.setText("<");
        if (maDate.get(Calendar.DATE) == Calendar.getInstance().get(Calendar.DATE))
            flecheRetour.setVisibility(View.INVISIBLE);

        new RecupCreneauxAsynchTask().execute();

        return myView;
    }

    public void clicFlecheDroite(View v){

    }



    private class RecupCreneauxAsynchTask extends AsyncTask<Void, ArrayList<String>, Void> {

        private LinearLayout llHoraires;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            llHoraires = (LinearLayout) myView.findViewById(R.id.llHoraires);
        }

        private void genererVueHoraire(String creneau){
            final TextView tvHoraire = new TextView(getActivity());
            tvHoraire.setPadding(20,20,20,20);
            tvHoraire.setTag(R.string.tag_horaire_checked, null);
            tvHoraire.setGravity(Gravity.CENTER_VERTICAL);
            tvHoraire.setText(creneau);
            tvHoraire.setTag(R.string.tag_horaire_obj, dateChk + " # " + creneau);
            tvHoraire.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvHoraire.setBackgroundColor(Color.parseColor("#FFFFFF"));
            tvHoraire.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            tvHoraire.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icone_heure), null, null, null);
            if (Globals.getDateSelected().contains(dateChk + " # " + creneau)){
                tvHoraire.setBackgroundColor(Color.parseColor("#DEB887"));
                tvHoraire.setTag(R.string.tag_horaire_checked, "checked");
            }
            tvHoraire.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> listeChecked = Globals.getDateSelected();
                    TextView tvNbCreneaux = (TextView) getActivity().findViewById(R.id.TV_PresentationHoraires);
                    if (v.getTag(R.string.tag_horaire_checked) == null) {
                        listeChecked.add(v.getTag(R.string.tag_horaire_obj).toString());
                        v.setBackgroundColor(Color.parseColor("#DEB887"));
                        v.setTag(R.string.tag_horaire_checked, "checked");
                    }else {
                        listeChecked.remove(listeChecked.indexOf(v.getTag(R.string.tag_horaire_obj).toString()));
                        v.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        v.setTag(R.string.tag_horaire_checked, null);
                    }
                    tvNbCreneaux.setText("Mes créneaux (" + Globals.getDateSelected().size() + ")");
                    Globals.setDateSelected(listeChecked);
                }
            });
            llHoraires.addView(tvHoraire);
        }

        @Override
        protected void onProgressUpdate(ArrayList<String>... creneaux) {
            final TextView tvSeparateurTop = new TextView(getActivity());
            tvSeparateurTop.setHeight(2);
            tvSeparateurTop.setBackgroundColor(Color.parseColor("#000000"));
            llHoraires.addView(tvSeparateurTop);
            for (int i = 0; i < creneaux[0].size(); i++) {
                final TextView tvSeparateur = new TextView(getActivity());
                tvSeparateur.setHeight(2);
                tvSeparateur.setBackgroundColor(Color.parseColor("#000000"));
                genererVueHoraire(creneaux[0].get(i));
                llHoraires.addView(tvSeparateur);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            String dateStringForRequest = Globals.stringCalendar(maDate.get(Calendar.DATE), maDate.get(Calendar.MONTH), maDate.get(Calendar.YEAR), "-");
            String url_dates = Globals.getApiUrl() + "/lieux/" + leLieu.getId() + "/creneaux/" + dateStringForRequest;
            StringRequest request = new StringRequest(Request.Method.GET, url_dates, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray arrayCreneaux = new JSONArray(response);
                        ArrayList<Creneaux> listeCreneaux = JsonConverter.convertListeCreneaux(arrayCreneaux);
                        ArrayList<String> creneaux = new ArrayList<String>();
                        for (int i = 0; i < listeCreneaux.size(); i++) {
                            String creneau_string = Globals.timeToString(listeCreneaux.get(i).getDate(), false) + " - " + Globals.timeToString(listeCreneaux.get(i).getDateFin(), false);
                            creneaux.add(creneau_string);
                        }
                        publishProgress(creneaux);
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
    }

}
