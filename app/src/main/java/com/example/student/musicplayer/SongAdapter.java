package com.example.student.musicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SongAdapter extends ArrayAdapter<SongObject> {

    private Context context;
    private ArrayList<SongObject> values;
    private MediaMetadataRetriever songData = new MediaMetadataRetriever();

    private byte [] data;
    private ImageView image;

    public SongAdapter(Context context, ArrayList<SongObject> values){
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View songView, ViewGroup parent) {
        if (songView == null) {
            songView = LayoutInflater.from(getContext()).inflate( R.layout.song_list_item  , parent, false);
        }
        data = songData.getEmbeddedPicture();

        SongObject thisSong = getItem(position);

        TextView songNameView = (TextView) songView.findViewById(R.id.title);
        songNameView.setText(thisSong.title);

        TextView songArtistView = (TextView) songView.findViewById(R.id.artist);
        songArtistView.setText(thisSong.artist);

        image = (ImageView) songView.findViewById(R.id.picture);
        image.setImageBitmap(thisSong.image);


        return songView;
    }
}
