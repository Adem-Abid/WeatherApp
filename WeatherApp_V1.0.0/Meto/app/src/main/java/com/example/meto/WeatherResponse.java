package com.example.meto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {
    @SerializedName("main")
    private Main main;

    @SerializedName("weather")
    private List<Weather> weather;

    @SerializedName("name")
    private String name;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("visibility")
    private int visibility;

    @SerializedName("cod")
    private int cod;

    public Main getMain() { return main; }
    public List<Weather> getWeather() { return weather; }
    public String getName() { return name; }
    public Wind getWind() { return wind; }
    public int getVisibility() { return visibility; }
    public int getCod() { return cod; }
}

class Main {
    @SerializedName("temp")
    private double temp;

    @SerializedName("feels_like")
    private double feels_like;

    @SerializedName("temp_min")
    private double temp_min;

    @SerializedName("temp_max")
    private double temp_max;

    @SerializedName("pressure")
    private int pressure;

    @SerializedName("humidity")
    private int humidity;

    @SerializedName("sea_level")
    private int sea_level;

    @SerializedName("grnd_level")
    private int grnd_level;

    public double getTemp() { return temp; }
    public double getFeelsLike() { return feels_like; }
    public double getTempMin() { return temp_min; }
    public double getTempMax() { return temp_max; }
    public int getPressure() { return pressure; }
    public int getHumidity() { return humidity; }
    public int getSeaLevel() { return sea_level; }
    public int getGrndLevel() { return grnd_level; }
}

class Weather {
    @SerializedName("id")
    private int id;

    @SerializedName("main")
    private String main;

    @SerializedName("description")
    private String description;

    @SerializedName("icon")
    private String icon;

    public String getMain() { return main; }
    public String getDescription() { return description; }
}

class Wind {
    @SerializedName("speed")
    private double speed;

    @SerializedName("deg")
    private int deg;

    @SerializedName("gust")
    private double gust;

    public double getSpeed() { return speed; }
    public int getDeg() { return deg; }
    public double getGust() { return gust; }
}
