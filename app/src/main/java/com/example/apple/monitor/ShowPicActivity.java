package com.example.apple.monitor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by apple on 16/5/8.
 */
public class ShowPicActivity extends Activity {
    ImageView showPic ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showpic);
        showPic = (ImageView)findViewById(R.id.showPic);
        String filename = getIntent().getStringExtra("picname");
        Bitmap bitmap = BitmapFactory.decodeFile(filename);
        showPic.setImageBitmap(bitmap);
    }
}
