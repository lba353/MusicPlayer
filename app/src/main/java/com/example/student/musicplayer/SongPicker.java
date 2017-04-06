package com.example.student.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaMetadata;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.media.MediaMetadataRetriever;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class SongPicker extends AppCompatActivity {

    private String songTitle;
    private String songArtist;
    private Bitmap songImage;

    private MediaMetadataRetriever songInfo;

    public static ArrayList<SongObject> songList ;

    private SongAdapter myAdapter;

    private byte[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_picker);

        data = new MediaMetadataRetriever().getEmbeddedPicture();

        songInfo = new MediaMetadataRetriever();
        songList = new ArrayList<SongObject>();

        String dir = "/storage/emulated/0/Music/Tracks/";
        File songsPath = new File(dir);
        File[] songFiles = songsPath.listFiles();

        for(File songFile : songFiles) {
            Uri mediaPath = Uri.fromFile(songFile);
            songInfo.setDataSource(this, mediaPath);

            songTitle = songInfo.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            songArtist = songInfo.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            data = songInfo.getEmbeddedPicture();

            Bitmap bitmap;
            if(data != null)
            {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            }
            else
            {
                bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.record);
            }

            songList.add( new SongObject(songFile, songTitle, songArtist, bitmap) );
        }

        myAdapter = new SongAdapter(this, songList);

        ListView listView = (ListView) findViewById(R.id.songListView);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                launchPlayer(String.valueOf(position));
            }
        });
    }

    public void launchPlayer(String songID) {
        Intent launchSong = new Intent(this, MainSong.class);
        String message = String.valueOf(songID);
        launchSong.putExtra("songMessage", message);
        startActivity(launchSong);
    }
}
