package com.couchbase.couchbase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class FilterType extends Fragment {
    private View filterTypeView;
    private Switch info;
    private Switch warning;
    private Switch filter_all;
    private boolean toast;
    private FileHelper fileHelper = new FileHelper();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        filterTypeView = inflater.inflate(R.layout.filter_type, container, false);

        info = filterTypeView.findViewById(R.id.info_switch);
        warning = filterTypeView.findViewById(R.id.warning_switch);
        filter_all = filterTypeView.findViewById(R.id.filter_all_types_switch);

        final Switch[] typeSwitches = {info, warning};
        final String[] typeStrings = {"info", "warning"};

        toast = true;
        int checked = 0;
        for(int i = 0; i < typeStrings.length; i++){
            if(fileHelper.findKey(getContext(), "TypeFilters", typeStrings[i])){
                checked += 1;
            }
            if(checked == typeStrings.length){
                filter_all.setOnCheckedChangeListener(null);
                filter_all.setChecked(true);
            }
        }

        filter_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                toast = false;
                for(int i = 0; i < typeSwitches.length; i++){
                    if(on){
                        if(!typeSwitches[i].isChecked()){
                            typeSwitches[i].setChecked(true);
                        }
                    }
                    else{
                        if(typeSwitches[i].isChecked()){
                            typeSwitches[i].setChecked(false);
                        }
                    }
                }
                if(on){
                    Toast.makeText(getContext(), "Filtered all listed types", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(), "Removed filteres on all listed types", Toast.LENGTH_SHORT).show();
                }
                toast = true;
                fileHelper.logFile(getContext(), "TypeFilters", "log123");
            }
        });

        for(int i = 0; i < typeSwitches.length; i++){
            final int k = i;
            if(fileHelper.findKey(getContext(), "TypeFilters", typeStrings[i])){
                typeSwitches[i].setOnClickListener(null);
                typeSwitches[i].setChecked(true);
            }
            typeSwitches[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                    if(on){
                        fileHelper.writeToFile(getContext(), "TypeFilters", typeStrings[k] + "\n", getContext().MODE_APPEND);
                        if(toast){
                            Toast.makeText(getContext(), "Filtered " + typeStrings[k], Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        fileHelper.removeKey(getContext(), "TypeFilters", typeStrings[k]);
                        if(toast){
                            Toast.makeText(getContext(), "Removed " + typeStrings[k] + " filter", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        return filterTypeView;
    }
}