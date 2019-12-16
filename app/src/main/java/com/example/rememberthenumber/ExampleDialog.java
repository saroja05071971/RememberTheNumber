package com.example.rememberthenumber;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ShareCompat;

import java.util.Objects;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class ExampleDialog extends AppCompatDialogFragment {
    private EditText userInputVal;
    private ExampleDialogListener listener;
    PlayActivity_Async.MyCountDownTimer myCountDownTimer;
    Spinner timerSpinner;
    private int score;

    public ExampleDialog(PlayActivity_Async.MyCountDownTimer myCountDownTimer,int score) {
        this.myCountDownTimer = myCountDownTimer;
        this.score = score;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.user_input_dialog_box, null);

        builder.setView(view)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //cancel functionality
                       // ((PlayActivity) getActivity()).setmStopLoop(FALSE);
                        dismiss();

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //String inputVal = userInputVal.getText().toString();
                        //listener.applyTexts(inputVal);
                        String textToShare = "I have scored "+score+" in Remember the Number "+"https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";;
                        shareText(textToShare);
                    }
                });

        //userInputVal = view.findViewById(R.id.userInputDialog);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogListener {
        void applyTexts(String favoriteNumber);
    }

    public void shareText(String text) {
        String mimeType = "text/plain";
        String title = "I recommend this app to ";

        Intent shareIntent =   ShareCompat.IntentBuilder.from(getActivity())
                .setType(mimeType)
                .setText(text)
                .getIntent();
        if (shareIntent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivity(shareIntent);
        }
    }
}
