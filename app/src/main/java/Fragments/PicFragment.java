package Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.apple.monitor.R;
import com.example.apple.monitor.ShowPicActivity;
import com.example.apple.monitor.WatchActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by apple on 16/5/3.
 */
@SuppressLint("ValidFragment")
public class PicFragment extends Fragment{
    Context context;
    View view;
    GridView picGridview;
    String picname[];
    List<File> flist ;
    MyAdapter myAdapter;
    public PicFragment(Context context){this.context =context;}
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File picDir = new File(context.getFilesDir().getPath().toString()+"picDir");
        File picFiles[] = picDir.listFiles();
        flist = new ArrayList<File>();
        for (File f:picFiles) {
            flist.add(f);
        }

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pic,null);
        picGridview = (GridView)view.findViewById(R.id.picGridview);
        myAdapter = new MyAdapter(inflater);
        picGridview.setAdapter(myAdapter);
        return view;
    }




    private class MyAdapter extends BaseAdapter{
        LayoutInflater inflater;
        public  MyAdapter (LayoutInflater inflater){this.inflater = inflater;}
        @Override
        public int getCount() {
            return flist.size();
        }

        @Override
        public Object getItem(int position) {
            return flist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            final int po = position;
            if (convertView == null) {
                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(240, 320));//设置ImageView对象布局
                imageView.setAdjustViewBounds(false);//设置边界对齐
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//设置刻度的类型
                imageView.setPadding(8, 8, 8, 8);//设置间距
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context,ShowPicActivity.class);
                        intent.putExtra("picname",flist.get(po).getPath());
                        startActivity(intent);
                    }
                });
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {


                        //todo 可能有错
                        new AlertDialog.Builder(context).setTitle("是否删除")
                                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        flist.get(po).delete();
                                        flist.remove(po);
                                        myAdapter.notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton("取消", null).show();
                        return true;
                    }
                });
            }
            else {
                imageView = (ImageView) convertView;
            }
            Bitmap bitmap = BitmapFactory.decodeFile(flist.get(position).getPath());
            imageView.setImageBitmap(bitmap);
            return imageView;
        }
    }

}
