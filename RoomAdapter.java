package com.example.ecostayretreat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.example.ecostayretreat.database.RoomEntity;
import java.util.List;

public class RoomAdapter extends ArrayAdapter<RoomEntity> {

    private Context context;
    private List<RoomEntity> roomList;

    public RoomAdapter(@NonNull Context context, int resource, @NonNull List<RoomEntity> objects) {
        super(context, resource, objects);
        this.context = context;
        this.roomList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the current RoomEntity
        RoomEntity currentRoom = getItem(position);

        // Inflate the custom layout if convertView is null
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.room_list_item, parent, false);
        }

        // Find the views in the custom layout
        ImageView roomImageView = convertView.findViewById(R.id.imageViewRoom);
        TextView titleTextView = convertView.findViewById(R.id.textViewRoomTitle);
        TextView descriptionTextView = convertView.findViewById(R.id.textViewRoomDescription);

        // Set the content to show room details
        if (currentRoom != null) {
            String title = currentRoom.getRoomType() + " - $" + currentRoom.getPricePerNight();
            String description = currentRoom.getDescription();

            titleTextView.setText(title);
            descriptionTextView.setText(description);
            
            // Load room image using Glide
            if (currentRoom.getImageUrl() != null && !currentRoom.getImageUrl().trim().isEmpty()) {
                String imageUrl = currentRoom.getImageUrl();
                
                // Handle local drawable resources
                if (imageUrl.startsWith("drawable://")) {
                    String drawableName = imageUrl.replace("drawable://", "");
                    int drawableId = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
                    if (drawableId != 0) {
                        roomImageView.setImageResource(drawableId);
                    } else {
                        // Fallback to placeholder if drawable not found
                        roomImageView.setImageResource(R.drawable.ic_placeholder_room);
                    }
                } else {
                    // Load from URL using Glide
                    Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_placeholder_room)
                        .error(R.drawable.ic_placeholder_room)
                        .into(roomImageView);
                }
            } else {
                // Use placeholder image if no URL is provided
                roomImageView.setImageResource(R.drawable.ic_placeholder_room);
            }
        }

        return convertView;
    }
}