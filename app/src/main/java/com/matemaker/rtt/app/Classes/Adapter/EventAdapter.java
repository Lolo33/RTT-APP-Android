package com.matemaker.rtt.app.Classes.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.matemaker.rtt.app.Classes.Evenement;
import com.matemaker.rtt.app.Classes.Globals;
import com.matemaker.rtt.app.R;

import java.util.Date;

public class EventAdapter extends ArrayAdapter<Evenement> {

    private int layoutResourceId;

    private static final String LOG_TAG = "MemoListAdapter";

    public EventAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        layoutResourceId = textViewResourceId;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        try {
            Evenement item = getItem(position);
            View v = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                v = inflater.inflate(layoutResourceId, null);

            } else {
                v = convertView;
            }

            TextView header = (TextView) v.findViewById(R.id.TV_Titre);
            TextView tvTarif = (TextView) v.findViewById(R.id.TV_Prix);
            TextView tvTournoi = (TextView) v.findViewById(R.id.TV_Tournoi);
            TextView tvDate = (TextView) v.findViewById(R.id.TV_Date);
            TextView tvEquipe = (TextView) v.findViewById(R.id.TV_NbEquipes);
            TextView tvHeure = (TextView) v.findViewById(R.id.TV_Heure);
            TextView tvOrga = (TextView) v.findViewById(R.id.TV_Orga);

            int background = Color.parseColor(Globals.COULEUR_MATCH);
            String nb_team;
            if (item.isTournoi()){
                background = Color.parseColor(Globals.COULEUR_TOURNOI);
                nb_team = item.getNombreEquipesString() + " équipes";
            }else{
                nb_team = item.getNombreJoueursString()  + " joueurs";
            }


            Date dateEvent = item.getDate();
            Date heureDebut = item.getHeureDebut();
            Date heureFin = item.getHeureFin();
            v.findViewById(R.id.rlFondEvent).setBackgroundColor(background);

            String s;
            if (item.isTarifEquipe()) {
                s = Float.valueOf(item.getTarif() / item.getNbJoueursMin()).toString();
            }else {
                s = String.valueOf(item.getTarif());
            }
            String prix = s.substring(0, s.indexOf(".") + 2);

            header.setText(item.getTitre().toUpperCase());
            tvTarif.setText(String.valueOf(Globals.tarifToString(prix) + " €"));
            tvTournoi.setText(item.getLieu().getNom());
            tvDate.setText("Le: " + Globals.dateToString(dateEvent));
            tvOrga.setText(item.getOrganisateur1().getPseudo());
            tvHeure.setText("De: " + Globals.timeToString(heureDebut, false) + " à " + Globals.timeToString(heureFin, false));
            tvEquipe.setText(nb_team);

            return v;

        } catch (Exception ex) {
            Log.e(LOG_TAG, "error", ex);
            return new View(getContext());
        }
    }

}