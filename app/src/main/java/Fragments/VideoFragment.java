package Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.apple.monitor.R;
import com.example.apple.monitor.ShowPicActivity;
import com.example.apple.monitor.ShowVideoActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 16/5/3.
 */
@SuppressLint("ValidFragment")
public class VideoFragment extends Fragment {
    Context context;
    View view;
    ListView videoList;
    List<File> flist;

    public VideoFragment(Context context){this.context = context;}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File recDir = new File(context.getFilesDir().getPath().toString() + "recDir");
        File recFiles[] = recDir.listFiles();
        flist = new ArrayList<File>();
        for (File f:recFiles) {
            flist.add(f);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_video,null);
        videoList = (ListView)view.findViewById(R.id.videoList);
        return view;
    }

    private class MyAdapter extends BaseAdapter{
        TextView textView;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                textView = new TextView(context);
                textView.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                textView.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);

            }
            else{
                textView = (TextView) convertView;
            }


            textView.setText(flist.get(position).getName());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ShowVideoActivity.class);
                    intent.putExtra("recPath",flist.get(position));
                    startActivity(intent);
                }
            });

            return null;
        }
    }


}
