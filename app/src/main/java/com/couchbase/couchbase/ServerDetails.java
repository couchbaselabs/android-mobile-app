package com.couchbase.couchbase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ServerDetails extends Fragment implements ConnectionResponse{
    private View serverDetailsView;
    private Node selectedNode;
    private ClusterMonitor serverMonitor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        serverDetailsView = inflater.inflate(R.layout.server_details, container, false);
        FragmentManager fragmentManager = (FragmentManager) getActivity();
        serverMonitor = new ClusterMonitor(this , getContext());
        serverMonitor.startPoolsMonitor();
        return serverDetailsView;
    }

    @Override
    public void getResponse(ConnectionData result, String extension) {
        if(result.getResult() != null){
            Cluster cluster = new Cluster(result);
            cluster.updateClusterFile(getContext());
            for(Node node : cluster.getClusterNodes()){
                if(node.getIp().equals(selectedNode.getIp()) && node.getPt().equals(selectedNode.getPt())){
                    this.selectedNode = node;
                }
            }
            displayServerDetails();
        }
        else{
            Toast.makeText(getActivity(), result.getMsg(), Toast.LENGTH_LONG).show();
        }
    }

    public void setSelectedNode(Node selectedNode) {
        this.selectedNode = selectedNode;
    }

    public void displayServerDetails(){
        TextView hostname = serverDetailsView.findViewById(R.id.server_details_hostname);
        TextView cpu = serverDetailsView.findViewById(R.id.server_details_cpu);
        TextView ram = serverDetailsView.findViewById(R.id.server_details_ram);
        TextView disk = serverDetailsView.findViewById(R.id.server_details_disk);
        TextView content = serverDetailsView.findViewById(R.id.server_details_content);

        String serviceString = "";
        for(int i = 0; i < selectedNode.getServices().size() - 1; i++) {
            serviceString += selectedNode.getServices().get(i) + ", ";
        }
        serviceString += selectedNode.getServices().get((selectedNode.getServices().size() - 1));

        hostname.setText(selectedNode.getHostname());
        cpu.setText("CPU" + "\n" + String.format("%.2f", selectedNode.getCpuRate()) + "%");
        ram.setText("RAM" + "\n" + String.format("%.2f", selectedNode.getRamRate()) + "%");
        disk.setText("Disk" + "\n" + selectedNode.getDiskUsed() + "MB");
        content.setText(
                "Services: " + serviceString + "\n\n" +
                "Status: " + selectedNode.getStatus() + "\n\n" +
                "Membership: " + selectedNode.getMembership() + "\n\n" +
                "Version: " + selectedNode.getVersion() + "\n\n" +
                "CPU Count: " + selectedNode.getCpuCount() + "\n\n" +
                "Swap: " + String.format("%.2f", selectedNode.getSwapRate()) + "%" + "\n\n" +
                "Uptime: " + selectedNode.getUptime() + " Minutes");
        }

    @Override
    public void onStop() {
        serverMonitor.stopPoolsMonitor();
        super.onStop();
    }
}