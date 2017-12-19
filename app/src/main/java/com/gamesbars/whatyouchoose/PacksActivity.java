package com.gamesbars.whatyouchoose;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_COINS;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_PACK_1;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_PACK_2;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_PACK_HARD;
import static com.gamesbars.whatyouchoose.MainActivity.APP_PREFERENCES_THEME;

public class PacksActivity extends AppCompatActivity {

    private static final Integer COST_PACK_1 = 100;
    private static final Integer COST_PACK_2 = 100;
    private static final Integer COST_PACK_HARD = 100;

    Integer theme;
    Integer coins;
    Integer clicked_pack_button_id;

    TextView packs_coins;
    ImageButton back_button;

    AlertDialog confirmDialog;
    AlertDialog dontEnoughCoinsDialog;
    String confirmDialogMessage;

    SharedPreferences mSettings;
    SharedPreferences.Editor editor;

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

        // Получаем настройки
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        //  Устанавливаем тему, отображаем Activity
        theme = mSettings.getInt(APP_PREFERENCES_THEME, R.style.AppTheme);
        setTheme(theme);
        setContentView(R.layout.activity_packs);

        //  Устанавливаем button_selector темы на Buttons backgrounds
        switch (theme) {
            case R.style.AppTheme: {
                findViewById(R.id.button_pack_1).setBackground(getResources().getDrawable(R.drawable.button_selector_standart));
                findViewById(R.id.button_pack_1_name).setBackground(getResources().getDrawable(R.drawable.button_selector_standart));
                findViewById(R.id.button_pack_2).setBackground(getResources().getDrawable(R.drawable.button_selector_standart));
                findViewById(R.id.button_pack_2_name).setBackground(getResources().getDrawable(R.drawable.button_selector_standart));
                findViewById(R.id.button_pack_hard).setBackground(getResources().getDrawable(R.drawable.button_selector_standart));
                findViewById(R.id.button_pack_hard_name).setBackground(getResources().getDrawable(R.drawable.button_selector_standart));
            } break;
            case R.style.BlackTheme: {
                findViewById(R.id.button_pack_1).setBackground(getResources().getDrawable(R.drawable.button_selector_black));
                findViewById(R.id.button_pack_1_name).setBackground(getResources().getDrawable(R.drawable.button_selector_black));
                findViewById(R.id.button_pack_2).setBackground(getResources().getDrawable(R.drawable.button_selector_black));
                findViewById(R.id.button_pack_2_name).setBackground(getResources().getDrawable(R.drawable.button_selector_black));
                findViewById(R.id.button_pack_hard).setBackground(getResources().getDrawable(R.drawable.button_selector_black));
                findViewById(R.id.button_pack_hard_name).setBackground(getResources().getDrawable(R.drawable.button_selector_black));
            } break;
             case R.style.WhiteTheme: {
                findViewById(R.id.button_pack_1).setBackground(getResources().getDrawable(R.drawable.button_selector_white));
                findViewById(R.id.button_pack_1_name).setBackground(getResources().getDrawable(R.drawable.button_selector_white));
                findViewById(R.id.button_pack_2).setBackground(getResources().getDrawable(R.drawable.button_selector_white));
                findViewById(R.id.button_pack_2_name).setBackground(getResources().getDrawable(R.drawable.button_selector_white));
                findViewById(R.id.button_pack_hard).setBackground(getResources().getDrawable(R.drawable.button_selector_white));
                findViewById(R.id.button_pack_hard_name).setBackground(getResources().getDrawable(R.drawable.button_selector_white));
            } break;
             case R.style.FreshTheme: {
                findViewById(R.id.button_pack_1).setBackground(getResources().getDrawable(R.drawable.button_selector_fresh));
                findViewById(R.id.button_pack_1_name).setBackground(getResources().getDrawable(R.drawable.button_selector_fresh));
                findViewById(R.id.button_pack_2).setBackground(getResources().getDrawable(R.drawable.button_selector_fresh));
                findViewById(R.id.button_pack_2_name).setBackground(getResources().getDrawable(R.drawable.button_selector_fresh));
                findViewById(R.id.button_pack_hard).setBackground(getResources().getDrawable(R.drawable.button_selector_fresh));
                findViewById(R.id.button_pack_hard_name).setBackground(getResources().getDrawable(R.drawable.button_selector_fresh));
            } break;
        }
        /*
         *
         *
         *
         */

        // Присваивам Views переменным по id
        back_button = (ImageButton) findViewById(R.id.back_button);
        packs_coins = (TextView) findViewById(R.id.packs_coins);

        //  Обновляем количество монет
        refreshCoins();

        //  Удаление кнопок купленных тем
        checkPurchasedPacks();

        // Загружаем изображения темы
        loadThemeImages();

