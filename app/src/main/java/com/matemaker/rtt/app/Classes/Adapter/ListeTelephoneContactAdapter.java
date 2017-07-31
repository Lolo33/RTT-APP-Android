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

import com.matemaker.rtt.app.Classes.Globals;
import com.matemaker.rtt.app.Classes.Lieu;
import com.matemaker.rtt.app.R;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.matemaker.rtt.app.Classes.Lieu;
import com.matemaker.rtt.app.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ListeTelephoneContactAdapter extends ArrayAdapter<ArrayList<String>> {

    private int layoutResourceId;

    private static final String LOG_TAG = "MemoListAdapter";

    public ListeTelephoneContactAdapter(Context context, int textViewResourceId) {
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

            TextView tvNom = (TextView) v.findViewById(R.id.tvNomContact);
            TextView tvNum = (TextView) v.findViewById(R.id.tvTelContact);

            if (item.get(2) == "selected"){
                v.setBackgroundColor(Color.parseColor(Globals.COULEUR_EVENT_PRIVE));
                tvNom.setTextColor(Color.WHITE);
                tvNum.setTextColor(Color.WHITE);
            }else {
                v.setBackgroundColor(Color.WHITE);
                tvNom.setTextColor(Color.BLACK);
                tvNum.setTextColor(Color.parseColor("#708090"));
            }
            tvNom.setText(item.get(0));
            tvNum.setText(item.get(1));

            return v;
        } catch (Exception ex) {
            Log.e(LOG_TAG, "error", ex);
            return new View(getContext());
        }
    }

}
