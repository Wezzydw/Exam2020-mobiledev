package com.example.exam2020_certificateapp.swipe;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class NewsAdapter extends FragmentStatePagerAdapter {
    public NewsAdapter(FragmentManager fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        NewsFragment newsFragment = new NewsFragment();
        Bundle bundle = new Bundle();
        position = position + 1;
        bundle.putString("position", position+"");
        newsFragment.setArguments(bundle);
        return newsFragment;
    }

    @Override
    public int getCount() {
        return 100;
    }

}
