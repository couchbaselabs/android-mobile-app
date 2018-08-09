package com.couchbase.couchbase;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class GeneralSettings extends Fragment {
    private View generalSettingsView;

    private RadioGroup logNumber;
    private RadioGroup delayTime;

    private RadioButton logFive;
    private RadioButton logTen;
    private RadioButton logTwenty;
    private RadioButton logTwentyFive;
    private RadioButton logFifty;

    private RadioButton delayFive;
    private RadioButton delayTen;
    private RadioButton delayThirty;
    private RadioButton delayOneMinute;
    private RadioButton delayFiveMinutes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        generalSettingsView = inflater.inflate(R.layout.general_settings, container, false);
        logNumber = generalSettingsView.findViewById(R.id.log_number);
        delayTime = generalSettingsView.findViewById(R.id.delay_time);

        logFive = generalSettingsView.findViewById(R.id.log_five);
        logTen = generalSettingsView.findViewById(R.id.log_ten);
        logTwenty = generalSettingsView.findViewById(R.id.log_twenty);
        logTwentyFive = generalSettingsView.findViewById(R.id.log_twenty_five);
        logFifty = generalSettingsView.findViewById(R.id.log_fifty);

        delayFive = generalSettingsView.findViewById(R.id.delay_five);
        delayTen = generalSettingsView.findViewById(R.id.delay_ten);
        delayThirty = generalSettingsView.findViewById(R.id.delay_thirty);
        delayOneMinute = generalSettingsView.findViewById(R.id.delay_one_minute);
        delayFiveMinutes = generalSettingsView.findViewById(R.id.delay_five_minutes);

        SharedPreferences logPrefs = getContext().getSharedPreferences("LogNumber", Context.MODE_PRIVATE);
        if(logPrefs.getInt("LogNum", 20) == 5){
            logFive.setChecked(true);
        }
        else if(logPrefs.getInt("LogNum", 20) == 10){
            logTen.setChecked(true);
        }
        else if(logPrefs.getInt("LogNum", 20) == 20){
            logTwenty.setChecked(true);
        }
        else if(logPrefs.getInt("LogNum", 20) == 25){
            logTwentyFive.setChecked(true);
        }
        else if(logPrefs.getInt("LogNum", 20) == 50){
            logFifty.setChecked(true);
        }

        SharedPreferences delayPrefs = getContext().getSharedPreferences("DelayTime", Context.MODE_PRIVATE);
        if(delayPrefs.getInt("Delay", 20) == 5){
            delayFive.setChecked(true);
        }
        else if(delayPrefs.getInt("Delay", 20) == 10){
            delayTen.setChecked(true);
        }
        else if(delayPrefs.getInt("Delay", 20) == 30){
            delayThirty.setChecked(true);
        }
        else if(delayPrefs.getInt("Delay", 20) == 60){
            delayOneMinute.setChecked(true);
        }
        else if(delayPrefs.getInt("Delay", 20) == 60 * 5){
            delayFiveMinutes.setChecked(true);
        }

        logNumber.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("LogNumber", Context.MODE_PRIVATE);
                if(logFive.isChecked()){
                    sharedPreferences.edit().putInt("LogNum", 5).apply();
                }
                else if(logTen.isChecked()){
                    sharedPreferences.edit().putInt("LogNum", 10).apply();
                }
                else if(logTwenty.isChecked()){
                    sharedPreferences.edit().putInt("LogNum", 20).apply();
                }
                else if(logTwentyFive.isChecked()){
                    sharedPreferences.edit().putInt("LogNum", 25).apply();
                }
                else if(logFifty.isChecked()){
                    sharedPreferences.edit().putInt("LogNum", 50).apply();
                }
            }
        });

        delayTime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("DelayTime", Context.MODE_PRIVATE);
                if(delayFive.isChecked()){
                    sharedPreferences.edit().putInt("Delay", 5).apply();
                }
                else if(delayTen.isChecked()){
                    sharedPreferences.edit().putInt("Delay", 10).apply();
                }
                else if(delayThirty.isChecked()){
                    sharedPreferences.edit().putInt("Delay", 30).apply();
                }
                else if(delayOneMinute.isChecked()){
                    sharedPreferences.edit().putInt("Delay", 60).apply();
                }
                else if(delayFiveMinutes.isChecked()){
                    sharedPreferences.edit().putInt("Delay", 60 * 5).apply();
                }
            }
        });

        return generalSettingsView;
    }
}
