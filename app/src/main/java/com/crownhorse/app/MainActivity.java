package com.crownhorse.app;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.crownhorse.app.utils.LocaleHelper;

/**
 * Base activity that applies the stored locale before creating the view.
 */
public abstract class MainActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("crownhorse_prefs", MODE_PRIVATE);
        String lang = prefs.getString("language", "en");
        Context ctx = LocaleHelper.setLocale(newBase, lang);
        super.attachBaseContext(ctx);
    }
}
