package com.example.rememberthenumber;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Spinner timeSpinner;
    Spinner difficultySpinner;
    String[] timeIntervals={"00:30","00:45","01:00","01:30","02:00","03:00","05:00"};
    String[] difficultyLevels={"Easy"};
    private TextView spinnerValueTextView;
    private TextView difficultyLevelTextView;
    ArrayAdapter timeIntervalArray;
    TimeSpinnerAdapter timeSpinnerAdapter;
    DifficultySpinnerAdapter difficultySpinnerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeSpinner = findViewById(R.id.timerSpinner);
        difficultySpinner = findViewById(R.id.difficultySpinner);

        spinnerValueTextView = findViewById(R.id.spinnerTextView);
        difficultyLevelTextView = findViewById(R.id.setDifficultyLevelTextView);

        timeSpinnerAdapter = new TimeSpinnerAdapter(this,R.layout.time_spinner_value,timeIntervals);
        difficultySpinnerAdapter = new DifficultySpinnerAdapter(this,R.layout.difficulty_spinner_value,difficultyLevels);

        timeSpinner.setAdapter(timeSpinnerAdapter);
        difficultySpinner.setAdapter(difficultySpinnerAdapter);
    }

    public void playGame(View view) {
        //openDialog();
        int timeIntervaPos = timeSpinner.getSelectedItemPosition();
        int difficultyLevelPos = difficultySpinner.getSelectedItemPosition();
        String timeSelected = timeIntervals[timeIntervaPos];
        String difficultyLevelChoosen= difficultyLevels[difficultyLevelPos];
        long timeInMillis = convertInMillis(timeSelected);

        Intent intent = new Intent(this,PlayActivity_Async.class);
        intent.putExtra("Time",timeInMillis);
        intent.putExtra("Difficulty",difficultyLevelChoosen);

        startActivity(intent);
    }

    private long convertInMillis(String timeSelected) {
        String[] time = timeSelected.split(":");
        int minutes = Integer.parseInt(time[0]);
        int seconds = Integer.parseInt(time[1]);
        return ((minutes*60)+seconds)*1000;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

