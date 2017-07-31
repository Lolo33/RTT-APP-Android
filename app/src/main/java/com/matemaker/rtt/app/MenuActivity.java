package com.matemaker.rtt.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v7.view.menu.MenuBuilder;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.matemaker.rtt.app.Classes.Api.JsonConverter;
import com.matemaker.rtt.app.Classes.Api.Token;
import com.matemaker.rtt.app.Classes.Departement;
import com.matemaker.rtt.app.Classes.Globals;
import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RequestQueue requestQueue;
    private NavigationView navigationView;
    private FragmentManager manager = getFragmentManager();

    public void clicBtnDpt(View view){
        //lmF.actualiserListViewLieux(dpt);
    }

    private class RemplirListeDepartementsAsnchTask extends AsyncTask<Void, String, Void>{
        @Override
        protected Void doInBackground(Void... params){
            // Création de la liste des départements
            final String url = Globals.getApiUrl() + "/departements";
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.start();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray departementsJson = new JSONArray(response);
                                ArrayList<Departement> listeDpt = JsonConverter.convertListeDepartements(departementsJson);
                                Globals.setListeDpt(listeDpt);
                            }catch (JSONException ex){
                                //textView.setText("Une erreur s'est produite.");
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //textView.setText("Pseudo / mot de passe incorrects");
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Token token = Globals.getTokenApi();
                    Globals.setTokenApi(token);
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Auth-Token", token.getValue());
                    return headers;
                }
            };
            requestQueue.add(stringRequest);
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Globals.redirectIfNotConnected(this);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new RemplirListeDepartementsAsnchTask().execute();

        String menu_item = getIntent().getStringExtra("menuItem");
        if (menu_item.equals("mes_tournois"))
            manager.beginTransaction().replace(R.id.contentFrame, new MesTournoisFragment()).commit();
        else if (menu_item.equals("liste_complexes"))
            manager.beginTransaction().replace(R.id.contentFrame, new ListeComplexeFragment()).commit();
        else if (menu_item.equals("orga"))
            manager.beginTransaction().replace(R.id.contentFrame, new HorairesLieuFragment()).commit();
        else
            manager.beginTransaction().replace(R.id.contentFrame, new AccueilFragment()).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        getMenuInflater().inflate(R.menu.menu, menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_accueil){
            manager.beginTransaction().replace(R.id.contentFrame, new AccueilFragment()).commit();
        } else if (id == R.id.nav_liste_match) {
            manager.beginTransaction().replace(R.id.contentFrame, new ListeComplexeFragment()).commit();
        } else if (id == R.id.nav_orga) {
            manager.beginTransaction().replace(R.id.contentFrame, new HorairesLieuFragment()).commit();
        } else if (id == R.id.nav_mes_tournois) {
            manager.beginTransaction().replace(R.id.contentFrame, new MesTournoisFragment()).commit();
        } else if (id == R.id.nav_param) {
            manager.beginTransaction().replace(R.id.contentFrame, new ParamFragment()).commit();
        } else if (id == R.id.nav_deco) {
            Globals.setMembreConnecte(null);
            AccessToken.setCurrentAccessToken(null);
            Intent i = new Intent(this, LoginnActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        Globals.redirectIfNotConnected(this);
    }

}
