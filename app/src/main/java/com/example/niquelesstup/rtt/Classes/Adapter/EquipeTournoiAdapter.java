package com.example.niquelesstup.rtt.Classes.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.niquelesstup.rtt.Classes.Equipe;
import com.example.niquelesstup.rtt.R;

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

            String nb_j = item.getListeMembres().size() + " joueurs";

            ListView lvJoueurs = (ListView) v.findViewById(R.id.LvJoueurs);
            if (item.getListeMembres().size() > 0) {
                MembreMatchAdapter adapter = new MembreMatchAdapter(getContext(), R.layout.liste_joueurs_match);
                lvJoueurs.setAdapter(adapter);
                adapter.addAll(item.getListeMembres());
                lvJoueurs.setVisibility(View.GONE);
                lvJoueurs.setId(lvJoueurs.getId() + position);
                //adapter.notifyDataSetChanged();
            }

            tvNomTeam.setText(item.getNom());
            tvNbJoueursTeam.setText(nb_j);

            return v;
        } catch (Exception ex) {
            Log.e(LOG_TAG, "error", ex);
            return new View(getContext());
        }
    }

}