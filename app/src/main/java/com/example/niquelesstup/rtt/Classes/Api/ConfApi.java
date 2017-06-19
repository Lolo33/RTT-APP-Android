package com.example.niquelesstup.rtt.Classes.Api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.niquelesstup.rtt.Classes.Globals;

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
                            Globals.setTokenApi(new Token(token.getString("value"), token.getString("createdAt")));
                        } catch (JSONException ex) {
                            Log.e("errReponseToken", ex.getMessage());
                            setToken(new Token("ZwbyKi5TGaXT5q9tflMO73iXxHyrE0XNuZJiRC61pmW49rCA3WeAfqx9NpilI2jx6iw=", "2017-06-18T14:12:49+01:00"));
                            Globals.setTokenApi(new Token("ZwbyKi5TGaXT5q9tflMO73iXxHyrE0XNuZJiRC61pmW49rCA3WeAfqx9NpilI2jx6iw=", "2017-06-18T14:12:49+01:00"));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("errReponseToken", error.getMessage());
                setToken(new Token("ZwbyKi5TGaXT5q9tflMO73iXxHyrE0XNuZJiRC61pmW49rCA3WeAfqx9NpilI2jx6iw=", "2017-06-18T14:12:49+01:00"));
                Globals.setTokenApi(new Token("ZwbyKi5TGaXT5q9tflMO73iXxHyrE0XNuZJiRC61pmW49rCA3WeAfqx9NpilI2jx6iw=", "2017-06-18T14:12:49+01:00"));
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
