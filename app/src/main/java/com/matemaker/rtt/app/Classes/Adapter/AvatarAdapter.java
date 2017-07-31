package com.matemaker.rtt.app.Classes.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.matemaker.rtt.app.Classes.Avatar;
import com.matemaker.rtt.app.Classes.DownloadImageTask;
import com.matemaker.rtt.app.R;

/**
 * Created by Niquelesstup on 27/06/2017.
 */

public class AvatarAdapter extends ArrayAdapter<Avatar> {

    private int layoutResourceId;

    private static final String LOG_TAG = "MembreMatchAdapter";

    public AvatarAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        layoutResourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        try {
            Avatar item = getItem(position);
            View v = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(layoutResourceId, null);
            } else {
                v = convertView;
            }

            ImageView ivAvatar = (ImageView) v.findViewById(R.id.ivAvatarChose);

            new DownloadImageTask(ivAvatar).execute("https://reservetonterrain.fr/" + item.getUrl());


            return v;

        } catch (Exception ex) {
            return new View(getContext());
        }
    }

}
