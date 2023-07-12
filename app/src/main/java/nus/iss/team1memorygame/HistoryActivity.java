package nus.iss.team1memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
List<userHistory> scoreList = new ArrayList<>();
Button btn;
    File mTargetFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Intent intent = getIntent();
        mTargetFile =(File) intent.getSerializableExtra("file");

        readFromFile();
        ListView listView = findViewById(R.id.listView);
        if(listView != null) {
            listView.setAdapter(new ListViewAdapter(this, scoreList));
            listView.setOnItemClickListener(this);
        }
        btn = findViewById(R.id.backHome);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backHome = new Intent(HistoryActivity.this,MainActivity.class);
                startActivity(backHome);
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
    protected void readFromFile() {
        try {
            FileInputStream fis = new FileInputStream(mTargetFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = br.readLine()) != null) {
                // Split the line into parts using ":" as the delimiter
                String[] parts = line.split(":");

                if (parts.length >= 2) {
                    String name = parts[0].trim();
                    String recordData = parts[1].trim();

                    // Split the recordData into score and time using whitespace as the delimiter
                    String[] recordParts = recordData.split("\\s+");

                    if (recordParts.length >= 3) {
                        int score = Integer.parseInt(recordParts[0].trim());
                        String time = recordParts[1].trim() + " " + recordParts[2].trim();

                        // Create a new userHistory object and set the data
                        userHistory his = new userHistory();
                        his.setName(name);
                        his.setScore(score);
                        his.setTime(time);

                        // Add the userHistory object to the scoreList
                        scoreList.add(his);
                    }
                }
            }

            br.close();
            Toast.makeText(this, "Read file ok!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}