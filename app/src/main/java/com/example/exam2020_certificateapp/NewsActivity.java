package com.example.exam2020_certificateapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.exam2020_certificateapp.swipe.NewsAdapter;

public class NewsActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private NewsAdapter newsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        viewPager = findViewById(R.id.newsViewPager);
        newsAdapter = new NewsAdapter(getSupportFragmentManager());
        viewPager.setAdapter(newsAdapter);
    }
}
