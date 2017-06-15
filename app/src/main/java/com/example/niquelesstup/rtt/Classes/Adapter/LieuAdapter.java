package com.example.niquelesstup.rtt.Classes.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.niquelesstup.rtt.Classes.Lieu;
import com.example.niquelesstup.rtt.R;

public class LieuAdapter extends ArrayAdapter<Lieu> {

    private int layoutResourceId;

    private static final String LOG_TAG = "MemoListAdapter";

    public LieuAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        layoutResourceId = textViewResourceId;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        try {
            Lieu item = getItem(position);
            View v = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                v = inflater.inflate(layoutResourceId, null);

            } else {
                v = convertView;
            }

            TextView header = (TextView) v.findViewById(R.id.TV_Titre);
            TextView description = (TextView) v.findViewById(R.id.TV_Adresse);
            TextView tvNbEvents = (TextView) v.findViewById(R.id.TV_Event);

            header.setText(item.getNom());
            description.setText(item.getVille());
            tvNbEvents.setText(String.valueOf(item.getCompteEvents()));
            tvNbEvents.setPadding(10, 5, 10, 5);
            if (item.getCompteEvents() != 0){
                tvNbEvents.setBackgroundColor(Color.parseColor("#4682B4"));
                tvNbEvents.setTextColor(Color.parseColor("#ffffff"));
            }else{
                tvNbEvents.setBackgroundColor(Color.parseColor("#ffffff"));
                tvNbEvents.setTextColor(Color.parseColor("#000000"));
            }

            return v;
        } catch (Exception ex) {
            Log.e(LOG_TAG, "error", ex);
            return new View(getContext());
        }
    }

}