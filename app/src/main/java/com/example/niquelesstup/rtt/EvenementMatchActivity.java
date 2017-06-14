package com.example.niquelesstup.rtt;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.niquelesstup.rtt.Classes.Evenement;
import com.example.niquelesstup.rtt.Classes.Globals;

public class EvenementMatchActivity extends AppCompatActivity {

    private Evenement lEvenement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evenement_match);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lEvenement = (Evenement) getIntent().getSerializableExtra("Evenement");

        findViewById(R.id.header_match).setBackgroundColor(Color.parseColor(Globals.COULEUR_MATCH));

        TextView tvTitreMatch = (TextView) findViewById(R.id.textViewTitreMatch);
        TextView tvLieuMatch = (TextView) findViewById(R.id.textViewLieuMatch);
        TextView tvDateMatch = (TextView) findViewById(R.id.textViewDateMatch);
        TextView tvNbTeamMatch = (TextView) findViewById(R.id.textViewNbJoueursMatch);
        TextView tvOrgaMatch = (TextView) findViewById(R.id.textViewOrgaMatch);

        Globals.setFont(this, tvTitreMatch, "Champagne & Limousines Bold.ttf");

        tvTitreMatch.setText(lEvenement.getTitre());
        tvLieuMatch.setText(lEvenement.getLieu().getNom());
        tvNbTeamMatch.setText(lEvenement.getNombreJoueursString());
        tvOrgaMatch.setText(lEvenement.getOrganisateur1().getPseudo());
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent intentListMatch = new Intent(getApplicationContext(), ListeMatchActivity.class);
        intentListMatch.putExtra("lieu", lEvenement.getLieu());
        startActivity(intentListMatch);
        return true;
    }

}
