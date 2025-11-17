Meteo App is a simple but complete Android weather application built in Java.
It consumes the OpenWeatherMap API using Retrofit and displays:

Current weather by city (temperature, feels like, min/max, humidity, pressure, visibility, sea level, ground level)

Detailed wind screen (speed, gust, direction with arrow icon)

5‑day forecast view with daily temperature and conditions

Search suggestions with history stored via SharedPreferences

The UI is designed with:

Gradient backgrounds that change with weather conditions (sunny, cloudy, rainy)

Custom icons for sun, clouds, rain, wind, humidity and pressure instead of emojis

A card‑based layout for current weather details

A dedicated wind screen and a small “Clear history” button aligned in the corner

Under the hood the app uses:

Java + Retrofit + Gson for REST API calls and JSON parsing

SharedPreferences for persistent search history

AppCompat + Material Components for theming

XML drawables & PNG icons for gradients and weather graphics

This project is a good example of:

Structuring a small Android app (activities, layout files, model classes, API interface)

Handling API errors and showing user‑friendly messages

Separating current weather and wind details into different screens

Applying basic UI/UX improvements (icons, spacing, typography, gradients)