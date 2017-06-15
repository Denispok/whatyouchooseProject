package com.gamesbars.whatyouchoose;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_COINS;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_LVL;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_PER;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_PER_LESS;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_PER_MOST;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_TIME_AVER;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_TIME_MAX;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_TIME_MIN;
import static com.gamesbars.whatyouchoose.MainActivity.KEY_QUESTION_ONE;
import static com.gamesbars.whatyouchoose.MainActivity.KEY_QUESTION_ONE_PERCENTAGE;
import static com.gamesbars.whatyouchoose.MainActivity.KEY_QUESTION_TWO;
import static com.gamesbars.whatyouchoose.MainActivity.KEY_QUESTION_TWO_PERCENTAGE;
import static com.gamesbars.whatyouchoose.MainActivity.TABLE_QUESTIONS_NAME;

public class LevelActivity extends AppCompatActivity {

    Boolean state; /*   Состояние layout: 0 - ожидает выбора (невозбужденное);
                                          1 - ожидает нажатия для перехода на следующий уровень
                                              (возбужденное) */

    Integer coins;
    Long level_time;
    String choice;
    Integer choice_per;

    Integer top_percent;
    Integer bot_percent;

    RelativeLayout top_choice;
    RelativeLayout bot_choice;

    TextView level_coins;
    TextView question_one;
    TextView question_two;
    TextView question_one_per;
    TextView question_two_per;

    Animation appear_coins_anim;
    Animation disappear_coins_anim;
    Animation.AnimationListener disappear_coins_anim_listener;

    Animation percents_anim_top;
    Animation percents_anim_bot;
    Animation question_anim_top;
    Animation question_anim_bot;
    Animation disappear_anim;
    Animation disappear_anim_with_listener;
    Animation appear_anim;
    Animation.AnimationListener percents_anim_listener;
    Animation.AnimationListener question_anim_listener;
    Animation.AnimationListener disappear_anim_listener;
    Animation.AnimationListener appear_anim_listener;

    SharedPreferences mSettings;
    SharedPreferences.Editor editor;

    DataBaseHelper myDbHelper;
    SQLiteDatabase myDb;
    Cursor cursor;

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

        setContentView(R.layout.activity_level);

        // Присваивам Views переменным по id
        top_choice = (RelativeLayout) findViewById(R.id.top_choice);
        bot_choice = (RelativeLayout) findViewById(R.id.bot_choice);

        level_coins = (TextView) findViewById(R.id.level_coins);
        question_one = (TextView) findViewById(R.id.question_one);
        question_two = (TextView) findViewById(R.id.question_two);
        question_one_per = (TextView) findViewById(R.id.question_one_per);
        question_two_per = (TextView) findViewById(R.id.question_two_per);

        //  Прописываем анимации
        loadAnimation();

        //  Открываем настройки
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        //  Начинаем отсчет времени уровня
        level_time = System.currentTimeMillis();

