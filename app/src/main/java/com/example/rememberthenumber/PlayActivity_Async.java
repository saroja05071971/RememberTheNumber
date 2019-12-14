package com.example.rememberthenumber;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.Random;

/*
//Add9ng adview api's
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
*/

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.compare;
import static java.lang.Thread.sleep;

public class PlayActivity_Async extends AppCompatActivity{

    MyCountDownTimer myCountDownTimer;
    private Context context = this;
    private TextView tvNum1;
    private TextView tvNum2;
    private TextView tvOperator;
    private TextView timeTextView;
    private TextView tvInstruction;
    private EditText str_result;
    private String fav_num;
    private int num1 = 0;
    private int num2 = 0;
    private boolean counter;
    private boolean mStopLoop;
    private static final String TAG = "PlayActivity";

    private Button option_1;
    private Button option_2;
    private Button option_3;
    private Button option_4;
    private int optionNumberOne;
    private int optionNumberTwo;
    private int optionNumberThree;
    private int optionNumberFour;

    private int optionCorrect;
    private int score;
    private TextView tvScore;

    private Toast timeOver;
    private Toast rightAnswerAsToast;

    private Random r;

    private int max;
    private int min;

    private int optionClickedNumber;

    private boolean optionSelected;

    CustomHandlerThread customHandlerThread;
    OptionRestoreHandlerThread optionRestoreHandlerThread;
    private int result;
    private int correctResult;
    private int numberToHide;
    private int operator;
    private int temp;

    private int yOffSet = 20;

    private long timeToSleep;

    ColorStateList timeTextOrigColor;
    ColorStateList optionsOrigColor;
    Animation startAnimation;
    private boolean stopAnimationCounter;

    LinearLayout optionLayout;
    private int timeToSleepAfterChangingColor;
    private int timeToSleepBeforeChangingOption;

    private String difficultyLevel;

    private ButtonClick buttonClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Intent intent = getIntent();
        long timeInMillis = intent.getLongExtra("Time", 30000);
        difficultyLevel = intent.getStringExtra("Difficulty");
        Log.d(TAG, "timeInMillis = " + timeInMillis + " difficultyLevel = " + difficultyLevel);

