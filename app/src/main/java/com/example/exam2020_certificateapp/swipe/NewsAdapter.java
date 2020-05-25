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
    public NewsAdapter(FragmentManager fragment, ArrayList<News> strings) {
        super(fragment);
        this.strings = strings;
    }

    private ArrayList<News> strings;
    @NonNull
    @Override
    public Fragment getItem(int position) {
        NewsFragment newsFragment = new NewsFragment();
        Bundle bundle = new Bundle();
        int whatIIs = 5;
        if(position*5 < strings.size()-1)
        {
            whatIIs = 5;
        } else {
            whatIIs = strings.size()%5;
        }

        for (int i = 0; i < whatIIs; i++)
        {
            bundle.putString("title" + i, strings.get(position*5+i).getTitle());
            bundle.putString("url" + i, strings.get(position*5+i).getUrl());
        }
        newsFragment.setArguments(bundle);
        return newsFragment;
    }

    @Override
    public int getCount() {
        if(strings.size()%5 == 0)
        {
            return strings.size()/5;
        }
        return strings.size()/5+1;
    }
}
