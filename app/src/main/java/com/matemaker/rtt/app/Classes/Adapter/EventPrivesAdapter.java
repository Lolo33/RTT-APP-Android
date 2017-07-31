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

import com.matemaker.rtt.app.Classes.CreneauxJoueurs;
import com.matemaker.rtt.app.Classes.Evenement;
import com.matemaker.rtt.app.Classes.EvenementPrive;
import com.matemaker.rtt.app.Classes.Globals;
import com.matemaker.rtt.app.R;

import java.util.Date;

public class EventPrivesAdapter extends ArrayAdapter<EvenementPrive> {

    private int layoutResourceId;
    private static final String LOG_TAG = "MemoListAdapter";

    public EventPrivesAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        layoutResourceId = textViewResourceId;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        try {
            EvenementPrive item = getItem(position);
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

            /*header.setTextColor(Color.BLACK);
            tvTarif.setTextColor(Color.GRAY);
            tvTournoi.setTextColor(Color.BLACK);
            tvDate.setTextColor(Color.BLACK);
            tvEquipe.setTextColor(Color.BLACK);
            tvHeure.setTextColor(Color.BLACK);
            tvOrga.setTextColor(Color.BLACK);*/

            tvOrga.setText(item.getOrganisateur().getPseudo());

            String titre_court = item.getTitre();
            if (item.getTitre().length() > 14)
               titre_court = item.getTitre().substring(0, 14) + "...";
            header.setText(titre_court.toUpperCase());
            tvTournoi.setText(item.getLieu().getNom());
            v.findViewById(R.id.rlFondEvent).setBackgroundColor(Color.parseColor(Globals.COULEUR_EVENT_PRIVE));
            String prix = "Non Réservé";
            CreneauxJoueurs leCreneau = item.getCreneauEvent();
            if (leCreneau == null) {
                tvEquipe.setText(item.getNbJoueursMax() + " joueurs max.");
                tvDate.setText("Créneau à déterminer");
                tvHeure.setText("Vérouillez une date pour votre évenement !");
            }else {
                tvEquipe.setText(item.getMembresCreneau(leCreneau).size() + " / " + item.getNbJoueursMax() + " joueurs");
                tvDate.setText(Globals.dateToString(leCreneau.getDateDebut()));
                tvHeure.setText("De: " + Globals.timeToString(leCreneau.getDateDebut(), false) + " à " + Globals.timeToString(leCreneau.getDateFin(), false));
            }
            if (item.getTarif() == 0 || item.getReservation() == null) {
                tvTarif.setBackgroundColor(Color.parseColor("#CD5C5C"));
            }else{
                String s=Float.valueOf(item.getTarif() / item.getMembresCreneau(leCreneau).size()).toString();
                prix = s.substring(0,s.indexOf(".")+2);
                prix = String.valueOf(Globals.tarifToString(prix)) + " €";
            }
            tvTarif.setText(prix);

            return v;

        } catch (Exception ex) {
            Log.e(LOG_TAG, "error", ex);
            return new View(getContext());
        }
    }

}