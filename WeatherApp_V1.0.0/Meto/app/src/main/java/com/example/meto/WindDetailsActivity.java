package com.example.meto;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WindDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wind_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        double windSpeed = getIntent().getDoubleExtra("wind_speed", 0);
        int windDegree = getIntent().getIntExtra("wind_degree", 0);
        double windGust = getIntent().getDoubleExtra("wind_gust", 0);
        String cityName = getIntent().getStringExtra("city_name");

        TextView windSpeedText = findViewById(R.id.wind_speed);
        TextView windDegreeText = findViewById(R.id.wind_direction_arrow);
        TextView windGustText = findViewById(R.id.wind_gust);
        TextView cityNameText = findViewById(R.id.city_name_wind);
        Button backButton = findViewById(R.id.back_button);

        // Display wind data
        windSpeedText.setText(String.format("%.2f m/s", windSpeed));
        windGustText.setText(String.format("%.2f m/s", windGust));
        windDegreeText.setText(getWindDirectionArrow(windDegree));
        cityNameText.setText(String.format("💨 Wind Info - %s", cityName));

        backButton.setOnClickListener(v -> finish());
    }

    private String getWindDirectionArrow(int deg) {
        if ((deg >= 338 || deg < 23))    return "↑";
        if (deg >= 23 && deg < 68)       return "↗";
        if (deg >= 68 && deg < 113)      return "→";
        if (deg >= 113 && deg < 158)     return "↘";
        if (deg >= 158 && deg < 203)     return "↓";
        if (deg >= 203 && deg < 248)     return "↙";
        if (deg >= 248 && deg < 293)     return "←";
        if (deg >= 293 && deg < 338)     return "↖";
        return "?";
    }
}
