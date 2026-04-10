package com.hotel.b5_firebase.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hotel.b5_firebase.R;
import com.hotel.b5_firebase.models.Ticket;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<Ticket> tickets;

    public HistoryAdapter(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Ticket ticket = tickets.get(position);
        holder.tvSeat.setText("Ghế: " + ticket.getSeatNumber());
        holder.tvTime.setText("Đặt lúc: " + ticket.getBookingTime());
        holder.tvTicketId.setText("Mã vé: " + ticket.getId());
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvSeat, tvTime, tvTicketId;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSeat = itemView.findViewById(R.id.tvHistorySeat);
            tvTime = itemView.findViewById(R.id.tvHistoryTime);
            tvTicketId = itemView.findViewById(R.id.tvHistoryTicketId);
        }
    }
}