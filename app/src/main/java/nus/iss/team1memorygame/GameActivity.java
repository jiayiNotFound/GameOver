package nus.iss.team1memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {
    private PopupWindow popupWindow;

    GridView gridView;
    TextView textView;
    TextView txtMatches;
    Timer timer;
    int count =0;
    String[] selectedImage =new String[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //Achieve full screen

        gridView = findViewById(R.id.gameGrid);
        Intent intent = getIntent();
        selectedImage = intent.getStringArrayExtra("listSelected");
        gridView.setAdapter(new GameImageAdapter(GameActivity.this,selectedImage));

        IntentFilter intentFilter = new IntentFilter("game_over");
        registerReceiver(completed_Msg, intentFilter);

        txtMatches = findViewById(R.id.txtMatches);
        txtMatches.setText("0 of 6 matches");
        textView = findViewById(R.id.textS);
        startTimer();

    }

    public void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                count++;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int minutes = count / 60;
                        int hours = minutes / 60;
                        int seconds = count % 60;
                        textView.setText("Time: " + String.format("%02d:%02d::%02d",hours, minutes, seconds));
                    }
                });
            }
        }, 0, 1000);
    }

    public void stopTimer(){
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
    }
    protected BroadcastReceiver completed_Msg = new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("game_over")){
               stopTimer();
               showPopup();
            }
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
    public void showPopup(){
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_layout, null);
        popupWindow = new PopupWindow(popupView, 900, 600);
        popupWindow.setElevation(10f);
        TextView textView1 = popupView.findViewById(R.id.popView);
        int minutes = count / 60;
        int seconds = count % 60;
        String timeTaken = String.format("%02d min %02d sec", minutes, seconds);
        textView1.setText("Congratulations!!! \n\nYour Time: "+ timeTaken);
        textView1.setVisibility(View.VISIBLE);

        Button button = popupView.findViewById(R.id.startBtn);
        button.setText("Back To Home");
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent stopMusicIntent = new Intent(GameActivity.this, MusicService.class);
//                stopMusicIntent.setAction("stop");
//                startService(stopMusicIntent);
                Intent intent = new Intent(GameActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        popupWindow.showAtLocation(this.gridView, Gravity.CENTER, 0, 0);
    }

}