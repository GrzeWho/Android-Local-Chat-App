package com.kot.mova;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
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
        Button buttonApply = findViewById(R.id.buttonApply);

        buttonApply.setOnClickListener(view -> {
            SharedPreferences.Editor editor = prefs.edit();
            if(messageNumber!=0) {
                editor.putInt("messages", messageNumber);
            }
            if(kilometers!=0) {
                editor.putFloat("distance", (float) kilometers);
            }
            editor.apply();
            finish();
        });
        kmSeekBar = findViewById(R.id.seekBarkm);
        prefs = getSharedPreferences("Mova", MODE_PRIVATE);
        int messages = prefs.getInt("messages", 500);
        messageNumberView.setText(messages+"");

        kmView.setText(String.format("%.2f", prefs.getFloat("distance", 10)) + " km");

        kmSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress != 0) {
                    kilometers = (double)(progress*2)/10;
                    kmView.setText(kilometers + " km");
                } else {
                    kmView.setText(0.01 + " km");
                    kilometers = 0.01;
                }

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
                if(progress != 0) {
                    messageNumberView.setText("" + progress*10);
                    messageNumber = progress*10;
                } else {
                    messageNumberView.setText("1");
                    messageNumber = 1;
                }
            }
        });

    }
}