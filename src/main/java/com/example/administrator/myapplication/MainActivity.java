package com.example.administrator.myapplication;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends Activity {

    ImageView img;
    Luckypan luckypan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(R.id.bt);
        luckypan = (Luckypan) findViewById(R.id.lucypan);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!luckypan.isStart()) {
                    luckypan.lukystart(2);
                    img.setImageResource(R.drawable.stop);
                } else {
                    luckypan.luckend();
                    img.setImageResource(R.drawable.start);
                    if (luckypan.getspeed() <= 0) {
                        Toast.makeText(MainActivity.this, "over", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }


}
