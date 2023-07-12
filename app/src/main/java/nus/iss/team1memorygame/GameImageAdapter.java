package nus.iss.team1memorygame;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameImageAdapter extends BaseAdapter {
    private Context mContext;
    private String[] hidden = new String[12];
    String previousSelect = "";
    int previousPosition;
    ImageView previousImage;
    ImageView temp;
    int match=0;


    public GameImageAdapter(Context context, String[] myselectedImage) {
        this.mContext = context;

        Random random = new Random();
        List<String> selectedImageList = Arrays.asList(myselectedImage);
        Collections.shuffle(selectedImageList);
        String[] shuffledArray1 = selectedImageList.toArray(new String[6]);

        Collections.shuffle(selectedImageList);
        String[] shuffledArray2 = selectedImageList.toArray(new String[6]);


        String[] mergedArray = new String[12];
        System.arraycopy(shuffledArray1, 0, mergedArray, 0, 6);
        System.arraycopy(shuffledArray2, 0, mergedArray, 6, 6);

        hidden = mergedArray;
        Collections.shuffle(Arrays.asList(hidden), random);

    }

    @Override
    public int getCount() {
        if (hidden != null) {
            return hidden.length;
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;


        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(400, 500);
            imageView.setLayoutParams(layoutParams);
        } else {

            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(R.drawable.dog);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  if(previousPosition !=position){
                      File imageFile = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), hidden[position]);
                      Uri imageUri = Uri.fromFile(imageFile);
                      int isSame =0;
                      imageView.setImageURI(imageUri);
//                    previousImage =imageView;

                      if(previousSelect.equals(hidden[position])){
                          imageView.setImageURI(imageUri);
                          imageView.setOnClickListener(null);
                          previousImage.setOnClickListener(null);

                          isSame =1;
                          match++;
                          if(mContext instanceof GameActivity){
                              TextView txtMatches = ((GameActivity)mContext).findViewById(R.id.txtMatches);
                              txtMatches.setText(String.format("%d of 6 matches", match));
                          }
                          if(match==6){
                              Intent completed = new Intent();
                              completed.setAction("game_over");
                              mContext.sendBroadcast(completed);
                          }
                      }
                      else if(previousImage!=null){
                          previousImage.setImageResource(R.drawable.dog);
                      }
                      if(isSame==1){
                          previousSelect="";
                          previousImage =null;
                      }else {
                          previousSelect=hidden[position];
                          previousImage =imageView;
                      }

                  }
                previousPosition =position;
            }
        });
        return imageView;
    }
}
