package com.example.exam2020_certificateapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.JsonReader;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.exam2020_certificateapp.swipe.NewsAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class NewsActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private NewsAdapter newsAdapter;
    private ProgressDialog dialog;
    private ArrayList<String> l = new ArrayList<>();
    private Handler mHandler = new Handler();
    private Runnable mUpdateResults = new Runnable() {
        @Override
        public void run() {
            updateUI();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        viewPager = findViewById(R.id.newsViewPager);
        newsAdapter = new NewsAdapter(getSupportFragmentManager(), l);
        viewPager.setAdapter(newsAdapter);
        progressBar();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                URL restapi = null;
                try {
                    restapi = new URL("https://api.github.com/");
                } catch (MalformedURLException e) {
                    Log.d("RESTAPI", "Error in url");
                    e.printStackTrace();
                }
                HttpsURLConnection connection = null;
                try {
                    connection = (HttpsURLConnection) restapi.openConnection();
                } catch (IOException e) {
                    Log.d("RESTAPI", "Error in connection");
                    e.printStackTrace();
                }
                try {
                    if(connection.getResponseCode() == 200)
                    {
                    } else {
                    }
                } catch (IOException e) {
                    Log.d("RESTAPI", "Error in response ");
                    e.printStackTrace();
                }
                InputStream responseBody = null;
                try {
                    responseBody = connection.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStreamReader responseBodyReader = null;
                try {
                    responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                try {
                    jsonReader.beginObject();
                    while (jsonReader.hasNext())
                    {
                        String key = jsonReader.nextName();
                        l.add(key);
                        String value = jsonReader.nextString();
                        l.add(value);
                    }
                    jsonReader.close();
                    connection.disconnect();
                    mHandler.post(mUpdateResults);
                    for (String a : l) {
                        Log.d("RESTAPI", a);
                    }
                    Log.d("RESTAPI", l.size()+"");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void progressBar(){
        Log.d("TAG","ProgressBar?");
        dialog = new ProgressDialog(NewsActivity.this);
        dialog.show();
        dialog.setContentView(R.layout.progress_loading_certificates);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void updateUI() {
        dialog.dismiss();
        newsAdapter.notifyDataSetChanged();
    }

}
