package com.rach.archexemplar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rach.archexemplar.forecastclasses.ForecastRootObject;
import com.rach.archexemplar.http.DownloadImageTask;
import com.rach.archexemplar.http.IAsyncTaskHelperResponse;
import com.rach.archexemplar.http.OWMManager;
import com.rach.archexemplar.utility.ConvertTime;
import com.rach.archexemplar.utility.Logs;
import com.rach.archexemplar.weatherclasses.WeatherRootObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements IAsyncTaskHelperResponse {

    private final String CID = getClass().getSimpleName();

    private ProgressBar pbProgressBar;
    private TextView txtCityName;
    private TextView txtTime;
    private TextView txtDate;
    private TextView txtCurrentTempValue;
    private TextView txtMinValue;
    private TextView txtMaxValue;
    private ImageView imgCurrentWeatherIcon;

    private final int NUM_OF_FORECAST_BLOCKS = 8;
    private final String MEASUREMENT_TYPE_METRIC = "Metric";
    private final String CELSIUS_CODE_POINT = "\u2103";
    private final String FAHRENHEIT_CODE_POINT = "\u2109";
    private final String COUNTRY_CODE = ",au";

    private TextView clockHourTextViews[];
    private ImageView iconImageViews[];

    private double temps[];

    private String currentCity = "Melbourne";
    private String currentMeasurementType = "metric";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logs.logs(CID,"onStart...");
        findControlsInLayout();
        startAPIRequests(currentCity+COUNTRY_CODE, currentMeasurementType);
        Intent intent = new Intent(this,ExService.class);
        startService(intent);
        //getSystemService(Context.)
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logs.logs(CID,"onResume...");
    }

    public void startAPIRequests(String cityName, String measurementType) {
        Logs.logs(CID,"startAPIRequests...");

        // Get weather.
        OWMManager getWeather = new OWMManager();
        getWeather.delegate = this;
        getWeather.startWeatherTask(cityName,measurementType, pbProgressBar);

        // Get forecast.
        OWMManager getForecast = new OWMManager();
        getForecast.delegate = this;
        getForecast.startForecastTask(cityName,measurementType, pbProgressBar);
    }

    @Override
    public void processFinish(WeatherRootObject output) {
        populateWeatherViews(output);
    }

    // http://openweathermap.org/img/w/" + iconcode + ".png"

    protected void populateWeatherViews(WeatherRootObject rootObject) {
        Logs.logs(CID,"populateWeatherViews...");

        // Display selected city name.
        txtCityName.setText(currentCity);

        if (rootObject == null) {
            Logs.logs(CID,"WeatherRootObject returned NULL");
            Toast.makeText(this, "Weather data unavailable at this time", Toast.LENGTH_LONG).show();
            return;
        }

        // Display current date.
        int currentDateTime = rootObject.getDt();
        String displayDate = ConvertTime.makeDateStringForCity(currentDateTime, currentCity);
        txtDate.setText(displayDate);

        // Display current time.
        txtTime.setText(ConvertTime.getTimeForCity(currentCity));
        // Display current temperature.
        String currentTemp = String.format("%.1f", rootObject.getMain().getTemp());
        txtCurrentTempValue.setText(currentTemp + makeUnitMeasurementSign());



        String currentWeatherIcon = rootObject.getWeather().get(0).getIcon();

        downloadAndDisplayIcon(imgCurrentWeatherIcon,currentWeatherIcon);
//        String iconUrl = "http://openweathermap.org/img/w/" + currentWeatherIcon + ".png";
//        DownloadImageTask dit = new DownloadImageTask(imgCurrentWeatherIcon);
//        dit.execute(iconUrl);
        imgCurrentWeatherIcon.setContentDescription(rootObject.getWeather().get(0).getDescription());


        // Display current weather icon.
//        String currentWeatherIcon = rootObject.getWeather().get(0).getIcon();
//        int iconResId = getResources().getIdentifier("img" + currentWeatherIcon, "drawable", getPackageName());
//        imgCurrentWeatherIcon.setContentDescription(rootObject.getWeather().get(0).getDescription());
//        imgCurrentWeatherIcon.setImageResource(iconResId);
    }

    public void downloadAndDisplayIcon(ImageView imgView, String iconName){
        String iconUrl = "http://openweathermap.org/img/w/" + iconName + ".png";
        DownloadImageTask dit = new DownloadImageTask(imgView);
        dit.execute(iconUrl);
    }

    @Override
    public void processFinish(ForecastRootObject output) {
        populateForecastViews(output);
    }

    protected void populateForecastViews(ForecastRootObject rootObject) {
        Logs.logs(CID,"populateForecastViews...");

        if (rootObject == null) {
            Logs.logs(CID,"ForecastRootObject returned NULL");
            Toast.makeText(this, "Forecast data unavailable at this time", Toast.LENGTH_LONG).show();
            return;
        }

        temps = new double[NUM_OF_FORECAST_BLOCKS];
        for (int i = 0; i < NUM_OF_FORECAST_BLOCKS; i++) {

            com.rach.archexemplar.forecastclasses.List currentListObject = rootObject.getList().get(i);

            Logs.logs(CID, currentListObject.getWeather().get(0).getIcon());

            temps[i] = currentListObject.getMain().getTemp();

            // Set content description for forecast icon.
            String contentDescription = currentListObject.getWeather().get(0).getDescription();
            iconImageViews[i].setContentDescription(contentDescription);


            String jsonIconName = currentListObject.getWeather().get(0).getIcon();
            downloadAndDisplayIcon(iconImageViews[i], jsonIconName);

//            // Display forecast icon.
//            String jsonIconName = currentListObject.getWeather().get(0).getIcon();
//            int iconResourceName = getResources()
//                    .getIdentifier("img" + jsonIconName, "drawable", getPackageName());
//            iconImageViews[i].setImageResource(iconResourceName);

            // Display forecast block time.
            String dateTime = ConvertTime.makeClockHourForCity(currentListObject.getDtTxt(), currentCity);
            clockHourTextViews[i].setText(dateTime);
        }

        // Sort the temps array to find the min & max temps for the forecast.
        Arrays.sort(temps);
        txtMinValue.setText(String.format("%.1f", temps[0]) + makeUnitMeasurementSign());
        txtMaxValue.setText(String.format("%.1f", temps[7]) + makeUnitMeasurementSign());
    }

    protected String makeUnitMeasurementSign() {
        Logs.logs(CID,"makeUnitMeasurementSign...");

        String unitSign = "empty";
        if (currentMeasurementType.equalsIgnoreCase(MEASUREMENT_TYPE_METRIC)) {
            unitSign = CELSIUS_CODE_POINT;
        } else {
            unitSign = FAHRENHEIT_CODE_POINT;
        }
        return unitSign;
    }

    protected void findControlsInLayout() {
        Logs.logs(CID,"findControlsInLayout...");

        pbProgressBar = (ProgressBar)findViewById(R.id.pbProgress);
        pbProgressBar.setVisibility(View.VISIBLE);
        txtCityName = (TextView) findViewById(R.id.txtCityName);
        txtTime = (TextView)findViewById(R.id.txtTime);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtCurrentTempValue = (TextView)findViewById(R.id.txtCurrentTempValue);
        txtMinValue = (TextView) findViewById(R.id.txtMinValue);
        txtMaxValue = (TextView) findViewById(R.id.txtMaxValue);
        imgCurrentWeatherIcon = (ImageView) findViewById(R.id.imgCurrentWeatherIcon);

        // forecast TextViews.
        clockHourTextViews = new TextView[NUM_OF_FORECAST_BLOCKS];
        clockHourTextViews[0] = (TextView)findViewById(R.id.txtForecast1);
        clockHourTextViews[1] = (TextView)findViewById(R.id.txtForecast2);
        clockHourTextViews[2] = (TextView)findViewById(R.id.txtForecast3);
        clockHourTextViews[3] = (TextView)findViewById(R.id.txtForecast4);
        clockHourTextViews[4] = (TextView)findViewById(R.id.txtForecast5);
        clockHourTextViews[5] = (TextView)findViewById(R.id.txtForecast6);
        clockHourTextViews[6] = (TextView)findViewById(R.id.txtForecast7);
        clockHourTextViews[7] = (TextView)findViewById(R.id.txtForecast8);

        // forecast ImageViews.
        iconImageViews = new ImageView[NUM_OF_FORECAST_BLOCKS];
        iconImageViews[0] = (ImageView)findViewById(R.id.imgForecast1);
        iconImageViews[1] = (ImageView)findViewById(R.id.imgForecast2);
        iconImageViews[2] = (ImageView)findViewById(R.id.imgForecast3);
        iconImageViews[3] = (ImageView)findViewById(R.id.imgForecast4);
        iconImageViews[4] = (ImageView)findViewById(R.id.imgForecast5);
        iconImageViews[5] = (ImageView)findViewById(R.id.imgForecast6);
        iconImageViews[6] = (ImageView)findViewById(R.id.imgForecast7);
        iconImageViews[7] = (ImageView)findViewById(R.id.imgForecast8);

    }
}





