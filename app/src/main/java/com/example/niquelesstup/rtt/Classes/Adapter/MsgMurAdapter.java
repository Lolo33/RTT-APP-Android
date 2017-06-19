package com.example.niquelesstup.rtt.Classes.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.niquelesstup.rtt.Classes.MessageMur;
import com.example.niquelesstup.rtt.R;

public class MsgMurAdapter extends ArrayAdapter<MessageMur> {

    private int layoutResourceId;

    private static final String LOG_TAG = "MsgMurAdapter";

    public MsgMurAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        layoutResourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        try {
            MessageMur item = getItem(position);
            View v = null;
            if (convertView == null) {
                LayoutInflater li = LayoutInflater.from(getContext());
                v = li.inflate(R.layout.liste_msg_mur , null);
            } else {
                v = convertView;
            }

            TextView tvContenu = (TextView) v.findViewById(R.id.textViewMSGMur);
            TextView tvDate = (TextView) v.findViewById(R.id.textViewDteMsg);
            TextView tvMembre = (TextView) v.findViewById(R.id.TV_MSGMEMBR);

            tvContenu.setText(item.getContenu());
            tvDate.setText(item.getDate());
            tvMembre.setText("Par: " + item.getMembre().getPseudo());

            return v;

        } catch (Exception ex) {
            Log.e(LOG_TAG, "error", ex);
            return new View(getContext());
        }
    }

}