        //  Загружаем уровень
        loadLevel();
    }

    public void loadAnimation(){

        //  Анимация коинов
        disappear_coins_anim_listener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //  Обновляем коины
                level_coins.setText(Integer.toString(coins));
                level_coins.startAnimation(appear_coins_anim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        disappear_coins_anim = AnimationUtils.loadAnimation(this, R.anim.disappear_coins_anim);
        disappear_coins_anim.setFillAfter(true);
        disappear_coins_anim.setAnimationListener(disappear_coins_anim_listener);

        appear_coins_anim = AnimationUtils.loadAnimation(this, R.anim.appear_coins_anim);
        appear_coins_anim.setFillAfter(true);

        //  Анимация процентов
        percents_anim_listener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                question_one_per.setAlpha(1);
                question_two_per.setAlpha(1);

                top_choice.setClickable(false);
                bot_choice.setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                top_choice.setClickable(true);
                bot_choice.setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        percents_anim_top = AnimationUtils.loadAnimation(this, R.anim.percents_anim_top);
        percents_anim_top.setFillAfter(true);
        percents_anim_top.setAnimationListener(percents_anim_listener);

        percents_anim_bot = AnimationUtils.loadAnimation(this, R.anim.percents_anim_bot);
        percents_anim_bot.setFillAfter(true);

        //  Анимация вопросов
        question_anim_listener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        question_anim_top = AnimationUtils.loadAnimation(this, R.anim.question_anim_top);
        question_anim_top.setFillAfter(true);

        question_anim_bot = AnimationUtils.loadAnimation(this, R.anim.question_anim_bot);
        question_anim_bot.setFillAfter(true);

        //  Анимации исчезновения/появления при загрузке нового уровня
        disappear_anim_listener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                top_choice.setClickable(false);
                bot_choice.setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                loadLevel();

                question_one.startAnimation(appear_anim);
                question_two.startAnimation(appear_anim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        disappear_anim = AnimationUtils.loadAnimation(this, R.anim.disappear_anim);
        disappear_anim.setFillAfter(true);

        disappear_anim_with_listener = AnimationUtils.loadAnimation(this, R.anim.disappear_anim);
        disappear_anim_with_listener.setFillAfter(true);
        disappear_anim_with_listener.setAnimationListener(disappear_anim_listener);

        appear_anim_listener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                top_choice.setClickable(true);
                bot_choice.setClickable(true);

                //  Начинаем новый отсчет времени уровня
                level_time = System.currentTimeMillis();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        appear_anim = AnimationUtils.loadAnimation(this, R.anim.appear_anim);
        appear_anim.setFillAfter(true);
        appear_anim.setAnimationListener(appear_anim_listener);
    }

    public void loadLevel(){
        //  Изначальное состояние = 0 (не возбужденное)
        state = false;

        //  Загружаем коины
        coins = mSettings.getInt(APP_PREFERENCES_COINS, 0);
        level_coins.setText(Integer.toString(coins));

        // Открываем DataBase и ситываем информацию для текущего уровня
        openDB();

        //  Считываем и применяем значения к Views
        //Toast.makeText(this, Integer.toString(cursor.getInt(cursor.getColumnIndex(KEY_QUESTION_ONE_PERCENTAGE))), Toast.LENGTH_LONG).show();

        question_one.setText(cursor.getString(cursor.getColumnIndex(KEY_QUESTION_ONE)));
        question_two.setText(cursor.getString(cursor.getColumnIndex(KEY_QUESTION_TWO)));

        top_percent = cursor.getInt(cursor.getColumnIndex(KEY_QUESTION_ONE_PERCENTAGE));
        bot_percent = cursor.getInt(cursor.getColumnIndex(KEY_QUESTION_TWO_PERCENTAGE));

        question_one_per.setText(String.format(getString(R.string.percent_top), top_percent));
        question_two_per.setText(String.format(getString(R.string.percent_bot), bot_percent));

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

            choice = "top";
            choice_per = top_percent;
            toExcitingState();
        } else {

            endCurrentLevel();
        }
    }

    public void clickBottom(View view){
        if (!state){

            choice = "bottom";
            choice_per = bot_percent;
            toExcitingState();
        } else {

            endCurrentLevel();
        }
    }

    public void endCurrentLevel(){
        /*  Запуск анимации исчезновения + анимации с listener'ом, ответственной за loadLevel()
                                                                    и запуск анимации появления */
        question_one.startAnimation(disappear_anim_with_listener);
        question_one_per.startAnimation(disappear_anim);
        question_two.startAnimation(disappear_anim);
        question_two_per.startAnimation(disappear_anim);
    }

    public void toExcitingState(){
        //  Запуск анимации
        level_coins.startAnimation(disappear_coins_anim);
        question_one_per.startAnimation(percents_anim_top);
        question_two_per.startAnimation(percents_anim_bot);
        question_one.startAnimation(question_anim_top);
        question_two.startAnimation(question_anim_bot);

        //  Прибавляем коины
        coins += 10;

        //  Записываем время уровня
        level_time = System.currentTimeMillis() - level_time;
        //  DEBUG TOAST
        Toast.makeText(this, Float.toString(Math.round(level_time / 10f) / 100f), Toast.LENGTH_LONG).show();

        //  Сохраняем значение нового уровня, монеты и статистику в настройки
        saveStatistic();

        //  DEBUG TOAST
       // Toast.makeText(this, Float.toString(mSettings.getFloat(APP_PREFERENCES_TIME_AVER, 0)), Toast.LENGTH_LONG).show();

        // DEBUG STRING
        if (mSettings.getInt(APP_PREFERENCES_LVL, 0) == 11){
            editor = mSettings.edit();
            editor.putInt(APP_PREFERENCES_LVL, 1);
            editor.commit();
        }
        // DEBUG END

        state = true;
    }

    public void saveStatistic(){
        editor = mSettings.edit();

        // Инициализация статистики в Preferences на 1 уровне
        if (mSettings.getInt(APP_PREFERENCES_LVL, 0) == 1) {
            editor.putFloat(APP_PREFERENCES_TIME_MAX, Math.round(level_time / 10f) / 100f);
            editor.putFloat(APP_PREFERENCES_TIME_MIN, Math.round(level_time / 10f) / 100f);
            editor.putFloat(APP_PREFERENCES_TIME_AVER, Math.round(level_time / 10f) / 100f);
            editor.putInt(APP_PREFERENCES_PER_MOST, choice_per);
            editor.putInt(APP_PREFERENCES_PER_LESS, choice_per);
            editor.putFloat(APP_PREFERENCES_PER, choice_per);
        } else {
            if (mSettings.getFloat(APP_PREFERENCES_TIME_MAX, 0) < (Math.round(level_time / 10f) / 100f)) {
                editor.putFloat(APP_PREFERENCES_TIME_MAX, Math.round(level_time / 10f) / 100f);
            }

            if (mSettings.getFloat(APP_PREFERENCES_TIME_MIN, 0) > (Math.round(level_time / 10f) / 100f)) {
                editor.putFloat(APP_PREFERENCES_TIME_MIN, Math.round(level_time / 10f) / 100f);
            }

            editor.putFloat(APP_PREFERENCES_TIME_AVER, Math.round(((mSettings.getFloat(APP_PREFERENCES_TIME_AVER, 0) * (mSettings.getInt(APP_PREFERENCES_LVL, 0) - 1)
                    + Math.round(level_time / 10f) / 100f) / mSettings.getInt(APP_PREFERENCES_LVL, 0)) * 100f) / 100f);

            if (choice_per > mSettings.getInt(APP_PREFERENCES_PER_MOST, 0)){
                editor.putInt(APP_PREFERENCES_PER_MOST, choice_per);
            }

            if (choice_per < mSettings.getInt(APP_PREFERENCES_PER_LESS, 0)){
                editor.putInt(APP_PREFERENCES_PER_LESS, choice_per);
            }

            editor.putFloat(APP_PREFERENCES_PER, Math.round(((mSettings.getFloat(APP_PREFERENCES_PER, 0) * (mSettings.getInt(APP_PREFERENCES_LVL, 0) - 1)
                    + choice_per / mSettings.getInt(APP_PREFERENCES_LVL, 0)) * 100f) / 100f));
        }

        editor.putInt(APP_PREFERENCES_LVL, mSettings.getInt(APP_PREFERENCES_LVL, 0) + 1);
        editor.putInt(APP_PREFERENCES_COINS, coins);

        editor.apply();
    }

    public void clickBack(View view){
        this.onBackPressed();
    }

    public void clickCoins(View view){

    }
}
