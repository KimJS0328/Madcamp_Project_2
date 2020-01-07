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
import android.view.Window;
import android.widget.Button;
import android.widget.DigitalClock;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StopAlarmActivity extends FragmentActivity implements SensorEventListener {

    String userid;

    private MediaPlayer mediaPlayer;
    Button button;
    DigitalClock digitalClock;

    //걸음수 재기
    SensorManager sensorManager;
    Sensor stepDetectorSensor;
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
    LottieAnimationView runmanView;

    //progressbar
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_stopalarm);

        SharedPreferences pref = getSharedPreferences("USER_ID", Context.MODE_PRIVATE);
        userid = pref.getString("id", "");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        digitalClock = findViewById(R.id.clock);
        button = findViewById(R.id.stopalarm);

        //알람 노래 재생
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.square);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        //카운트다운
        countTxt = (TextView)findViewById(R.id.countTxT);

        //성공 or fail에니메이션
        AnimationView = findViewById(R.id.successIcon);
        AnimationView.loop(false);

        //에니메이션
        runmanView = findViewById(R.id.runMan);
        runmanView.loop(true);

        //progressbar
        progressBar = findViewById(R.id.stepCount);
        progressBar.setVisibility(View.INVISIBLE);
    }


    public void mOnClick(View view) {
        mediaPlayer.stop();
        RelativeLayout relativeLayout = findViewById(R.id.background);
        relativeLayout.setBackground(null);

        //activity 새로 안만들고 재활용함
        button.setVisibility(view.INVISIBLE);
        digitalClock.setVisibility(view.INVISIBLE);

        //타이머 시작
        countDownTimer();
        countDownTimer.start();

        //runman 에니메이션
        runmanView.setAnimation("runman.json");
        runmanView.playAnimation();

        //progressbar
        progressBar.setVisibility(View.VISIBLE);
    }

    //뒤로가기 버튼이 동작되지 않게하기 위함
    @Override
    public void onBackPressed(){

    }

    //for countdown
    public void countDownTimer(){

        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {
                countTxt.setText("00:"+String.valueOf(count));
                count--;
                if (count == 10){
                    countTxt.setTextColor(Color.RED);
                }
            }
            public void onFinish() {
                countTxt.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                mediaPlayer.stop();
                runmanView.setVisibility(View.INVISIBLE);
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

                                    SharedPreferences pref = getSharedPreferences("NAME", Context.MODE_PRIVATE);
                                    String name = pref.getString("name", "");

                                    for (int i = 0; i < list.size(); ++i) {
                                        smsManager.sendTextMessage(list.get(i).getUser_phNumber(), null, name + "씨가 알람을 통해 일어나지 못하고 있습니다. \n 전화해서 깨워주세요.", null, null);
                                    }
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<List<ContactItem>> call, Throwable t) {
                            }
                        });
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
                progressBar.setProgress(mStepDetector);

                //성공시에
                if(count != 0 && mStepDetector == stepGoal){
                    runmanView.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    countTxt.setVisibility(View.INVISIBLE);
                    AnimationView.setAnimation("success.json");
                    AnimationView.playAnimation();
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            finish();
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