        //  Загружаем диалоговое окно с подтверждением покупки темы
        loadAlertDialogs();
    }

    private void refreshCoins() {
        coins = mSettings.getInt(APP_PREFERENCES_COINS, 0);
        packs_coins.setText(coins.toString());
    }

    private void checkPurchasedPacks() {
        if (mSettings.getBoolean(APP_PREFERENCES_PACK_1, false)) {
            findViewById(R.id.buy_button_pack_1).setVisibility(View.GONE);
        }
        if (mSettings.getBoolean(APP_PREFERENCES_PACK_2, false)) {
            findViewById(R.id.buy_button_pack_2).setVisibility(View.GONE);
        }
        if (mSettings.getBoolean(APP_PREFERENCES_PACK_HARD, false)) {
            findViewById(R.id.buy_button_pack_hard).setVisibility(View.GONE);
        }
    }

    private void loadThemeImages() {
        if (theme == R.style.WhiteTheme) {
            back_button.setImageResource(R.drawable.ic_arrow_back_black_36dp);
            packs_coins.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_stars_black_24dp), null, null, null);
        }
        else if (theme == R.style.BlackTheme) {
            TextView buy_button_pack_1 = (TextView) findViewById(R.id.buy_button_pack_1);
            buy_button_pack_1.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_stars_black_24dp), null);
            TextView buy_button_pack_2 = (TextView) findViewById(R.id.buy_button_pack_2);
            buy_button_pack_2.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_stars_black_24dp), null);
            TextView buy_button_pack_hard = (TextView) findViewById(R.id.buy_button_pack_hard);
            buy_button_pack_hard.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_stars_black_24dp), null);
        }
    }

    private void loadAlertDialogs() {
        //                      CONFIRM DIALOG

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        confirmBuilder.setTitle("");

        // Add the buttons
        confirmBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                editor = mSettings.edit();
                switch (clicked_pack_button_id) {
                    case R.id.button_pack_1:
                        editor.putInt(APP_PREFERENCES_COINS, coins - COST_PACK_1);
                        editor.putBoolean(APP_PREFERENCES_PACK_1, true);
                        break;
                    case R.id.button_pack_2:
                        editor.putInt(APP_PREFERENCES_COINS, coins - COST_PACK_2);
                        editor.putBoolean(APP_PREFERENCES_PACK_2, true);
                        break;
                    case R.id.button_pack_hard:
                        editor.putInt(APP_PREFERENCES_COINS, coins - COST_PACK_HARD);
                        editor.putBoolean(APP_PREFERENCES_PACK_HARD, true);
                        break;
                }
                editor.commit();
                recreate();
            }
        });
        confirmBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        // 3. Get the AlertDialog from create()
        confirmDialog = confirmBuilder.create();

        //                      DONT ENOUGH COINS DIALOG

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder coinsBuilder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        coinsBuilder.setTitle(R.string.dont_enough_coins_dialog_title);

        // Add the buttons
        coinsBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });


        // 3. Get the AlertDialog from create()
        dontEnoughCoinsDialog = coinsBuilder.create();
    }

    public void clickBuyPack(View view) {
        clicked_pack_button_id = view.getId();

        switch (clicked_pack_button_id) {
            case R.id.button_pack_1:
                    if (mSettings.getBoolean(APP_PREFERENCES_PACK_1, true)) {
                        Intent playIntent = new Intent(this, LevelActivity.class);
                        playIntent.putExtra(Intent.EXTRA_TEXT, "PACK_1");
                        startActivity(playIntent);
                    }
                    else if (coins >= COST_PACK_1) {
                        confirmDialogMessage = String.format(getString(R.string.confirm_dialog_title_pack), "Pack 1", COST_PACK_1);
                        confirmDialog.setTitle(confirmDialogMessage);
                        confirmDialog.show();
                    } else dontEnoughCoinsDialog.show();
                break;
            case R.id.button_pack_2:
                if (mSettings.getBoolean(APP_PREFERENCES_PACK_2, true)) {
                    Intent playIntent = new Intent(this, LevelActivity.class);
                    playIntent.putExtra(Intent.EXTRA_TEXT, "PACK_2");
                    startActivity(playIntent);
                }
                else if (coins >= COST_PACK_2) {
                    confirmDialogMessage = String.format(getString(R.string.confirm_dialog_title_pack), "Pack 2", COST_PACK_2);
                    confirmDialog.setTitle(confirmDialogMessage);
                    confirmDialog.show();
                } else dontEnoughCoinsDialog.show();
                break;
            case R.id.button_pack_hard:
                if (mSettings.getBoolean(APP_PREFERENCES_PACK_HARD, true)) {
                    Intent playIntent = new Intent(this, LevelActivity.class);
                    playIntent.putExtra(Intent.EXTRA_TEXT, "PACK_HARD");
                    startActivity(playIntent);
                }
                else if (coins >= COST_PACK_HARD) {
                    confirmDialogMessage = String.format(getString(R.string.confirm_dialog_title_pack), "Pack hard", COST_PACK_HARD);
                    confirmDialog.setTitle(confirmDialogMessage);
                    confirmDialog.show();
                } else dontEnoughCoinsDialog.show();
                break;
        }
    }

    public void clickBack(View view) {
        this.onBackPressed();
    }

}

