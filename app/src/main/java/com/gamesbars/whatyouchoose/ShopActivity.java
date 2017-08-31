package com.gamesbars.whatyouchoose;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.L;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_COINS;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_THEME;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_THEME_BLACK;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_THEME_FRESH;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_THEME_STD;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_THEME_WHITE;

public class ShopActivity extends AppCompatActivity {

    private static final Integer THEME_COST_STD = 100;

    Integer theme;
    Integer coins;
    Integer clicked_buy_button_id;

    ImageButton back_button;
    TextView shop_coins;

    AlertDialog confirmDialog;

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

        // Получаем настройки
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        //  Устанавливаем тему, отображаем Activity
        theme = mSettings.getInt(APP_PREFERENCES_THEME, R.style.AppTheme);
        setTheme(theme);
        setContentView(R.layout.activity_shop);

        // Присваивам Views переменным по id
        back_button = (ImageButton) findViewById(R.id.back_button);
        shop_coins = (TextView) findViewById(R.id.shop_coins);

        //  Обновляем количество монет
        refresh_coins();

        //  Удаление кнопок купленных тем
        check_purchased_themes();

        // Загружаем изображения темы
        loadThemeImages();

        //  Загружаем диалоговое окно с подтверждением покупки темы
        loadAlertDialog();
    }

    private void refresh_coins() {
        coins = mSettings.getInt(APP_PREFERENCES_COINS, 0);
        shop_coins.setText(coins.toString());
    }

    private void check_purchased_themes() {
        if(mSettings.getBoolean(APP_PREFERENCES_THEME_STD, false)){
            findViewById(R.id.buy_button_std).setVisibility(View.GONE);
        }
        if(mSettings.getBoolean(APP_PREFERENCES_THEME_BLACK, false)){
            findViewById(R.id.buy_button_black).setVisibility(View.GONE);
        }
        if(mSettings.getBoolean(APP_PREFERENCES_THEME_WHITE, false)){
            findViewById(R.id.buy_button_white).setVisibility(View.GONE);
        }
        if(mSettings.getBoolean(APP_PREFERENCES_THEME_FRESH, false)){
            findViewById(R.id.buy_button_fresh).setVisibility(View.GONE);
        }
    }

    private void loadThemeImages() {
        if(theme == R.style.WhiteTheme){
            back_button.setImageResource(R.drawable.ic_arrow_back_black_36dp);
            shop_coins.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_stars_black_24dp), null, null, null);
        }
    }

    private void loadAlertDialog(){
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.confirm_dialog_message)
                .setTitle(R.string.confirm_dialog_title);

        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                switch (clicked_buy_button_id){

                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        // 3. Get the AlertDialog from create()
        confirmDialog = builder.create();
    }

    public void clickBuyTheme(View view) {
        clicked_buy_button_id = view.getId();

        switch (clicked_buy_button_id){
            case R.id.buy_button_std:
                if (coins >= THEME_COST_STD){
                    confirmDialog.show();
                } else {
                    // СОЗДАТЬ ТОСТ С НАДПИСЬЮ НЕДОСТАТОЧНО СРЕДСТВ
                }
        }
    }

    public void clickBack(View view){
        this.onBackPressed();
    }

}
