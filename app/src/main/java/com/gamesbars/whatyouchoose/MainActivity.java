package com.gamesbars.whatyouchoose;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    //  Для работы с SQLite
    public static final String TABLE_QUESTIONS_NAME = "QUESTIONS";
    public static final String KEY_QUESTION_ONE = "QUEST1";
    public static final String KEY_QUESTION_TWO = "QUEST2";
    public static final String KEY_QUESTION_ONE_PERCENTAGE = "QUEST1PER";
    public static final String KEY_QUESTION_TWO_PERCENTAGE = "QUEST2PER";
    DataBaseHelper myDbHelper;

    //  Для работы с Shared Preferences
    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_THEME = "theme";
    public static final String APP_PREFERENCES_LVL = "level";   //  Текущий уровень
    public static final String APP_PREFERENCES_PER = "percent"; /*  Средний процент уникальности
                                                        (т.е. выбирал как такой-то процент людей */
    public static final String APP_PREFERENCES_PER_MOST = "percent_most"; // самый неуникальный выбор
    public static final String APP_PREFERENCES_PER_LESS = "percent_less"; // самый уникальный выбор
    public static final String APP_PREFERENCES_TIME_MAX = "time_max";      // max время выбора
    public static final String APP_PREFERENCES_TIME_AVER = "time_average"; // среднее время выбора
    public static final String APP_PREFERENCES_TIME_MIN = "time_min";      // min время выбора
    public static final String APP_PREFERENCES_COINS = "coins";      // монеты

    public static final String APP_PREFERENCES_THEME_STD = "theme_std"; // куплена ли стандартная тема
    public static final String APP_PREFERENCES_THEME_BLACK = "theme_black"; // куплена ли черная тема
    public static final String APP_PREFERENCES_THEME_WHITE = "theme_white"; // куплена ли белая тема
    public static final String APP_PREFERENCES_THEME_FRESH = "theme_fresh"; // куплена ли тема свежесть
    public SharedPreferences mSettings;
    public SharedPreferences.Editor editor;

    Intent playIntent;
    Intent statIntent;
    Intent shopIntent;

    @Override   //  Подруб шрифтов
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

        //  Загружаем настройки или создаем новые при первом запуске
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (!mSettings.contains(APP_PREFERENCES_LVL)){
            editor = mSettings.edit();
            editor.putInt(APP_PREFERENCES_LVL, 1);
            editor.commit();
        }

        if (!mSettings.contains(APP_PREFERENCES_COINS)){
            editor = mSettings.edit();
            editor.putInt(APP_PREFERENCES_COINS, 0);
            editor.commit();
        }

        if (!mSettings.contains(APP_PREFERENCES_THEME)){
            editor = mSettings.edit();
            editor.putInt(APP_PREFERENCES_THEME, R.style.AppTheme);
            editor.commit();
        }

        if (!mSettings.contains(APP_PREFERENCES_THEME_STD)){
            editor = mSettings.edit();
            editor.putBoolean(APP_PREFERENCES_THEME_STD, false);
            editor.commit();
        }
        if (!mSettings.contains(APP_PREFERENCES_THEME_BLACK)){
            editor = mSettings.edit();
            editor.putBoolean(APP_PREFERENCES_THEME_BLACK, false);
            editor.commit();
        }
        if (!mSettings.contains(APP_PREFERENCES_THEME_WHITE)){
            editor = mSettings.edit();
            editor.putBoolean(APP_PREFERENCES_THEME_WHITE, false);
            editor.commit();
        }
        if (!mSettings.contains(APP_PREFERENCES_THEME_FRESH)){
            editor = mSettings.edit();
            editor.putBoolean(APP_PREFERENCES_THEME_FRESH, false);
            editor.commit();
        }

        /*if (!mSettings.contains(APP_PREFERENCES_TIME_MAX)){
            editor = mSettings.edit();
            editor.putFloat(APP_PREFERENCES_TIME_MAX, 0);
            editor.commit();
        }

        if (!mSettings.contains(APP_PREFERENCES_TIME_AVER)){
            editor = mSettings.edit();
            editor.putFloat(APP_PREFERENCES_TIME_AVER, 0);
            editor.commit();
        }

        if (!mSettings.contains(APP_PREFERENCES_TIME_MIN)){
            editor = mSettings.edit();
            editor.putFloat(APP_PREFERENCES_TIME_MIN, 0);
            editor.commit();
        }*/

        //  Устанавливаем тему, отображаем Activity
        setTheme(mSettings.getInt(APP_PREFERENCES_THEME, R.style.AppTheme));
        setContentView(R.layout.activity_main);

        //  Создаем DataBase с помощью DataBaseHelper
        myDbHelper = new DataBaseHelper(this);

        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
    }

    public void play(View view){
        playIntent = new Intent(this, LevelActivity.class);
        startActivity(playIntent);
    }

    public void statistic(View view){
        statIntent = new Intent(this, StatActivity.class);
        startActivity(statIntent);
    }

    public void shop(View view){
        shopIntent = new Intent(this, ShopActivity.class);
        startActivity(shopIntent);
    }

    public void setStd(View view){
        editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_THEME, R.style.AppTheme);
        editor.commit();
        this.recreate();
    }

    public void setBlack(View view){
        editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_THEME, R.style.BlackTheme);
        editor.commit();
        this.recreate();
    }

    public void setWhite(View view){
        editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_THEME, R.style.WhiteTheme);
        editor.commit();
        this.recreate();
    }

    public void setFresh(View view){
        editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_THEME, R.style.FreshTheme);
        editor.commit();
        this.recreate();
    }
}
