package com.example.exam2020_certificateapp.swipe;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.exam2020_certificateapp.helpers.PhotoHolder;
import com.example.exam2020_certificateapp.model.Certificate;

public class CertificateAdapter extends FragmentStatePagerAdapter {
    public CertificateAdapter(FragmentManager fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        CertificateFragment certificateFragment = new CertificateFragment();
        Bundle bundle = new Bundle();
        position = position+1;
        bundle.putInt("position", position);
        certificateFragment.setArguments(bundle);
        return certificateFragment;
    }

    @Override
    public int getCount() {
        return 100;
    }
}
