package com.example.ecostayretreat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecostayretreat.database.AppDatabase;
import com.example.ecostayretreat.database.ActivityDao;
import com.example.ecostayretreat.database.ActivityEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class ActivityListActivity extends AppCompatActivity {

    private ListView activitiesListView;
    private Spinner filterSpinner; // Example: filter by type
    private List<ActivityEntity> activityList;
    private ArrayAdapter<ActivityEntity> activityAdapter;
    private AppDatabase database;
    private ActivityDao activityDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_list);

        database = AppDatabase.getDatabase(this);
        activityDao = database.activityDao();

        activitiesListView = findViewById(R.id.listViewActivities);
        filterSpinner = findViewById(R.id.spinnerFilter);

        // Initialize list (will be populated from database)
        activityList = new ArrayList<>();
        activityAdapter = new ActivityAdapter(this, R.layout.activity_list_item, activityList);
        activitiesListView.setAdapter(activityAdapter);

        // Check if database is empty and insert sample data if needed
        insertSampleActivitiesIfEmpty();

        // Load initial data
        loadActivities();

        // Setup filter spinner (example)
        String[] filterOptions = {
                getString(R.string.filter_all),
                getString(R.string.filter_guided_hike),
                getString(R.string.filter_eco_tour),
                getString(R.string.filter_bird_watching),
                getString(R.string.filter_workshop)
        };
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filterOptions);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(filterAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = filterOptions[position];
                if (position == 0) { // "All"
                    loadActivities(); // Reload all available activities
                } else {
                    loadFilteredActivities(selectedFilter);
                }
                Toast.makeText(ActivityListActivity.this, getString(R.string.msg_filter_applied, selectedFilter), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Item click listener
        activitiesListView.setOnItemClickListener((parent, view, position, id) -> {
            ActivityEntity selectedActivity = activityList.get(position);
            Intent intent = new Intent(ActivityListActivity.this, ActivityDetailActivity.class);
            intent.putExtra("ACTIVITY_ID", selectedActivity.getActivityId()); // Pass ID or serialize object (Parcelable)
            startActivity(intent);
        });
    }

    private void insertSampleActivitiesIfEmpty() {
        new Thread(() -> {
            List<ActivityEntity> existingActivities = activityDao.getAllAvailableActivities();

            // If DB is empty, insert exactly the four requested activities
            if (existingActivities.isEmpty()) {
                insertGuidedHikeActivity();
                insertEcoTourActivity();
                insertSustainabilityWorkshopActivity();
                insertBirdWatchingActivity();
            }

            // Always enforce whitelist and a hard limit of 4 activities
            enforceActivityWhitelistAndLimit();

            runOnUiThread(() -> {
                loadActivities();
            });
        }).start();
    }
    
    private void insertGuidedHikeActivity() {
        ActivityEntity guidedHike = new ActivityEntity(
            "Guided Hike",
            "Explore our trails with a certified guide and learn about local flora, fauna, and conservation efforts.",
            40.0, "2.5 hours", "Daily 8AM & 2PM"
        );
        guidedHike.setImageUrl("drawable://guided_hike");
        guidedHike.setAvailable(true);
        activityDao.insertActivity(guidedHike);
    }

    private void insertEcoTourActivity() {
        ActivityEntity ecoTour = new ActivityEntity(
            "Eco-Tour",
            "Tour our sustainable facilities including solar, water recycling, and permaculture gardens.",
            35.0, "1.5 hours", "Daily 10AM & 3PM"
        );
        ecoTour.setImageUrl("drawable://eco_tour");
        ecoTour.setAvailable(true);
        activityDao.insertActivity(ecoTour);
    }

    private void insertSustainabilityWorkshopActivity() {
        ActivityEntity sustainabilityWorkshop = new ActivityEntity(
            "Sustainability Workshop",
            "Hands-on session to create eco-friendly products and learn zero-waste practices.",
            45.0, "2 hours", "Saturdays 1PM"
        );
        sustainabilityWorkshop.setImageUrl("drawable://sustainability_workshop");
        sustainabilityWorkshop.setAvailable(true);
        activityDao.insertActivity(sustainabilityWorkshop);
    }

    private void insertBirdWatchingActivity() {
        ActivityEntity birdWatching = new ActivityEntity(
            "Bird-Watching Session",
            "Early morning guided bird-watching with binoculars provided. Learn about local species and habitats.",
            50.0, "3 hours", "Daily 6AM"
        );
        birdWatching.setImageUrl("drawable://bird_watching");
        birdWatching.setAvailable(true);
        activityDao.insertActivity(birdWatching);
    }

    private void insertEcoFriendlyActivities() {
        // Core eco-friendly activities
        ActivityEntity ecoTour = new ActivityEntity(
            "Guided Eco-Tour",
            "Explore our sustainable facilities including solar panels, rainwater systems, and organic gardens. Learn how EcoStay Retreat operates in harmony with nature.",
            35.0, "1.5 hours", "Daily 10AM & 3PM"
        );
        ecoTour.setImageUrl("drawable://eco_tour");
        ecoTour.setAvailable(true);
        activityDao.insertActivity(ecoTour);
        
        ActivityEntity permacultureTour = new ActivityEntity(
            "Permaculture Garden Tour",
            "Discover sustainable gardening practices in our demonstration permaculture gardens. Learn about companion planting, composting, and soil regeneration.",
            25.0, "1 hour", "Tue, Thu, Sat 2PM"
        );
        permacultureTour.setImageUrl("drawable://permaculture_garden");
        permacultureTour.setAvailable(true);
        activityDao.insertActivity(permacultureTour);
        
        ActivityEntity renewableEnergyTour = new ActivityEntity(
            "Renewable Energy Facility Tour",
            "See how we generate clean energy with solar panels, wind turbines, and geothermal systems. Perfect for families and education groups.",
            20.0, "45 minutes", "Mon, Wed, Fri 11AM"
        );
        renewableEnergyTour.setImageUrl("drawable://renewable_energy");
        renewableEnergyTour.setAvailable(true);
        activityDao.insertActivity(renewableEnergyTour);
    }
    
    private void insertEducationalWorkshops() {
        // Educational sustainability workshops
        ActivityEntity sustainabilityWorkshop = new ActivityEntity(
            "DIY Eco-Products Workshop",
            "Learn to create natural cleaning products, biodegradable soaps, and reusable household items. Take your creations home!",
            45.0, "2 hours", "Saturdays 1PM"
        );
        sustainabilityWorkshop.setImageUrl("drawable://sustainability_workshop");
        sustainabilityWorkshop.setAvailable(true);
        activityDao.insertActivity(sustainabilityWorkshop);
        
        ActivityEntity composting = new ActivityEntity(
            "Composting & Waste Reduction Workshop",
            "Master the art of composting and learn zero-waste living techniques. Includes hands-on bin setup and troubleshooting.",
            30.0, "1.5 hours", "Sundays 10AM"
        );
        composting.setImageUrl("drawable://composting");
        composting.setAvailable(true);
        activityDao.insertActivity(composting);
        
        ActivityEntity wildcraftingWorkshop = new ActivityEntity(
            "Wildcrafting & Herbal Medicine Workshop",
            "Learn to identify, harvest, and use local medicinal plants sustainably. Create your own herbal remedies and teas.",
            55.0, "3 hours", "First Saturday each month 9AM"
        );
        wildcraftingWorkshop.setImageUrl("drawable://wildcrafting");
        wildcraftingWorkshop.setAvailable(true);
        activityDao.insertActivity(wildcraftingWorkshop);
    }
    
    private void insertOutdoorAdventures() {
        // Nature-based outdoor activities
        ActivityEntity guidedHike = new ActivityEntity(
            "Interpretive Nature Hike",
            "Explore diverse ecosystems on our trail system. Learn about local flora, fauna, and conservation efforts with our certified naturalist guides.",
            40.0, "2.5 hours", "Daily 8AM & 2PM"
        );
        guidedHike.setImageUrl("drawable://guided_hike");
        guidedHike.setAvailable(true);
        activityDao.insertActivity(guidedHike);
        
        ActivityEntity birdWatching = new ActivityEntity(
            "Dawn Bird Watching Experience",
            "Join expert ornithologists for early morning bird watching. Spot rare species and learn about avian conservation. Binoculars provided.",
            50.0, "3 hours", "Daily 6AM"
        );
        birdWatching.setImageUrl("drawable://bird_watching");
        birdWatching.setAvailable(true);
        activityDao.insertActivity(birdWatching);
        
        ActivityEntity starGazing = new ActivityEntity(
            "Dark Sky Stargazing & Astronomy",
            "Experience our certified Dark Sky location with professional telescopes. Learn about celestial navigation and light pollution impact.",
            45.0, "2 hours", "Clear nights 8PM (seasonal)"
        );
        starGazing.setImageUrl("drawable://stargazing");
        starGazing.setAvailable(true);
        activityDao.insertActivity(starGazing);
        
        ActivityEntity foraging = new ActivityEntity(
            "Sustainable Foraging Adventure",
            "Learn ethical wild food harvesting with our expert forager. Discover edible plants, mushrooms, and berries while practicing Leave No Trace principles.",
            60.0, "4 hours", "Weekends 9AM (seasonal)"
        );
        foraging.setImageUrl("drawable://foraging");
        foraging.setAvailable(true);
        activityDao.insertActivity(foraging);
    }
    
    private void enforceActivityWhitelistAndLimit() {
        Set<String> allowed = new HashSet<>(Arrays.asList(
                "Guided Hike",
                "Eco-Tour",
                "Sustainability Workshop",
                "Bird-Watching Session"
        ));

        // Remove any activities not in the allowed set
        List<ActivityEntity> all = activityDao.getAllAvailableActivitiesOrderedById();
        for (ActivityEntity a : all) {
            if (!allowed.contains(a.getActivityName())) {
                activityDao.deleteActivity(a);
            }
        }

        // Ensure there are at most 4 activities
        List<ActivityEntity> ordered = activityDao.getAllAvailableActivitiesOrderedById();
        if (ordered.size() > 4) {
            for (int i = 4; i < ordered.size(); i++) {
                activityDao.deleteActivity(ordered.get(i));
            }
        }

        // If some of the four are missing (e.g., partial list), insert them
        List<ActivityEntity> after = activityDao.getAllAvailableActivitiesOrderedById();
        Set<String> existingNames = new HashSet<>();
        for (ActivityEntity a : after) existingNames.add(a.getActivityName());
        if (!existingNames.contains("Guided Hike")) insertGuidedHikeActivity();
        if (!existingNames.contains("Eco-Tour")) insertEcoTourActivity();
        if (!existingNames.contains("Sustainability Workshop")) insertSustainabilityWorkshopActivity();
        if (!existingNames.contains("Bird-Watching Session")) insertBirdWatchingActivity();
    }

    private void insertConservationPrograms() {
        // Conservation and citizen science activities
        ActivityEntity treePlanting = new ActivityEntity(
            "Forest Restoration Tree Planting",
            "Participate in reforestation efforts by planting native species. Learn about forest ecology and your trees' growth tracking.",
            25.0, "2 hours", "Weekends 9AM (planting season)"
        );
        treePlanting.setImageUrl("drawable://tree_planting");
        treePlanting.setAvailable(true);
        activityDao.insertActivity(treePlanting);
        
        ActivityEntity wildlifeMonitoring = new ActivityEntity(
            "Citizen Science Wildlife Monitoring",
            "Assist researchers in tracking local wildlife populations using trail cameras and tracking techniques. Contribute to conservation science.",
            35.0, "3 hours", "Thursdays 1PM"
        );
        wildlifeMonitoring.setImageUrl("drawable://wildlife_monitoring");
        wildlifeMonitoring.setAvailable(true);
        activityDao.insertActivity(wildlifeMonitoring);
        
        ActivityEntity streamRestoration = new ActivityEntity(
            "Stream Restoration Project",
            "Help restore local waterways by removing invasive species and planting native riparian vegetation. Learn about watershed health.",
            40.0, "3.5 hours", "Second Saturday each month 8AM"
        );
        streamRestoration.setImageUrl("drawable://stream_restoration");
        streamRestoration.setAvailable(true);
        activityDao.insertActivity(streamRestoration);
        
        ActivityEntity pollinatorGarden = new ActivityEntity(
            "Pollinator Garden Maintenance",
            "Maintain our certified pollinator gardens and learn about native bee conservation. Perfect for families and garden enthusiasts.",
            30.0, "2 hours", "Wednesdays 10AM"
        );
        pollinatorGarden.setImageUrl("drawable://pollinator_garden");
        pollinatorGarden.setAvailable(true);
        activityDao.insertActivity(pollinatorGarden);
    }

    private void loadActivities() {
        new Thread(() -> {
            List<ActivityEntity> dbActivities = activityDao.getAllAvailableActivities();
            runOnUiThread(() -> {
                activityList.clear();
                activityList.addAll(dbActivities);
                activityAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void loadFilteredActivities(String filter) {
        new Thread(() -> {
            List<ActivityEntity> dbActivities = activityDao.getFilteredAvailableActivities(filter);
            runOnUiThread(() -> {
                activityList.clear();
                activityList.addAll(dbActivities);
                activityAdapter.notifyDataSetChanged();
            });
        }).start();
    }
}