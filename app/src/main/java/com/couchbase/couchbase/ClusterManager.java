package com.couchbase.couchbase;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.JsonReader;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.json.JSONObject;

import java.util.ArrayList;

public class ClusterManager {
    private Context context;
    private ConnectionResponse process;
    private ClusterFinder clusterFinder = new ClusterFinder();
    private String[] services;
    private String[] serviceVal;

    public ClusterManager(Context context, ConnectionResponse process, String[] services, String[] serviceVal){
        this.context = context;
        this.process = process;
        this.services = services;
        this.serviceVal = serviceVal;
    }

    public void addNode(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle("Add Node");

        final EditText ip = new EditText(context);
        ip.setHint("IP");
        ip.setInputType(InputType.TYPE_CLASS_NUMBER);
        ip.setKeyListener(DigitsKeyListener.getInstance("0123456789."));

        final EditText pt = new EditText(context);
        pt.setHint("Port");
        pt.setInputType(InputType.TYPE_CLASS_NUMBER);
        pt.setKeyListener(DigitsKeyListener.getInstance("0123456789."));

        final CheckBox[] checkBoxList = new CheckBox[services.length];

        for(int i = 0; i < services.length; i++){
            CheckBox checkBox = new CheckBox(context);
            checkBox.setText(services[i]);
            checkBoxList[i] = checkBox;
        }

        final Button add = new Button(context);
        add.setText("Add");
        Button cancel = new Button(context);
        cancel.setText("Cancel");

        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true);
        add.setBackgroundResource(outValue.resourceId);
        cancel.setBackgroundResource(outValue.resourceId);

        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        buttonLayout.addView(cancel);
        buttonLayout.addView(add);

        LinearLayout dialogLayout = new LinearLayout(context);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        dialogLayout.addView(ip);
        dialogLayout.addView(pt);

        for(CheckBox checkBox : checkBoxList){
            dialogLayout.addView(checkBox);
        }
        dialogLayout.addView(buttonLayout);
        dialogBuilder.setView(dialogLayout);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String addService = "";
                ArrayList<Integer> checkedIndex = new ArrayList<>();
                for(int i = 0; i < services.length; i++){
                    if(checkBoxList[i].isChecked()){
                        checkedIndex.add(i);
                    }
                }
                for(int i = 0; i < checkedIndex.size(); i++){
                    if(i != checkedIndex.size() - 1){
                        addService += serviceVal[checkedIndex.get(i)] + ",";
                    }
                    else{
                        addService += serviceVal[checkedIndex.get(i)];
                    }
                }
                String[] paramKeys = {"hostname", "user", "password", "services"};
                String[] paramValues = {ip.getText().toString() + ":" + pt.getText().toString(), "fill", "fill", addService};
                ConnectionTask connectionTask = new ConnectionTask(process, "/controller/addNode", "POST", paramKeys, paramValues, "ADD");
                connectionTask.execute(clusterFinder.findCluster(context));
                alertDialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    public void rebalance(Cluster cluster){
        String nodeString = "";
        for(int i = 0; i < cluster.getClusterNodes().size(); i++){
            if(i != cluster.getClusterNodes().size() - 1){
                nodeString += cluster.getClusterNodes().get(i).getOtp() + ",";
            }
            else{
                nodeString += cluster.getClusterNodes().get(i).getOtp();
            }
        }
        String[] paramKeys = {"knownNodes"};
        String[] paramValues = {nodeString};
        ConnectionTask connectionTask = new ConnectionTask(process, "/controller/rebalance", "POST", paramKeys, paramValues, "REBALANCE");
        connectionTask.execute(clusterFinder.findCluster(context));
    }
}
