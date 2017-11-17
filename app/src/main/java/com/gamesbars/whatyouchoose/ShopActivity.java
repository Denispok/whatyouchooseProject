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
import android.widget.TextView;

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
    private static final Integer THEME_COST_BLACK = 100;
    private static final Integer THEME_COST_WHITE = 100;
    private static final Integer THEME_COST_FRESH = 100;

    Integer theme;
    Integer coins;
    Integer clicked_buy_button_id;

    ImageButton back_button;
    TextView shop_coins;

    AlertDialog confirmDialog;
    AlertDialog dontEnoughCoinsDialog;
    String confirmDialogMessage;

    SharedPreferences mSettings;
    SharedPreferences.Editor editor;

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
        refreshCoins();

        //  Удаление кнопок купленных тем
        checkPurchasedThemes();

        // Загружаем изображения темы
        loadThemeImages();

        //  Загружаем диалоговое окно с подтверждением покупки темы
        loadAlertDialogs();
    }

    private void refreshCoins() {
        coins = mSettings.getInt(APP_PREFERENCES_COINS, 0);
        shop_coins.setText(coins.toString());
    }

    private void checkPurchasedThemes() {
        if (mSettings.getBoolean(APP_PREFERENCES_THEME_STD, false)) {
            findViewById(R.id.buy_button_std).setVisibility(View.GONE);
        }
        if (mSettings.getBoolean(APP_PREFERENCES_THEME_BLACK, false)) {
            findViewById(R.id.buy_button_black).setVisibility(View.GONE);
        }
        if (mSettings.getBoolean(APP_PREFERENCES_THEME_WHITE, false)) {
            findViewById(R.id.buy_button_white).setVisibility(View.GONE);
        }
        if (mSettings.getBoolean(APP_PREFERENCES_THEME_FRESH, false)) {
            findViewById(R.id.buy_button_fresh).setVisibility(View.GONE);
        }
    }

    private void loadThemeImages() {
        if (theme == R.style.WhiteTheme) {
            back_button.setImageResource(R.drawable.ic_arrow_back_black_36dp);
            shop_coins.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_stars_black_24dp), null, null, null);
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

                switch (clicked_buy_button_id) {
                    case R.id.button_theme_std:
                        editor = mSettings.edit();
                        editor.putInt(APP_PREFERENCES_COINS, coins - THEME_COST_STD);
                        editor.putBoolean(APP_PREFERENCES_THEME_STD, true);
                        editor.commit();
                        setStd();
                        break;

                    case R.id.button_theme_black:
                        editor = mSettings.edit();
                        editor.putInt(APP_PREFERENCES_COINS, coins - THEME_COST_BLACK);
                        editor.putBoolean(APP_PREFERENCES_THEME_BLACK, true);
                        editor.commit();
                        setBlack();
                        break;

                    case R.id.button_theme_white:
                        editor = mSettings.edit();
                        editor.putInt(APP_PREFERENCES_COINS, coins - THEME_COST_WHITE);
                        editor.putBoolean(APP_PREFERENCES_THEME_WHITE, true);
                        editor.commit();
                        setWhite();
                        break;

                    case R.id.button_theme_fresh:
                        editor = mSettings.edit();
                        editor.putInt(APP_PREFERENCES_COINS, coins - THEME_COST_FRESH);
                        editor.putBoolean(APP_PREFERENCES_THEME_FRESH, true);
                        editor.commit();
                        setFresh();
                        break;

                }
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

    public void clickBuyTheme(View view) {
        clicked_buy_button_id = view.getId();

        switch (clicked_buy_button_id) {
            case R.id.button_theme_std:
                if (theme != R.style.AppTheme) {    // if theme already present do nothing
                    if (mSettings.getBoolean(APP_PREFERENCES_THEME_STD, true)) setStd();
                    else if (coins >= THEME_COST_STD) {
                        confirmDialogMessage = String.format(getString(R.string.confirm_dialog_title), "Std", THEME_COST_STD);
                        confirmDialog.setTitle(confirmDialogMessage);
                        confirmDialog.show();
                    } else dontEnoughCoinsDialog.show();
                }
                break;

            case R.id.button_theme_black:
                if (theme != R.style.BlackTheme) {    // if theme already present do nothing
                    if (mSettings.getBoolean(APP_PREFERENCES_THEME_BLACK, true)) setBlack();
                    else if (coins >= THEME_COST_BLACK) {
                        confirmDialogMessage = getString(R.string.confirm_dialog_title, "Black", THEME_COST_BLACK);
                        confirmDialog.setTitle(confirmDialogMessage);
                        confirmDialog.show();
                    } else dontEnoughCoinsDialog.show(); // SHOW YOU DON'T HAVE ENOUGH COINS DIALOG
                }
                break;

            case R.id.button_theme_white:
                if (theme != R.style.WhiteTheme) {    // if theme already present do nothing
                    if (mSettings.getBoolean(APP_PREFERENCES_THEME_WHITE, true)) setWhite();
                    else if (coins >= THEME_COST_WHITE) {
                        confirmDialogMessage = getString(R.string.confirm_dialog_title, "White", THEME_COST_WHITE);
                        confirmDialog.setTitle(confirmDialogMessage);
                        confirmDialog.show();
                    } else dontEnoughCoinsDialog.show(); // SHOW YOU DON'T HAVE ENOUGH COINS DIALOG
                }
                break;

            case R.id.button_theme_fresh:
                if (theme != R.style.FreshTheme) {    // if theme already present do nothing
                    if (mSettings.getBoolean(APP_PREFERENCES_THEME_FRESH, true)) setFresh();
                    else if (coins >= THEME_COST_FRESH) {
                        confirmDialogMessage = getString(R.string.confirm_dialog_title, "Fresh", THEME_COST_FRESH);
                        confirmDialog.setTitle(confirmDialogMessage);
                        confirmDialog.show();
                    } else dontEnoughCoinsDialog.show(); // SHOW YOU DON'T HAVE ENOUGH COINS DIALOG
                }
                break;
        }
    }

    public void setStd() {
        editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_THEME, R.style.AppTheme);
        editor.commit();
        this.recreate();
    }

    public void setBlack() {
        editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_THEME, R.style.BlackTheme);
        editor.commit();
        this.recreate();
    }

    public void setWhite() {
        editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_THEME, R.style.WhiteTheme);
        editor.commit();
        this.recreate();
    }

    public void setFresh() {
        editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_THEME, R.style.FreshTheme);
        editor.commit();
        this.recreate();
    }

    public void clickBack(View view) {
        this.onBackPressed();
    }

}

