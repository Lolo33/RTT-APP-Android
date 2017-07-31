package com.matemaker.rtt.app.Classes.GoogleServices;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.matemaker.rtt.app.Classes.Api.JsonConverter;
import com.matemaker.rtt.app.Classes.Api.Token;
import com.matemaker.rtt.app.Classes.Evenement;
import com.matemaker.rtt.app.Classes.EvenementPrive;
import com.matemaker.rtt.app.Classes.Globals;
import com.matemaker.rtt.app.EvenementMatchActivity;
import com.matemaker.rtt.app.MenuActivity;
import com.matemaker.rtt.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Niquelesstup on 03/07/2017.
 */

public class HandlerMessagesFirebase extends FirebaseMessagingService {

    private String TAG = "HandlerFirebaseMessage";
    private RequestQueue requestQueue;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            requestQueue = Volley.newRequestQueue(this);
            requestQueue.start();

            String msg;
            int event_id = 0;
            if (remoteMessage.getNotification() != null) {
                msg = remoteMessage.getNotification().getBody();
                Log.i("PVL", "RECEIVED MESSAGE: " + msg);
            } else {
                msg = remoteMessage.getData().get("message");
                event_id = Integer.valueOf(remoteMessage.getData().get("event"));
                Log.i("PVL", "RECEIVED MESSAGE: " + msg);
            }

            if (event_id != 0) {
                if (remoteMessage.getData().get("type") != "prive")
                    RecupEventFromNotif(msg, event_id);
                else
                    RecupEventPriveFromNotif(msg, event_id);
            }else {
                Log.e("event:", "event: " + event_id);
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }


    private void RecupEventFromNotif(final String msg, int event_id){
        String url = Globals.getApiUrl() + "/evenements/" + event_id;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Evenement lEvent = JsonConverter.convertEvenement(new JSONObject(response), true);
                    sendNotification(msg, lEvent);
                }catch (JSONException ex){
                    Log.e("excEventNotif", "err: " + ex.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("excNetEventNotif", "err: " + error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Token token = Globals.getTokenApi();
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("X-Auth-Token", token.getValue());
                return headers;
            }
        };
        requestQueue.add(request);
    }

    private void RecupEventPriveFromNotif(final String msg, int event_id){
        String url = Globals.getApiUrl() + "/event-prives/" + event_id;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    EvenementPrive lEvent = JsonConverter.convertEvenementPrive(new JSONObject(response), true);
                    sendNotification(msg, lEvent);
                }catch (JSONException ex){
                    Log.e("excEventPNotif", "err: " + ex.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("excNetEventPNotif", "err: " + error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Token token = Globals.getTokenApi();
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("X-Auth-Token", token.getValue());
                return headers;
            }
        };
        requestQueue.add(request);
    }

    private void sendNotification(String messageBody, Evenement evenement) {
        Intent intent = new Intent(this, EvenementMatchActivity.class);
        intent.putExtra("Evenement", evenement);
        intent.putExtra("from", "mes_tournois");
        intent.putExtra("prive", false);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_rtt)
                .setContentTitle("Nouvel évenement dans un match")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendNotification(String messageBody, EvenementPrive evenement) {
        Intent intent = new Intent(this, EvenementMatchActivity.class);
        intent.putExtra("Evenement", evenement);
        intent.putExtra("from", "mes_tournois");
        intent.putExtra("prive", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_rtt)
                .setContentTitle("Nouvel évenement dans un match")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
