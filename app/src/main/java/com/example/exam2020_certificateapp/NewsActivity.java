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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

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
                try {
                    /*CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    InputStream caInput = getAssets().open("certificate_der.crt");
                    //InputStream caInput = new BufferedInputStream(new FileInputStream(getAssets().open("certifcate_der.crt")));
                    Certificate ca;
                    ca = cf.generateCertificate(caInput);
                    System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());

                    String keyStoreType = KeyStore.getDefaultType();
                    KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                    keyStore.load(null, null);
                    keyStore.setCertificateEntry("ca", ca);

                    String tmfAlgo = TrustManagerFactory.getDefaultAlgorithm();
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgo);
                    tmf.init(keyStore);

                    SSLContext context = SSLContext.getInstance("TLS");
                    context.init(null, tmf.getTrustManagers(), null);
*/
                    URL restapi = null;
                    restapi = new URL("http://192.168.0.111:5000/api/news");
                    HttpURLConnection connection = null;

                    connection = (HttpURLConnection) restapi.openConnection();
                    //connection.setSSLSocketFactory(context.getSocketFactory());

                    if(connection.getResponseCode() == 200)
                    {
                    } else {
                    }
                    InputStream responseBody = null;
                    InputStreamReader responseBodyReader = null;
                    responseBody = connection.getInputStream();
                    responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                    JsonReader jsonReader = new JsonReader(responseBodyReader);
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

                } catch ( FileNotFoundException |    MalformedURLException e) {
                    e.printStackTrace();
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
