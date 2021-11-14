package com.iAms0nu.flexmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class playsong extends AppCompatActivity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    TextView textView;
    TextView curpos,totalpos;
    ImageView play,previous,next;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    String textContent;
    int position;
    Thread updateSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playsong);
        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        curpos = findViewById(R.id.curpos);
        totalpos = findViewById(R.id.totalpos);




        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList)bundle.getParcelableArrayList("SongList");
        textContent = intent.getStringExtra("CurrentSong");
        textView.setText(textContent);
        textView.setSelected(true);
        position = intent.getIntExtra("Positions",0);
        Uri uri = Uri.parse(songs.get(position).toString());

        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();

        seekBar.setMax(mediaPlayer.getDuration());


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });


        String endtime = creattime(mediaPlayer.getDuration());
        totalpos.setText(endtime);

        final Handler handler = new Handler();
        final int delay = 1000;
         handler.postDelayed(new Runnable() {
             @Override
             public void run() {
                 String currenttime = creattime(mediaPlayer.getCurrentPosition());
                 curpos.setText(currenttime);
                 handler.postDelayed(this,delay);
             }
         },delay);

        updateSeek = new Thread(){
            @Override
            public void run() {
                int CurrentPosition = 0;
                try{
                    while(CurrentPosition<mediaPlayer.getDuration()){
                        CurrentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(CurrentPosition);
                        sleep(800);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else{
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position-1)<0)?(songs.size()-1):(position-1);

               /* if(position!=0){
                    position = position - 1;
                }
                else{
                    position = songs.size() - 1;
                }*/

                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

                String endtime = creattime(mediaPlayer.getDuration());
                totalpos.setText(endtime);

                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();

                position=((position+1)% songs.size());
                //or
               /* if(position!=songs.size()-1){
                    position = position + 1;
                }
                else{
                    position = 0;

                }*/
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                String endtime = creattime(mediaPlayer.getDuration());
                totalpos.setText(endtime);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);

            }
        });




    }
//1
    public String creattime(int duration){
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        time+=min+":";

        if (sec<10){
            time+=0;
        }
        time+=sec;

        return time;
    }


}

