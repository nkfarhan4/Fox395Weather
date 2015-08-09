package com.expert.weather;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Node;

import zh.wang.android.apis.yweathergetter4a.WeatherInfo;
import zh.wang.android.apis.yweathergetter4a.YahooWeather;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherExceptionListener;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherInfoListener;


public class MainActivity extends ActionBarActivity implements YahooWeatherInfoListener,
        YahooWeatherExceptionListener {
    private ImageView mIvWeather0;
    private TextView mTvWeather0,txtWeather,mainTitle,txtTemp,txtWind,txtWindDirection,txtWindSpeed,txtVisibility,txtHumidty;
    private EditText mEtAreaOfCity;

    private Button mBtGPS;
    private LinearLayout mWeatherInfosLayout;
    private Toolbar toolbar;

    private YahooWeather mYahooWeather = YahooWeather.getInstance(5000, 5000, true);


    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Weather Expert");
//            toolbar.setLogo(R.drawable.logo);
            setSupportActionBar(toolbar);
        }
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);

        mYahooWeather.setExceptionListener(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Fetching Weather data... ");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
       // mProgressDialog.show();

        mIvWeather0 = (ImageView) findViewById(R.id.imageview_weather_info_0);

        mEtAreaOfCity = (EditText) findViewById(R.id.edittext_area);

        txtWeather =  (TextView) findViewById(R.id.txtWeather);
        mainTitle =  (TextView) findViewById(R.id.mainTitle);
        txtTemp =  (TextView) findViewById(R.id.txtTemp);
        txtWind =  (TextView) findViewById(R.id.txtWind);
        txtWindDirection=  (TextView) findViewById(R.id.txtWindDirection);
        txtWindSpeed=  (TextView) findViewById(R.id.txtWindSpeed);
        txtVisibility=  (TextView) findViewById(R.id.txtVisibility);
        txtHumidty=  (TextView) findViewById(R.id.txtHumidty);


        mEtAreaOfCity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mEtAreaOfCity.getRight() - mEtAreaOfCity.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        if (mEtAreaOfCity.getText().toString().trim().length() == 0) {
                            Toast.makeText(MainActivity.this, "Please Enter any word for search !!!", Toast.LENGTH_SHORT).show();
                        } else {
                            String _location = mEtAreaOfCity.getText().toString();
                            searchByPlaceName(_location);
                            showProgressDialog();
                        }

                        return true;
                    }
                }

                return false;
            }
        });




        mBtGPS = (Button) findViewById(R.id.gps_button);
        mBtGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByGPS();
                showProgressDialog();
            }
        });


        mWeatherInfosLayout = (LinearLayout) findViewById(R.id.weather_infos);

        String _location = mEtAreaOfCity.getText().toString();
        searchByPlaceName(_location);
        showProgressDialog();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        hideProgressDialog();
        mProgressDialog = null;
        super.onDestroy();
    }


    @Override
    public void gotWeatherInfo(WeatherInfo weatherInfo) {
        // TODO Auto-generated method stub
        hideProgressDialog();
        if (weatherInfo != null) {
            setNormalLayout();
            if (mYahooWeather.getSearchMode() == YahooWeather.SEARCH_MODE.GPS) {
                mEtAreaOfCity.setText("YOUR CURRENT LOCATION");
            }
            mWeatherInfosLayout.removeAllViews();







            txtWeather.setText(weatherInfo.getCurrentText());
            mainTitle.setText(mEtAreaOfCity.getText().toString().trim());
            txtTemp.setText("+" + weatherInfo.getCurrentTemp() + "\u00b0");

            txtWind.setText("Wind speed: " + weatherInfo.getWindSpeed());


            txtWindDirection.setText(weatherInfo.getCurrentConditionDate() );
            txtWindSpeed.setText("Wind direction: " + weatherInfo.getWindDirection());
            txtVisibility.setText("Visibility: " + weatherInfo.getAtmosphereVisibility());
            txtHumidty.setText("Humidity: " + weatherInfo.getAtmosphereHumidity());
           /* mTvWeather0.setText("====== CURRENT ======" + "\n" +
                            "date: " + weatherInfo.getCurrentConditionDate() + "\n" +
                            "weather: " + weatherInfo.getCurrentText() + "\n" +
                            "temperature in �C: " + weatherInfo.getCurrentTemp() + "\n" +
                            "wind chill: " + weatherInfo.getWindChill() + "\n" +
                            "wind direction: " + weatherInfo.getWindDirection() + "\n" +
                            "wind speed: " + weatherInfo.getWindSpeed() + "\n" +
                            "Humidity: " + weatherInfo.getAtmosphereHumidity() + "\n" +
                            "Pressure: " + weatherInfo.getAtmospherePressure() + "\n" +
                            "Visibility: " + weatherInfo.getAtmosphereVisibility()
            );*/
            if (weatherInfo.getCurrentConditionIcon() != null) {
                mIvWeather0.setImageBitmap(weatherInfo.getCurrentConditionIcon());
            }
            for (int i = 0; i < YahooWeather.FORECAST_INFO_MAX_SIZE; i++) {
                final LinearLayout forecastInfoLayout = (LinearLayout)
                        getLayoutInflater().inflate(R.layout.forecastinfo2, null);


                final TextView txtDate = (TextView) forecastInfoLayout.findViewById(R.id.txtDate);
                final TextView txtMonth = (TextView) forecastInfoLayout.findViewById(R.id.txtMonth);
                final TextView txtTemp1 = (TextView) forecastInfoLayout.findViewById(R.id.txtTemp1);
                final TextView txtTemp2 = (TextView) forecastInfoLayout.findViewById(R.id.txtTemp2);


                final WeatherInfo.ForecastInfo forecastInfo = weatherInfo.getForecastInfoList().get(i);


                String date = forecastInfo.getForecastDay();
                //String month = forecastInfo.getForecast();
                String temp1 = "+"+forecastInfo.getForecastTempHigh();
                String temp2 = "+"+forecastInfo.getForecastTempLow();

                txtDate.setText(date);
                txtTemp1.setText(temp1+"\u00b0");
                txtTemp2.setText(temp2+"\u00b0");




             /*   tvWeather.setText("====== FORECAST " + (i+1) + " ======" + "\n" +
                                "date: " + forecastInfo.getForecastDate() + "\n" +
                                "weather: " + forecastInfo.getForecastText() + "\n" +
                                "low  temperature in �C: " + forecastInfo.getForecastTempLow() + "\n" +
                                "high temperature in �C: " + forecastInfo.getForecastTempHigh() + "\n"
//						           "low  temperature in �F: " + forecastInfo.getForecastTempLowF() + "\n" +
//				                   "high temperature in �F: " + forecastInfo.getForecastTempHighF() + "\n"
                );*/
                final ImageView ivForecast = (ImageView) forecastInfoLayout.findViewById(R.id.imageview_forecast_info);
                if (forecastInfo.getForecastConditionIcon() != null) {
                    ivForecast.setImageBitmap(forecastInfo.getForecastConditionIcon());



                    String number = ""+forecastInfo.getForecastCode();
                    setImage(ivForecast,number);

                }
                mWeatherInfosLayout.addView(forecastInfoLayout);
            }
        } else {
            setNoResultLayout();
        }
    }


    private void setImage(ImageView img_weather,String no) {
        int ii = Integer.parseInt(no);
        switch (ii) {
            case 0 :
                img_weather.setImageResource(R.drawable.a0);
                break;
            case 1:
                img_weather.setImageResource(R.drawable.a2);
                break;
            case 2:
                img_weather.setImageResource(R.drawable.a2);
                break;
            case 3:
                img_weather.setImageResource(R.drawable.a2);
                break;
            case 4:
                img_weather.setImageResource(R.drawable.a2);
                break;
            case 5:
                img_weather.setImageResource(R.drawable.a5);
                break;
            case 6:
                img_weather.setImageResource(R.drawable.a5);
                break;
            case 7:
                img_weather.setImageResource(R.drawable.a5);
                break;
            case 8:
                img_weather.setImageResource(R.drawable.a8);
                break;
            case 9:
                img_weather.setImageResource(R.drawable.a9);
                break;
            case 10:
                img_weather.setImageResource(R.drawable.a9);
                break;
            case 11:
                img_weather.setImageResource(R.drawable.a8);
                break;
            case 12:
                img_weather.setImageResource(R.drawable.a8);
                break;
            case 13:
                img_weather.setImageResource(R.drawable.a13);
                break;
            case 14:
                img_weather.setImageResource(R.drawable.a13);
                break;
            case 15:
                img_weather.setImageResource(R.drawable.a13);
                break;
            case 16:
                img_weather.setImageResource(R.drawable.a13);
                break;
            case 17:
                img_weather.setImageResource(R.drawable.a19);
                break;
            case 18:
                img_weather.setImageResource(R.drawable.a19);
                break;
            case 19:
                img_weather.setImageResource(R.drawable.a19);
                break;
            case 20:
                img_weather.setImageResource(R.drawable.a19);
                break;
            case 21:
                img_weather.setImageResource(R.drawable.a19);
                break;
            case 22:
                img_weather.setImageResource(R.drawable.a19);
                break;
            case 23:
                img_weather.setImageResource(R.drawable.a19);
                break;
            case 24:
                img_weather.setImageResource(R.drawable.a24);
                break;
            case 25:
                img_weather.setImageResource(R.drawable.a25);
                break;
            case 26:
                img_weather.setImageResource(R.drawable.a26);
                break;
            case 27:
                img_weather.setImageResource(R.drawable.a27);
                break;
            case 28:
                img_weather.setImageResource(R.drawable.a28);
                break;
            case 29:
                img_weather.setImageResource(R.drawable.a29);
                break;
            case 30:
                img_weather.setImageResource(R.drawable.a30);
                break;
            case 31:
                img_weather.setImageResource(R.drawable.a31);
                break;
            case 32:
                img_weather.setImageResource(R.drawable.a32);
                break;
            case 33:
                img_weather.setImageResource(R.drawable.a33);
                break;
            case 34:
                img_weather.setImageResource(R.drawable.a34);
                break;
            case 35:
                img_weather.setImageResource(R.drawable.a35);
                break;
            case 36:
                img_weather.setImageResource(R.drawable.a36);
                break;
            case 37:
                img_weather.setImageResource(R.drawable.a2);
                break;
            case 38:
                img_weather.setImageResource(R.drawable.a2);
                break;
            case 39:
                img_weather.setImageResource(R.drawable.a2);
                break;
            case 40:
                img_weather.setImageResource(R.drawable.a2);
                break;
            case 41:
                img_weather.setImageResource(R.drawable.a41);
                break;
            case 42:
                img_weather.setImageResource(R.drawable.a41);
                break;
            case 43:
                img_weather.setImageResource(R.drawable.a41);
                break;
            case 44:
                img_weather.setImageResource(R.drawable.a44);
                break;
            case 45:
                img_weather.setImageResource(R.drawable.a45);
                break;
            case 46:
                img_weather.setImageResource(R.drawable.a46);
                break;
            case 47:
                img_weather.setImageResource(R.drawable.a46);
                break;
            case 3200:
                img_weather.setImageResource(R.drawable.a3200);
                break;

            default:
                break;
        }
    }


    @Override
    public void onFailConnection(final Exception e) {
        // TODO Auto-generated method stub
        setNoResultLayout();
        Toast.makeText(getApplicationContext(), "Fail Connection", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onFailParsing(final Exception e) {
        // TODO Auto-generated method stub
        setNoResultLayout();
        Toast.makeText(getApplicationContext(), "Fail Parsing", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onFailFindLocation(final Exception e) {
        // TODO Auto-generated method stub
        setNoResultLayout();
        Toast.makeText(getApplicationContext(), "Fail Find Location", Toast.LENGTH_SHORT).show();
    }


    private void setNormalLayout() {
        mWeatherInfosLayout.setVisibility(View.VISIBLE);

    }

    private void setNoResultLayout() {


        Toast.makeText(MainActivity.this,"No Data Found !!!",Toast.LENGTH_SHORT).show();
        mWeatherInfosLayout.setVisibility(View.INVISIBLE);
        mProgressDialog.cancel();
    }

    private void searchByGPS() {
        mYahooWeather.setNeedDownloadIcons(true);
        mYahooWeather.setUnit(YahooWeather.UNIT.CELSIUS);
        mYahooWeather.setSearchMode(YahooWeather.SEARCH_MODE.GPS);
        mYahooWeather.queryYahooWeatherByGPS(getApplicationContext(), this);
    }

    private void searchByPlaceName(String location) {
        mYahooWeather.setNeedDownloadIcons(true);
        mYahooWeather.setUnit(YahooWeather.UNIT.CELSIUS);
        mYahooWeather.setSearchMode(YahooWeather.SEARCH_MODE.PLACE_NAME);
        mYahooWeather.queryYahooWeatherByPlaceName(getApplicationContext(), location, MainActivity.this);
    }

    private void showProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
        mProgressDialog = new ProgressDialog(MainActivity.this);

        mProgressDialog.setMessage("Fetching Weather data... ");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    //end of main class
}