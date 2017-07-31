package com.matemaker.rtt.app;

import android.app.Dialog;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.matemaker.rtt.app.Classes.Adapter.AvatarAdapter;
import com.matemaker.rtt.app.Classes.Api.JsonConverter;
import com.matemaker.rtt.app.Classes.Api.Token;
import com.matemaker.rtt.app.Classes.Avatar;
import com.matemaker.rtt.app.Classes.DownloadImageTask;
import com.matemaker.rtt.app.Classes.ErrorDialog;
import com.matemaker.rtt.app.Classes.Globals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Niquelesstup on 21/06/2017.
 */

public class ParamFragment extends Fragment {

    View myView;
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    private ErrorDialog errorDialog;
    private RequestQueue requestQueue;
    ImageView ivAvatar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.parametres, container, false);
        TextView tvTitreParam = (TextView) myView.findViewById(R.id.tvTitreParam);
        Globals.setFont(getActivity(), tvTitreParam, "Champagne & Limousines Bold.ttf");
        ivAvatar = (ImageView) myView.findViewById(R.id.ivParamAvatar);
        errorDialog = new ErrorDialog(getActivity());
        Button btnChange = (Button) myView.findViewById(R.id.btnChangeAvatar);
        majAvatar(Globals.getMembreConnecte().getAvatar());
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClicChangeAvatar();
            }
        });
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.start();
        return myView;
    }

    private void majAvatar(Avatar avatar){
        if (Globals.getMembreConnecte().getIdFacebook() != null && Globals.getMembreConnecte().getIdFacebook() != "null" && avatar.getId() == 1){
            new DownloadImageTask(ivAvatar).execute("https://graph.facebook.com/" + Globals.getMembreConnecte().getIdFacebook() + "/picture");
        }else {
            new DownloadImageTask(ivAvatar).execute("https://reservetonterrain.fr/" + avatar.getUrl());
        }
    }

    private void ClicChangeAvatar(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_avatar);
        dialog.setTitle("Modifier mon avatar");

        // set the custom dialog components - text, image and button
        new AsyncTask<Void, String, Void>(){

            private ArrayList<Avatar> listeAvatar;
            private ListView lvAvatars;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                lvAvatars = (ListView) dialog.findViewById(R.id.lvAvatars);
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                if (values[0] == "err"){
                    errorDialog.show("Une erreur s'est produite pendant la récupération des avatars.");
                }else if (values[0] == "connect") {
                    AvatarAdapter adapter = new AvatarAdapter(getActivity().getApplicationContext(), R.layout.list_custom_avatars);
                    adapter.addAll(listeAvatar);
                    lvAvatars.setAdapter(adapter);
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                String url_av = Globals.getApiUrl() + "/avatars";
                StringRequest request = new StringRequest(Request.Method.GET, url_av, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray listeAv = new JSONArray(response);
                            listeAvatar = JsonConverter.convertListeAvatars(listeAv);
                            publishProgress("connect");
                        }catch (JSONException ex){
                            publishProgress("err");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        publishProgress("err");
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
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                lvAvatars.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Avatar avatar = (Avatar) lvAvatars.getItemAtPosition(position);
                        String url = Globals.getApiUrl() + "/membres/" + Globals.getMembreConnecte().getId() + "/avatars/" + avatar.getId();
                        new ChangeAvatarAsynchTask(url).execute();
                        Globals.getMembreConnecte().setAvatar(avatar);
                        dialog.dismiss();
                    }
                });
            }
        }.execute();
        dialog.show();
    }

    private class ChangeAvatarAsynchTask extends AsyncTask<Void, String, Void>{

        private String url;
        private Avatar newAvatar;
        private ChangeAvatarAsynchTask(String url){
            this.url = url;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values[0] == "changed"){
                majAvatar(newAvatar);
            }else {
                errorDialog.show("Votre avatar n'a pas pu être changé. Assurez vous d'être connecté à internet");
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            StringRequest request = new StringRequest(Request.Method.PATCH, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject objNewMembre = new JSONObject(response);
                        newAvatar = JsonConverter.convertAvatar(objNewMembre.getJSONObject("membreAvatar"));
                        publishProgress("changed");
                    }catch (JSONException ex){
                        Log.e("errJSONAv:", "errAv: " + ex.getMessage());
                        publishProgress("err");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("errVolleyAv:", "errAv: " + error.getMessage());
                    publishProgress("err");
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
            return null;
        }
    }


}
