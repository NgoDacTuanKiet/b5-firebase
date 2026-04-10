package com.hotel.b5_firebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotel.b5_firebase.adapters.ShowtimeAdapter;
import com.hotel.b5_firebase.models.Movie;
import com.hotel.b5_firebase.models.Showtime;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {
    private ImageView ivPoster;
    private TextView tvTitle, tvGenre, tvDescription;
    private RecyclerView rvShowtimes;
    private ShowtimeAdapter adapter;
    private List<Showtime> showtimeList;
    private DatabaseReference mDatabase;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        movie = (Movie) getIntent().getSerializableExtra("movie");
        if (movie == null) {
            finish();
            return;
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("showtimes");
        ivPoster = findViewById(R.id.ivDetailPoster);
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvGenre = findViewById(R.id.tvDetailGenre);
        tvDescription = findViewById(R.id.tvDetailDescription);
        rvShowtimes = findViewById(R.id.rvShowtimes);

        tvTitle.setText(movie.getTitle());
        tvGenre.setText(movie.getGenre());
        tvDescription.setText(movie.getDescription());
        Glide.with(this).load(movie.getPosterUrl()).into(ivPoster);

        rvShowtimes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        showtimeList = new ArrayList<>();
        adapter = new ShowtimeAdapter(showtimeList, showtime -> {
            Intent intent = new Intent(MovieDetailActivity.this, BookingActivity.class);
            intent.putExtra("showtime", showtime);
            intent.putExtra("movieTitle", movie.getTitle());
            startActivity(intent);
        });
        rvShowtimes.setAdapter(adapter);

        loadShowtimes();
    }

    private void loadShowtimes() {
        mDatabase.orderByChild("movieId").equalTo(movie.getId())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showtimeList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Showtime showtime = postSnapshot.getValue(Showtime.class);
                    if (showtime != null) {
                        showtime.setId(postSnapshot.getKey());
                        showtimeList.add(showtime);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MovieDetailActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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