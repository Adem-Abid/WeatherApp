package com.example.meto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ForecastResponse {
    @SerializedName("list")
    private List<ForecastItem> forecastList;

    public List<ForecastItem> getForecastList() { return forecastList; }
}

class ForecastItem {
    @SerializedName("dt")
    private long dt;

    @SerializedName("main")
    private Main main;

    @SerializedName("weather")
    private List<Weather> weather;

    public long getDt() { return dt; }
    public Main getMain() { return main; }
    public List<Weather> getWeather() { return weather; }
}
