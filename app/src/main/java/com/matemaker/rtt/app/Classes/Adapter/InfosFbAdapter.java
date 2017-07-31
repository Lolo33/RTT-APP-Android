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
import com.matemaker.rtt.app.Classes.Globals;
import com.matemaker.rtt.app.R;

import java.util.ArrayList;

public class InfosFbAdapter extends ArrayAdapter<ArrayList<String>> {

    private int layoutResourceId;

    private static final String LOG_TAG = "MembreMatchAdapter";

    public InfosFbAdapter(Context context, int textViewResourceId) {
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

            TextView tvPseudo = (TextView) v.findViewById(R.id.tvNomAmiFb);
            //v.setBackgroundColor(Color.parseColor(Globals.COULEUR_MATCH));
            ImageView ivAvatar = (ImageView) v.findViewById(R.id.ivAvatarAmiFb);

            if (item.get(2) == "selected"){
                v.setBackgroundColor(Color.parseColor(Globals.COULEUR_EVENT_PRIVE));
                tvPseudo.setTextColor(Color.WHITE);
            }else {
                v.setBackgroundColor(Color.WHITE);
                tvPseudo.setTextColor(Color.BLACK);
            }

            //Log.e("MembrFb", item.getMembre().getPseudo() + " : " + item.getMembre().getIdFacebook());
            new DownloadImageTask(ivAvatar).execute("https://graph.facebook.com/" + item.get(1) + "/picture");

            //Globals.majImg("https://reservetonterrain.fr/" + item.getMembre().getAvatar().getUrl(), ivAvatar);

            tvPseudo.setText(item.get(0));

            return v;

        } catch (Exception ex) {
            Log.e(LOG_TAG, "error", ex);
            return new View(getContext());
        }
    }

}
