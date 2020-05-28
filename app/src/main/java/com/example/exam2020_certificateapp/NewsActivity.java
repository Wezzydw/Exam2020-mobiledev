package com.example.exam2020_certificateapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.exam2020_certificateapp.model.News;
import com.example.exam2020_certificateapp.swipe.NewsAdapter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {
    private final static int CONNECTION_TIMEOUT = 5000;
    private ViewPager viewPager; //Viewpager this displays our swipes
    private NewsAdapter newsAdapter; //Adapter this creates our pages for our viewpager
    private ProgressDialog dialog; //Loading dialog
    private ArrayList<News> listOfNews = new ArrayList<>(); //List of news
    private Handler mHandler = new Handler(); // Handler to return to gui thread
    private Runnable mUpdateResults = new Runnable() {
        @Override
        public void run() {
            updateUI();
        }
    }; //Calls GUI Thread to ensure correct updates
    private Runnable mCallToastError = new Runnable() {
        @Override
        public void run() {
            toastError();
        }
    }; //Calls GUI Thread to ensure toast is run in gui

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        viewPager = findViewById(R.id.newsViewPager);
        newsAdapter = new NewsAdapter(getSupportFragmentManager(), listOfNews);
        viewPager.setAdapter(newsAdapter);
        downloadNewsFromRestAPI();
    }

    /**
     * Initiates a download of news from RESTAPI which will be done from another thread
     */
    private void downloadNewsFromRestAPI() {
        progressBar();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL restapi = null;
                    restapi = new URL("http://77.75.161.9:1337/api/news");
                    HttpURLConnection connection = null;

                    connection = (HttpURLConnection) restapi.openConnection();
                    connection.setConnectTimeout(CONNECTION_TIMEOUT);
                    if(connection.getResponseCode() == 200)
                    {
                    } else {
                    }
                    InputStream responseBody = null;
                    InputStreamReader responseBodyReader = null;
                    responseBody = connection.getInputStream();
                    responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                    List<News> tempList = readNewsArray(jsonReader);
                    connection.disconnect();
                    listOfNews.addAll(tempList);
                    mHandler.post(mUpdateResults);
                    if(tempList.isEmpty())
                    {
                        mHandler.post(mCallToastError);
                    }
                    dialog.dismiss();
                } catch ( FileNotFoundException | MalformedURLException | SocketTimeoutException e) {
                    e.printStackTrace();
                    mHandler.post(mCallToastError);
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.post(mCallToastError);
                }
            }
        });
    }

    /**
     * Starts a dialog which display a loading icon until stopped by calling dialog.dismiss();
     */
    private void progressBar(){
        dialog = new ProgressDialog(NewsActivity.this);
        dialog.show();
        dialog.setContentView(R.layout.progress_loading_certificates);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    /**
     * Notifies newsAdapter that the data it is currently using has been updated
     */
    private void updateUI() {
        newsAdapter.notifyDataSetChanged();
    }

    /**
     * Calls a toast that displays an error to user
     */
    private void toastError() {
        Toast ErrorConnectingToRESTAPI = Toast.makeText(NewsActivity.this, "Error Connecting to Server", Toast.LENGTH_LONG);
        ErrorConnectingToRESTAPI.show();
        dialog.dismiss();
        finish();
    }

    /**
     * Consumes the [] from a json, which gives all objects{} in that array
     * By using jsonreader as the base object, it then extracts all objects from the array
     * @param jsonReader
     * @return a list of news
     * @throws IOException if anything goes wrong with the jsonreaders methods
     */
    private List<News> readNewsArray(JsonReader jsonReader)throws IOException {
        List<News> newsList = new ArrayList<>();
        jsonReader.beginArray();
        while (jsonReader.hasNext())
        {
            newsList.add(readNewsObject(jsonReader));
        }
        jsonReader.endArray();
        return newsList;
    }

    /**
     * Extracts all data in a given json object{}
     * @param jsonReader
     * @return a news object with that data
     * @throws IOException if anything goes wrong with the jsonreaders methods
     */
    private News readNewsObject(JsonReader jsonReader) throws IOException {
        News news = new News();
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if(name.equals("id")){
                news.setId(jsonReader.nextInt());
            } else if( name.equals("title")){
                news.setTitle(jsonReader.nextString());
            } else if(name.equals("url")){
                news.setUrl(jsonReader.nextString());
            }
        }
        jsonReader.endObject();
        return news;
    }

}
