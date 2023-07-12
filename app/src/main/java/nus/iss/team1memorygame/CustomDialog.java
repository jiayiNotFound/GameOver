package nus.iss.team1memorygame;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class CustomDialog extends Dialog {
    private OnDialogButtonClickListener buttonClickListener;

    public CustomDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register_user);
        Button startButton = findViewById(R.id.startgame);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonClickListener != null) {
                    EditText playerNameEditText = findViewById(R.id.playername);
                    String inputText = playerNameEditText.getText().toString();
                    buttonClickListener.onButtonClicked(inputText);
                }
                dismiss();
            }
        });

    }
    public interface OnDialogButtonClickListener {
        void onButtonClicked(String inputText);
    }
    public void setOnDialogButtonClickListener(OnDialogButtonClickListener listener) {
        buttonClickListener = listener;
    }
}
