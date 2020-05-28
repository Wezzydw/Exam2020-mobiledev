package com.example.exam2020_certificateapp.swipe;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.exam2020_certificateapp.model.News;

import java.util.ArrayList;

public class NewsAdapter extends FragmentStatePagerAdapter {
    public NewsAdapter(FragmentManager fragment, ArrayList<News> news) {
        super(fragment);
        this.news = news;
    }
    private ArrayList<News> news; //List of news

    /**
     * gets the fragment in the given position to be displayed
     * @param position
     * @return fragment that contains the data for the news in the given position
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        NewsFragment newsFragment = new NewsFragment();
        Bundle bundle = new Bundle();
        int whatIIs = 5;
        if(position*5 < news.size()-1)
        {
            whatIIs = 5;
        } else {
            whatIIs = news.size()%5;
        }

        for (int i = 0; i < whatIIs; i++)
        {
            bundle.putString("title" + i, news.get(position*5+i).getTitle());
            bundle.putString("url" + i, news.get(position*5+i).getUrl());
        }
        newsFragment.setArguments(bundle);
        return newsFragment;
    }

    /**
     * Returns the int of pages of news there should be made for the viewpager
     * @return
     */
    @Override
    public int getCount() {
        if(news.size()%5 == 0)
        {
            return news.size()/5;
        }
        return news.size()/5+1;
    }
}
