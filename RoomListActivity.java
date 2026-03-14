package com.example.ecostayretreat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecostayretreat.database.AppDatabase;
import com.example.ecostayretreat.database.RoomDao;
import com.example.ecostayretreat.database.RoomEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RoomListActivity extends AppCompatActivity {

    private ListView roomsListView;
    private Spinner filterSpinner;
    private Spinner sortSpinner;
    private EditText minPriceEditText, maxPriceEditText;
    private Button applyPriceFilterButton;
    private List<RoomEntity> roomList;

    private ArrayAdapter<RoomEntity> roomAdapter;
    private AppDatabase database;
    private RoomDao roomDao;

    private String currentFilter = "All";
    private String currentSort = "Default";
    private double currentMinPrice = -1;
    private double currentMaxPrice = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        database = AppDatabase.getDatabase(this);
        roomDao = database.roomDao();

        roomsListView = findViewById(R.id.listViewRooms);
        filterSpinner = findViewById(R.id.spinnerFilter);
        sortSpinner = findViewById(R.id.spinnerSort);
        minPriceEditText = findViewById(R.id.editTextMinPrice);
        maxPriceEditText = findViewById(R.id.editTextMaxPrice);
        applyPriceFilterButton = findViewById(R.id.buttonApplyPriceFilter);


        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(this, R.layout.room_list_item, roomList);
        roomsListView.setAdapter(roomAdapter);


        insertSampleRoomsIfEmpty();


        loadRooms();


        String[] filterOptions = {
                getString(R.string.filter_all),
                getString(R.string.filter_mountain_view),
                getString(R.string.filter_eco_pod),
                getString(R.string.filter_standard)
        };
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filterOptions);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(filterAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = filterOptions[position];
                currentFilter = selectedFilter;
                applyFiltersAndSorting();
                Toast.makeText(RoomListActivity.this, getString(R.string.msg_filter_applied, selectedFilter), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        String[] sortOptions = {
                "Default",
                "Price: Low to High",
                "Price: High to Low",
                "Name: A-Z",
                "Name: Z-A"
        };
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortOptions);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSort = sortOptions[position];
                currentSort = selectedSort;
                applyFiltersAndSorting();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        applyPriceFilterButton.setOnClickListener(v -> {
            String minStr = minPriceEditText.getText().toString().trim();
            String maxStr = maxPriceEditText.getText().toString().trim();

            double minPrice = -1;
            double maxPrice = -1;


            if (!minStr.isEmpty()) {
                try {
                    minPrice = Double.parseDouble(minStr);
                    if (minPrice < 0) {
                        minPriceEditText.setError("Price must be positive.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    minPriceEditText.setError("Invalid number format.");
                    return;
                }
            }


            if (!maxStr.isEmpty()) {
                try {
                    maxPrice = Double.parseDouble(maxStr);
                    if (maxPrice < 0) {
                        maxPriceEditText.setError("Price must be positive.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    maxPriceEditText.setError("Invalid number format.");
                    return;
                }
            }


            if (minPrice != -1 && maxPrice != -1 && minPrice > maxPrice) {
                minPriceEditText.setError("Min price cannot be greater than Max price.");
                maxPriceEditText.setError("Max price cannot be less than Min price.");
                return;
            }


            currentMinPrice = minPrice;
            currentMaxPrice = maxPrice;


            minPriceEditText.setError(null);
            maxPriceEditText.setError(null);

            applyFiltersAndSorting();
        });


        roomsListView.setOnItemClickListener((parent, view, position, id) -> {
            RoomEntity selectedRoom = roomList.get(position);
            Intent intent = new Intent(RoomListActivity.this, RoomDetailActivity.class);
            intent.putExtra("ROOM_ID", selectedRoom.getRoomId());
            startActivity(intent);
        });
    }

    private void insertSampleRoomsIfEmpty() {
        new Thread(() -> {
            List<RoomEntity> existingRooms = roomDao.getAllAvailableRooms();

            boolean needsUpdate = false;
            if (!existingRooms.isEmpty()) {
                for (RoomEntity room : existingRooms) {
                    if (room.getImageUrl() != null && 
                        (room.getImageUrl().contains("via.placeholder.com/300") || 
                         room.getImageUrl().contains("unsplash.com") || 
                         !room.getImageUrl().startsWith("drawable://"))) {
                        needsUpdate = true;
                        break;
                    }
                }
            }
            
            if (existingRooms.isEmpty() || needsUpdate) {

                if (needsUpdate) {
                    for (RoomEntity room : existingRooms) {
                        roomDao.deleteRoom(room);
                    }
                }

                insertEcoMountainCabin();
                insertInnovativeEcoPod();
                insertSustainableStandardRoom();
            }


            enforceRoomLimit(3);

            runOnUiThread(() -> {
                loadRooms();
            });
        }).start();
    }
    
    private void insertEcoMountainCabin() {
        RoomEntity room = new RoomEntity(
            "Mountain-View Eco Cabin",
            "Stunning panoramic mountain views from this sustainably-built cabin featuring reclaimed wood construction, " +
            "passive solar heating, and composting toilets. Includes organic cotton bedding, rainwater shower system, " +
            "and a private deck with hammock. Perfect for couples seeking an authentic eco-experience.",
            165.0, 2
        );
        room.setImageUrl("drawable://mountain_view_cabin");
        room.setAvailable(true);
        roomDao.insertRoom(room);
    }
    
    private void insertInnovativeEcoPod() {
        RoomEntity room = new RoomEntity(
            "Innovative Eco-Pod",
            "Experience our award-winning sustainable architecture in this geodesic dome pod. Features include living roof with " +
            "native plants, 100% solar power, greywater recycling system, and natural ventilation. Compact yet comfortable " +
            "design with fold-away furniture. Ideal for solo eco-travelers or minimalist couples.",
            135.0, 1
        );
        room.setImageUrl("drawable://eco_pod_exterior");
        room.setAvailable(true);
        roomDao.insertRoom(room);
    }
    
    private void insertSustainableStandardRoom() {
        RoomEntity room = new RoomEntity(
            "Sustainable Standard Suite",
            "Comfortable accommodations with thoughtful eco-features including bamboo flooring, low-VOC paints, " +
            "energy-efficient LED lighting, and locally-sourced furnishings. Amenities include refillable toiletries, " +
            "filtered water station, organic linens, and educational materials about local ecology.",
            110.0, 2
        );
        room.setImageUrl("drawable://standard_eco_room");
        room.setAvailable(true);
        roomDao.insertRoom(room);
    }
    
    private void insertLuxuryGreenSuite() {
        RoomEntity room = new RoomEntity(
            "Luxury Green Suite",
            "Premium eco-luxury featuring green roof garden, private hot tub with solar heating, and floor-to-ceiling " +
            "windows showcasing forest views. Includes kitchenette with energy-efficient appliances, locally-crafted furniture, " +
            "organic spa amenities, and private composting garden. Maximum comfort meets environmental responsibility.",
            225.0, 2
        );
        room.setImageUrl("drawable://luxury_green_suite");
        room.setAvailable(true);
        roomDao.insertRoom(room);
    }
    
    private void insertSolarPoweredTinyHome() {
        RoomEntity room = new RoomEntity(
            "Solar-Powered Tiny Home",
            "Fully self-sufficient tiny house on wheels powered by rooftop solar panels and battery storage. Features " +
            "smart home technology, murphy bed, composting toilet, and rainwater collection system. Outdoor deck with " +
            "fire pit and herb garden. Perfect introduction to off-grid sustainable living.",
            145.0, 2
        );
        room.setImageUrl("drawable://solar_tiny_home");
        room.setAvailable(true);
        roomDao.insertRoom(room);
    }
    
    private void insertEarthshipBioclimaticHome() {
        RoomEntity room = new RoomEntity(
            "Earthship Bioclimatic Home",
            "Revolutionary sustainable architecture using recycled tires, bottles, and earth for thermal mass. Natural " +
            "temperature regulation eliminates need for external heating/cooling. Features food-producing greenhouse, " +
            "complete water recycling system, and waste treatment. Unique sleeping loft and living area with panoramic views.",
            185.0, 3
        );
        room.setImageUrl("drawable://earthship_home");
        room.setAvailable(true);
        roomDao.insertRoom(room);
    }

    private void enforceRoomLimit(int limit) {
        List<RoomEntity> all = roomDao.getAllAvailableRoomsOrderedById();
        if (all.size() > limit) {
            for (int i = limit; i < all.size(); i++) {
                roomDao.deleteRoom(all.get(i));
            }
        }
    }

    private void loadRooms() {

        new Thread(() -> {
            List<RoomEntity> dbRooms = roomDao.getAllAvailableRooms();
            runOnUiThread(() -> {
                roomList.clear();
                roomList.addAll(dbRooms);
                applySorting(roomList);
                roomAdapter.notifyDataSetChanged();
            });
        }).start();
    }


    private void applyFiltersAndSorting() {
        new Thread(() -> {
            List<RoomEntity> filteredList;


            if (currentMinPrice != -1 && currentMaxPrice != -1) {
                filteredList = roomDao.getAvailableRoomsWithinPriceRange(currentMinPrice, currentMaxPrice);

                if (!"All".equals(currentFilter)) {
                    String filterLower = currentFilter.toLowerCase();
                    filteredList.removeIf(room -> !room.getRoomType().toLowerCase().contains(filterLower));
                }
            } else {

                if ("All".equals(currentFilter)) {
                    filteredList = roomDao.getAllAvailableRooms();
                } else {
                    String filterLower = currentFilter.toLowerCase();
                    List<RoomEntity> allAvailable = roomDao.getAllAvailableRooms();
                    filteredList = new ArrayList<>();
                    for (RoomEntity room : allAvailable) {
                        if (room.getRoomType().toLowerCase().contains(filterLower)) {
                            filteredList.add(room);
                        }
                    }
                }
            }


            applySorting(filteredList);

            runOnUiThread(() -> {
                roomList.clear();
                roomList.addAll(filteredList);
                roomAdapter.notifyDataSetChanged();
            });
        }).start();
    }


    private void applySorting(List<RoomEntity> listToSort) {
        switch (currentSort) {
            case "Price: Low to High":
                Collections.sort(listToSort, Comparator.comparingDouble(RoomEntity::getPricePerNight));
                break;
            case "Price: High to Low":
                Collections.sort(listToSort, (r1, r2) -> Double.compare(r2.getPricePerNight(), r1.getPricePerNight()));
                break;
            case "Name: A-Z":
                Collections.sort(listToSort, (r1, r2) -> r1.getRoomType().compareToIgnoreCase(r2.getRoomType()));
                break;
            case "Name: Z-A":
                Collections.sort(listToSort, (r1, r2) -> r2.getRoomType().compareToIgnoreCase(r1.getRoomType()));
                break;

            default:

                break;
        }
    }
}