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
import com.matemaker.rtt.app.R;

public class MembreMatchAdapter extends ArrayAdapter<EquipeMembre> {

    private int layoutResourceId;

    private static final String LOG_TAG = "MembreMatchAdapter";

    public MembreMatchAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        layoutResourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        try {
            EquipeMembre item = getItem(position);
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

            String str_pay = "Non payé";
            tvPay.setTextColor(Color.parseColor("#CD5C5C"));
            if (item.isPay()){
                str_pay = "Payé";
                tvPay.setTextColor(Color.parseColor("#2E8B57"));
            }

            //Log.e("MembrFb", item.getMembre().getPseudo() + " : " + item.getMembre().getIdFacebook());
            if (item.getMembre().getIdFacebook() != null && !item.getMembre().getIdFacebook().equals("null") && item.getMembre().getAvatar().getId() == 1){
                new DownloadImageTask(ivAvatar).execute("https://graph.facebook.com/" + item.getMembre().getIdFacebook() + "/picture");
            }else {
                new DownloadImageTask(ivAvatar).execute("https://reservetonterrain.fr/" + item.getMembre().getAvatar().getUrl());
            }

            //Globals.majImg("https://reservetonterrain.fr/" + item.getMembre().getAvatar().getUrl(), ivAvatar);

            tvPay.setText(str_pay);
            tvPseudo.setText(item.getMembre().getPseudo());

            return v;

        } catch (Exception ex) {
            Log.e(LOG_TAG, "error", ex);
            return new View(getContext());
        }
    }

}
