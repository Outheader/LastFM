package com.example.testlastfm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "LastFM_TAG ";
    public static final String API_KEY = "734df4355aeea4ec7802cec249500e74";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public static String stringToUTF8(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String utf8ToString(String string) {
        try {
            return URLEncoder.encode(string, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String displayDensity(Context context){
        float density = context.getResources().getDisplayMetrics().density;
        if (density < 1.5f) return "small";//mdpi
        if (density >= 1.5f && density < 2.0f) return "medium";//hdpi
        if (density >= 2.0f && density < 3.0f) return "large";//xhdpi
        if (density >= 3.0f && density < 4.0f) return "extralarge";//xxhdpi
        if (density >= 4.0f ) return "mega";//xxxhdpi
        return "medium";
    }
}