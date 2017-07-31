package com.matemaker.rtt.app.Classes.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
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
import com.matemaker.rtt.app.Classes.Membre;
import com.matemaker.rtt.app.R;

public class MembreMatchPriveNDAdapter extends ArrayAdapter<Membre> {

    private int layoutResourceId;
    private EvenementPrive evenementPrive;
    private static final String LOG_TAG = "MembreMatchAdapter";

    public MembreMatchPriveNDAdapter(Context context, int textViewResourceId, EvenementPrive event) {
        super(context, textViewResourceId);
        layoutResourceId = textViewResourceId;
        evenementPrive = event;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        try {
            Membre item = getItem(position);
            View v = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(layoutResourceId, null);
            } else {
                v = convertView;
            }

            TextView tvPseudo = (TextView) v.findViewById(R.id.TV_Team);
            TextView tvPay = (TextView) v.findViewById(R.id.TV_NBJ);
            //v.setBackgroundColor(Color.parseColor(Globals.COULEUR_MATCH));
            ImageView ivAvatar = (ImageView) v.findViewById(R.id.ivAvatar);

            int nbCreneaux = evenementPrive.getListeCreneauxMembre(item).size();
            String str_pay = nbCreneaux + " créneaux proposés";
            tvPay.setTextColor(Color.parseColor("#2E8B57"));
            tvPay.setText(str_pay);

            Log.e("MembrFb", item.getPseudo() + " : " + item.getIdFacebook());
            if (item.getIdFacebook() != null && !item.getIdFacebook().equals("null") && item.getAvatar().getId() == 1){
                new DownloadImageTask(ivAvatar).execute("https://graph.facebook.com/" + item.getIdFacebook() + "/picture");
            }else {
                new DownloadImageTask(ivAvatar).execute("https://reservetonterrain.fr/" + item.getAvatar().getUrl());
            }

            tvPseudo.setText(item.getPseudo());

            return v;

        } catch (Exception ex) {
            Log.e(LOG_TAG, "error", ex);
            return new View(getContext());
        }
    }

}
