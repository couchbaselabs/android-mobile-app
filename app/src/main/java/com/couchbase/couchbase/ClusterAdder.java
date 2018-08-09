package com.couchbase.couchbase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class ClusterAdder extends Fragment implements ConnectionResponse {
    private View clusterAdderView;
    private EditText name;
    private EditText ip;
    private EditText pt;
    private EditText un;
    private EditText pw;

    private String nameVal;
    private String ipVal;
    private String ptVal;
    private String unVal;
    private String pwVal;

    private TextInputLayout nameLayout;
    private TextInputLayout ipLayout;
    private TextInputLayout ptLayout;
    private TextInputLayout unLayout;
    private TextInputLayout pwLayout;

    private Button loginBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        clusterAdderView = inflater.inflate(R.layout.cluster_adder, container, false);

        name = clusterAdderView.findViewById(R.id.login_name);
        ip = clusterAdderView.findViewById(R.id.login_ip);
        pt = clusterAdderView.findViewById(R.id.login_pt);
        un = clusterAdderView.findViewById(R.id.login_un);
        pw = clusterAdderView.findViewById(R.id.login_pw);

        nameLayout = clusterAdderView.findViewById(R.id.login_name_layout);
        ipLayout = clusterAdderView.findViewById(R.id.login_ip_layout);
        ptLayout = clusterAdderView.findViewById(R.id.login_pt_layout);
        unLayout = clusterAdderView.findViewById(R.id.login_un_layout);
        pwLayout = clusterAdderView.findViewById(R.id.login_pw_layout);

        loginBtn = clusterAdderView.findViewById(R.id.login_button);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameVal = name.getText().toString();
                ipVal = ip.getText().toString();
                ptVal = pt.getText().toString();
                unVal = un.getText().toString();
                pwVal = pw.getText().toString();

                ArrayList<String> serverInfo = new ArrayList<>();
                serverInfo.add(nameVal);
                serverInfo.add(ipVal + "/" + ptVal + "/" + unVal + "/" + pwVal + "/");

                ConnectionTask checkConnection = new ConnectionTask(ClusterAdder.this, "/pools/default", "GET");
                checkConnection.execute(serverInfo);
            }
        });
        return clusterAdderView;
    }

    @Override
    public void getResponse(ConnectionData result, String extension) {
        if (result.getResult() != null) {
            Cluster cluster = new Cluster(result);
            String msg;
            if((msg = repeatChecker(cluster)) == ""){
                addCluster(cluster);
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()  //TODO do not begin transaction if addCluster fails
                        .addToBackStack(null)
                        .replace(R.id.login_frame, new ClusterList()).commit();
            }
            else{
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), result.getMsg(), Toast.LENGTH_LONG).show();
        }
    }

    public String repeatChecker(Cluster cluster){
        FileHelper fileHelper = new FileHelper();
        ArrayList<ArrayList> clusterList = fileHelper.readToFile(getContext(), "ClusterList", ",");
        for (ArrayList<String> serverList : clusterList) {
            Log.d("name", serverList.get(0).trim() + "," + nameVal.trim());
            if(serverList.get(0).trim().equals(nameVal.trim())){
                return "Cluster name is already taken.";
            }
            for(int j = 1; j < serverList.size(); j++){
                ArrayList<String> serverInfo = fileHelper.readLine(serverList.get(j), "/");
                for(int k = 0; k < cluster.getClusterNodes().size(); k++){
                    if (cluster.getClusterNodes().get(k).getIp().equals(serverInfo.get(0)) && cluster.getClusterNodes().get(k).getPt().equals(serverInfo.get(1))) {
                        return "Cluster has a server already in system.";
                    }
                }
            }
        }
        return "";
    }

    public void addCluster(Cluster cluster){
        final FileHelper fileHelper = new FileHelper();
        try {
            ArrayList<Node> newNodes = cluster.getClusterNodes();
            String clusterInfo = nameVal + ",";
            for(Node node : newNodes){
                clusterInfo += node.getIp() + "/" + node.getPt() + "/" + unVal + "/" + pwVal + "/" + ",";
            }
            fileHelper.writeToFile(getContext(), "ClusterList", clusterInfo + "\n", getContext().MODE_APPEND);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}