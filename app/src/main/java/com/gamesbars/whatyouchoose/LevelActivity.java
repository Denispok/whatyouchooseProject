package com.gamesbars.whatyouchoose;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_LVL;
import static com.gamesbars.whatyouchoose.MainActivity.KEY_QUESTION_ONE;
import static com.gamesbars.whatyouchoose.MainActivity.KEY_QUESTION_ONE_PERCENTAGE;
import static com.gamesbars.whatyouchoose.MainActivity.KEY_QUESTION_TWO;
import static com.gamesbars.whatyouchoose.MainActivity.KEY_QUESTION_TWO_PERCENTAGE;
import static com.gamesbars.whatyouchoose.MainActivity.TABLE_QUESTIONS_NAME;

public class LevelActivity extends AppCompatActivity {

    Boolean state; /*   Состояние layout: 0 - ожидает выбора (невозбужденное);
                                          1 - ожидает нажатия для перехода на следующий уровень
                                              (возбужденное) */

    TextView question_one;
    TextView question_two;
    TextView question_one_per;
    TextView question_two_per;

    Animation percents_anim;
    Animation.AnimationListener percents_anim_listener;

    SharedPreferences mSettings;
    SharedPreferences.Editor editor;

    DataBaseHelper myDbHelper;
    SQLiteDatabase myDb;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        // Присваивам Views переменным по id
        question_one = (TextView) findViewById(R.id.question_one);
        question_two = (TextView) findViewById(R.id.question_two);
        question_one_per = (TextView) findViewById(R.id.question_one_per);
        question_two_per = (TextView) findViewById(R.id.question_two_per);

        //  Прописываем анимации
        loadAnimation();

        //  Открываем настройки
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        //  Загружаем уровень
        loadLevel();
    }

    public void loadAnimation(){
        percents_anim_listener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                question_one_per.setAlpha(1);
                question_two_per.setAlpha(1);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        percents_anim = AnimationUtils.loadAnimation(this, R.anim.percents_anim);
        percents_anim.setAnimationListener(percents_anim_listener);
    }

    public void loadLevel(){
        //  Изначальное состояние = 0 (не возбужденное)
        state = false;

        // Открываем DataBase и ситываем информацию для текущего уровня
        openDB();

        //  Считываем и применяем значения к Views
        //Toast.makeText(this, Integer.toString(cursor.getInt(cursor.getColumnIndex(KEY_QUESTION_ONE_PERCENTAGE))), Toast.LENGTH_LONG).show();

        question_one.setText(cursor.getString(cursor.getColumnIndex(KEY_QUESTION_ONE)));
        question_two.setText(cursor.getString(cursor.getColumnIndex(KEY_QUESTION_TWO)));
        question_one_per.setText(cursor.getString(cursor.getColumnIndex(KEY_QUESTION_ONE_PERCENTAGE)) + "%");
        question_two_per.setText(cursor.getString(cursor.getColumnIndex(KEY_QUESTION_TWO_PERCENTAGE)) + "%");


        //  Закрываем DataBase
        myDb.close();
    }

    public void openDB() {
        myDbHelper = new DataBaseHelper(this);
        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        myDb = myDbHelper.getDB();

        cursor = myDb.query(TABLE_QUESTIONS_NAME, new String[]{KEY_QUESTION_ONE, KEY_QUESTION_TWO,
                        KEY_QUESTION_ONE_PERCENTAGE, KEY_QUESTION_TWO_PERCENTAGE},
                "_id = " + Integer.toString(mSettings.getInt(APP_PREFERENCES_LVL, 0)), null,
                null, null, null);

        cursor.moveToNext();
    }

    public void clickTop(View view){
        if (!state){

            toExcitingState();
        } else {

            endCurrentLevel();
            loadLevel();
        }
    }

    public void endCurrentLevel(){
        question_one_per.setAlpha(0);
        question_two_per.setAlpha(0);
    }

    public void toExcitingState(){
        question_one_per.startAnimation(percents_anim);
        question_two_per.startAnimation(percents_anim);

        //  Сохраняем значение нового уровня и статистику в настройки
        editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_LVL, mSettings.getInt(APP_PREFERENCES_LVL, 0) + 1);
        editor.commit();

        state = true;
    }
}
