package com.hotel.b5_firebase;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotel.b5_firebase.adapters.HistoryAdapter;
import com.hotel.b5_firebase.models.Ticket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookingHistoryActivity extends AppCompatActivity {
    private RecyclerView rvHistory;
    private HistoryAdapter adapter;
    private List<Ticket> ticketList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        Toolbar toolbar = findViewById(R.id.toolbarHistory);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("tickets");

        rvHistory = findViewById(R.id.rvBookingHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        ticketList = new ArrayList<>();
        adapter = new HistoryAdapter(ticketList);
        rvHistory.setAdapter(adapter);

        loadHistory();
    }

    private void loadHistory() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ticketList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Ticket ticket = postSnapshot.getValue(Ticket.class);
                    if (ticket != null) {
                        ticketList.add(ticket);
                    }
                }
                // Đảo ngược danh sách để vé mới nhất hiện lên đầu
                Collections.reverse(ticketList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookingHistoryActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}