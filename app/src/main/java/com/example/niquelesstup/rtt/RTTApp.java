package com.example.niquelesstup.rtt;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Niquelesstup on 10/06/2017.
 */

public final class RTTApp extends Application {

    private RequestQueue requestQueue;

    @Override
    public void onCreate(){
        super.onCreate();
        requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.start();
    }

    public RequestQueue getVolleyRequestQueue() {
        return requestQueue;
    }

    @Override
    public void onTerminate() {
        requestQueue.stop();
        super.onTerminate();
    }

}
