package com.example.exam2020_certificateapp.swipe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.exam2020_certificateapp.R;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NewsFragment extends Fragment {

    /**
     * uses a standard formula where the only thing done we specify is what xml to use
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.swipe_news_container, container, false);
    }

    /**
     * fills the view components with the data from the bundle.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@Nonnull View view, @Nullable Bundle savedInstanceState) {
        final Bundle args = getArguments();
        //Initialize buttons
        Button button0 = view.findViewById(R.id.swipe_news_btnOpenLink);
        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(args.getString("url0")));
                startActivity(browserIntent);
            }
        });
        Button button1 = view.findViewById(R.id.swipe_news_btnOpenLink1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(args.getString("url1")));
                startActivity(browserIntent);
            }
        });
        Button button2 = view.findViewById(R.id.swipe_news_btnOpenLink2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(args.getString("url2")));
                startActivity(browserIntent);
            }
        });
        Button button3 = view.findViewById(R.id.swipe_news_btnOpenLink3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(args.getString("url3")));
                startActivity(browserIntent);
            }
        });
        Button button4 = view.findViewById(R.id.swipe_news_btnOpenLink4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(args.getString("url4")));
                startActivity(browserIntent);
            }
        });
        //Initialize news text
        ((TextView)view.findViewById(R.id.newsTxtNewsHeader)).setText((args.getString("title0")));
        ((TextView)view.findViewById(R.id.newsTxtNewsHeader1)).setText((args.getString("title1")));
        ((TextView)view.findViewById(R.id.newsTxtNewsHeader2)).setText((args.getString("title2")));
        ((TextView)view.findViewById(R.id.newsTxtNewsHeader3)).setText((args.getString("title3")));
        ((TextView)view.findViewById(R.id.newsTxtNewsHeader4)).setText((args.getString("title4")));
    }
}
