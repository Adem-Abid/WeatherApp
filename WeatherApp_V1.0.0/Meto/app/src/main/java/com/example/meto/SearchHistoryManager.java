package com.example.meto;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class SearchHistoryManager {
    private static final String PREF_NAME = "weather_app_prefs";
    private static final String KEY_SEARCH_HISTORY = "search_history";
    private static final int MAX_HISTORY = 10;

    private SharedPreferences sharedPreferences;

    public SearchHistoryManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Save a city to search history
    public void addCity(String city) {
        if (city == null || city.isEmpty()) {
            return;
        }

        // Get current history as string
        String historyString = sharedPreferences.getString(KEY_SEARCH_HISTORY, "");
        ArrayList<String> history = new ArrayList<>();

        if (!historyString.isEmpty()) {
            history.addAll(Arrays.asList(historyString.split(",")));
        }

        // Remove if already exists
        history.remove(city);

        // Add new city at the beginning
        history.add(0, city);

        // Keep only last 10
        if (history.size() > MAX_HISTORY) {
            history.remove(history.size() - 1);
        }

        // Save back as comma-separated string
        String newHistoryString = String.join(",", history);
        sharedPreferences.edit().putString(KEY_SEARCH_HISTORY, newHistoryString).apply();
    }

    // Get all searched cities in order
    public ArrayList<String> getSearchHistory() {
        String historyString = sharedPreferences.getString(KEY_SEARCH_HISTORY, "");
        ArrayList<String> history = new ArrayList<>();

        if (!historyString.isEmpty()) {
            history.addAll(Arrays.asList(historyString.split(",")));
        }

        return history;
    }

    // Clear search history
    public void clearHistory() {
        sharedPreferences.edit().remove(KEY_SEARCH_HISTORY).apply();
    }

    // Delete a specific city from history
    public void deleteCity(String city) {
        String historyString = sharedPreferences.getString(KEY_SEARCH_HISTORY, "");
        ArrayList<String> history = new ArrayList<>();

        if (!historyString.isEmpty()) {
            history.addAll(Arrays.asList(historyString.split(",")));
        }

        history.remove(city);

        String newHistoryString = String.join(",", history);
        sharedPreferences.edit().putString(KEY_SEARCH_HISTORY, newHistoryString).apply();
    }
}
