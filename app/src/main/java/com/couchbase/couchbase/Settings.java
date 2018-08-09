package com.couchbase.couchbase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Settings extends Fragment {
    private View settingsView;
    private LinearLayout regEx;
    private LinearLayout moduleFilter;
    private LinearLayout typeFilter;
    private LinearLayout customize;
    private LinearLayout disconnect;
    private LinearLayout generalSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        settingsView = inflater.inflate(R.layout.settings, container, false);
        FragmentManager fragmentManager = (FragmentManager) getActivity();
        fragmentManager.setActionBarTitle("Settings");

        generalSettings = settingsView.findViewById(R.id.general_settings);
        regEx = settingsView.findViewById(R.id.reg_ex);
        moduleFilter = settingsView.findViewById(R.id.filter_modules);
        typeFilter = settingsView.findViewById(R.id.filter_type);
        customize = settingsView.findViewById(R.id.customize);
        disconnect = settingsView.findViewById(R.id.disconnect);

        generalSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.content_frame, new GeneralSettings()).commit();
            }
        });

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("ClusterName", Context.MODE_PRIVATE);
                sharedPreferences.edit().remove("Cluster").apply();

                Intent login = new Intent(getContext(), Login.class);
                startActivity(login);
            }
        });

        regEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.content_frame, new RegEx()).commit();
            }
        });

        moduleFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.content_frame, new FilterModule()).commit();
            }
        });

        typeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.content_frame, new FilterType()).commit();
            }
        });

        customize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.content_frame, new NotificationCustomizer()).commit();
            }
        });
        return settingsView;
    }

}
