package com.gamesbars.whatyouchoose;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.io.IOException;

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
    public static final String APP_PREFERENCES_LVL = "level";   //  Текущий уровень
    public static final String APP_PREFERENCES_PER = "percent"; /*  Средний процент уникальности
                                                        (т.е. выбирал как такой-то процент людей */
    public static final String APP_PREFERENCES_TIME_MAX = "time_max";      // max время выбора
    public static final String APP_PREFERENCES_TIME_AVER = "time_average"; // среднее время выбора
    public static final String APP_PREFERENCES_TIME_MIN = "time_min";      // min время выбора
    public SharedPreferences mSettings;
    public SharedPreferences.Editor editor;

    Intent playIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  Делаем Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        //  Загружаем настройки или создаем новые при первом запуске
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (!mSettings.contains(APP_PREFERENCES_LVL)){
            editor = mSettings.edit();
            editor.putInt(APP_PREFERENCES_LVL, 1);
            editor.commit();
        }

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
}
