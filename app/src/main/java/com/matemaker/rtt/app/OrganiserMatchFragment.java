package com.matemaker.rtt.app;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.matemaker.rtt.app.Classes.ErrorDialog;
import com.matemaker.rtt.app.Classes.Globals;
import com.matemaker.rtt.app.Classes.Lieu;

import java.util.ArrayList;

/**
 * Created by Niquelesstup on 12/06/2017.
 */

public class OrganiserMatchFragment extends Fragment {

    View myView;
    private ArrayList<String> listeLieuxString;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.organiser_match, container, false);
        TextView tvTitreOrga = (TextView) myView.findViewById(R.id.tvTitreOrga);
        Globals.setFont(getActivity(), tvTitreOrga, "Champagne & Limousines Bold.ttf");

        final AutoCompleteTextView tvLieuxAC = (AutoCompleteTextView) myView.findViewById(R.id.ACTV_Lieux);
        listeLieuxString = new ArrayList<String>();
        for (int i=0;i<Globals.getListeDpt().size();i++)
            for (int j=0;j<Globals.getListeDpt().get(i).getListeLieux().size();j++)
                listeLieuxString.add(Globals.getListeDpt().get(i).getListeLieux().get(j).getNom());
        final Spinner spinner = (Spinner) myView.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapterSpi = ArrayAdapter.createFromResource(getActivity(),
                R.array.joueurs_array, android.R.layout.simple_spinner_item);
        adapterSpi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSpi);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice, listeLieuxString);
        tvLieuxAC.setAdapter(adapter);

        Button btnValider = (Button) myView.findViewById(R.id.btnValiderLieu);
        btnValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lieu_selected = ((AutoCompleteTextView) myView.findViewById(R.id.ACTV_Lieux)).getText().toString();
                String descriptif = ((EditText) myView.findViewById(R.id.inputDescriptif)).getText().toString();
                String nbJoueurs = spinner.getSelectedItem().toString();
                Intent i = new Intent(getActivity(), OrganiserHorairesActivity.class);
                if (listeLieuxString.contains(lieu_selected)){
                    i.putExtra("lieu", Lieu.getLieuByNom(lieu_selected));
                    i.putExtra("descriptif", descriptif);
                    i.putExtra("nbJoueurs", nbJoueurs);
                    startActivity(i);
                } else {
                    ErrorDialog.showWithActivity(getActivity(), "Ce lieu n'existe pas, veuillez en selectionner un dans la liste d'autocomplétion.");
                }
            }
        });

        return myView;
    }

}
