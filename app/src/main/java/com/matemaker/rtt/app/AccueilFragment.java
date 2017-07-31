package com.matemaker.rtt.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.matemaker.rtt.app.Classes.Globals;

/**
 * Created by Niquelesstup on 21/06/2017.
 */

public class AccueilFragment extends Fragment {

    View myView;
    private FragmentManager manager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        manager = getActivity().getFragmentManager();

        myView = inflater.inflate(R.layout.accueil_screen, container, false);
        TextView tvTitreAccueil = (TextView) myView.findViewById(R.id.tvTitreAccueil);
        tvTitreAccueil.setText("Bienvenue, " + Globals.getMembreConnecte().getPseudo());
        Globals.setFont(getActivity(), tvTitreAccueil, "Champagne & Limousines Bold.ttf");

        Button btnTrouver = (Button) myView.findViewById(R.id.btnSearchEvent);
        btnTrouver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MenuActivity.class);
                i.putExtra("menuItem", "liste_complexes");
                startActivity(i);
            }
        });

        Button btnOrga = (Button) myView.findViewById(R.id.btnOrgaEvent);
        btnOrga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MenuActivity.class);
                i.putExtra("menuItem", "orga");
                startActivity(i);
            }
        });

        Button btnMesEvents = (Button) myView.findViewById(R.id.btnMesEvents);
        btnMesEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MenuActivity.class);
                i.putExtra("menuItem", "mes_tournois");
                startActivity(i);
            }
        });

        return myView;
    }

}
