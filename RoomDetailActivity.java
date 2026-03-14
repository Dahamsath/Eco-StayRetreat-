package com.example.ecostayretreat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.ecostayretreat.database.AppDatabase;
import com.example.ecostayretreat.database.RoomDao;
import com.example.ecostayretreat.database.RoomEntity;

public class RoomDetailActivity extends AppCompatActivity {

    private ImageView roomImageView;
    private TextView roomTypeTextView, descriptionTextView, priceTextView, capacityTextView, availabilityTextView;
    private Button bookRoomButton;
    private RoomEntity currentRoom;
    private AppDatabase database;
    private RoomDao roomDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        database = AppDatabase.getDatabase(this);
        roomDao = database.roomDao();

        roomImageView = findViewById(R.id.imageViewRoom);
        roomTypeTextView = findViewById(R.id.textViewRoomType);
        descriptionTextView = findViewById(R.id.textViewDescription);
        priceTextView = findViewById(R.id.textViewPrice);
        capacityTextView = findViewById(R.id.textViewCapacity);
        availabilityTextView = findViewById(R.id.textViewAvailability);
        bookRoomButton = findViewById(R.id.buttonBookRoom);


        int roomId = getIntent().getIntExtra("ROOM_ID", -1);

        if (roomId != -1) {
            new Thread(() -> {
                currentRoom = roomDao.getRoomById(roomId);
                runOnUiThread(() -> {
                    if (currentRoom != null) {
                        roomTypeTextView.setText(currentRoom.getRoomType());
                        descriptionTextView.setText(currentRoom.getDescription());
                        priceTextView.setText(String.format(getString(R.string.label_price) + " $%.2f", currentRoom.getPricePerNight()));
                        capacityTextView.setText(String.format(getString(R.string.label_capacity) + " %d", currentRoom.getCapacity()));
                        availabilityTextView.setText(currentRoom.isAvailable() ? getString(R.string.label_availability) : getString(R.string.label_unavailable));
                        // Load image using Glide or set a local drawable
                        loadRoomImage(currentRoom.getImageUrl());
                        bookRoomButton.setEnabled(currentRoom.isAvailable()); // Disable button if not available
                    } else {
                        // Handle case where room is not found
                        Toast.makeText(RoomDetailActivity.this, getString(R.string.msg_item_not_found), Toast.LENGTH_SHORT).show();
                        finish(); // Close activity or show error
                    }
                });
            }).start();
        } else {
            Toast.makeText(this, getString(R.string.error_invalid_room_id), Toast.LENGTH_SHORT).show();
            finish(); // Close activity if ID is invalid
        }

        bookRoomButton.setOnClickListener(v -> {
            if (currentRoom != null && currentRoom.isAvailable()) {
                // Navigate to booking activity
                Intent intent = new Intent(RoomDetailActivity.this, BookingActivity.class);
                intent.putExtra("ITEM_ID", currentRoom.getRoomId());
                intent.putExtra("ITEM_TYPE", "room");
                startActivity(intent);
            } else {
                Toast.makeText(RoomDetailActivity.this, "This room is not available for booking.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Loads room image from drawable resource or URL
     * Handles the new local images you've added
     */
    private void loadRoomImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            // Handle local drawable resources (new pictures)
            if (imageUrl.startsWith("drawable://")) {
                String drawableName = imageUrl.replace("drawable://", "");
                int drawableId = getResources().getIdentifier(drawableName, "drawable", getPackageName());
                
                if (drawableId != 0) {
                    // Successfully found the drawable resource
                    roomImageView.setImageResource(drawableId);
                    roomImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else {
                    // Drawable not found, use placeholder
                    roomImageView.setImageResource(R.drawable.ic_placeholder_room);
                    Toast.makeText(this, "Image not found: " + drawableName, Toast.LENGTH_SHORT).show();
                }
            } else {
                // Load from URL using Glide (fallback for online images)
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder_room)
                    .error(R.drawable.ic_placeholder_room)
                    .centerCrop()
                    .into(roomImageView);
            }
        } else {
            // No image URL provided, use placeholder
            roomImageView.setImageResource(R.drawable.ic_placeholder_room);
        }
    }
}
