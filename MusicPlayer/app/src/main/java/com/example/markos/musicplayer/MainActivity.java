package com.example.markos.musicplayer;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> mediaPaths = new ArrayList<String>();
    private List<String> songs = new ArrayList<String>();
    private MediaPlayer player = new MediaPlayer();
    private LoadSongTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        File parent = Environment.getExternalStorageDirectory().getParentFile();
        File files[] = parent.listFiles();
        for(int i = 0; i< files.length; i++){
            mediaPaths.add(files[i].getPath());
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    player.reset();
                    player.setDataSource(songs.get(position));
                    player.prepare();
                    player.start();
                }catch (IOException e){
                    Toast.makeText(getBaseContext(), "Cannot start audio!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        task = new LoadSongTask();
        task.execute();
    }

    private class LoadSongTask extends AsyncTask<Void, String, Void>
    {
        private List loadedSonds = new ArrayList<String>();

        @Override
        protected void onPreExecute() {
            Toast.makeText(getBaseContext(), "Loading...", Toast.LENGTH_SHORT);
        }

        @Override
        protected Void doInBackground(Void... params) {
            for(String directName: mediaPaths){
                File temp = new File(directName+"/Music/");
                if(temp.exists()) {
                    System.out.println(temp.getPath());
                    updateSongListRecursive(temp);
                }else{
                    temp = new File(directName+"/Hudba/");
                    if(temp.exists()) {
                        System.out.println(temp.getPath());
                        updateSongListRecursive(temp);
                    }
                }
            }
            return null;
        }

        public void updateSongListRecursive(File path){
            if(path.isDirectory()){
                for(int i =0; i<path.listFiles().length; i++){
                    File file = path.listFiles()[i];
                    updateSongListRecursive(file);
                }
            }else{
                String name = path.getAbsolutePath();
                publishProgress(name);
                if(name.endsWith(".mp3")){
                    loadedSonds.add(name);
                }
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ArrayAdapter<String> songList = new ArrayAdapter<String>
                    (MainActivity.this, R.layout.support_simple_spinner_dropdown_item, loadedSonds);
            listView.setAdapter(songList);
            songs = loadedSonds;

            Toast.makeText(getApplicationContext(), "Songs="+songs.size(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(player.isPlaying()){
            player.reset();
        }
    }
}
