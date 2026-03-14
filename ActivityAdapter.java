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
import com.example.ecostayretreat.database.ActivityEntity;
import java.util.List;

public class ActivityAdapter extends ArrayAdapter<ActivityEntity> {

    private Context context;
    private List<ActivityEntity> activityList;

    public ActivityAdapter(@NonNull Context context, int resource, @NonNull List<ActivityEntity> objects) {
        super(context, resource, objects);
        this.context = context;
        this.activityList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the current ActivityEntity
        ActivityEntity currentActivity = getItem(position);

        // Inflate the custom layout if convertView is null
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_list_item, parent, false);
        }

        // Find the views in the custom layout
        ImageView activityImageView = convertView.findViewById(R.id.imageViewActivity);
        TextView titleTextView = convertView.findViewById(R.id.textViewActivityTitle);
        TextView descriptionTextView = convertView.findViewById(R.id.textViewActivityDescription);

        // Set the content to show activity details
        if (currentActivity != null) {
            String title = currentActivity.getActivityName() + " - $" + currentActivity.getPrice();
            String description = currentActivity.getDescription();

            titleTextView.setText(title);
            descriptionTextView.setText(description);
            
            // Load activity image using Glide
            if (currentActivity.getImageUrl() != null && !currentActivity.getImageUrl().trim().isEmpty()) {
                String imageUrl = currentActivity.getImageUrl();
                
                // Handle local drawable resources
                if (imageUrl.startsWith("drawable://")) {
                    String drawableName = imageUrl.replace("drawable://", "");
                    int drawableId = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
                    if (drawableId != 0) {
                        activityImageView.setImageResource(drawableId);
                    } else {
                        // Fallback to placeholder if drawable not found
                        activityImageView.setImageResource(R.drawable.ic_placeholder_activity);
                    }
                } else {
                    // Load from URL using Glide
                    Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_placeholder_activity)
                        .error(R.drawable.ic_placeholder_activity)
                        .into(activityImageView);
                }
            } else {
                // Use placeholder image if no URL is provided
                activityImageView.setImageResource(R.drawable.ic_placeholder_activity);
            }
        }

        return convertView;
    }
}