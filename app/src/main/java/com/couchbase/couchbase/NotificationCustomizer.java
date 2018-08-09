package com.couchbase.couchbase;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class NotificationCustomizer extends Fragment {
    private View notificationCustomizerView;
    private TextView cpuText;
    private TextView ramText;
    private TextView storageText;

    private SeekBar cpuSeekbar;
    private SeekBar ramSeekbar;
    private EditText storageEditText;

    private Switch cpuSwitch;
    private Switch ramSwitch;
    private Switch storageSwitch;
    private Switch statusSwitch;

    private SharedPreferences customNotifications;
    private FileHelper fileHelper = new FileHelper();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        notificationCustomizerView = inflater.inflate(R.layout.notification_customizer, container, false);
        customNotifications = getActivity().getSharedPreferences("CustomNotifications", Context.MODE_PRIVATE);

        cpuText = notificationCustomizerView.findViewById(R.id.cpu_text);
        ramText = notificationCustomizerView.findViewById(R.id.ram_text);
        storageText = notificationCustomizerView.findViewById(R.id.storage_text);

        cpuSeekbar = notificationCustomizerView.findViewById(R.id.cpu_seekbar);
        ramSeekbar = notificationCustomizerView.findViewById(R.id.ram_seekbar);
        storageEditText = notificationCustomizerView.findViewById(R.id.storage_edit_text);

        cpuSwitch = notificationCustomizerView.findViewById(R.id.cpu_switch);
        ramSwitch = notificationCustomizerView.findViewById(R.id.ram_switch);
        storageSwitch = notificationCustomizerView.findViewById(R.id.storage_switch);
        statusSwitch = notificationCustomizerView.findViewById(R.id.status_switch);

        final Switch[] customSwitches = {cpuSwitch, ramSwitch, storageSwitch};
        final String[] customStrings = {"CPU", "memory", "storage", "status"};

        for(int i = 0; i < customSwitches.length; i++){
            final int k = i;
            if(fileHelper.findKey(getContext(), "CustomFilters", customStrings[i])){
                customSwitches[i].setOnClickListener(null);
                customSwitches[i].setChecked(true);
            }
            customSwitches[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                    if(on){
                        fileHelper.writeToFile(getContext(), "CustomFilters", customStrings[k] + "\n", getContext().MODE_APPEND);
                        Toast.makeText(getContext(), "Filtered " + customStrings[k] + " notifications", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        fileHelper.removeKey(getContext(), "CustomFilters", customStrings[k]);
                        Toast.makeText(getContext(), "Removed " + customStrings[k] +" notification"+ " filter", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        cpuText.setText(customNotifications.getInt("CPU", 50) + "%");
        cpuSeekbar.setProgress(customNotifications.getInt("CPU", 50));
        ramText.setText(customNotifications.getInt("RAM", 50) + "%");
        ramSeekbar.setProgress(customNotifications.getInt("RAM", 50));
        storageText.setText(customNotifications.getInt("Storage", 50) + " MB");

        cpuSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
                cpuText.setText(i + "%");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                customNotifications.edit().putInt("CPU" , progress).commit();
            }
        });

        ramSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
                ramText.setText(i + "%");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                customNotifications.edit().putInt("RAM" , progress).commit();
            }
        });
        storageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(getActivity().getCurrentFocus() == storageEditText){
                    storageText.setText(charSequence + " MB");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        storageEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    storageEditText.clearFocus();
                    InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(storageEditText.getWindowToken(), 0);
                    customNotifications.edit().putInt("Storage", Integer.parseInt(storageEditText.getText().toString())).commit();
                    return true;
                }
                return false;
            }
        });

        return notificationCustomizerView;
    }
}
