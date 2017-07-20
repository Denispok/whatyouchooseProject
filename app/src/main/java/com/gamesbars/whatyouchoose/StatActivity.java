package com.gamesbars.whatyouchoose;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_LVL;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_PER;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_PER_LESS;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_PER_MOST;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_TIME_AVER;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_TIME_MAX;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_TIME_MIN;

public class StatActivity extends AppCompatActivity {

    TextView stat_per_aver;
    TextView stat_count;
    TextView stat_time_aver;
    TextView stat_per_min;
    TextView stat_per_max;
    TextView stat_time_min;
    TextView stat_time_max;

    SharedPreferences mSettings;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  Подруб шрифтов
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Exo2-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        //  Делаем Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_stat);

        // Присваивам Views переменным по id
        stat_per_aver = (TextView) findViewById(R.id.stat_per_aver);
        stat_count = (TextView) findViewById(R.id.stat_count);
        stat_time_aver = (TextView) findViewById(R.id.stat_time_aver);
        stat_per_min = (TextView) findViewById(R.id.stat_per_min);
        stat_per_max = (TextView) findViewById(R.id.stat_per_max);
        stat_time_min = (TextView) findViewById(R.id.stat_time_min);
        stat_time_max = (TextView) findViewById(R.id.stat_time_max);

        // Получаем настройки
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        // Загружаем статистику из настроек
        loadStat();
    }

    void loadStat(){
        stat_per_aver.setText(String.format(getString(R.string.stat_per_aver), String.valueOf(mSettings.getFloat(APP_PREFERENCES_PER, 0))));
        stat_count.setText(String.valueOf(mSettings.getInt(APP_PREFERENCES_LVL, 0) - 1));
        stat_time_aver.setText(String.format(getString(R.string.stat_time_value), String.valueOf(mSettings.getFloat(APP_PREFERENCES_TIME_AVER, 0))));
        stat_per_min.setText(String.format(getString(R.string.stat_per_value), mSettings.getInt(APP_PREFERENCES_PER_LESS, 0)));
        stat_per_max.setText(String.format(getString(R.string.stat_per_value), mSettings.getInt(APP_PREFERENCES_PER_MOST, 0)));
        stat_time_min.setText(String.format(getString(R.string.stat_time_value), String.valueOf(mSettings.getFloat(APP_PREFERENCES_TIME_MIN, 0))));
        stat_time_max.setText(String.format(getString(R.string.stat_time_value), String.valueOf(mSettings.getFloat(APP_PREFERENCES_TIME_MAX, 0))));

    }

    public void clickBack(View view){
        this.onBackPressed();
    }

}
