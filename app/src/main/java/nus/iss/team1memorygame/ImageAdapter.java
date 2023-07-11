package nus.iss.team1memorygame;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;


    private String[] mImagePaths;
    float scale;
    String[] selectedImage =new String[6];
    int seletedCount =0;
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
            imageView.setPadding(0, 20, 0,20);

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

                Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.scale_animation);
                imageView.setBackground(mContext.getDrawable(R.drawable.border_selected));
                imageView.startAnimation(anim);
                selectImageView.add(imageView);
                selectedImage[seletedCount] = imageId;
                seletedCount++;

                if(seletedCount==6){

                    showPopup(v);
                }

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
        PopupWindow popupWindow = new PopupWindow(popupView, 700, 700);
        Button play = popupView.findViewById(R.id.startBtn);

        Button choose = popupView.findViewById(R.id.choose);

        play.setVisibility(v.VISIBLE);
        choose.setVisibility(v.VISIBLE);

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                Arrays.fill(selectedImage, null);
                seletedCount = 0;
                for (ImageView imageView : selectImageView) {
                    imageView.setBackground(null);

                }

            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                Intent intentS = new Intent(mContext, MusicService.class);
                intentS.setAction("play");
                mContext.startService(intentS);

                Intent intent = new Intent(mContext,GameActivity.class);
                intent.putExtra("listSelected",selectedImage);
                mContext.startActivity(intent);
            }
        });
        popupWindow.setElevation(10f);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }
}