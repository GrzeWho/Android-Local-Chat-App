package com.kot.mova;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kot.mova.model.Coordinates;
import com.kot.mova.model.Message;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Calendar;

public class MessageActivity extends AppCompatActivity {
    CollectionReference messages;
    EditText messageEdit;
    TextView reachView;
    SeekBar seekBar;
    String API_URL = "http://worldtimeapi.org/api/ip/";
    String IP_API_URL = "https://touch.whatsmyip.org/";
    double reach;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        messages = firebaseFirestore.collection("messages");
        Button button = findViewById(R.id.button);
        seekBar = findViewById(R.id.seekBar);
        messageEdit = findViewById(R.id.editText);
        reachView = findViewById(R.id.reach);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                reachView.setText((double) progress/10 + " km");
                reach = (double) progress / 10;
            }
        });
        button.setOnClickListener(view -> {
            IpAsync ipTask = new IpAsync();
            ipTask.execute(IP_API_URL);
        });
    }

    @SuppressLint("MissingPermission")
    public void sendMessage(long timestamp) {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MessageActivity.this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(MessageActivity.this, location -> {
                    if (location != null) {
                        Bundle bundle = getIntent().getExtras();
                        Coordinates coordinates = new Coordinates(location.getLatitude(), location.getLongitude());
                        Message messageToSend =
                                new Message(messageEdit.getText().toString(), bundle.getString("username"), timestamp, coordinates, reach, false);
                        messages.add(messageToSend);
                        finish();
                    }
                });
    }

    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        Log.e("testing", "making a request");
        if (url == null)
            return jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream is = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(7500);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                is = urlConnection.getInputStream();
                jsonResponse = readFromStream(is);
            } else {
                Log.e("lol", urlConnection.getResponseMessage());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            if (is != null)
                is.close();
        }
        return jsonResponse;
    }

    private String readFromStream(InputStream is) throws IOException {
        StringBuilder output = new StringBuilder();
        Log.e("testing", "readfromstream");

        if (is != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(is, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        Log.e("testing", output.toString());

        return output.toString();
    }
    public class IpAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            String jsonResponse = "";
            try {
                Document doc = Jsoup.connect(strings[0]).get();
                Log.e("a", doc.getElementById("ip").text());
                return doc.getElementById("ip").text();
            } catch (IOException e) {
                Log.e("suppressing", "This exception means either the ip service is down or the user has no internet access.");
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            if(!s.equals("")) {
                TimeAsync task = new TimeAsync();
                task.execute(API_URL);
            } else {
                sendMessage(Calendar.getInstance().getTime().getTime()/1000);
            }
        }
    }

    public class TimeAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            String jsonResponse = "";
            try {
                url = new URL(strings[0]);
                jsonResponse = makeHttpRequest(url);
                Log.e("b", jsonResponse);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResponse;
        }

        @SuppressLint("MissingPermission")
        @Override
        protected void onPostExecute(String s) {
            Log.e("testing", s);
            JSONObject root = null;
            try {
                root = new JSONObject(s);
                long timestamp = root.getLong("unixtime");
                sendMessage(timestamp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
