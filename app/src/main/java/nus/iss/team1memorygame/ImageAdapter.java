package nus.iss.team1memorygame;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageAdapter extends BaseAdapter implements CustomDialog.OnDialogButtonClickListener{
    private Context mContext;

    private String[] mImagePaths;
    String previousId;
    float scale;
    String[] selectedImage =new String[6];
    int selectedCount = 0;
    List<ImageView> selectImageView = new ArrayList<>();


    public ImageAdapter(Context context) {
        mContext = context;
        scale = context.getResources().getDisplayMetrics().density;
        File picturesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        mImagePaths = picturesDir.list();
    }

    @Override
    public int getCount() {
        return mImagePaths.length;
    }

    @Override
    public Object getItem(int position) {
        return mImagePaths[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;


        if (convertView == null) {

            imageView = new ImageView(mContext);

            imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(15, 15, 15,15);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        } else {
            imageView = (ImageView) convertView;
        }

        File imageFile = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), mImagePaths[position]);
        Uri imageUri = Uri.fromFile(imageFile);
        imageView.setImageURI(imageUri);

        String imageName = "image" + (position + 1);
        imageView.setTag(imageName);
        if(position>19){
            imageView.setVisibility(View.INVISIBLE);
        }
        String imageId = getItem(position).toString();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean res =false;
                if(selectedImage.length!=0){
                    for (int i = 0; i < selectedImage.length; i++) {
                        if (selectedImage[i] == imageId) {
                            res = true;
                        }
                    }
                }
                if(!res){
                    if(selectedCount<6){
                        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.scale_animation);
                        imageView.setBackground(mContext.getDrawable(R.drawable.border_selected));
                        imageView.setElevation(10f);
                        imageView.startAnimation(anim);
                        selectImageView.add(imageView);
                        selectedImage[selectedCount] = imageId;
                    }
                    selectedCount++;
                    if(selectedCount==6){
                        showPopup(v);
                    }
                }
                previousId = imageId;
            }
        });

        return imageView;
    }
    private int dpToPx(int dp) {
        float density = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
    public void showPopup(View v){
        View popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_layout, null);
        PopupWindow popupWindow = new PopupWindow(popupView, 1000, 800);

        TextView txtPopup = popupView.findViewById(R.id.popView);
        txtPopup.setText("Images Selected! \n\nReady to Play?");
        Button play = popupView.findViewById(R.id.startBtn);
        Button choose = popupView.findViewById(R.id.choose);

        txtPopup.setVisibility(View.VISIBLE);
        play.setVisibility(v.VISIBLE);
        choose.setVisibility(v.VISIBLE);

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                Arrays.fill(selectedImage, null);
                selectedCount = 0;
                for (ImageView imageView : selectImageView) {
                    imageView.setBackground(null);
                }
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                showDialog();
            }
        });
        popupWindow.setElevation(10f);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }


    private void showDialog() {
        CustomDialog customDialog = new CustomDialog(mContext);
        customDialog.setOnDialogButtonClickListener(this);
        customDialog.show();
    }
    @Override
    public void onButtonClicked(String inputText) {

        if(inputText!=null){
            Intent intentS = new Intent(mContext, MusicService.class);
            intentS.setAction("play");
            mContext.startService(intentS);

            Intent intent = new Intent(mContext,GameActivity.class);
            intent.putExtra("listSelected",selectedImage);
            intent.putExtra("username",inputText);
            mContext.startActivity(intent);
        }

    }

}

