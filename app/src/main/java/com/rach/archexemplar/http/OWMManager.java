package com.rach.archexemplar.http;

import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rach.archexemplar.BuildConfig;
import com.rach.archexemplar.forecastclasses.ForecastRootObject;
import com.rach.archexemplar.utility.Logs;
import com.rach.archexemplar.weatherclasses.WeatherRootObject;

/**
 * Created by rachaelcolley on 19/04/2017.
 */
public class OWMManager implements IAsyncTaskResponse {

    private final String CID = getClass().getSimpleName();
    private String operationStatus = "The " + CID + " core looks beautiful";
    final static String FORECAST_SERVICE_ADDRESS = "http://api.openweathermap.org/data/2.5/forecast?";
    final static String WEATHER_SERVICE_ADDRESS = "http://api.openweathermap.org/data/2.5/weather?";
    final static String CITY_KEY_TEXT = "q=";
    final static String MEASUREMENTTYPE_KEY_TEXT = "&units=";
    private CommonHttpAsyncTask commonHttpAsyncTask;
    private String serviceAddress = "";
    private String apikeyID = BuildConfig.OPEN_WEATHER_MAP_API_KEY;

    private String apikey = "&APPID=" + apikeyID;


    public IAsyncTaskHelperResponse delegate = null;


    /**
     * Gets the current weather for any given city in the world.
     * Callers should register as a delegate to receive results from this task. The result contains a WeatherRootObject instance containing the deserialised feed.
     * @param cityName String. The city to fetch the weather for. If the city name is spelled incorrectly, the api will attempt to resolve the city name.
     * @param unitsType String. Metric or Imperial. If the unitsType parameter is not well formed, temperature data will be returned in Kelvins.
     */
    public void startWeatherTask(String cityName, String unitsType) {
        Logs.logs(CID,"Start startWeatherTask...");

        startWeatherTask(cityName, unitsType, null);
    }

    /**
     * Gets the current weather for any given city in the world.
     * Callers should register as a delegate to receive results from this task. The result contains a WeatherRootObject instance containing the deserialised feed.
     * @param cityName String. The city to fetch the weather for. If the city name is spelled incorrectly, the api will attempt to resolve the city name.
     * @param unitsType String. Metric or Imperial. If the unitsType parameter is not well formed, temperature data will be returned in Kelvins.
     * @param progressBar android.widget.ProgressBar. The ProgressBar to send updates to.
     */
    public void startWeatherTask(String cityName, String unitsType, ProgressBar progressBar) {
        Logs.logs(CID,"Start startWeatherTask...");

        if (!paramsValid(cityName,unitsType)) return;
        serviceAddress = WEATHER_SERVICE_ADDRESS;
        startHttp(cityName,unitsType,progressBar);
    }

    /**
     * Gets the 5 day forecast for any given city in the world.
     * Callers should register as a delegate to receive results from this task. The result contains a ForecastRootObject instance containing the deserialised feed.
     * @param cityName String. The city to fetch the forecast for. If the city name is spelled incorrectly, the api will attempt to resolve the city name.
     * @param unitsType String. Metric or Imperial. If the unitsType parameter is not well formed, temperature data will be returned in Kelvins.
     */
    public void startForecastTask(String cityName, String unitsType) {
        Logs.logs(CID,"Start startForecastTask...");
        startForecastTask(cityName, unitsType, null);
    }

    /**
     * Gets the 5 day forecast for any given city in the world.
     * Callers should register as a delegate to receive results from this task. The result contains a ForecastRootObject instance containing the deserialised feed.
     * @param cityName String. The city to fetch the forecast for. If the city name is spelled incorrectly, the api will attempt to resolve the city name.
     * @param unitsType String. Metric or Imperial. If the unitsType parameter is not well formed, temperature data will be returned in Kelvins.
     */
    public void startForecastTask(String cityName, String unitsType, ProgressBar progressBar) {
        Logs.logs(CID,"Start startForecastTask...");

        if (!paramsValid(cityName, unitsType)) return;

        serviceAddress = FORECAST_SERVICE_ADDRESS;

        startHttp(cityName, unitsType, progressBar);
    }

    /**
     * Constructs the serviceAddress with the given parameters, registers as a delegate of the http task and asks for a HTTP client connection.
     * Results are returned to the processFinish method.
     * @param cityName String. The city name value.
     * @param unitsType String. Metric or Imperial. The unitsType value.
     * @param progressBar android.widget.ProgressBar. The ProgressBar to send updates to.
     */
    private void startHttp(String cityName, String unitsType, ProgressBar progressBar) {
        Logs.logs(CID,"startHttp...");

        String url = serviceAddress + CITY_KEY_TEXT + cityName + MEASUREMENTTYPE_KEY_TEXT + unitsType + apikey;

        Logs.logs(CID,"url:",url);

        try {
            if (progressBar != null) {
                commonHttpAsyncTask = new CommonHttpAsyncTask(progressBar);
            } else {
                commonHttpAsyncTask = new CommonHttpAsyncTask();
            }
            commonHttpAsyncTask.delegate = this;
            commonHttpAsyncTask.execute(url);
        } finally {
            Logs.logs(CID, operationStatus);
        }
    }

    /**
     * TODO Error handling.
     */
    @Override
    public void processFinish(String output) {
        Logs.logs(CID, "processFinish...");

        Gson gson = new Gson();

        if (serviceAddress.contentEquals(WEATHER_SERVICE_ADDRESS)) {

            WeatherRootObject wro = null;

            if (output == null || output.isEmpty()) {
                operationStatus = "No weather data received";
            }

            try {
                wro = gson.fromJson(output, WeatherRootObject.class);
            } catch (JsonSyntaxException jse) {
                operationStatus = "JsonSyntaxException was thrown when attempting "
                        + "to deserialise the weather result";
                jse.printStackTrace();
            } finally {
                Logs.logs(CID, operationStatus);
            }
            delegate.processFinish(wro);

        } else if (serviceAddress.contentEquals(FORECAST_SERVICE_ADDRESS)) {

            ForecastRootObject fro = null;

            if (output == null || output.isEmpty()) {
                operationStatus = "No forecast data received";
            }

            try {
                fro = gson.fromJson(output, ForecastRootObject.class);
            } catch (JsonSyntaxException jse) {
                operationStatus = "JsonSyntaxException was thrown when attempting "
                        + "to deserialise the forecast result";
                jse.printStackTrace();
            } finally {
                Logs.logs(CID, operationStatus);
            }

            delegate.processFinish(fro);
        }
    }

    /**
     * Checks validity of incoming parameters.
     * @param cityName
     * @param unitsType
     * @return True if the parameters are valid. False if the parameters in not valid.
     */
    private boolean paramsValid(String cityName, String unitsType) {
        if (cityName == null || cityName.isEmpty()) {
            operationStatus = "provided cityName was null or empty.";
            Logs.logs(CID, operationStatus);
            return false;
        }
        if (unitsType == null || unitsType.isEmpty()) {
            operationStatus = "provided unitsType was null or empty.";
            Logs.logs(CID, operationStatus);
            return false;
        }
        return true;
    }


}
