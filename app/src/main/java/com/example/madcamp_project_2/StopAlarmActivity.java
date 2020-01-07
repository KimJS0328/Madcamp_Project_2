package com.example.madcamp_project_2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaActionSound;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.telephony.SmsManager;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StopAlarmActivity extends AppCompatActivity implements SensorEventListener {

    String userid;

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

        SharedPreferences pref = getSharedPreferences("USER_ID", Context.MODE_PRIVATE);
        userid = pref.getString("id", "");

        stepCount = findViewById(R.id.stepCount);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        digitalClock = findViewById(R.id.clock);
        button = findViewById(R.id.stopalarm);

        //알람 노래 재생
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.defaultalarm);
        mediaPlayer.setLooping(true);
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
                count--;
                if (count == 10){
                    countTxt.setTextColor(Color.RED);
                }
            }
            public void onFinish() {
                countTxt.setVisibility(View.INVISIBLE);
                stepCount.setVisibility(View.INVISIBLE);
                mediaPlayer.stop();
                AnimationView.setAnimation("fail.json");
                AnimationView.playAnimation();
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //새롭게 notification을 20초 후에 설정
                        Calendar calendar = Calendar.getInstance();
                        Date date = new Date();
                        calendar.setTime(date);
                        calendar.add(Calendar.SECOND, 20);
                        diaryNotification(calendar);

                        RetrofitConnection retrofitConnection = new RetrofitConnection();
                        retrofitConnection.server.getContactList(userid).enqueue(new Callback<List<ContactItem>>() {
                            @Override
                            public void onResponse(Call<List<ContactItem>> call, Response<List<ContactItem>> response) {
                                if (response.isSuccessful()) {
                                    List<ContactItem> list = response.body();
                                    SmsManager smsManager = SmsManager.getDefault();

                                    for (int i = 0; i < list.size(); ++i) {
                                        smsManager.sendTextMessage(list.get(i).getUser_phNumber(), null, "깨워줘!!!!!!!!!!!!", null, null);
                                    }
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<List<ContactItem>> call, Throwable t) {

                            }
                        });

                        //등록된 친구들에게 자신을 깨워달라는 메시지를 보내기 위해 intent 전달
                        /*Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("contact", "contact");
                        setResult(1221, intent);
                        startActivity(intent);
                        finish();*/
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

    void diaryNotification(Calendar calendar)
    {
//        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//        Boolean dailyNotify = sharedPref.getBoolean(SettingsActivity.KEY_PREF_DAILY_NOTIFICATION, true);
        Boolean dailyNotify = true; // 무조건 알람을 사용
        //후에 알람리스트에서 버튼을 만들어 이를 편집

//        PackageManager pm = getActivity().getPackageManager();
//        ComponentName receiver = new ComponentName(getActivity(), DeviceBootReceiver.class);
        Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);


        // 사용자가 매일 알람을 허용했다면
        if (dailyNotify) {


            if (alarmManager != null) {

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }

//            // 부팅 후 실행되는 리시버 사용가능하게 설정
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                    PackageManager.DONT_KILL_APP);

        }
//        else { //Disable Daily Notifications
//            if (PendingIntent.getBroadcast(this, 0, alarmIntent, 0) != null && alarmManager != null) {
//                alarmManager.cancel(pendingIntent);
//                //Toast.makeText(this,"Notifications were disabled",Toast.LENGTH_SHORT).show();
//            }
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                    PackageManager.DONT_KILL_APP);
//        }
    }
}
