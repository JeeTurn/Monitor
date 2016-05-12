package com.example.apple.monitor;

import android.app.Activity;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

/**
 * Created by apple on 16/5/11.
 */
public class ShowVideoActivity extends Activity {
    VideoView videoView;
    MediaController mediaco;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showrec);
//        String path = getIntent().getStringExtra("recPath");
//        Uri uri  = Uri.parse(path);
//        videoView = (VideoView) findViewById(R.id.videoView);
//
//        videoView.setVideoURI(uri);
//        videoView.start();
//        videoView.requestFocus();




    }
}
