package com.example.meto;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ForecastActivity extends AppCompatActivity {

    private static final String API_KEY = "1699e6c3371707760b5c205eeac79b8d";
    private static final String BASE_URL = "https://api.openweathermap.org/";

    private String cityName;
    private ProgressBar progressBar;
    private LinearLayout forecastContainer;
    private Button backButton;
    private WeatherAPI weatherAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forecast);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherAPI = retrofit.create(WeatherAPI.class);

        // Get city name from intent
        cityName = getIntent().getStringExtra("city_name");

        // Initialize views
        progressBar = findViewById(R.id.progress_bar_forecast);
        forecastContainer = findViewById(R.id.forecast_container);
        backButton = findViewById(R.id.back_button_forecast);
        TextView titleText = findViewById(R.id.forecast_title);

        titleText.setText("5-Day Forecast - " + cityName);

        backButton.setOnClickListener(v -> finish());

        // Fetch forecast
        fetchForecast(cityName);
    }

    private void fetchForecast(String city) {
        progressBar.setVisibility(View.VISIBLE);
        forecastContainer.setVisibility(View.GONE);

        Call<ForecastResponse> call = weatherAPI.getForecast(city, API_KEY);

        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    displayForecast(response.body());
                } else {
                    Toast.makeText(ForecastActivity.this, "Failed to load forecast", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ForecastActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayForecast(ForecastResponse response) {
        forecastContainer.removeAllViews();

        if (response.getForecastList() != null) {
            // Get one forecast item per day (every 24 hours = index 8 in 3-hour intervals)
            for (int i = 0; i < Math.min(40, response.getForecastList().size()); i += 8) {
                ForecastItem item = response.getForecastList().get(i);
                addForecastCard(item);
            }
        }

        forecastContainer.setVisibility(View.VISIBLE);
    }

    private void addForecastCard(ForecastItem item) {
        LinearLayout card = new LinearLayout(this);
        card.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        card.setPadding(16, 12, 16, 12);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setBackgroundResource(R.drawable.card_background);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 8, 0, 8);
        card.setLayoutParams(cardParams);

        // Date
        TextView dateText = new TextView(this);
        dateText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        dateText.setText(formatDate(item.getDt()));
        dateText.setTextSize(14);
        dateText.setTextColor(getResources().getColor(android.R.color.black, getTheme()));
        card.addView(dateText);

        // Weather condition
        TextView conditionText = new TextView(this);
        conditionText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        String condition = item.getWeather().get(0).getDescription();
        conditionText.setText(condition.substring(0, 1).toUpperCase() + condition.substring(1));
        conditionText.setTextSize(14);
        conditionText.setTextColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
        card.addView(conditionText);

        // Temperature
        TextView tempText = new TextView(this);
        tempText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        double temp = item.getMain().getTemp() - 273.15;
        tempText.setText(String.format("%.0f°C", temp));
        tempText.setTextSize(16);
        tempText.setTextSize(Typeface.BOLD);
        tempText.setTextColor(getResources().getColor(android.R.color.holo_blue_dark, getTheme()));
        card.addView(tempText);

        forecastContainer.addView(card);
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd", Locale.getDefault());
        return sdf.format(new Date(timestamp * 1000));
    }
}
