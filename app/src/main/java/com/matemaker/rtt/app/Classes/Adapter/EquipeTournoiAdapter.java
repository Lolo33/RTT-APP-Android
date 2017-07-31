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

import com.matemaker.rtt.app.Classes.Equipe;
import com.matemaker.rtt.app.Classes.Globals;
import com.matemaker.rtt.app.R;

public class EquipeTournoiAdapter extends ArrayAdapter<Equipe> {

    private int layoutResourceId;

    private static final String LOG_TAG = "EquipeAdapter";

    public EquipeTournoiAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        layoutResourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        try {
            Equipe item = getItem(position);
            View v = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                v = inflater.inflate(layoutResourceId, null);

            } else {
                v = convertView;
            }

            TextView tvNomTeam = (TextView) v.findViewById(R.id.textViewNomTeam);
            TextView tvNbJoueursTeam = (TextView) v.findViewById(R.id.textViewNbJoueursTeam);

            for (int i=0; i<item.getListeMembres().size();i++){
                if (item.getListeMembres().get(i).getMembre().getId() == Globals.getMembreConnecte().getId())
                    v.setBackgroundColor(Color.parseColor("#DEB887"));
            }

            String nb_j = item.getListeMembres().size() + " joueurs";

            tvNomTeam.setText(item.getNom());
            tvNbJoueursTeam.setText(nb_j);

            return v;
        } catch (Exception ex) {
            Log.e(LOG_TAG, "error", ex);
            return new View(getContext());
        }
    }

}