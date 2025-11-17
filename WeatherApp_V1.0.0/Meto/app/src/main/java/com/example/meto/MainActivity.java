package com.example.meto;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.animation.core.Animation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "1699e6c3371707760b5c205eeac79b8d";
    private static final String BASE_URL = "https://api.openweathermap.org/";

    // UI Components
    AutoCompleteTextView cityName;
    Button search;
    Button windInfoButton;
    Button clearHistoryButton;
    Button retryButton;
    Button forecastButton;
    ProgressBar progressBar;
    LinearLayout errorContainer;
    LinearLayout weatherCard;
    TextView errorMessage;

    // Weather TextViews
    TextView weatherCityName;
    TextView weatherDescription;
    TextView weatherTemperature;
    TextView weatherFeelsLike;
    TextView weatherTempMin;
    TextView weatherTempMax;
    TextView weatherHumidity;
    TextView weatherPressure;
    TextView weatherSeaLevel;
    TextView weatherGroundLevel;
    TextView weatherVisibility;
    ImageView weatherIcon;



    // API and Data
    WeatherAPI weatherAPI;
    WeatherResponse currentWeatherData;
    SearchHistoryManager historyManager;


    private String lastSearchedCity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherAPI = retrofit.create(WeatherAPI.class);
        historyManager = new SearchHistoryManager(this);

        // Initialize ALL views
        cityName = findViewById(R.id.textView);
        search = findViewById(R.id.search);
        windInfoButton = findViewById(R.id.wind_info_button);
        clearHistoryButton = findViewById(R.id.clear_history_button);
        progressBar = findViewById(R.id.progress_bar);
        errorContainer = findViewById(R.id.error_container);
        weatherCard = findViewById(R.id.weather_card);
        errorMessage = findViewById(R.id.error_message);
        retryButton = findViewById(R.id.retry_button);

        // Initialize Weather TextViews
        weatherCityName = findViewById(R.id.weather_city_name);
        weatherDescription = findViewById(R.id.weather_description);
        weatherTemperature = findViewById(R.id.weather_temperature);
        weatherFeelsLike = findViewById(R.id.weather_feels_like);
        weatherTempMin = findViewById(R.id.weather_temp_min);
        weatherTempMax = findViewById(R.id.weather_temp_max);
        weatherHumidity = findViewById(R.id.weather_humidity);
        weatherPressure = findViewById(R.id.weather_pressure);
        weatherSeaLevel = findViewById(R.id.weather_sea_level);
        weatherGroundLevel = findViewById(R.id.weather_ground_level);
        weatherVisibility = findViewById(R.id.weather_visibility);
        forecastButton = findViewById(R.id.forecast_button);
        weatherIcon = findViewById(R.id.weather_icon);


        setupAutoComplete();

        search.setOnClickListener(v -> {
            String city = cityName.getText().toString().trim();

            if (city.isEmpty()) {
                showError(getString(R.string.invalid_input), false);
                return;
            }

            lastSearchedCity = city;
            fetchWeather(city);
        });

        retryButton.setOnClickListener(v -> {
            if (!lastSearchedCity.isEmpty()) {
                fetchWeather(lastSearchedCity);
            }
        });

        windInfoButton.setOnClickListener(v -> {
            if (currentWeatherData != null && currentWeatherData.getWind() != null) {
                Intent intent = new Intent(MainActivity.this, WindDetailsActivity.class);
                intent.putExtra("wind_speed", currentWeatherData.getWind().getSpeed());
                intent.putExtra("wind_degree", currentWeatherData.getWind().getDeg());
                intent.putExtra("wind_gust", currentWeatherData.getWind().getGust());
                intent.putExtra("visibility", currentWeatherData.getVisibility());
                intent.putExtra("city_name", currentWeatherData.getName());
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.search_for_city), Toast.LENGTH_SHORT).show();
            }
        });
        forecastButton.setOnClickListener(v -> {
            if (currentWeatherData != null) {
                Intent intent = new Intent(MainActivity.this, ForecastActivity.class);
                intent.putExtra("city_name", currentWeatherData.getName());
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Search for a city first!", Toast.LENGTH_SHORT).show();
            }
        });

        clearHistoryButton.setOnClickListener(v -> {
            historyManager.clearHistory();
            setupAutoComplete();
            Toast.makeText(MainActivity.this, getString(R.string.history_cleared), Toast.LENGTH_SHORT).show();
        });

        weatherCard.setVisibility(View.GONE);




    }

    private void setupAutoComplete() {
        ArrayList<String> searchHistory = historyManager.getSearchHistory();
        ArrayList<String> allCities = new ArrayList<>();

        allCities.addAll(searchHistory);

        for (String city : CityList.CITIES) {
            if (!allCities.contains(city)) {
                allCities.add(city);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                allCities
        );

        cityName.setAdapter(adapter);
        cityName.setThreshold(1);
        cityName.setDropDownBackgroundDrawable(
                getResources().getDrawable(android.R.color.white, getTheme())
        );
    }

    private void fetchWeather(String city) {
        if (city.isEmpty() || !city.matches("^[a-zA-Z\\s-]+$")) {
            showError(getString(R.string.invalid_input), false);
            return;
        }

        showLoading();
        historyManager.addCity(city);

        Call<WeatherResponse> call = weatherAPI.getWeather(city, API_KEY);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                hideLoading();
                Log.d("WeatherApp", "Response Code: " + response.code());

                if (response.isSuccessful()) {
                    WeatherResponse body = response.body();
                    if (body != null) {
                        currentWeatherData = body;
                        setupAutoComplete();
                        displayWeather(body);
                        hideError();
                        windInfoButton.setEnabled(true);
                    } else {
                        showError(getString(R.string.no_results), true);
                    }
                } else if (response.code() == 404) {
                    showError(getString(R.string.city_not_found), true);
                } else if (response.code() >= 500) {
                    showError(getString(R.string.server_error), true);
                } else {
                    showError(getString(R.string.error_occurred) + ": " + response.code(), true);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                hideLoading();
                Log.e("WeatherApp", "Error: ", t);

                String errorMsg;
                if (t instanceof TimeoutException) {
                    errorMsg = getString(R.string.connection_timeout);
                } else if (t instanceof IOException) {
                    errorMsg = getString(R.string.network_error_detailed);
                } else {
                    errorMsg = getString(R.string.network_error_detailed);
                }

                showError(errorMsg, true);
            }
        });
    }

    private void displayWeather(WeatherResponse weather) {
        if (weather == null || weather.getWeather() == null || weather.getWeather().isEmpty()) {
            showError(getString(R.string.invalid_weather_data), false);
            return;
        }

        double tempCelsius = weather.getMain().getTemp() - 273.15;
        double feelsLike = weather.getMain().getFeelsLike() - 273.15;
        double tempMin = weather.getMain().getTempMin() - 273.15;
        double tempMax = weather.getMain().getTempMax() - 273.15;
        int humidity = weather.getMain().getHumidity();
        int pressure = weather.getMain().getPressure();
        int seaLevel = weather.getMain().getSeaLevel();
        int grndLevel = weather.getMain().getGrndLevel();
        int visibility = weather.getVisibility() / 1000;
        String description = weather.getWeather().get(0).getDescription();
        String cityNameResult = weather.getName();

        // Set all TextViews
        weatherCityName.setText(cityNameResult);
        weatherDescription.setText(description.substring(0, 1).toUpperCase() + description.substring(1));
        weatherTemperature.setText(String.format("%.0f°", tempCelsius));
        weatherFeelsLike.setText(String.format("%.0f°C", feelsLike));
        weatherTempMin.setText(String.format("%.0f°C", tempMin));
        weatherTempMax.setText(String.format("%.0f°C", tempMax));
        weatherHumidity.setText(String.format("%d%%", humidity));
        weatherPressure.setText(String.format("%d hPa", pressure));
        weatherSeaLevel.setText(String.format("%d hPa", seaLevel));
        weatherGroundLevel.setText(String.format("%d hPa", grndLevel));
        weatherVisibility.setText(String.format("%d km", visibility));

        // Apply dynamic background
        updateBackgroundByWeather(weather.getWeather().get(0).getMain());

        // Show card with animation
        weatherCard.setVisibility(View.VISIBLE);
        windInfoButton.setEnabled(true);
        forecastButton.setEnabled(true);

        String mainCondition = weather.getWeather().get(0).getMain().toLowerCase();

// Set icon based on main condition
        if (mainCondition.contains("clear")) {
            weatherIcon.setImageResource(R.drawable.ic_sunny);
        } else if (mainCondition.contains("cloud")) {
            weatherIcon.setImageResource(R.drawable.ic_clousy);
        } else if (mainCondition.contains("rain")
                || mainCondition.contains("drizzle")
                || mainCondition.contains("thunderstorm")) {
            weatherIcon.setImageResource(R.drawable.ic_rainy);
        } else {
            // fallback icon
            weatherIcon.setImageResource(R.drawable.ic_clousy);
        }


    }

    private void updateBackgroundByWeather(String weatherMain) {
        LinearLayout mainLayout = findViewById(R.id.main);

        switch (weatherMain.toLowerCase()) {
            case "clear":
                mainLayout.setBackgroundResource(R.drawable.gradient_sunny);
                break;
            case "rain":
            case "drizzle":
            case "thunderstorm":
                mainLayout.setBackgroundResource(R.drawable.gradient_rainy);
                break;
            case "clouds":
                mainLayout.setBackgroundResource(R.drawable.gradient_cloudy);
                break;
            default:
                mainLayout.setBackgroundResource(R.drawable.gradient_background);
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);
        weatherCard.setVisibility(View.GONE);
        windInfoButton.setEnabled(false);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void showError(String message, boolean showRetry) {
        errorContainer.setVisibility(View.VISIBLE);
        errorMessage.setText(message);
        weatherCard.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        windInfoButton.setEnabled(false);

        retryButton.setVisibility(showRetry ? View.VISIBLE : View.GONE);
    }

    private void hideError() {
        errorContainer.setVisibility(View.GONE);
    }


}
