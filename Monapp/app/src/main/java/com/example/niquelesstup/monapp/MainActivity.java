package com.matemaker.rtt.monapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public int changeText(){
        return R.string.app_name;
    }

    public void clicBouton(View view){
        TextView tv = (TextView) findViewById(R.id.textViewSalut);
        tv.setText(changeText());
    }



}
