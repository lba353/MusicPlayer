package com.example.student.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainSong extends AppCompatActivity {

    private static MediaPlayer song;

    private Button pauseButton;
    private Button playButton;
    private Button stopButton;
    private Button rewindButton;
    private Button forwardButton;

    private TextView currentTimeView;
    private TextView totalTimeView;
    private TextView title;
    private TextView author;

    private double currentTimeMS;
    private double totalTimeMS;

    private Handler time = new Handler();

    private SeekBar seek;
    private int seekTime = 0;

    private int songNumber;
    private int numOfSongs;
    private SongObject thisSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_song);

        //Getting the exact song from the Song Picker class
        Intent thisIntent = getIntent();
        String songID = thisIntent.getStringExtra("songMessage");

        //Getting the number of the selected song in the array.
        songNumber = Integer.parseInt( thisIntent.getStringExtra("songMessage"));
        numOfSongs = SongPicker.songList.size();

        //Title + Author View Config
        title = (TextView) findViewById(R.id.title);
        author = (TextView) findViewById(R.id.author);

        //Sets "Total" and "Current Time" text view
        currentTimeView = (TextView) findViewById(R.id.currentTime);
        totalTimeView = (TextView) findViewById(R.id.totalTime);

        //Button Config
        pauseButton = (Button) findViewById(R.id.pause);
        playButton = (Button) findViewById(R.id.play);
        stopButton = (Button) findViewById(R.id.stop);
        rewindButton = (Button) findViewById(R.id.rewind);
        forwardButton = (Button) findViewById(R.id.forward);

        //Seek Bar Config
        seek = (SeekBar) findViewById(R.id.seeker);

        //Seek Bar Listener
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               seekTime = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                song.seekTo(seekTime);
                currentTimeMS = seekTime;
            }
        });

        //For starting a new song from the beginning.
        playNewSong(songNumber);

        //Retrieving Specific Song Info
        MediaMetadataRetriever songInfo = new MediaMetadataRetriever();

        Uri filepath= Uri.parse("android.resource://" + getPackageName() + "/" + thisSong.songID);
        songInfo.setDataSource(this, filepath);

        //Auto-Play the selected song.
        song.start();
        playButton.setEnabled(false);
    }

    //Time Updater
    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            currentTimeMS = song.getCurrentPosition();

            seek.setProgress((int) currentTimeMS);

            int currentMinutes = (int) (currentTimeMS / 1000 / 60);
            int currentSeconds = ((int) (currentTimeMS / 1000)) % 60;

            currentTimeView = (TextView) findViewById(R.id.currentTime);
            currentTimeView.setText(currentMinutes + " min, " + currentSeconds + " sec");

            //For Rewind / Forward Buttons
            if(currentTimeMS > 5000) {
                rewindButton.setEnabled(true);
            }
            else {
                rewindButton.setEnabled(false);
            }

            if(currentTimeMS < totalTimeMS - 5000) {
                forwardButton.setEnabled(true);
            }
            else {
                forwardButton.setEnabled(false);
            }

            time.postDelayed(this, 100);
        }
    };

    //Method Section of Music Player
    private void playNewSong(int position) {
        //Title, Author, ID Config
        thisSong = SongPicker.songList.get(position);
        title.setText(thisSong.title);
        author.setText(thisSong.artist);

        //Checking if a song is already playing.
        if(song != null){
            song.stop();
            song.release();
            song = null;
        }

        //Song Config
        song = MediaPlayer.create(getApplicationContext(), thisSong.songID);
        song.seekTo(0);

        playSongNow();
    }

    public void playSongNow() {
        //Initial Configs
        Toast.makeText(getApplicationContext(), "Playing Music", Toast.LENGTH_SHORT).show();
        song.start();
        totalTimeMS = song.getDuration();
        currentTimeMS = song.getCurrentPosition();
        seek.setMax((int) totalTimeMS);

        //Calculating and displaying total time.
        int totalMinutes = (int) (totalTimeMS / 1000 / 60);
        int totalSeconds = ((int) (totalTimeMS / 1000)) % 60;
        totalTimeView.setText(totalMinutes + " min, " + totalSeconds + " sec");

        //Calculating and displaying current time.
        int currentMinutes = (int) (currentTimeMS / 1000 / 60);
        int currentSeconds = ((int) (currentTimeMS / 1000)) % 60;
        currentTimeView.setText(currentMinutes + " min, " + currentSeconds + " sec");

        //Seek Bar updates
        seek.setProgress((int) currentTimeMS);
        time.postDelayed(UpdateSongTime, 100);

        //Button Standings
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
        playButton.setEnabled(false);

        song.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                nextSong();
            }
        });
    }

    public void play(View view) {
        playSongNow();
    }

    public void pause(View view) {
        song.pause();

        playButton.setEnabled(true);
        pauseButton.setEnabled(false);

        Context context = getApplicationContext();
        CharSequence text = "The song is now paused.";
        int duration = Toast.LENGTH_SHORT;
        Toast pauseMessage= Toast.makeText(context, text, duration);
        pauseMessage.show();
    }

    public void stop(View view) {
        song.seekTo(0);
        song.pause();

        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);

        Context context = getApplicationContext();
        CharSequence text = "The song is now stopped.";
        int duration = Toast.LENGTH_SHORT;
        Toast stopMessage= Toast.makeText(context, text, duration);
        stopMessage.show();
    }

    public void rewind(View view) {
        song.seekTo((int) (currentTimeMS - 5000));
    }

    public void forward(View view) {
        song.seekTo((int) (currentTimeMS + 5000));
    }

    public void next(View view){
        nextSong();
    }

    public void nextSong() {
        songNumber++;

        if(songNumber >= numOfSongs) {
            songNumber = 0;
        }

        playNewSong(songNumber);
    }

    public void previous(View view) {
        if(currentTimeMS > 5000) {
            song.seekTo(0);
        }
        else {
            if(songNumber == 0) {
                songNumber = numOfSongs;
            }

            songNumber--;
            playNewSong(songNumber);
        }
    }
}
