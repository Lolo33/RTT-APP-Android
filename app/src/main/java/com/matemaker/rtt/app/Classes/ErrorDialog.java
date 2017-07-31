package com.matemaker.rtt.app.Classes;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.matemaker.rtt.app.R;

/**
 * Created by Niquelesstup on 23/06/2017.
 */

public class ErrorDialog {

    private Activity activity;

    public ErrorDialog(Activity activity){
        this.activity = activity;
    }

    public void show(String contenu){
        final Dialog dialog = new Dialog(this.activity);
        dialog.setContentView(R.layout.dialog_error);
        dialog.setTitle("Erreur");
        TextView tvErr = (TextView) dialog.findViewById(R.id.tvDialogErr);
        tvErr.setText(contenu);
        // set the custom dialog components - text, image and button
        final Button btnRet = (Button) dialog.findViewById(R.id.btnQuitDialog);
        dialog.show();
        btnRet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void showWithRetry(String contenu, final AsyncTask<Void, String, Void> taskToRetry){
        final Dialog dialog = new Dialog(this.activity);
        dialog.setContentView(R.layout.dialog_error);
        dialog.setTitle("Erreur");
        TextView tvErr = (TextView) dialog.findViewById(R.id.tvDialogErr);
        tvErr.setText(contenu);
        // set the custom dialog components - text, image and button
        final Button btnRet = (Button) dialog.findViewById(R.id.btnQuitDialog);
        btnRet.setText("Réessayer");
        dialog.show();
        btnRet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskToRetry.cancel(false);
                taskToRetry.execute();
            }
        });
    }

    public static void showWithActivity(Activity activity, String contenu){
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_error);
        dialog.setTitle("Erreur");
        TextView tvErr = (TextView) dialog.findViewById(R.id.tvDialogErr);
        tvErr.setText(contenu);
        // set the custom dialog components - text, image and button
        final Button btnRet = (Button) dialog.findViewById(R.id.btnQuitDialog);
        dialog.show();
        btnRet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}
