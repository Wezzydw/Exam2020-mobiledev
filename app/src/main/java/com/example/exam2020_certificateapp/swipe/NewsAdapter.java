package com.example.exam2020_certificateapp.swipe;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class NewsAdapter extends FragmentStatePagerAdapter {
    public NewsAdapter(FragmentManager fragment, ArrayList<String> strings) {
        super(fragment);
        this.strings = strings;
    }

    private ArrayList<String> strings;
    @NonNull
    @Override
    public Fragment getItem(int position) {
        NewsFragment newsFragment = new NewsFragment();
        Bundle bundle = new Bundle();
        position = position + 1;
        bundle.putString("position", strings.get(position));
        newsFragment.setArguments(bundle);
        return newsFragment;
    }

    @Override
    public int getCount() {
        return strings.size();
    }

}
