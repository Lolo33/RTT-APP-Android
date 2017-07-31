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

import com.matemaker.rtt.app.Classes.DownloadImageTask;
import com.matemaker.rtt.app.Classes.EquipeMembre;
import com.matemaker.rtt.app.Classes.Mates;
import com.matemaker.rtt.app.R;

public class MatesAdapter extends ArrayAdapter<Mates> {

    private int layoutResourceId;

    private static final String LOG_TAG = "MembreMatchAdapter";

    public MatesAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        layoutResourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        try {
            Mates item = getItem(position);
            View v = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(layoutResourceId, null);
            } else {
                v = convertView;
            }

            TextView tvStatut = (TextView) v.findViewById(R.id.tvStatutMate);
            TextView tvPseudo = (TextView) v.findViewById(R.id.tvMateNom);

            ImageView ivAvatar = (ImageView) v.findViewById(R.id.ivAvatarMate);


            v.setTag(R.string.tag_horaire_checked, "null");

            //Log.e("MembrFb", item.getMembre().getPseudo() + " : " + item.getMembre().getIdFacebook());
            if (item.getMate().getIdFacebook() != null && !item.getMate().getIdFacebook().equals("null") && item.getMate().getAvatar().getId() == 1){
                new DownloadImageTask(ivAvatar).execute("https://graph.facebook.com/" + item.getMate().getIdFacebook() + "/picture");
            }else {
                new DownloadImageTask(ivAvatar).execute("https://reservetonterrain.fr/" + item.getMate().getAvatar().getUrl());
            }

            //Globals.majImg("https://reservetonterrain.fr/" + item.getMembre().getAvatar().getUrl(), ivAvatar);

            tvStatut.setText(item.getStatut().getNom());
            tvPseudo.setText(item.getMate().getPseudo());

            return v;

        } catch (Exception ex) {
            Log.e(LOG_TAG, "error", ex);
            return new View(getContext());
        }
    }

}