//package com.rach.archexemplar;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.rach.archexemplar.forecastclasses.ForecastRootObject;
//import com.rach.archexemplar.http.IAsyncTaskHelperResponse;
//import com.rach.archexemplar.http.OWMManager;
//import com.rach.archexemplar.utility.ConvertTime;
//import com.rach.archexemplar.utility.Logs;
//import com.rach.archexemplar.weatherclasses.WeatherRootObject;
//
//import java.util.Arrays;
//
//public class MainActivity extends AppCompatActivity implements IAsyncTaskHelperResponse {
//
//    private final String CID = getClass().getSimpleName();
//
//    private ProgressBar pbProgressBar;
//    private TextView txtCityName;
//    private TextView txtTime;
//    private TextView txtDate;
//    private TextView txtCurrentTempValue;
//    private TextView txtMinValue;
//    private TextView txtMaxValue;
//    private ImageView imgCurrentWeatherIcon;
//
//    private final int NUM_OF_FORECAST_BLOCKS = 8;
//    private final String MEASUREMENT_TYPE_METRIC = "Metric";
//    private final String CELSIUS_CODE_POINT = "\u2103";
//    private final String FAHRENHEIT_CODE_POINT = "\u2109";
//    private final String COUNTRY_CODE = ",au";
//
//    private TextView clockHourTextViews[];
//    private ImageView iconImageViews[];
//
//    private double temps[];
//
//    private String currentCity = "Melbourne";
//    private String currentMeasurementType = "metric";
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Logs.logs(CID,"onStart...");
//        findControlsInLayout();
//        startAPIRequests(currentCity+COUNTRY_CODE, currentMeasurementType);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Logs.logs(CID,"onResume...");
//    }
//
//    public void startAPIRequests(String cityName, String measurementType) {
//        Logs.logs(CID,"startAPIRequests...");
//
//        // Get weather.
//        OWMManager getWeather = new OWMManager();
//        getWeather.delegate = this;
//        getWeather.startWeatherTask(cityName,measurementType, pbProgressBar);
//
//        // Get forecast.
//        OWMManager getForecast = new OWMManager();
//        getForecast.delegate = this;
//        getForecast.startForecastTask(cityName,measurementType, pbProgressBar);
//    }
//
//    @Override
//    public void processFinish(WeatherRootObject output) {
//        populateWeatherViews(output);
//    }
//
//    protected void populateWeatherViews(WeatherRootObject rootObject) {
//        Logs.logs(CID,"populateWeatherViews...");
//
//        // Display selected city name.
//        txtCityName.setText(currentCity);
//
//        if (rootObject == null) {
//            Logs.logs(CID,"WeatherRootObject returned NULL");
//            Toast.makeText(this, "Weather data unavailable at this time", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        // Display current date.
//        int currentDateTime = rootObject.getDt();
//        String displayDate = ConvertTime.makeDateStringForCity(currentDateTime, currentCity);
//        txtDate.setText(displayDate);
//
//        // Display current time.
//        txtTime.setText(ConvertTime.getTimeForCity(currentCity));
//        // Display current temperature.
//        String currentTemp = String.format("%.1f", rootObject.getMain().getTemp());
//        txtCurrentTempValue.setText(currentTemp + makeUnitMeasurementSign());
//
//        // Display current weather icon.
//        String currentWeatherIcon = rootObject.getWeather().get(0).getIcon();
//        int iconResId = getResources().getIdentifier("img" + currentWeatherIcon, "drawable", getPackageName());
//        imgCurrentWeatherIcon.setContentDescription(rootObject.getWeather().get(0).getDescription());
//        imgCurrentWeatherIcon.setImageResource(iconResId);
//    }
//
//    @Override
//    public void processFinish(ForecastRootObject output) {
//        populateForecastViews(output);
//    }
//
//    protected void populateForecastViews(ForecastRootObject rootObject) {
//        Logs.logs(CID,"populateForecastViews...");
//
//        if (rootObject == null) {
//            Logs.logs(CID,"ForecastRootObject returned NULL");
//            Toast.makeText(this, "Forecast data unavailable at this time", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        temps = new double[NUM_OF_FORECAST_BLOCKS];
//        for (int i = 0; i < NUM_OF_FORECAST_BLOCKS; i++) {
//
//            com.rach.archexemplar.forecastclasses.List currentListObject = rootObject.getList().get(i);
//
//            temps[i] = currentListObject.getMain().getTemp();
//
//            // Set content description for forecast icon.
//            String contentDescription = currentListObject.getWeather().get(0).getDescription();
//            iconImageViews[i].setContentDescription(contentDescription);
//
//            // Display forecast icon.
//            String jsonIconName = currentListObject.getWeather().get(0).getIcon();
//            int iconResourceName = getResources()
//                    .getIdentifier("img" + jsonIconName, "drawable", getPackageName());
//            iconImageViews[i].setImageResource(iconResourceName);
//
//            // Display forecast block time.
//            String dateTime = ConvertTime.makeClockHourForCity(currentListObject.getDtTxt(), currentCity);
//            clockHourTextViews[i].setText(dateTime);
//        }
//
//        // Sort the temps array to find the min & max temps for the forecast.
//        Arrays.sort(temps);
//        txtMinValue.setText(String.format("%.1f", temps[0]) + makeUnitMeasurementSign());
//        txtMaxValue.setText(String.format("%.1f", temps[7]) + makeUnitMeasurementSign());
//    }
//
//    protected String makeUnitMeasurementSign() {
//        Logs.logs(CID,"makeUnitMeasurementSign...");
//
//        String unitSign = "empty";
//        if (currentMeasurementType.equalsIgnoreCase(MEASUREMENT_TYPE_METRIC)) {
//            unitSign = CELSIUS_CODE_POINT;
//        } else {
//            unitSign = FAHRENHEIT_CODE_POINT;
//        }
//        return unitSign;
//    }
//
//    protected void findControlsInLayout() {
//        Logs.logs(CID,"findControlsInLayout...");
//
//        pbProgressBar = (ProgressBar)findViewById(R.id.pbProgress);
//        pbProgressBar.setVisibility(View.VISIBLE);
//        txtCityName = (TextView) findViewById(R.id.txtCityName);
//        txtTime = (TextView)findViewById(R.id.txtTime);
//        txtDate = (TextView) findViewById(R.id.txtDate);
//        txtCurrentTempValue = (TextView)findViewById(R.id.txtCurrentTempValue);
//        txtMinValue = (TextView) findViewById(R.id.txtMinValue);
//        txtMaxValue = (TextView) findViewById(R.id.txtMaxValue);
//        imgCurrentWeatherIcon = (ImageView) findViewById(R.id.imgCurrentWeatherIcon);
//
//        // forecast TextViews.
//        clockHourTextViews = new TextView[NUM_OF_FORECAST_BLOCKS];
//        clockHourTextViews[0] = (TextView)findViewById(R.id.txtForecast1);
//        clockHourTextViews[1] = (TextView)findViewById(R.id.txtForecast2);
//        clockHourTextViews[2] = (TextView)findViewById(R.id.txtForecast3);
//        clockHourTextViews[3] = (TextView)findViewById(R.id.txtForecast4);
//        clockHourTextViews[4] = (TextView)findViewById(R.id.txtForecast5);
//        clockHourTextViews[5] = (TextView)findViewById(R.id.txtForecast6);
//        clockHourTextViews[6] = (TextView)findViewById(R.id.txtForecast7);
//        clockHourTextViews[7] = (TextView)findViewById(R.id.txtForecast8);
//
//        // forecast ImageViews.
//        iconImageViews = new ImageView[NUM_OF_FORECAST_BLOCKS];
//        iconImageViews[0] = (ImageView)findViewById(R.id.imgForecast1);
//        iconImageViews[1] = (ImageView)findViewById(R.id.imgForecast2);
//        iconImageViews[2] = (ImageView)findViewById(R.id.imgForecast3);
//        iconImageViews[3] = (ImageView)findViewById(R.id.imgForecast4);
//        iconImageViews[4] = (ImageView)findViewById(R.id.imgForecast5);
//        iconImageViews[5] = (ImageView)findViewById(R.id.imgForecast6);
//        iconImageViews[6] = (ImageView)findViewById(R.id.imgForecast7);
//        iconImageViews[7] = (ImageView)findViewById(R.id.imgForecast8);
//
//    }
//}
