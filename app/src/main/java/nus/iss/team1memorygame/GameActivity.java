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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {
    private PopupWindow popupWindow;

    GridView gridView;
    TextView textView;
    Timer timer;
    int count =0;

    String username;
    String[] selectedImage =new String[6];


    String filePath = "userHistory";
    String fileName = "history.txt";
    File mTargetFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        gridView = findViewById(R.id.gameGrid);
        Intent intent = getIntent();
        selectedImage = intent.getStringArrayExtra("listSelected");
        username  = intent.getStringExtra("username");
        gridView.setAdapter(new GameImageAdapter(GameActivity.this,selectedImage));

        IntentFilter intentFilter = new IntentFilter("game_over");
        registerReceiver(completed_Msg, intentFilter);

        textView =findViewById(R.id.textS);
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
                        textView.setText("Time Spend: " + count + " s");
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
                writeToFile(username,count);
            }
        }

    };

    protected void writeToFile(String resource, int count) {
        try {
            // Make sure that the parent folder exists
            File parent = new File(getFilesDir(), filePath);
            if (!parent.exists() && !parent.mkdirs()) {
                throw new IllegalStateException("Couldn't create dir: " + parent);
            }

            // Create or append to the target file
            mTargetFile = new File(parent, fileName);
            FileWriter fileWriter = new FileWriter(mTargetFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Get the current local date and time
            LocalDateTime now = LocalDateTime.now();

            // Format the record with username, count, and current time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTime = now.format(formatter);
            String record = resource + ": " + count + " " + formattedTime;

            // Write the record to the file
            bufferedWriter.write(record);
            bufferedWriter.newLine(); // Add a new line

            bufferedWriter.close(); // Close the writer
            Toast.makeText(this, "Write file ok!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
    public void showPopup(){
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_layout, null);
        popupWindow = new PopupWindow(popupView, 1000, 1000);
        popupWindow.setElevation(10f);
        TextView textView1 = popupView.findViewById(R.id.popView);
        textView1.setText("Game Over, time spend: "+count);
        textView1.setVisibility(View.VISIBLE);

        Button button =popupView.findViewById(R.id.startBtn);
        button.setText("Play again");
        button.setVisibility(View.VISIBLE);

        Button viewHistory = popupView.findViewById(R.id.choose);
        viewHistory.setText("View History");
        viewHistory.setVisibility(View.VISIBLE);
        viewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameActivity.this,HistoryActivity.class);
                intent.putExtra("file",mTargetFile);
                startActivity(intent);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        popupWindow.showAtLocation(this.gridView, Gravity.CENTER, 0, 0);
    }



}