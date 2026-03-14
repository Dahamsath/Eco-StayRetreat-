package com.example.ecostayretreat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ecostayretreat.database.BookingEntity;

import java.util.List;

public class ProfileBookingAdapter extends ArrayAdapter<ProfileBookingAdapter.Item> {

    public static class Item {
        public final BookingEntity booking;
        public final String title;
        public final String subtitle;
        public Item(BookingEntity booking, String title, String subtitle) {
            this.booking = booking;
            this.title = title;
            this.subtitle = subtitle;
        }
    }

    public interface OnDeleteClick {
        void onDelete(BookingEntity booking);
    }

    private final Context context;
    private final List<Item> items;
    private final OnDeleteClick onDeleteClick;

    public ProfileBookingAdapter(@NonNull Context context, @NonNull List<Item> items, @NonNull OnDeleteClick onDeleteClick) {
        super(context, R.layout.profile_booking_list_item, items);
        this.context = context;
        this.items = items;
        this.onDeleteClick = onDeleteClick;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.profile_booking_list_item, parent, false);
        }

        Item item = getItem(position);
        TextView titleView = convertView.findViewById(R.id.textViewBookingTitle);
        TextView subtitleView = convertView.findViewById(R.id.textViewBookingSubtitle);
        ImageButton deleteBtn = convertView.findViewById(R.id.buttonDeleteBooking);

        if (item != null) {
            titleView.setText(item.title);
            subtitleView.setText(item.subtitle);
            deleteBtn.setOnClickListener(v -> onDeleteClick.onDelete(item.booking));
        }
        return convertView;
    }
}
