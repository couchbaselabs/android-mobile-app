package com.couchbase.couchbase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ServerList extends Fragment implements ConnectionResponse{
    private View serverListView;
    private ClusterMonitor serverMonitor;
    private Cluster cluster;
    private LinearLayout serverListLayout;
    private LinearLayout serverListHolder;
    private String selectedFilter;
    TextView filter;
    LinearLayout filterLayout;
    LayoutInflater inflater;
    ViewGroup container;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        serverListView = inflater.inflate(R.layout.server_list, container, false);
        serverListLayout = serverListView.findViewById(R.id.server_list_layout);
        serverListHolder = serverListView.findViewById(R.id.server_list_holder);
        filter = serverListView.findViewById(R.id.server_filter);
        filterLayout = serverListLayout.findViewById(R.id.server_filter_layout);

        FragmentManager fragmentManager = (FragmentManager) getActivity();
        fragmentManager.setActionBarTitle("Servers");

        filterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] items = {"None", "Data", "Index", "Search", "Query", "Eventing", "Analytics"};
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                dialogBuilder.setTitle("Filter");
                dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String[] filters = {null, "kv", "index", "fts", "n1ql", "eventing", "cbas"};
                        selectedFilter = filters[which];
                        serverMonitor.stopPoolsMonitor();
                        serverMonitor.startPoolsMonitor();
                        if(which != 0){
                            filter.setText(items[which]);
                        }
                        else{
                            filter.setText("Filter Servers");
                        }
                    }
                });
                AlertDialog dialog = dialogBuilder.create();
                ListView filterList = dialog.getListView();
                filterList.setDivider(new ColorDrawable(getResources().getColor(R.color.colorDivider)));
                filterList.setDividerHeight(3);
                filterList.setFooterDividersEnabled(true);
                dialog.show();
                }
        });
        serverMonitor = new ClusterMonitor(this , getContext());
        serverMonitor.startPoolsMonitor();
        return serverListView;
    }

    @Override
    public void getResponse(ConnectionData result, String extension) {
        if(result.getResult() != null){
            serverListHolder.removeAllViews();
            cluster = new Cluster(result);
            cluster.updateClusterFile(getContext());
            for(Node node : cluster.getClusterNodes()){
                if(selectedFilter == null){
                    serverDisplay(node);
                }
                else if (node.getServices().contains(selectedFilter)){
                    serverDisplay(node);
                }
            }
        }
        else{
            Toast.makeText(getActivity(), result.getMsg(), Toast.LENGTH_LONG).show();
        }
    }

    public void serverDisplay(final Node node){
        View serverListItem = inflater.inflate(R.layout.server_list_item, container, false);
        TextView hostname = serverListItem.findViewById(R.id.server_list_hostname);
        TextView cpu = serverListItem.findViewById(R.id.server_list_cpu);
        TextView ram = serverListItem.findViewById(R.id.server_list_ram);
        TextView disk = serverListItem.findViewById(R.id.server_list_disk);
        Button serverDetailsButton = serverListItem.findViewById(R.id.server_details_button);

        hostname.setText(node.getHostname());
        cpu.setText("CPU" + "\n" + String.format("%.2f", node.getCpuRate()) + "%");
        ram.setText("RAM" + "\n" + String.format("%.2f", node.getRamRate()) + "%");
        disk.setText("Disk" + "\n" + node.getDiskUsed() + "MB");

        serverDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.content_frame, new ServerDetails(), "ServerDetails").commit();
                getFragmentManager().executePendingTransactions();
                ServerDetails serverDetails = (ServerDetails) getFragmentManager().findFragmentByTag("ServerDetails");
                serverDetails.setSelectedNode(node);
            }
        });

        serverListHolder.addView(serverListItem);
    }

    public void setSelectedFilter(String filterVal, String filterText) {
        selectedFilter = filterVal;
        filter.setText(filterText);
    }

    @Override
    public void onStop() {
        super.onStop();
        serverMonitor.stopPoolsMonitor();
    }
}
