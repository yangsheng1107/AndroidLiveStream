package com.example.yangsheng.video;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    static final String TAG = CircularListAdapter.class.getSimpleName();
    private VideoView videoView;
    private int index = 0;
    private List<Movie> Movielists;
    private ProgressDialog dialog;
    private ListView listView;
    private SharedPreferences settings;
    private static final String data = "DATA";
    private ArrayList<String> Channellists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrieveChannels();

        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Channellists);
        CircularListAdapter circularListAdapter = new CircularListAdapter(arrayAdapter);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(circularListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(VideoActivity.this, Movielists.get(i).getTitle(), Toast.LENGTH_SHORT).show();
                if(index != i)
                {
                    index = i;
                    PlayCurrentVideo();
                }
                listView.setVisibility(View.INVISIBLE);
            }
        });
        listView.setVisibility(View.INVISIBLE);

        videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                dialog.dismiss();
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "無法播放 '" + Movielists.get(index).getTitle() + "'", Toast.LENGTH_SHORT).show();
                //return false;
                return true; // remove can't play this video
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        readData();
        PlayCurrentVideo();
        Log.v(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoView != null) {
            videoView.resume();
        }
        Log.v(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView != null) {
            videoView.suspend();
        }
        Log.v(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (videoView != null) {
            videoView.stopPlayback();
        }
        Log.v(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /*
     * Key mapping
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (listView.getVisibility() == View.VISIBLE) {
                    listView.setVisibility(View.INVISIBLE);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (listView.getVisibility() == View.INVISIBLE) {
                    listView.setVisibility(View.VISIBLE);
                    listView.setSelection(index);
                    listView.requestFocus();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (listView.getVisibility() == View.INVISIBLE) {
                    PlayPreviousVideo();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (listView.getVisibility() == View.INVISIBLE) {
                    PlayNextVideo();
                }
                break;
            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    /*
     * Progress Dialog with timeout
     */
    public void CreateProgressDialogWithTimeout(int miliseconds)
    {
        dialog = ProgressDialog.show(this, "Please wait ...", "Retrieving data ...", true);
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                dialog.dismiss(); // when the task active then close the dialog
                t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
            }
        }, miliseconds); // after 2 second (or 2000 miliseconds), the task will be active.
    }

    /*
     * Video Controller
     */
    public void PlayCurrentVideo() {
        if (videoView != null) {
            CreateProgressDialogWithTimeout(10000);

            if (videoView.isPlaying()) {
                videoView.stopPlayback();
            }
            videoView.setVideoURI(Uri.parse(Movielists.get(index).getVideoUrl()));
            videoView.requestFocus();
            videoView.start();
            saveData();
        }
    }

    public void PlayNextVideo() {
        if (videoView != null) {
            CreateProgressDialogWithTimeout(10000);

            index++;
            index = (index > (Movielists.size() - 1)) ? 0 : index;
            if (videoView.isPlaying()) {
                videoView.stopPlayback();
            }
            videoView.setVideoURI(Uri.parse(Movielists.get(index).getVideoUrl()));
            videoView.requestFocus();
            videoView.start();
            saveData();
        }
    }

    public void PlayPreviousVideo() {
        if (videoView != null) {
            CreateProgressDialogWithTimeout(10000);

            index--;
            index = (index < 0) ? (Movielists.size() - 1) : index;
            if (videoView.isPlaying()) {
                videoView.stopPlayback();
            }
            videoView.setVideoURI(Uri.parse(Movielists.get(index).getVideoUrl()));
            videoView.requestFocus();
            videoView.start();
            saveData();
        }
    }

    /*
     * Shared Preferences
     */
    public void readData(){
        settings = getSharedPreferences(data,0);
        index = settings.getInt("ch", 0);
    }

    public void saveData(){
        settings = getSharedPreferences(data,0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("ch", index);
        editor.commit();

    }

    /*
     * others
     */
    private int retrieveChannels() {
        Movielists = MovieList.setupMovies();
        Channellists = new ArrayList<String>();
        for (int i = 0; i < Movielists.size(); i++) {
            Channellists.add(Movielists.get(i).getTitle());
        }

        return 0;
    }
}
