package com.rach.archexemplar.http;

import com.rach.archexemplar.forecastclasses.ForecastRootObject;
import com.rach.archexemplar.weatherclasses.WeatherRootObject;

/**
 * Created by rachaelcolley on 19/04/2017.
 */
public interface IAsyncTaskHelperResponse {

    void processFinish(WeatherRootObject output);
    void processFinish(ForecastRootObject output);
}
