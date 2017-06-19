package com.example.niquelesstup.rtt.Classes.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.niquelesstup.rtt.Classes.Evenement;
import com.example.niquelesstup.rtt.Classes.Globals;
import com.example.niquelesstup.rtt.R;

import java.sql.Time;
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

            //int background = Color.parseColor("#2E8B57");
            int background = Color.parseColor(Globals.COULEUR_MATCH);
            String nb_team;
            if (item.isTournoi()){
                background = Color.parseColor(Globals.COULEUR_TOURNOI);
                nb_team = item.getNombreEquipesString() + " équipes";
            }else{
                nb_team = item.getNombreJoueursString()  + " joueurs";
            }



            Date dateEvent = item.getDate();
            Time time = new Time(10, 0, 0);
            v.setBackgroundColor(background);

            header.setText(item.getTitre());
            tvTarif.setText(String.valueOf(item.getTarif() + " €"));
            tvTournoi.setText(item.getOrganisateur1().getPseudo());
            tvDate.setText(String.valueOf(dateEvent.toString()));
            tvEquipe.setText(nb_team);

            return v;

        } catch (Exception ex) {
            Log.e(LOG_TAG, "error", ex);
            return new View(getContext());
        }
    }

}