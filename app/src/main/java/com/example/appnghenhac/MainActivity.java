package com.example.appnghenhac;

import static com.example.appnghenhac.services.MusicServices.ACTION_BACK;
import static com.example.appnghenhac.services.MusicServices.ACTION_NEXT;
import static com.example.appnghenhac.services.MusicServices.ACTION_PAUSE;
import static com.example.appnghenhac.services.MusicServices.ACTION_RESUME;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.appnghenhac.model.Music;
import com.example.appnghenhac.services.MusicServices;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
@RequiresApi(api = Build.VERSION_CODES.S)
public class MainActivity extends AppCompatActivity {
    //Static var
    public static ArrayList<Music> musicArrayList = new ArrayList<>();
    public static int index  = 0;
    public static MediaPlayer mediaPlayer = null;
    public static boolean isPlay = false;
    IntentFilter intentFilter;
    ImageView play,back,next;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        play = findViewById(R.id.play);
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seek);
        play.setOnClickListener(clickListener);
        next.setOnClickListener(clickListener);
        back.setOnClickListener(clickListener);
        intentFilter = new IntentFilter();
        intentFilter.addAction("PAUSE");
        intentFilter.addAction("RESUME");
        //Handle xin quyen
        xinquyen();
    }
    //Handle click Event
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.play:
                    if(isPlay)
                    {
                        if(mediaPlayer == null) break;
                        stopSong();
                    }else {
                        playSong();

                    }
                    break;
                case R.id.back:
                    backSong();
                    break;
                case R.id.next:
                    nextSong();
                    break;
                case R.id.seek:
                    break;
            }
        }
    };

    private void nextSong() {
        Intent intent = new Intent(this, MusicServices.class);
        intent.putExtra("action", ACTION_NEXT);
        startService(intent);
    }

    private void backSong() {
        Intent intent = new Intent(this, MusicServices.class);
        intent.putExtra("action", ACTION_BACK);
        startService(intent);
    }




    private void stopSong() {
        Intent intent = new Intent(this, MusicServices.class);
        intent.putExtra("action", ACTION_PAUSE);
        startService(intent);
        play.setImageResource(R.drawable.baseline_play_circle_24);
    }

    //Ham chơi nhạc
    void playSong()
    {
        if(musicArrayList.size() == 0)
        {
            Toast.makeText(this, "Ban chua co bai nhac nao o thu muc down", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mediaPlayer != null)
        {
            Intent intent = new Intent(this, MusicServices.class);
            intent.putExtra("action", ACTION_RESUME);
            startService(intent);
            play.setImageResource(R.drawable.baseline_pause_circle_24);
        }else {

            Intent intent = new Intent(this, MusicServices.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("music",musicArrayList.get(index));
            intent.putExtra("action", ACTION_RESUME);
            intent.putExtras(bundle);
            startService(intent);

            isPlay = !isPlay;
            play.setImageResource(R.drawable.baseline_pause_circle_24);
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);

    }
    //BroadcastReceiver nhận sự kiện từ service
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int x = 2;
            switch (intent.getAction())
            {
                case "PAUSE" :
                    play.setImageResource(R.drawable.baseline_play_circle_24);
                    break;
                case "RESUME" :
                    play.setImageResource(R.drawable.baseline_pause_circle_24);
                    break;
            }
        }
    };
    //Hàm xin quyền
    private void xinquyen() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //Lấy các file MP3 từ folder down
            loadMP3();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private  void loadMP3()
    {
        ArrayList<HashMap<String,String>> songList=getPlayList(
                Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS)
                        .getAbsolutePath());
        if(songList!=null){
            for(int i=0;i<songList.size();i++){
                String fileName=songList.get(i).get("file_name");
                String filePath=songList.get(i).get("file_path");
                musicArrayList.add(new Music(fileName,fileName,"",filePath));
            }
        }
    }
    //Kiểm tra nếu xin quyền thành công
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1 && grantResults.length > 0)
        {
            loadMP3();
        }
    }

    //Hàm truy cập lấy các file có đuôi là mp3
    ArrayList<HashMap<String,String>> getPlayList(String rootPath) {
        ArrayList<HashMap<String,String>> fileList = new ArrayList<>();
        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles(); //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (File file : files) {
                if (file.isDirectory()) {
                    if (getPlayList(file.getAbsolutePath()) != null) {
                        fileList.addAll(getPlayList(file.getAbsolutePath()));
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(".mp3")) {
                    HashMap<String, String> song = new HashMap<>();
                    song.put("file_path", file.getAbsolutePath());
                    song.put("file_name", file.getName());
                    fileList.add(song);
                }
            }
            return fileList;
        } catch (Exception e) {
            return null;
        }
    }

}