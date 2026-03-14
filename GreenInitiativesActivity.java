package com.example.ecostayretreat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GreenInitiativesActivity extends AppCompatActivity {

    private Button joinInitiativeButton, bookEcoTourButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_green_initiatives);

        setupViews();
        loadGreenInitiatives();
        setupActionButtons();
    }
    
    private void setupViews() {
        // Initialize UI components from layout
        joinInitiativeButton = findViewById(R.id.buttonJoinInitiative);
        bookEcoTourButton = findViewById(R.id.buttonBookEcoTour);
    }
    
    private void setupActionButtons() {
        if (joinInitiativeButton != null) {
            joinInitiativeButton.setOnClickListener(v -> {
                // Join sustainability initiative
                android.widget.Toast.makeText(this, "Thank you for joining our sustainability initiative! You'll receive updates about eco-events.", android.widget.Toast.LENGTH_LONG).show();
                // In a real app, this would save user preference and send notifications
            });
        }
        
        if (bookEcoTourButton != null) {
            bookEcoTourButton.setOnClickListener(v -> {
                // Navigate to activities with eco-tour filter
                Intent intent = new Intent(GreenInitiativesActivity.this, ActivityListActivity.class);
                intent.putExtra("FILTER_TYPE", "eco-tour");
                startActivity(intent);
            });
        }
    }
    
    private void loadGreenInitiatives() {
        // Set comprehensive green initiatives content
        TextView initiativesTextView = findViewById(R.id.textViewInitiatives);
        TextView natureReservesTextView = findViewById(R.id.textViewNatureReserves);
        TextView sustainabilityTextView = findViewById(R.id.textViewSustainability);
        
        if (initiativesTextView != null) {
            String initiativesText = buildInitiativesContent();
            initiativesTextView.setText(initiativesText);
        }
        
        if (natureReservesTextView != null) {
            String natureReservesText = buildNatureReservesContent();
            natureReservesTextView.setText(natureReservesText);
        }
        
        if (sustainabilityTextView != null) {
            String sustainabilityText = buildSustainabilityContent();
            sustainabilityTextView.setText(sustainabilityText);
        }
    }
    
    private String buildInitiativesContent() {
        return "🌿 EcoStay Retreat's Green Initiatives\n\n" +
               "♻️ RENEWABLE ENERGY\n" +
               "• 100% solar-powered facilities\n" +
               "• Wind-assisted ventilation systems\n" +
               "• Geothermal heating and cooling\n\n" +
               
               "💧 WATER CONSERVATION\n" +
               "• Rainwater harvesting system\n" +
               "• Greywater recycling for gardens\n" +
               "• Low-flow fixtures throughout\n" +
               "• Natural pond filtration systems\n\n" +
               
               "🌱 SUSTAINABLE AGRICULTURE\n" +
               "• Organic vegetable gardens\n" +
               "• Permaculture design principles\n" +
               "• Composting program\n" +
               "• Herb spiral and food forests\n\n" +
               
               "🗂️ WASTE REDUCTION\n" +
               "• Zero-waste kitchen practices\n" +
               "• Plastic-free dining experience\n" +
               "• Upcycling workshops\n" +
               "• Digital-first communication";
    }
    
    private String buildNatureReservesContent() {
        return "🏞️ Local Nature Reserves & Conservation Areas\n\n" +
               "🌲 WHISPERING PINES NATIONAL PARK (5km)\n" +
               "• 2,500 acres of old-growth forest\n" +
               "• Home to 150+ bird species\n" +
               "• Certified Dark Sky location\n" +
               "• Educational trails and ranger programs\n\n" +
               
               "🏔️ CRYSTAL LAKE NATURE RESERVE (10km)\n" +
               "• Pristine alpine lake ecosystem\n" +
               "• Native trout conservation project\n" +
               "• Wildflower meadows (seasonal)\n" +
               "• Guided canoe eco-tours available\n\n" +
               
               "🦅 EAGLE'S NEST WILDLIFE SANCTUARY (15km)\n" +
               "• Raptor rehabilitation center\n" +
               "• Endangered species protection\n" +
               "• Wildlife photography blinds\n" +
               "• Volunteer conservation programs\n\n" +
               
               "🦋 MOUNTAIN MEADOW PRESERVE (8km)\n" +
               "• Butterfly migration corridor\n" +
               "• Native pollinator gardens\n" +
               "• Seasonal nature walks\n" +
               "• Citizen science opportunities";
    }
    
    private String buildSustainabilityContent() {
        return "🌍 Ongoing Sustainability Practices\n\n" +
               "📚 EDUCATIONAL PROGRAMS\n" +
               "• Daily sustainability workshops\n" +
               "• Renewable energy facility tours\n" +
               "• Permaculture design classes\n" +
               "• Children's nature education programs\n\n" +
               
               "🤝 COMMUNITY PARTNERSHIPS\n" +
               "• Local artisan marketplace\n" +
               "• Indigenous cultural exchange\n" +
               "• Regional food sourcing network\n" +
               "• Carbon offset tree planting\n\n" +
               
               "🎯 CERTIFICATION & GOALS\n" +
               "• LEED Platinum certified buildings\n" +
               "• Carbon neutral operations by 2025\n" +
               "• Green Key Eco-Rating: 5 stars\n" +
               "• B-Corp certification pending\n\n" +
               
               "🎁 GUEST PARTICIPATION\n" +
               "• Eco-challenge rewards program\n" +
               "• Seedling adoption program\n" +
               "• Digital detox experiences\n" +
               "• Sustainable souvenir shop\n\n" +
               
               "💡 Tips for Eco-Conscious Travel:\n" +
               "• Pack reusable water bottles\n" +
               "• Choose digital receipts\n" +
               "• Participate in towel/linen programs\n" +
               "• Use refill stations instead of single-use items";
    }
}