package nus.iss.team1memorygame;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<Object> {
    private final Context context;

    protected List<userHistory> scoreList;

    public ListViewAdapter(Context context, List<userHistory> scoreList) {
        super(context, R.layout.row);
        this.context = context;
        this.scoreList = scoreList;

        addAll(new Object[scoreList.size()]);
    }

    @androidx.annotation.NonNull
    public View getView(int pos, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row, parent, false);
        }

        // set the image for ImageView
        TextView textView = view.findViewById(R.id.text1);
        String name =scoreList.get(pos).getName();

        textView.setText(name);

        TextView textView2 = view.findViewById(R.id.text2);
        int score = scoreList.get(pos).getScore();
        String scoreString = String.valueOf(score);
        textView2.setText(scoreString);

        TextView textView3 = view.findViewById(R.id.text3);
        String time =scoreList.get(pos).getTime();
        textView3.setText(time);

        return view;
    }
}
