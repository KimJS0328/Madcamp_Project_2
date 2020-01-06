package com.example.madcamp_project_2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.DigitalClock;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;

public class StopAlarmActivity extends AppCompatActivity implements SensorEventListener {

    private MediaPlayer mediaPlayer;
    Button button;
    DigitalClock digitalClock;

    //걸음수 재기
    SensorManager sensorManager;
    Sensor stepDetectorSensor;
    TextView stepCount;
    int mStepDetector;
    int stepGoal = 20;

    //카운트다운 타이머
    int count = 20;
    TextView countTxt;
    CountDownTimer countDownTimer;
    static final int COUNT_DOWN_INTERVAL = 1000;
    static final int MILLISINFUTURE = 21 *  COUNT_DOWN_INTERVAL;

    //성공시 에니메이션
    LottieAnimationView AnimationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopalarm);
        stepCount = findViewById(R.id.stepCount);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        digitalClock = findViewById(R.id.clock);
        button = findViewById(R.id.stopalarm);

        //알람 노래 재생
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.defaultalarm);
        mediaPlayer.isLooping();
        mediaPlayer.start();

        //카운트다운
        countTxt = (TextView)findViewById(R.id.countTxT);

        //성공 or fail에니메이션
        AnimationView = findViewById(R.id.successIcon);
        AnimationView.loop(false);
    }


    public void mOnClick(View view) {
        mediaPlayer.stop();

        //activity 새로 안만들고 재활용함
        button.setVisibility(view.INVISIBLE);
        digitalClock.setVisibility(view.INVISIBLE);
        stepCount.setText("걸음수 : " + mStepDetector + " / " + stepGoal);

        //타이머 시작
        countDownTimer();
        countDownTimer.start();

    }

    //뒤로가기 버튼이 동작되지 않게하기 위함
//    @Override
//    public void onBackPressed(){
//        if (count != 0 && mStepDetector >= stepGoal)
//            super.onBackPressed();
//    }

    //for countdown
    public void countDownTimer(){

        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {
                countTxt.setText("남은 시간 : " + String.valueOf(count) + "초");
                count --;
                if (count == 10){
                    countTxt.setTextColor(Color.RED);
                }
            }
            public void onFinish() {
                countTxt.setVisibility(View.INVISIBLE);
                stepCount.setVisibility(View.INVISIBLE);
                AnimationView.setAnimation("fail.json");
                AnimationView.playAnimation();
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //새롭게 notification을 1분후에 설정
                        //등록된 친구들에게 자신을 깨워달라는 메시지를 보냄
                        onBackPressed();
                    }
                }, 1500);// 1.5초의 딜레이 후 시작됨
            }
        };
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            countDownTimer.cancel();
        } catch (Exception e) {}
        countDownTimer=null;
    }

    //for step detect

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
            if (event.values[0] == 1.0f){
                mStepDetector++;
                stepCount.setText("걸음수 : " + mStepDetector + " / " + stepGoal);

                //성공시에
                if(count != 0 && mStepDetector == stepGoal){
                    stepCount.setVisibility(View.INVISIBLE);
                    countTxt.setVisibility(View.INVISIBLE);
                    AnimationView.setAnimation("success.json");
                    AnimationView.playAnimation();
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            onBackPressed();
                        }
                    }, 1500);// 1.5초의 딜레이 후 시작됨
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
