package com.mindtree.amexalerter.util;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.mindtree.amexalerter.R;

import java.util.List;

/**
 * Created by M1030452 on 4/19/2018.
 */

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.MyViewHolder> {

    private List<TicketDetail> moviesList;
    private boolean showFullTicketDetail = false;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView inc, severity, queueName, shortDesc, receivingTime, acceptedTime, acceptedBy;
        TableRow tableRow5, tableRow6, tableRow7;

        public MyViewHolder(View view) {
            super(view);
            inc = view.findViewById(R.id.inc);
            severity = view.findViewById(R.id.severity);
            queueName = view.findViewById(R.id.queue_name);
            shortDesc = view.findViewById(R.id.desc);
            receivingTime = view.findViewById(R.id.time1);
            acceptedTime = view.findViewById(R.id.tim2);
            acceptedBy = view.findViewById(R.id.accepted_by);
            tableRow5 = view.findViewById(R.id.tableRow5);
            tableRow6 = view.findViewById(R.id.tableRow6);
            tableRow7 = view.findViewById(R.id.tableRow7);
        }
    }


    public TicketAdapter(List<TicketDetail> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TicketDetail ticketDetail = moviesList.get(position);
        holder.inc.setText(ticketDetail.getInc());
        holder.severity.setText(ticketDetail.getSeverity());
        holder.queueName.setText(ticketDetail.getQueueName());
        holder.shortDesc.setText(ticketDetail.getTicketDesc());
        if (showFullTicketDetail) {
            holder.tableRow5.setVisibility(View.VISIBLE);
            holder.tableRow6.setVisibility(View.VISIBLE);
            holder.tableRow7.setVisibility(View.VISIBLE);
            holder.receivingTime.setText(ticketDetail.getTicketReceiveTime());
            holder.acceptedTime.setText(ticketDetail.getTicketAcceptanceTime());
            holder.acceptedBy.setText(ticketDetail.getTicketAcceptedBy());
        }
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public void setFullDetailMod(boolean showFullTicketDetail) {
        this.showFullTicketDetail = showFullTicketDetail;
    }
}
