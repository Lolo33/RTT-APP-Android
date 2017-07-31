package com.matemaker.rtt.app.Classes.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.matemaker.rtt.app.Classes.CreneauxJoueurs;
import com.matemaker.rtt.app.Classes.DownloadImageTask;
import com.matemaker.rtt.app.Classes.EquipeMembre;
import com.matemaker.rtt.app.Classes.EvenementPrive;
import com.matemaker.rtt.app.Classes.Globals;
import com.matemaker.rtt.app.R;

import java.util.ArrayList;

public class CreneauAdapter extends ArrayAdapter<ArrayList<String>> {

    private int layoutResourceId;
    private static final String LOG_TAG = "MembreMatchAdapter";

    public CreneauAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        layoutResourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        try {
            ArrayList<String> item = getItem(position);
            View v = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(layoutResourceId, null);
            } else {
                v = convertView;
            }

            TextView tvCrenJour = (TextView) v.findViewById(R.id.tvCreneauJour);

            if (item.get(0).equals("no")) {
                tvCrenJour.setText("X");
                tvCrenJour.setTextColor(Color.RED);
                tvCrenJour.setPadding(0, 10, 0, 10);
            }else{
                if (item.get(1) == "selected") {
                    v.setBackgroundColor(Color.parseColor(Globals.COULEUR_EVENT_PRIVE));
                    tvCrenJour.setTextColor(Color.WHITE);
                } else {
                    v.setBackgroundColor(Color.WHITE);
                    tvCrenJour.setTextColor(Color.BLACK);
                }
                TextView tvHor = (TextView) v.findViewById(R.id.tvCreneauJour);
                String str_horaire;
                String[] tab_horaire = item.get(0).split(" - ");
                str_horaire = tab_horaire[0] + "\n" + tab_horaire[1];
                tvHor.setText(str_horaire);
            }
            return v;

        } catch (Exception ex) {
            Log.e(LOG_TAG, "error", ex);
            return new View(getContext());
        }
    }

}
