package com.example.niquelesstup.rtt.Classes.Api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Niquelesstup on 11/06/2017.
 */

public class ConfApi {

    private String url = "http://mate-maker.fr/auth-tokens";
    private RequestQueue requestQueue;
    private Token token;

    public void newToken(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject token = new JSONObject(response);
                            setToken(new Token(token.getString("value"), token.getString("createdAt")));
                        } catch (JSONException ex) {
                            //setToken(null);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //err reponse
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("login", "mate-maker33");
                params.put("password", "lololepro");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public Token getToken() {
        return this.token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
