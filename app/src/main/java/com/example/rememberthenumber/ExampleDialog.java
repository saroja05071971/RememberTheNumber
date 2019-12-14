package com.example.rememberthenumber;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Objects;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class ExampleDialog extends AppCompatDialogFragment {
    private EditText userInputVal;
    private ExampleDialogListener listener;
    PlayActivity.MyCountDownTimer myCountDownTimer;
    Spinner timerSpinner;

    public ExampleDialog(PlayActivity.MyCountDownTimer myCountDownTimer) {
        this.myCountDownTimer = myCountDownTimer;
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
                        ((PlayActivity) getActivity()).setmStopLoop(FALSE);

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String inputVal = userInputVal.getText().toString();
                        listener.applyTexts(inputVal);
                    }
                });

        userInputVal = view.findViewById(R.id.userInputDialog);

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
}
