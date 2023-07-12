package nus.iss.team1memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.Looper;
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button fetchBtn;
    EditText urlText;
    GridView gridView;

    TextView textView;

    ProgressBar progressBar;
    String url;
    File dir;
    File destFile;
    int downloadProgress;

    private PopupWindow popupWindow;

    Thread bkgdThread;
//    List<String> imageUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //use to store image
        dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        gridView = findViewById(R.id.gridView);

        fetchBtn = findViewById(R.id.FetchBtn);
        fetchBtn.setOnClickListener(this);

        urlText = findViewById(R.id.EditURL);

        progressBar = findViewById(R.id.progressBar);

        textView = findViewById(R.id.textView);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        url = urlText.getText().toString();
        if(id==R.id.FetchBtn){
//            url="https://stocksnap.io";
            if(isHttpUrl(url)){
                downloadProgress = 0;
                Toast.makeText(MainActivity.this, "Downloading...", Toast.LENGTH_SHORT).show();
                if (bkgdThread != null) {
                    bkgdThread.interrupt();
                    return;
                }
                bkgdThread = findImageUrls(url);
                bkgdThread.start();
            }
            else {
                Toast.makeText(MainActivity.this, "Please enter valid URL", Toast.LENGTH_SHORT).show();
            }
        }
        else if(id == R.id.gridView){
            ImageView imageView = (ImageView) view;
            String imageName = (String) imageView.getTag();
            Toast.makeText(this, "Clicked image: " + imageName, Toast.LENGTH_LONG).show();
        }
    }

    public Thread findImageUrls(String urlString) {

        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    int count = 1;
                    if (Thread.interrupted()) {
                        bkgdThread = null;
                        return;
                    }
                    Document doc = Jsoup.connect(urlString).get();
                    Elements images = doc.select("img[src$=.jpg], img[src$=.png], img[src$=.jpeg], img[src$=.gif]");
                    Elements first20Images = images.stream()
                            .limit(20)
                            .collect(Collectors.toCollection(Elements::new));

                    for (Element image : first20Images) {

                        String imageUrl = image.attr("src");
                        String fileExtension = imageUrl.substring(imageUrl.lastIndexOf("."));
                        String filename = "image" + count + fileExtension;
                        destFile = new File(dir, filename);
                        count++;

                        if(FetchPic(imageUrl)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    downloadProgress+=1;
//                                    textView.setText(downloadProgress+"/20");
                                    textView.setText(String.format("Downloading %d of 20 images..", downloadProgress));
                                    textView.setVisibility(View.VISIBLE);
                                    progressBar.setProgress(downloadProgress);
                                    progressBar.setVisibility(View.VISIBLE);
                                    gridView.setAdapter(new ImageAdapter(MainActivity.this));
                                    if(downloadProgress==20){
                                        Handler handler = new Handler();
                                        Intent intentS = new Intent(MainActivity.this, MusicService.class);
                                        intentS.setAction("downloaded");
                                        startService(intentS);
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Handler handler = new Handler();
                                                showPopup();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // remove the progress bar after download complete
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        textView.setVisibility(View.INVISIBLE);
                                                        popupWindow.dismiss(); // 关闭弹出窗口
                                                    }
                                                }, 3000);
                                            }
                                        }, 1000);
                                    }
                                }
                            });
                        }
                    }
                    bkgdThread = null;
                    if(images.size()==0 ){
                        Toast.makeText(MainActivity.this, "No image were downloaded", Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                } catch (Exception e) {
                    bkgdThread= null;
                    e.printStackTrace();
                }
            }
        });

    }
    public void showPopup(){
        View popupView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popup_layout, null);
        popupWindow = new PopupWindow(popupView, 900, 500);
        popupWindow.setElevation(10f);
        TextView textView1 = popupView.findViewById(R.id.popView);
        textView1.setVisibility(View.VISIBLE);
        popupWindow.showAtLocation(gridView, Gravity.CENTER, 0, 0);
    }

    public boolean FetchPic(String pathToDownload){
        try{
            URL url = new URL(pathToDownload);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
            InputStream in  = conn.getInputStream();
            FileOutputStream out = new FileOutputStream(destFile);
            byte[] tempBuff = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead =in.read(tempBuff)) !=-1){
                out.write(tempBuff,0,bytesRead);
            }
            out.close();
            in.close();
            return true;

        }catch (Exception e){
            bkgdThread =null;
            Log.e(e.toString(), "pullPic: "+e.toString());
            return false;
        }
    }

    public static boolean isHttpUrl(String urls) {
        try {
            URL url = new URL(urls);
            url.toURI();
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}