        buttonClick = new ButtonClick();
        initUI();
        customHandlerThread = new CustomHandlerThread("CustomHandlerThread");
        customHandlerThread.start();
        optionRestoreHandlerThread = new OptionRestoreHandlerThread("OptionRestoreHandlerThread");
        optionRestoreHandlerThread.start();
        myCountDownTimer = new MyCountDownTimer(timeInMillis, 1000);
        startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blinking_animation);
        startGame();
    }

    private void customizeUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "Setting background color");
            option_1.setBackground(getDrawable(R.drawable.btn_options));
            option_2.setBackground(getDrawable(R.drawable.btn_options));
            option_3.setBackground(getDrawable(R.drawable.btn_options));
            option_4.setBackground(getDrawable(R.drawable.btn_options));
        }
    }

    public boolean ismStopLoop() {
        return mStopLoop;
    }

    public void setmStopLoop(boolean mStopLoop) {
        this.mStopLoop = mStopLoop;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (customHandlerThread != null) {
            customHandlerThread.getLooper().quit();
            Log.d(TAG, "customHandlerThread is cancelled");
        }
        if (myCountDownTimer != null) {
            myCountDownTimer.cancel();
            Log.d(TAG, "myCountDownTimer is cancelled");
        }
        if (rightAnswerAsToast != null) {
            rightAnswerAsToast.cancel();
            Log.d(TAG, "rightAnswerAsToast is cancelled");
        }
        if (optionRestoreHandlerThread != null) {
            optionRestoreHandlerThread.getLooper().quit();
            Log.d(TAG, "optionRestoreHandlerThread is cancelled");
        }
    }

    private void initUI() {
        tvNum1 = findViewById(R.id.num1_text);
        tvNum2 = findViewById(R.id.num2_text);
        tvOperator = findViewById(R.id.operator_text);
        timeTextView = findViewById(R.id.timeTextView);
        timeTextOrigColor = timeTextView.getTextColors();
        option_1 = findViewById(R.id.option1);
        option_2 = findViewById(R.id.option2);
        option_3 = findViewById(R.id.option3);
        option_4 = findViewById(R.id.option4);
        option_1.setOnClickListener(buttonClick);
        option_2.setOnClickListener(buttonClick);
        option_3.setOnClickListener(buttonClick);
        option_4.setOnClickListener(buttonClick);
        optionsOrigColor = option_4.getTextColors();
        tvScore = findViewById(R.id.tvScore);
        r = new Random();
        tvInstruction = findViewById(R.id.tvInstruction);
        modifyExpressionVisibility(FALSE);

        timeOver = Toast.makeText(context, "Time's Up", Toast.LENGTH_LONG);
        LinearLayout toastLayout = (LinearLayout) timeOver.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toastTV.setTextSize(30);
        timeOver.setGravity(Gravity.CENTER, 0, 0);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        rightAnswerAsToast = Toast.makeText(context, "Correct Answer is " + result, Toast.LENGTH_SHORT);
        yOffSet = (displayMetrics.heightPixels) / 2 - 10;
        Log.d(TAG, "yOffSet = " + yOffSet);
        rightAnswerAsToast.setGravity(Gravity.CENTER, 0, yOffSet);

        timeToSleepAfterChangingColor = 300;
        timeToSleepBeforeChangingOption = 250;
        timeToSleep = 350;
        optionLayout = findViewById(R.id.optionLayout);
        numberToHide = -1;
    }

    public void startGame() {
        setmStopLoop(TRUE);
        if (ismStopLoop() == TRUE) {
            optionSelected = TRUE;
            score = 0;
            stopAnimationCounter = TRUE;
            tvScore.setText("Score :" + score);
            modifyOptionLayoutVisibilityVisibility(TRUE);
            myCountDownTimer.start();
            restoreOptionsOrigColor();
            tvInstruction.setVisibility(View.INVISIBLE);
            modifyExpressionVisibility(TRUE);
            executeOnCustoLooperWithCustomHandlerFirstTime();
        }
    }

    private void executeOnCustoLooperWithCustomHandlerFirstTime() {
        customHandlerThread.mHandle.post(new Runnable() {
            @Override
            public void run() {
                if (optionSelected == TRUE) {
                    num1 = r.nextInt(20) + 1;
                    num2 = r.nextInt(20) + 1;
                    operator = r.nextInt(2) + 1;
                    generateResultBasedOnOperator(operator);
                    while (TRUE) {
                        if (result == 0 && numberToHide == 1) {
                            num2 = r.nextInt(30) + 1;
                            generateResultBasedOnOperator(operator);
                        } else if (result == 0 && numberToHide == 2) {
                            num1 = r.nextInt(30) + 1;
                            generateResultBasedOnOperator(operator);
                        } else {
                            break;
                        }
                    }
                    optionCorrect = r.nextInt(4) + 1;
                    correctResult = optionCorrect;
                    if (optionCorrect == 1) {
                        optionNumberOne = result;
                        generateWrongOptions(1, result);
                    } else if (optionCorrect == 2) {
                        optionNumberTwo = result;
                        generateWrongOptions(2, result);
                    } else if (optionCorrect == 3) {
                        optionNumberThree = result;
                        generateWrongOptions(3, result);
                    } else if (optionCorrect == 4) {
                        optionNumberFour = result;
                        generateWrongOptions(4, result);
                    } else {
                        optionNumberOne = result;
                        generateWrongOptions(1, result);
                    }
                    optionSelected = TRUE;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "Inside the runOnUI thread First Time");
                            tvNum1.setVisibility(View.VISIBLE);
                            tvNum2.setVisibility(View.VISIBLE);
                            tvNum1.setText(String.valueOf(num1));
                            tvNum2.setText(String.valueOf(num2));

                            if (operator == 1) {
                                tvOperator.setText("+");
                            } else if (operator == 2) {
                                tvOperator.setText("-");
                            }

                            option_1.setText(String.valueOf(optionNumberOne));
                            option_2.setText(String.valueOf(optionNumberTwo));
                            option_3.setText(String.valueOf(optionNumberThree));
                            option_4.setText(String.valueOf(optionNumberFour));
                        }
                    });
                }
            }
        });
    }

    private void updateUI() {
        //disableOptions();
        if (numberToHide == 1) {
            tvNum1.setVisibility(View.INVISIBLE);
            tvNum2.setVisibility(View.VISIBLE);
            tvNum2.setText(String.valueOf(num2));
        } else if (numberToHide == 2) {
            tvNum1.setVisibility(View.VISIBLE);
            tvNum2.setVisibility(View.INVISIBLE);
            tvNum1.setText(String.valueOf(num1));
        } else if (numberToHide == -1) {
            tvNum1.setVisibility(View.VISIBLE);
            tvNum2.setVisibility(View.VISIBLE);
            tvNum1.setText(String.valueOf(num1));
            tvNum2.setText(String.valueOf(num2));
        }
        if (operator == 1) {
            tvOperator.setText("+");
        } else if (operator == 2) {
            tvOperator.setText("-");
        }
        option_1.setText(String.valueOf(optionNumberOne));
        option_2.setText(String.valueOf(optionNumberTwo));
        option_3.setText(String.valueOf(optionNumberThree));
        option_4.setText(String.valueOf(optionNumberFour));
    }

    private void generateResultBasedOnOperator(int operator) {
        if (operator == 1) {
            result = num1 + num2;
        } else if (operator == 2) {
            result = num1 - num2;
        }
    }

    private void generateWrongOptions(int numberToExclude, int result) {
        max = (result * 3) / 2;
        min = (result * 1) / 2;
        if (max < 0 || min < 0) {
            temp = min;
            min = max;
            max = temp;
        }
        if (Math.abs(max - min) < 3) {
            min = min - 2;
            max = max + 2;
        }
        if (numberToExclude == 1) {
            do {
                optionNumberTwo = r.nextInt(max - min + 1) + min;
                optionNumberThree = r.nextInt(max - min + 1) + min;
                optionNumberFour = r.nextInt(max - min + 1) + min;
            } while (everyOptionIsDifferent());
        } else if (numberToExclude == 2) {
            do {
                optionNumberOne = r.nextInt(max - min + 1) + min;
                optionNumberThree = r.nextInt(max - min + 1) + min;
                optionNumberFour = r.nextInt(max - min + 1) + min;
            } while (everyOptionIsDifferent());
        } else if (numberToExclude == 3) {
            do {
                optionNumberOne = r.nextInt(max - min + 1) + min;
                optionNumberTwo = r.nextInt(max - min + 1) + min;
                optionNumberFour = r.nextInt(max - min + 1) + min;
            } while (everyOptionIsDifferent());
        } else if (numberToExclude == 4) {
            do {
                optionNumberOne = r.nextInt(max - min + 1) + min;
                optionNumberTwo = r.nextInt(max - min + 1) + min;
                optionNumberThree = r.nextInt(max - min + 1) + min;
            } while (everyOptionIsDifferent());
        }
    }

    private boolean everyOptionIsDifferent() {
        if (optionNumberOne == optionNumberTwo || optionNumberOne == optionNumberThree || optionNumberOne == optionNumberFour ||
                optionNumberTwo == optionNumberThree || optionNumberTwo == optionNumberFour || optionNumberThree == optionNumberFour)
            return TRUE;
        else
            return FALSE;

    }

    private int checkResult() {
        if (optionCorrect == 1) {
            return 1;
        } else if (optionCorrect == 2) {
            return 2;
        } else if (optionCorrect == 3) {
            return 3;
        } else if (optionCorrect == 4) {
            return 4;
        } else {
            return 0;
        }
    }

    private void sleepForSomeTime(long millisToSleep) {
        try {
            // thread to sleep for custom milliseconds
            //restoreOptionsOrigColor();
            sleep(millisToSleep);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void changeOptionColor(Button button, Boolean isTrue) {
        if (isTrue == TRUE) {
            button.setBackgroundColor(Color.GREEN);
        } else {
            button.setBackgroundColor(Color.RED);
        }

    }

    private void executeScoreAndOptionUpdation() {
        optionRestoreHandlerThread.mHandle.post(new Runnable() {
            @Override
            public void run() {
                if (optionClickedNumber == correctResult) {
                    correctOptionColor(correctResult, TRUE);
                } else if (optionClickedNumber == 1) {
                    correctOptionColor(correctResult, TRUE);
                    changeOptionColor(option_1, FALSE);
                    //rightAnswerAsToast.show();
                    rightAnswerAsToast.setGravity(Gravity.CENTER, 0, yOffSet);
                    rightAnswerAsToast = Toast.makeText(context, "Correct Answer is " + result, Toast.LENGTH_SHORT);
                    //rightAnswerAsToast.getView().getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                    rightAnswerAsToast.show();
                } else if (optionClickedNumber == 2) {
                    correctOptionColor(correctResult, TRUE);
                    changeOptionColor(option_2, FALSE);
                    rightAnswerAsToast.setGravity(Gravity.CENTER, 0, yOffSet);
                    rightAnswerAsToast = Toast.makeText(context, "Correct Answer is " + result, Toast.LENGTH_SHORT);
                    //rightAnswerAsToast.getView().getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                    rightAnswerAsToast.show();
                } else if (optionClickedNumber == 3) {
                    correctOptionColor(correctResult, TRUE);
                    changeOptionColor(option_3, FALSE);
                    rightAnswerAsToast.setGravity(Gravity.CENTER, 0, yOffSet);
                    rightAnswerAsToast = Toast.makeText(context, "Correct Answer is " + result, Toast.LENGTH_SHORT);
                    //rightAnswerAsToast.getView().getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                    rightAnswerAsToast.show();
                } else if (optionClickedNumber == 4) {
                    correctOptionColor(correctResult, TRUE);
                    changeOptionColor(option_4, FALSE);
                    rightAnswerAsToast.setGravity(Gravity.CENTER, 0, yOffSet);
                    rightAnswerAsToast = Toast.makeText(context, "Correct Answer is " + result, Toast.LENGTH_SHORT);
                    //rightAnswerAsToast.getView().getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                    rightAnswerAsToast.show();
                }
                try {
                    sleep(timeToSleep);
                    restoreOptionsOrigColor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void restoreOptionsOrigColor() {
        option_1.setBackgroundResource(R.drawable.btn_options);
        option_2.setBackgroundResource(R.drawable.btn_options);
        option_3.setBackgroundResource(R.drawable.btn_options);
        option_4.setBackgroundResource(R.drawable.btn_options);
    }

    private void correctOptionColor(int correctResultNumber, Boolean isTrue) {
        if (correctResultNumber == 1) {
            changeOptionColor(option_1, isTrue);
        } else if (correctResultNumber == 2) {
            changeOptionColor(option_2, isTrue);
        } else if (correctResultNumber == 3) {
            changeOptionColor(option_3, isTrue);
        } else if (correctResultNumber == 4) {
            changeOptionColor(option_4, isTrue);
        }
    }

    private void executeOnCustoLooperWithCustomHandler() {
        customHandlerThread.mHandle.post(new Runnable() {
            @Override
            public void run() {
                if (optionSelected == TRUE) {
                    numberToHide = r.nextInt(2) + 1;
                    operator = r.nextInt(2) + 1;
                    operator = r.nextInt(2) + 1;
                    if (numberToHide == 1) {
                        num2 = r.nextInt(30) + 1;
                        num1 = result;
                    } else if (numberToHide == 2) {
                        num1 = r.nextInt(30) + 1;
                        num2 = result;
                    }
                    generateResultBasedOnOperator(operator);
                    while (TRUE) {
                        if (result == 0 && numberToHide == 1) {
                            num2 = r.nextInt(30) + 1;
                            generateResultBasedOnOperator(operator);
                        } else if (result == 0 && numberToHide == 2) {
                            num1 = r.nextInt(30) + 1;
                            generateResultBasedOnOperator(operator);
                        } else {
                            break;
                        }
                    }
                    optionCorrect = r.nextInt(4) + 1;
                    correctResult = optionCorrect;
                    if (optionCorrect == 1) {
                        optionNumberOne = result;
                        generateWrongOptions(1, result);
                    } else if (optionCorrect == 2) {
                        optionNumberTwo = result;
                        generateWrongOptions(2, result);
                    } else if (optionCorrect == 3) {
                        optionNumberThree = result;
                        generateWrongOptions(3, result);
                    } else if (optionCorrect == 4) {
                        optionNumberFour = result;
                        generateWrongOptions(4, result);
                    } else {
                        optionNumberOne = result;
                        generateWrongOptions(1, result);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (numberToHide == 1) {
                                tvNum1.setVisibility(View.INVISIBLE);
                                tvNum2.setVisibility(View.VISIBLE);
                                tvNum2.setText(String.valueOf(num2));
                            } else if (numberToHide == 2) {
                                tvNum1.setVisibility(View.VISIBLE);
                                tvNum2.setVisibility(View.INVISIBLE);
                                tvNum1.setText(String.valueOf(num1));
                            }
                            if (operator == 1) {
                                tvOperator.setText("+");
                            } else if (operator == 2) {
                                tvOperator.setText("-");
                            }
                            option_1.setText(String.valueOf(optionNumberOne));
                            option_2.setText(String.valueOf(optionNumberTwo));
                            option_3.setText(String.valueOf(optionNumberThree));
                            option_4.setText(String.valueOf(optionNumberFour));
                        }
                    });
                }
            }
        });
    }

    class ButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.option1:
                    //disableOptions();
                    optionClickedNumber = 1;
                    if (ismStopLoop() == TRUE) {
                        optionSelected = TRUE;
                        if (checkResult() == 1) {
                            score += 1;
                        } else {
                            if (score > 0) {
                                score -= 1;
                            }
                        }
                        executeScoreAndOptionUpdation();
                        tvScore.setText("Score :" + score);
                        sleepForSomeTime(timeToSleepBeforeChangingOption);
                        executeOnCustoLooperWithCustomHandler();
                        while (TRUE) {
                            if (optionSelected == TRUE) {
                                updateUI();
                                break;
                            }
                        }
                    } else {
                        optionSelected = FALSE;
                    }
                    //enableOptions();
                    break;

                case R.id.option2:
                    //disableOptions();
                    optionClickedNumber = 2;
                    if (ismStopLoop() == TRUE) {
                        optionSelected = TRUE;
                        if (checkResult() == 2) {
                            score += 1;
                        } else {
                            if (score > 0) {
                                score -= 1;
                            }
                        }
                        executeScoreAndOptionUpdation();
                        tvScore.setText("Score :" + score);
                        sleepForSomeTime(timeToSleepBeforeChangingOption);
                        executeOnCustoLooperWithCustomHandler();
                        while (TRUE) {
                            if (optionSelected == TRUE) {
                                updateUI();
                                break;
                            }
                        }
                    } else {
                        optionSelected = FALSE;
                    }
                    //enableOptions();
                    break;

                case R.id.option3:
                    //disableOptions();
                    optionClickedNumber = 3;
                    if (ismStopLoop() == TRUE) {
                        optionSelected = TRUE;
                        if (checkResult() == 3) {
                            score += 1;
                        } else {
                            if (score > 0) {
                                score -= 1;
                            }
                        }
                        executeScoreAndOptionUpdation();
                        tvScore.setText("Score :" + score);
                        sleepForSomeTime(timeToSleepBeforeChangingOption);
                        executeOnCustoLooperWithCustomHandler();
                        while (TRUE) {
                            if (optionSelected == TRUE) {
                                updateUI();
                                break;
                            }
                        }
                    } else {
                        optionSelected = FALSE;
                    }
                    //enableOptions();
                    break;
                case R.id.option4:
                    //disableOptions();
                    optionClickedNumber = 4;
                    if (ismStopLoop() == TRUE) {
                        optionSelected = TRUE;
                        if (checkResult() == 4) {
                            score += 1;
                        } else {
                            if (score > 0) {
                                score -= 1;
                            }
                            changeOptionColor(option_4, FALSE);
                        }
                        executeScoreAndOptionUpdation();
                        tvScore.setText("Score :" + score);
                        sleepForSomeTime(timeToSleepBeforeChangingOption);
                        executeOnCustoLooperWithCustomHandler();
                        while (TRUE) {
                            if (optionSelected == TRUE) {
                                updateUI();
                                break;
                            }
                        }
                    } else {
                        optionSelected = FALSE;
                    }
                    //enableOptions();
                    break;
            }
        }
    }

    public class MyCountDownTimer extends CountDownTimer{
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long l) {
            int seconds = (int) (l/1000);
            int timeRemaining = (int)l/1000;
            int minutes = seconds/60;
            seconds = seconds % 60;
            timeTextView.setText("Time Remaining "+String.format("%02d",minutes)+":"+String.format("%02d",seconds));
            if(timeRemaining<10){
                timeTextView.setTextColor(Color.RED);
                //blinkTextView();
                if(stopAnimationCounter==TRUE) {
                    timeTextView.startAnimation(startAnimation);
                    stopAnimationCounter=FALSE;
                }
            }
        }
        @Override
        public void onFinish() {
            optionSelected=FALSE;
            setmStopLoop(FALSE);
            tvInstruction.setVisibility(View.VISIBLE);
            tvScore.setText("Final Score :"+score);
            //modifyOptionsText();
            timeTextView.setTextColor(timeTextOrigColor);
            stopAnimation();
            modifyOptionLayoutVisibilityVisibility(FALSE);
            modifyExpressionVisibility(FALSE);
            rightAnswerAsToast.cancel();
            timeOver.show();
            //String textToShare = "I have scored "+score+"in Remember the Number "+"https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";;
            //shareText(textToShare);

        }
    }

    public void shareText(String text) {
        String mimeType = "text/plain";
        String title = "I recommend this app to ";

        Intent shareIntent =   ShareCompat.IntentBuilder.from(this)
                .setType(mimeType)
                .setText(text)
                .getIntent();
        if (shareIntent.resolveActivity(getPackageManager()) != null){
            startActivity(shareIntent);
        }
    }

    private void stopAnimation() {
        timeTextView.clearAnimation();
        startAnimation.cancel();
        startAnimation.reset();
    }

    private void modifyExpressionVisibility(Boolean visible) {
        if(visible==FALSE){
            tvNum1.setVisibility(View.INVISIBLE);
            tvNum2.setVisibility(View.INVISIBLE);
            tvOperator.setVisibility(View.INVISIBLE);
        }else if(visible==TRUE){
            tvNum1.setVisibility(View.VISIBLE);
            tvNum2.setVisibility(View.VISIBLE);
            tvOperator.setVisibility(View.VISIBLE);
        }
    }

    private void modifyOptionLayoutVisibilityVisibility(Boolean visible) {
        if(visible==FALSE){
            optionLayout.setVisibility(View.INVISIBLE);
        }else if(visible==TRUE){
            optionLayout.setVisibility(View.VISIBLE);
        }
    }

    private void disableOptions() {
        option_1.setClickable(FALSE);
        option_2.setClickable(FALSE);
        option_3.setClickable(FALSE);
        option_4.setClickable(FALSE);
    }

    private void enableOptions() {
        option_1.setClickable(TRUE);
        option_2.setClickable(TRUE);
        option_3.setClickable(TRUE);
        option_4.setClickable(TRUE);
    }
}
