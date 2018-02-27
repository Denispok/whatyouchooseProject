package com.gamesbars.whatyouchoose;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.concurrent.TimeUnit;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_ADS;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_COINS;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_FIRST_COINS_TOUCH;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_LVL_PACK_1;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_LVL_PACK_1_COMPLETED;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_LVL_PACK_2;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_LVL_PACK_2_COMPLETED;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_LVL_PACK_HARD;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_LVL_PACK_HARD_COMPLETED;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_LVL_SKIPPED;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_PER;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_PER_LESS;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_PER_MOST;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_THEME;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_TIME_AVER;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_TIME_MAX;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_TIME_MIN;
import static com.gamesbars.whatyouchoose.MainActivity.KEY_QUESTION_ONE;
import static com.gamesbars.whatyouchoose.MainActivity.KEY_QUESTION_ONE_PERCENTAGE;
import static com.gamesbars.whatyouchoose.MainActivity.KEY_QUESTION_TWO;
import static com.gamesbars.whatyouchoose.MainActivity.KEY_QUESTION_TWO_PERCENTAGE;

public class LevelActivity extends AppCompatActivity {

    private static final Long AD_FREQUENCY = 40000L; // ЧАСТОТА ПОКАЗА РЕКЛАМЫ
    String TABLE_QUESTIONS_NAME;
    String APP_PREFERENCES_LVL;
    Boolean state; /*   Состояние layout: 0 - ожидает выбора;
                                          1 - ожидает нажатия для перехода на следующий уровень */

    Integer coins;
    Long level_time;
    String choice;
    Integer choice_per;
    Integer theme;

    Integer top_percent;
    Integer bot_percent;

    RelativeLayout top_choice;
    RelativeLayout bot_choice;

    ImageButton back_button;
    ImageButton help_button;

    TextView level_coins;
    TextView level_coins_add;
    TextView question_one;
    TextView question_two;
    TextView question_one_per;
    TextView question_two_per;
    TextView answered_top;
    TextView answered_bot;

    Animation appear_coins_anim;
    Animation disappear_coins_anim;
    Animation.AnimationListener disappear_coins_anim_listener;
    Animation coins_add_anim;

    Animation percents_anim_top;
    Animation percents_anim_bot;
    Animation question_anim_top;
    Animation question_anim_bot;
    Animation answered_top_anim;
    Animation disappear_anim;
    Animation disappear_anim_with_listener;
    Animation appear_anim;
    Animation.AnimationListener percents_anim_listener;
    Animation.AnimationListener disappear_anim_listener;
    Animation.AnimationListener appear_anim_listener;

    AlertDialog helpDialog;
    AlertDialog coinsDialog;
    AlertDialog packEndDialog;

    SharedPreferences mSettings;
    SharedPreferences.Editor editor;

    DataBaseHelper myDbHelper;
    SQLiteDatabase myDb;
    Cursor cursor;

    private InterstitialAd mInterstitialAd;
    private Long adsTimer;

    @Override   //  Подруб шрифтов
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  Получаем название таблицы из экстра инфы
        Intent intent = getIntent();
        TABLE_QUESTIONS_NAME = intent.getStringExtra(Intent.EXTRA_TEXT);
        switch (TABLE_QUESTIONS_NAME) {
            case "PACK_1": APP_PREFERENCES_LVL = APP_PREFERENCES_LVL_PACK_1; break;
            case "PACK_2": APP_PREFERENCES_LVL = APP_PREFERENCES_LVL_PACK_2; break;
            case "PACK_HARD": APP_PREFERENCES_LVL = APP_PREFERENCES_LVL_PACK_HARD; break;
        }

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

        //  Открываем настройки
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        //  Устанавливаем тему, отображаем Activity
        theme = mSettings.getInt(APP_PREFERENCES_THEME, R.style.AppTheme);
        setTheme(theme);
        setContentView(R.layout.activity_level);

        // Присваивам Views переменным по id
        top_choice = (RelativeLayout) findViewById(R.id.top_choice);
        bot_choice = (RelativeLayout) findViewById(R.id.bot_choice);

        back_button = (ImageButton) findViewById(R.id.back_button);
        help_button = (ImageButton) findViewById(R.id.help_button);

        level_coins = (TextView) findViewById(R.id.level_coins);
        level_coins_add = (TextView) findViewById(R.id.level_coins_add);
        question_one = (TextView) findViewById(R.id.question_one);
        question_two = (TextView) findViewById(R.id.question_two);
        question_one_per = (TextView) findViewById(R.id.question_one_per);
        question_two_per = (TextView) findViewById(R.id.question_two_per);
        answered_top = (TextView) findViewById(R.id.answered_top);
        answered_bot = (TextView) findViewById(R.id.answered_bot);

