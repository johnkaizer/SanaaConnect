package com.example.sanaaconnect.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.sanaaconnect.R;

public class MainActivity extends AppCompatActivity {
    Button getStarted;
    VideoView OpenerBG;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getStarted = (Button)  findViewById(R.id.getStarted);
        OpenerBG = (VideoView) findViewById(R.id.OpenerBG);

        String path ="android.resource://com.example.sanaaconnect/" + R.raw.kboard;

        Uri u = Uri.parse(path);
        OpenerBG.setVideoURI(u);
        OpenerBG.start();

        OpenerBG.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(MainActivity.this, CreatorWelcomePage.class);
                Bundle b = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle();

                startActivity(a,b);
            }


        });


    }

    @Override
    protected void onResume(){
        OpenerBG.resume();
        super.onResume();
    }

    @Override
    protected void onPause(){
        OpenerBG.suspend();
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        OpenerBG.stopPlayback();
        super.onDestroy();
    }


}