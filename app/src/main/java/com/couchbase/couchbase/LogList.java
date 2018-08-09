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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LogList extends Fragment implements ConnectionResponse{
    private View logListView;
    private LinearLayout logListLayout;
    private ClusterMonitor logsMonitor;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        logListView = inflater.inflate(R.layout.log_list, container, false);
        logListLayout = logListView.findViewById(R.id.log_list_layout);

        FragmentManager fragmentManager = (FragmentManager) getActivity();
        fragmentManager.setActionBarTitle("Logs");

        logsMonitor = new ClusterMonitor(this, getContext());
        logsMonitor.startLogsMonitor();

        return logListView;
    }

    @Override
    public void getResponse(ConnectionData result, String extension) {
        if(result.getResult() != null){
            logsDisplay(result.getResult());
        }
        else{
            Toast.makeText(getActivity(), result.getMsg(), Toast.LENGTH_LONG).show();
        }
    }

    public void logsDisplay(JSONObject result){
        logListLayout.removeAllViews();
        JsonHelper JsonReader = new JsonHelper(result);
        JSONArray logList = JsonReader.getTargetArray("list");
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("LogNumber", Context.MODE_PRIVATE);
        for(int i = logList.length() - 1; i >= logList.length() - sharedPreferences.getInt("LogNum", 20); i--){

            LayoutHelper logLayoutHelper = new LayoutHelper(getContext());
            TextView logText = new TextView(getContext());
            View divider = new View(getContext());

            Log log = new Log(logList, i);
            if(log.getTime() != null){
                logLayoutHelper.setTextView(logText, logListLayout, "Time: " + log.getTime() + "\n" +"Node: " + log.getNode() + "\n" + "Module: " + log.getModule() + "\n" + "Type: " + log.getType() + "\n" + "Event: " + log.getEvent(), 15,  -1,-2,40, 40, 40, 40);
                logLayoutHelper.setDividerView(divider, logListLayout);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        logsMonitor.stopLogsMonitor();
    }
}