        // Загружаем изображения темы
        loadThemeImages();

        //  Прописываем анимации
        loadAnimation();

        //  Загружаем диалоговое окно с инфой
        loadHelpDialog();

        //  Загружаем диалоговое окно с покупками
        loadCoinsDialog();

        //  Загружаем диалоговое окно окончания пака с вопросами
        loadPackEndDialog();

        //  Загружаем рекламу
        loadAds();

        //  Загружаем уровень
        loadLevel();

        //  Если не было первого нажатия на level_coins, то запускает его подсветку
        if (!mSettings.getBoolean(APP_PREFERENCES_FIRST_COINS_TOUCH, true)) {
            coinsAlertStarter();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  Начинаем отсчет времени уровня
        level_time = System.currentTimeMillis();
    }

    private void loadThemeImages() {
        if (theme == R.style.BlackTheme || theme == R.style.FreshTheme) {
            back_button.setImageResource(R.drawable.ic_arrow_back_white_36dp);
            level_coins.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_stars_white_24dp), null, null, null);
            help_button.setImageResource(R.drawable.ic_help_outline_white_36dp);
        }
    }

    private void loadAnimation() {

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

        coins_add_anim = AnimationUtils.loadAnimation(this, R.anim.coins_add_anim);
        coins_add_anim.setFillAfter(true);

        //  Анимация процентов
        percents_anim_listener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                question_one_per.setAlpha(1.0f);
                question_two_per.setAlpha(1.0f);
                answered_top.setAlpha(1.0f);
                answered_bot.setAlpha(1.0f);

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

        answered_top_anim = AnimationUtils.loadAnimation(this, R.anim.percents_anim_top);
        answered_top_anim.setFillAfter(true);

        percents_anim_bot = AnimationUtils.loadAnimation(this, R.anim.percents_anim_bot);
        percents_anim_bot.setFillAfter(true);

        //  Анимация вопросов
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

    private void loadHelpDialog() {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.help_dialog_message)
                .setTitle(R.string.help_dialog_title);

        builder.setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        // 3. Get the AlertDialog from create()
        helpDialog = builder.create();
    }

    private void loadCoinsDialog() {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_coins, null));

        // 3. Get the AlertDialog from create()
        coinsDialog = builder.create();
    }

    private void loadPackEndDialog() {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.pack_end_dialog_message)
                .setTitle(R.string.pack_end_dialog_title);

        builder.setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        // 3. Get the AlertDialog from create()
        packEndDialog = builder.create();
    }

    private void loadAds() {
        MobileAds.initialize(this, "INSERT ID HERE");

        //  Межстраничная реклама
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("INSERT ID HERE");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        adsTimer = System.currentTimeMillis();
    }

    private void loadLevel() {
        //  Изначальное состояние = 0 (не возбужденное)
        state = false;

        //  Загружаем коины
        coins = mSettings.getInt(APP_PREFERENCES_COINS, 0);
        level_coins.setText(Integer.toString(coins));

        //  Отображаем текущий уровень
        TextView current_level = (TextView) findViewById(R.id.current_level);
        current_level.setText(String.format(getString(R.string.current_lvl), mSettings.getInt(APP_PREFERENCES_LVL, 1)));

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

        //  Показ рекламы
        if (mSettings.getBoolean(APP_PREFERENCES_ADS, true) && System.currentTimeMillis() - adsTimer >= AD_FREQUENCY) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                adsTimer = System.currentTimeMillis();
            } else {
                Log.d("ADS", "The interstitial wasn't loaded yet.");
            }

        }
    }

    private void coinsAlertStarter() {
        class CoinsStarterTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                new CoinsAlertTask().execute();
            }
        }

        if (!mSettings.getBoolean(APP_PREFERENCES_FIRST_COINS_TOUCH, true))
            new CoinsStarterTask().execute();
    }

    private class CoinsAlertTask extends AsyncTask<Void, Void, Void> {

        TransitionDrawable transition = (TransitionDrawable) level_coins.getBackground();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            transition.startTransition(750);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            transition.reverseTransition(750);
            coinsAlertStarter();
        }

    }

    private void openDB() {
        myDbHelper = new DataBaseHelper(this);
        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        myDb = myDbHelper.getDB();

        cursor = myDb.query(TABLE_QUESTIONS_NAME, new String[]{KEY_QUESTION_ONE, KEY_QUESTION_TWO,
                        KEY_QUESTION_ONE_PERCENTAGE, KEY_QUESTION_TWO_PERCENTAGE},
                "rowid = " + Integer.toString(mSettings.getInt(APP_PREFERENCES_LVL, 0)), null,
                null, null, null);

        cursor.moveToNext();
    }

    public void clickTop(View view) {
        if (!state) {

            choice = "top";
            choice_per = top_percent;

            answered_top.setText(R.string.answered_true);
            answered_bot.setText(R.string.answered_false);

            toExcitingState();
        } else {

            endCurrentLevel();
        }
    }

    public void clickBottom(View view) {
        if (!state) {

            choice = "bottom";
            choice_per = bot_percent;

            answered_top.setText(R.string.answered_false);
            answered_bot.setText(R.string.answered_true);

            toExcitingState();
        } else {

            endCurrentLevel();
        }
    }

    private void endCurrentLevel() {
        /*  Запуск анимации исчезновения + анимации с listener'ом, ответственной за loadLevel()
                                                                    и запуск анимации появления */
        question_one.startAnimation(disappear_anim_with_listener);
        question_one_per.startAnimation(disappear_anim);
        question_two.startAnimation(disappear_anim);
        question_two_per.startAnimation(disappear_anim);
        answered_top.startAnimation(disappear_anim);
        answered_bot.startAnimation(disappear_anim);
    }

    private void toExcitingState() {
        //  Запуск анимации
        level_coins.startAnimation(disappear_coins_anim);
        level_coins_add.setAlpha(1.0f);
        level_coins_add.startAnimation(coins_add_anim);
        question_one_per.startAnimation(percents_anim_top);
        question_two_per.startAnimation(percents_anim_bot);
        question_one.startAnimation(question_anim_top);
        question_two.startAnimation(question_anim_bot);
        answered_top.startAnimation(answered_top_anim);
        answered_bot.startAnimation(percents_anim_bot);

        //  Прибавляем коины
        editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_COINS, mSettings.getInt(APP_PREFERENCES_COINS, 0) + 10);
        editor.apply();
        coins += 10;

        //  Записываем время уровня
        level_time = System.currentTimeMillis() - level_time;

        //  Сохраняем значение нового уровня и статистику в настройки
        if (!mSettings.getBoolean(APP_PREFERENCES_LVL + "_completed", true)) saveStatistic();
        editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_LVL, mSettings.getInt(APP_PREFERENCES_LVL, 0) + 1);
        editor.apply();

        //  Проверка закончился ли пак
        checkPackEnd();

        state = true;
    }

    private void saveStatistic() {
        editor = mSettings.edit();

        // Инициализация статистики в Preferences на 1 уровне
        if (mSettings.getInt(APP_PREFERENCES_PER_LESS, 0) == 0) {
            editor.putFloat(APP_PREFERENCES_TIME_MAX, Math.round(level_time / 10f) / 100f);
            editor.putFloat(APP_PREFERENCES_TIME_MIN, Math.round(level_time / 10f) / 100f);
            editor.putFloat(APP_PREFERENCES_TIME_AVER, Math.round(level_time / 10f) / 100f);
            editor.putInt(APP_PREFERENCES_PER_MOST, choice_per);
            editor.putInt(APP_PREFERENCES_PER_LESS, choice_per);
            editor.putFloat(APP_PREFERENCES_PER, choice_per);
        } else {
            // Записывание новых значений статистики
            if (mSettings.getFloat(APP_PREFERENCES_TIME_MAX, 0) < (Math.round(level_time / 10f) / 100f)) {
                editor.putFloat(APP_PREFERENCES_TIME_MAX, Math.round(level_time / 10f) / 100f);
            }

            if (mSettings.getFloat(APP_PREFERENCES_TIME_MIN, 0) > (Math.round(level_time / 10f) / 100f)) {
                editor.putFloat(APP_PREFERENCES_TIME_MIN, Math.round(level_time / 10f) / 100f);
            }

            Integer levels_pack_1;
            Integer levels_pack_2;
            Integer levels_pack_hard;
            if (mSettings.getBoolean(APP_PREFERENCES_LVL_PACK_1_COMPLETED , false)) levels_pack_1 = 40; else levels_pack_1 = mSettings.getInt(APP_PREFERENCES_LVL_PACK_1, 0);
            if (mSettings.getBoolean(APP_PREFERENCES_LVL_PACK_2_COMPLETED , false)) levels_pack_2 = 40; else levels_pack_2 = mSettings.getInt(APP_PREFERENCES_LVL_PACK_2, 0);
            if (mSettings.getBoolean(APP_PREFERENCES_LVL_PACK_HARD_COMPLETED , false)) levels_pack_hard = 40; else levels_pack_hard = mSettings.getInt(APP_PREFERENCES_LVL_PACK_HARD, 0);
            Integer all_levels = levels_pack_1 + levels_pack_2 + levels_pack_hard - 2;

            editor.putFloat(APP_PREFERENCES_TIME_AVER, Math.round(((mSettings.getFloat(APP_PREFERENCES_TIME_AVER, 0) * (all_levels - mSettings.getInt(APP_PREFERENCES_LVL_SKIPPED, 0) - 1)
                    + Math.round(level_time / 10f) / 100f) / (all_levels - mSettings.getInt(APP_PREFERENCES_LVL_SKIPPED, 0))) * 100f) / 100f);

            if (choice_per > mSettings.getInt(APP_PREFERENCES_PER_MOST, 0)) {
                editor.putInt(APP_PREFERENCES_PER_MOST, choice_per);
            }

            if (choice_per < mSettings.getInt(APP_PREFERENCES_PER_LESS, 0)) {
                editor.putInt(APP_PREFERENCES_PER_LESS, choice_per);
            }

            editor.putFloat(APP_PREFERENCES_PER, Math.round(((mSettings.getFloat(APP_PREFERENCES_PER, 0) * (all_levels - mSettings.getInt(APP_PREFERENCES_LVL_SKIPPED, 0) - 1)
                    + choice_per) / (all_levels - mSettings.getInt(APP_PREFERENCES_LVL_SKIPPED, 0))) * 100f) / 100f);
        }

        editor.apply();
    }

    public void clickBack(View view) {
        this.onBackPressed();
    }

    public void clickCoins(View view) {
        if (!mSettings.getBoolean(APP_PREFERENCES_FIRST_COINS_TOUCH, true)) {
            editor = mSettings.edit();
            editor.putBoolean(APP_PREFERENCES_FIRST_COINS_TOUCH, true);
            editor.apply();
        }
        coinsDialog.show();
        //  Устанавливаем button_selector темы на Buttons backgrounds
        Drawable button_drawable = null;
        switch (theme) {
            case R.style.AppTheme:
                button_drawable = getResources().getDrawable(R.drawable.button_selector_standart);
                break;
            case R.style.BlackTheme:
                button_drawable = getResources().getDrawable(R.drawable.button_selector_black);
                break;
            case R.style.WhiteTheme:
                button_drawable = getResources().getDrawable(R.drawable.button_selector_white);
                break;
            case R.style.FreshTheme:
                button_drawable = getResources().getDrawable(R.drawable.button_selector_fresh);
                break;
        }
        coinsDialog.findViewById(R.id.skip_for_coins_button).setBackground(button_drawable);
        coinsDialog.findViewById(R.id.skip_for_ads_button).setBackground(button_drawable);
        coinsDialog.findViewById(R.id.remove_ad_button).setBackground(button_drawable);
    }

    public void clickHelp(View view) {
        helpDialog.show();
    }

    public void skipForCoins(View view) {
        if (!state && coins >= 100) {
            editor = mSettings.edit();
            editor.putInt(APP_PREFERENCES_COINS, coins - 100);
            editor.apply();

            adsTimer = System.currentTimeMillis() + 5000L;
            skipLevel();
            coinsDialog.cancel();
        }
    }

    public void getCoins(View view) {
        startActivity(new Intent(this, ShopActivity.class));
    }

    public void removeAds(View view) {
        startActivity(new Intent(this, ShopActivity.class));
    }

    private void skipLevel() {
        editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_LVL, mSettings.getInt(APP_PREFERENCES_LVL, 0) + 1);
        if (!mSettings.getBoolean(APP_PREFERENCES_LVL + "_completed", true))
            editor.putInt(APP_PREFERENCES_LVL_SKIPPED, mSettings.getInt(APP_PREFERENCES_LVL_SKIPPED, 0) + 1);
        editor.apply();

        checkPackEnd();

        loadLevel();

        //  Начинаем новый отсчет времени уровня
        level_time = System.currentTimeMillis();
    }

    private void checkPackEnd() {
        if (mSettings.getInt(APP_PREFERENCES_LVL, 0) == 41) {
            editor = mSettings.edit();
            editor.putInt(APP_PREFERENCES_LVL, 1);
            editor.putBoolean(APP_PREFERENCES_LVL + "_completed", true);
            editor.commit();
            packEndDialog.show();
        }
    }
}

