package com.hotel.b5_firebase;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotel.b5_firebase.models.Showtime;
import com.hotel.b5_firebase.models.Ticket;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {
    private TextView tvMovie, tvTime;
    private GridLayout glSeats;
    private Button btnConfirm;
    private Showtime showtime;
    private String movieTitle;
    private List<String> selectedSeats = new ArrayList<>();
    private List<String> bookedSeats = new ArrayList<>();
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        Toolbar toolbar = findViewById(R.id.toolbarBooking);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        showtime = (Showtime) getIntent().getSerializableExtra("showtime");
        movieTitle = getIntent().getStringExtra("movieTitle");
        
        if (showtime == null) {
            finish();
            return;
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        tvMovie = findViewById(R.id.tvBookingMovie);
        tvTime = findViewById(R.id.tvBookingTime);
        glSeats = findViewById(R.id.glSeats);
        btnConfirm = findViewById(R.id.btnConfirmBooking);

        tvMovie.setText(movieTitle);
        tvTime.setText("Thời gian: " + showtime.getTime());

        loadBookedSeats();

        btnConfirm.setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một ghế", Toast.LENGTH_SHORT).show();
                return;
            }
            saveTickets();
        });
    }

    private void loadBookedSeats() {
        mDatabase.child("tickets").orderByChild("showtimeId").equalTo(showtime.getId())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookedSeats.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Ticket ticket = postSnapshot.getValue(Ticket.class);
                    if (ticket != null) {
                        bookedSeats.add(ticket.getSeatNumber());
                    }
                }
                setupSeats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setupSeats() {
        glSeats.removeAllViews();
        String[] rows = {"A", "B", "C", "D"};
        int cols = 5;

        for (String row : rows) {
            for (int col = 1; col <= cols; col++) {
                Button seat = new Button(this);
                String seatLabel = row + col;
                seat.setText(seatLabel);
                
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 150;
                params.height = 150;
                params.setMargins(8, 8, 8, 8);
                seat.setLayoutParams(params);

                if (bookedSeats.contains(seatLabel)) {
                    seat.setBackgroundColor(Color.RED);
                    seat.setEnabled(false);
                } else {
                    updateSeatUI(seat, seatLabel);
                    seat.setOnClickListener(v -> {
                        if (selectedSeats.contains(seatLabel)) {
                            selectedSeats.remove(seatLabel);
                        } else {
                            selectedSeats.add(seatLabel);
                        }
                        updateSeatUI(seat, seatLabel);
                    });
                }
                glSeats.addView(seat);
            }
        }
    }

    private void updateSeatUI(Button seat, String seatLabel) {
        if (selectedSeats.contains(seatLabel)) {
            seat.setBackgroundColor(Color.GREEN);
        } else {
            seat.setBackgroundColor(Color.LTGRAY);
        }
    }

    private void saveTickets() {
        String userId = mAuth.getCurrentUser().getUid();
        String bookingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        for (String seat : selectedSeats) {
            String ticketId = mDatabase.child("tickets").push().getKey();
            Ticket ticket = new Ticket(ticketId, userId, showtime.getId(), seat, bookingTime);
            mDatabase.child("tickets").child(ticketId).setValue(ticket);
        }

        Toast.makeText(this, "Đặt " + selectedSeats.size() + " vé thành công!", Toast.LENGTH_LONG).show();
        finish();
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