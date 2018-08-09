package com.couchbase.couchbase;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Dashboard extends Fragment implements ConnectionResponse{
    private View dashboardView;
    private ClusterMonitor dashboardMonitor;
    private Cluster cluster;

    private TextView dataNodeCount;
    private TextView indexNodeCount;
    private TextView searchNodeCount;
    private TextView queryNodeCount;
    private TextView eventingNodeCount;
    private TextView analyticNodeCount;
    private TextView[] nodeCount;

    private LinearLayout dataLayout;
    private LinearLayout indexLayout;
    private LinearLayout searchLayout;
    private LinearLayout queryLayout;
    private LinearLayout eventingLayout;
    private LinearLayout analyticsLayout;

    private ClusterManager clusterManager;
    private Button addNode;
    private Button rebalance;
    String[] services;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dashboardView = inflater.inflate(R.layout.dashboard, container, false);

        dataNodeCount = dashboardView.findViewById(R.id.dashboard_data_node_count);
        indexNodeCount = dashboardView.findViewById(R.id.dashboard_index_node_count);
        searchNodeCount = dashboardView.findViewById(R.id.dashboard_search_node_count);
        queryNodeCount = dashboardView.findViewById(R.id.dashboard_query_node_count);
        eventingNodeCount = dashboardView.findViewById(R.id.dashboard_eventing_node_count);
        analyticNodeCount = dashboardView.findViewById(R.id.dashboard_analytics_node_count);

        dataLayout = dashboardView.findViewById(R.id.dashboard_data_layout);
        indexLayout = dashboardView.findViewById(R.id.dashboard_index_layout);
        searchLayout = dashboardView.findViewById(R.id.dashboard_search_layout);
        queryLayout = dashboardView.findViewById(R.id.dashboard_query_layout);
        eventingLayout = dashboardView.findViewById(R.id.dashboard_eventing_layout);
        analyticsLayout = dashboardView.findViewById(R.id.dashboard_analytics_layout);

        addNode = dashboardView.findViewById(R.id.add_node);
        rebalance = dashboardView.findViewById(R.id.rebalance);

        nodeCount = new TextView[] {dataNodeCount, indexNodeCount, searchNodeCount, queryNodeCount, eventingNodeCount, analyticNodeCount};
        LinearLayout[] nodeLayout = new LinearLayout[] {dataLayout, indexLayout, searchLayout, queryLayout, eventingLayout, analyticsLayout};
        final String[] serviceVal = {"kv", "index", "fts", "n1ql", "eventing", "cbas"};
        services = new String[] {"Data", "Index", "Search", "Query", "Eventing", "Analytics"};
        clusterManager = new ClusterManager(getContext(), this, services, serviceVal);

        addNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clusterManager.addNode();
            }
        });

        for(int i = 0; i < nodeLayout.length; i++){
            final int k = i;
            nodeLayout[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = (FragmentManager) getActivity();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.content_frame, new ServerList(), "ServerList").commit();
                    fragmentManager.getSupportFragmentManager().executePendingTransactions();
                    ServerList serverList = (ServerList) getFragmentManager().findFragmentByTag("ServerList");
                    serverList.setSelectedFilter(serviceVal[k], services[k]);
                }
            });
        }
        FragmentManager fragmentManager = (FragmentManager) getActivity();
        fragmentManager.setActionBarTitle("Dashboard");
        dashboardMonitor = new ClusterMonitor(this , getContext());
        dashboardMonitor.startPoolsMonitor();
        return dashboardView;
    }

    @Override
    public void getResponse(ConnectionData result, String extension) {
        if(result.getResult() != null){
            cluster = new Cluster(result);
            cluster.updateClusterFile(getContext());
            displayServiceNodes(cluster);
            rebalance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clusterManager.rebalance(cluster);
                }
            });
        }
    }

    public void displayServiceNodes(Cluster cluster){
        for(int i = 0; i < nodeCount.length; i++){
            String plural = (cluster.getServiceCount()[i] == 1) ? "" : "s";
            nodeCount[i].setText(services[i] + ": " + cluster.getServiceCount()[i] + " Node" + plural);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        dashboardMonitor.stopPoolsMonitor();
    }
}
