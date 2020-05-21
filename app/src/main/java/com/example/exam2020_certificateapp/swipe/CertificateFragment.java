package com.example.exam2020_certificateapp.swipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.exam2020_certificateapp.R;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CertificateFragment extends Fragment {

    public static final String ARG_OBJECT = "object";


    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.swipe_container, container, false);
    }

    @Override
    public void onViewCreated(@Nonnull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        ((TextView)view.findViewById(R.id.textView)).setText((args.getString("message")));
    }
}
