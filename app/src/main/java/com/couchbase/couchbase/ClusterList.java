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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class ClusterList extends Fragment implements ConnectionResponse{
    private View clusterListView;
    private LinearLayout clusterListHolder;
    private FileHelper fileHelper;
    private ArrayList<ArrayList> clusterList;
    private ConnectionTask clusterConnector;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        clusterListView = inflater.inflate(R.layout.cluster_list, container, false);
        clusterListHolder = clusterListView.findViewById(R.id.cluster_list_holder);
        fileHelper = new FileHelper();
        sharedPreferences = getContext().getSharedPreferences("ClusterName", Context.MODE_PRIVATE);
            buildList();
        return clusterListView;
    }

    public void buildList(){
        clusterList = fileHelper.readToFile(getContext(), "ClusterList", ",");
        LayoutHelper listHelper = new LayoutHelper(getContext());

        for(int i = 0; i < clusterList.size(); i++){
            final ArrayList<String> clusterInfo = clusterList.get(i);

            CardView cardLayout = new CardView(getContext());
            listHelper.setCard(cardLayout, clusterListHolder, -1, -2);

            final LinearLayout clusterLayout = new LinearLayout(getContext());
            listHelper.setCardLayout(clusterLayout, cardLayout, LinearLayout.HORIZONTAL, -1, -2, Gravity.LEFT, 4);

            TextView clusterName = new TextView(getContext());
            listHelper.setWeightedTextView(clusterName, clusterLayout, clusterInfo.get(0), 15, 0, -2, 40, 40, 0, 40, 3);

            LinearLayout imageLayout = new LinearLayout(getContext());
            imageLayout.setVerticalGravity(Gravity.CENTER_VERTICAL);
            listHelper.setWeightedLayout(imageLayout, clusterLayout, LinearLayout.HORIZONTAL, 0, -2, Gravity.RIGHT, 1);

            ImageView deleteButton = new ImageView(getContext());
            listHelper.setImageView(imageLayout, deleteButton, getContext(), R.drawable.delete, -2, -2, 0, 40, 40, 40);

            clusterLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clusterConnector = new ConnectionTask(ClusterList.this, "/pools/default", "GET");
                    clusterConnector.execute(clusterInfo);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fileHelper.writeToFile(getContext(), "ClusterList", "", getContext().MODE_PRIVATE);
                    for(int j = 0; j < clusterList.size(); j++){
                        if(!clusterInfo.get(0).equals(clusterList.get(j).get(0))){
                            String append = "";
                            ArrayList<String> clusterInfoAdder = clusterList.get(j);
                            for(String info : clusterInfoAdder){
                                append += info;
                                append += ",";
                            }
                            fileHelper.writeToFile(getContext(), "ClusterList", append + "\n", getContext().MODE_APPEND);
                        }
                    }
                    clusterListHolder.removeAllViews();
                    buildList();
                }
            });
        }
    }

    @Override
    public void getResponse(ConnectionData result, String extension) {
        if(result.getResult() != null) {
            sharedPreferences = getContext().getSharedPreferences("ClusterName", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString("Cluster", clusterConnector.getLoginVars().get(0) + "/" + clusterConnector.getLoginVars().get(3) + "/" + clusterConnector.getLoginVars().get(4) + "/").apply();

            Intent fragmentManager = new Intent(getContext(), FragmentManager.class);
            startActivity(fragmentManager);
        }
        else{
            Toast.makeText(getContext(), result.getMsg(), Toast.LENGTH_LONG).show();
        }
    }
}
