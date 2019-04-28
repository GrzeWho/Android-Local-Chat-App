package com.kot.mova;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    SeekBar kmSeekBar;
    SeekBar messagesSeekBar;
    double kilometers;
    int messageNumber;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        TextView kmView = findViewById(R.id.distanceTextView);
        TextView messageNumberView = findViewById(R.id.numberOfMessagesTextView);
        kmSeekBar = findViewById(R.id.seekBarkm);
        prefs = getSharedPreferences("Mova", MODE_PRIVATE);
        int messages = prefs.getInt("messages", 500);
        messageNumberView.setText(messages+"");
        double distance = (double) prefs.getFloat("distance", 10);
        kmView.setText(distance + " km");

        kmSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                kilometers = (double)(progress*5)/10;
                kmView.setText(kilometers + " km");
            }
        });

        messagesSeekBar = findViewById(R.id.seekBarMessages);
        messagesSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                messageNumberView.setText("" + progress*10);
                messageNumber = progress*10;
            }
        });

    }
    @Override
    protected void onStop() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("messages", messageNumber);
        editor.putFloat("distance", (float) kilometers);
        editor.apply();
        super.onStop();
    }
}