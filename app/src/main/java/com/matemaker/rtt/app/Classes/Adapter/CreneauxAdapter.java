package com.matemaker.rtt.app.Classes.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Html;
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
import com.matemaker.rtt.app.Classes.Globals;
import com.matemaker.rtt.app.R;

public class CreneauxAdapter extends ArrayAdapter<CreneauxJoueurs> {

    private int layoutResourceId;
    private EvenementPrive lEventPrive;
    private static final String LOG_TAG = "MembreMatchAdapter";

    public CreneauxAdapter(Context context, int textViewResourceId, EvenementPrive event) {
        super(context, textViewResourceId);
        layoutResourceId = textViewResourceId;
        this.lEventPrive = event;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        try {
            CreneauxJoueurs item = getItem(position);
            View v = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(layoutResourceId, null);
            } else {
                v = convertView;
            }

            TextView tvHoraire = (TextView) v.findViewById(R.id.tvHoraireLV);
            v.setTag(R.string.tag_horaire_checked, "null");
            v.setTag(R.string.tag_horaire_obj, Globals.dateToString(item.getDateDebut()) + " # " +
                    Globals.timeToStringFormatBdd(item.getDateDebut()) + " - " + Globals.timeToStringFormatBdd(item.getDateFin()));
            //Log.e("tag", v.getTag(R.string.tag_horaire_obj).toString());
            tvHoraire.setText(Html.fromHtml(
                    "Le " + Globals.dateToString(item.getDateDebut()) + "<br />" +
                    Globals.timeToString(item.getDateDebut(), false) + " - " + Globals.timeToString(item.getDateFin(), false) +
                            " <b>(" + lEventPrive.getMembresCreneau(item).size() + ")</b>"
            ));

            return v;

        } catch (Exception ex) {
            Log.e(LOG_TAG, "error", ex);
            return new View(getContext());
        }
    }

}
