package com.expert.weather;

import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Node;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import zh.wang.android.apis.yweathergetter4a.WeatherInfo;
import zh.wang.android.apis.yweathergetter4a.YahooWeather;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherExceptionListener;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherInfoListener;


public class MainActivity extends ActionBarActivity implements YahooWeatherInfoListener,
        YahooWeatherExceptionListener {

    private ImageView mIvWeather0,ic_action_settings;
    private TextView txtTime,mTvWeather0, txtWeather, mainTitle, txtTemp, txtWind, txtWindDirection, txtWindSpeed, txtVisibility, txtHumidty;
    private EditText mEtAreaOfCity;
    String LAT, LONG;
    private Button mBtGPS;
    private LinearLayout mWeatherInfosLayout;
    private Toolbar toolbar;
    int flagTemp = 0;
    private YahooWeather mYahooWeather = YahooWeather.getInstance(5000, 5000, true);
    InterstitialAd interstitial;
    AdRequest adRequest;
    String CITYNAME;
    private ProgressDialog mProgressDialog;
    Location nwLocation;
    TextView txtTempMain;
    ToggleButton toggle;
    DBAdapter db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBAdapter(MainActivity.this);

        //   \u2109 -- for degree F
        // \u2103 -- for degree C

/*
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Weather Expert");
//            toolbar.setLogo(R.drawable.logo);
            setSupportActionBar(toolbar);
        }
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);*/


        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/akadora.ttf");


        TextView txtTitle = (TextView)findViewById(R.id.txtTitle);
        txtTitle.setTypeface(tf,Typeface.BOLD);

        ic_action_settings= (ImageView) findViewById(R.id.imgSettings);
        txtTime= (TextView) findViewById(R.id.txtTime);
        setDateandTime();
        txtTempMain = (TextView) findViewById(R.id.txtTempMain);
        ImageView ic_location = (ImageView) findViewById(R.id.ic_location);

        ic_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkGPS();
            }
        });

        ImageView imgSettings = (ImageView) findViewById(R.id.imgSettings);
        imgSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               final CustomDialog dialog = new CustomDialog(MainActivity.this,0);
                dialog.show();
                dialog.setResponse(new CustomDialog.CustomDialogInterface() {
                    @Override
                    public void okButton(String place) {
                        dialog.dismiss();
                        mEtAreaOfCity.setText(place);
                                String _location = place;
                        searchByPlaceName(_location);
                        showProgressDialog();
                    }

                    @Override
                    public void removeButton(int pos) {

                    }
                });


               /* Intent i = new Intent(MainActivity.this, AdLocation.class);
                startActivity(i);*/
            }
        });


 txtTempMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 0 for C
                // 1 for F


                if (flagTemp == 0) {
                    txtTempMain.setText("\u2109");
                    flagTemp = 1;
                    String _location = mEtAreaOfCity.getText().toString();
                    searchByPlaceName(_location);
                    showProgressDialog();
                } else {
                    txtTempMain.setText("\u2103");
                    flagTemp = 0;
                    String _location = mEtAreaOfCity.getText().toString();
                    searchByPlaceName(_location);
                    showProgressDialog();
                }
            }
        });

        mYahooWeather.setExceptionListener(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Fetching Weather data... ");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // mProgressDialog.show();

        mIvWeather0 = (ImageView) findViewById(R.id.imageview_weather_info_0);

        mEtAreaOfCity = (EditText) findViewById(R.id.edittext_area);

        txtWeather = (TextView) findViewById(R.id.txtWeather);
        mainTitle = (TextView) findViewById(R.id.mainTitle);
        txtTemp = (TextView) findViewById(R.id.txtTemp);
        txtWind = (TextView) findViewById(R.id.txtWind);
        txtWindDirection = (TextView) findViewById(R.id.txtWindDirection);
        txtWindSpeed = (TextView) findViewById(R.id.txtWindSpeed);
        txtVisibility = (TextView) findViewById(R.id.txtVisibility);
        txtHumidty = (TextView) findViewById(R.id.txtHumidty);


        interstitial = new InterstitialAd(MainActivity.this);
        interstitial.setAdUnitId("ca-app-pub-1878227272753934/8361723600");


        AdView adView = (AdView) this.findViewById(R.id.adView);
        // Request for Ads
        adRequest = new AdRequest.Builder()
                .build();

        // Load ads into Banner Ads
        adView.loadAd(adRequest);


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


        mWeatherInfosLayout = (LinearLayout) findViewById(R.id.weather_infos);

        getCellTowerInfo();


    }

    private void setDateandTime(){


        Thread t = new Thread() {


            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy hh:mm a");

                                String formattedDate = df.format(c.getTime());


                                txtTime.setText(formattedDate);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };


        t.start();




    }


    private void checkGPS() {


        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }


        if (!gps_enabled) {
            // notify user
            final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setMessage("GPS Network Not Enabled");
            dialog.setPositiveButton(this.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                }
            });
            dialog.show();
        } else {
            Log.e("ELSE", "ELSE");


            callLocation();
            //new CallLocation(getActivity()).execute();
        }
    }

    private void callLocation() {

        GPSTracker gps = new GPSTracker(this);

        if (gps.canGetLocation()) { // gps enabled} // return boolean true/false

            try {
                LAT = "" + gps.getLatitude();
                LONG = "" + gps.getLongitude();

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(gps.getLatitude(), gps.getLongitude(), 1);

                String cityName = addresses.get(0).getAddressLine(0);
                String stateName = addresses.get(0).getAddressLine(1);
                String countryName = addresses.get(0).getAddressLine(2);

                Log.e("NEW LAT", gps.getLatitude() + "");
                Log.e("NEW LONG", gps.getLongitude() + "");

                Log.e("cityName", cityName + "");
                Log.e("stateName", stateName + "");
                Log.e("countryName", countryName + "");

                mEtAreaOfCity.setText(cityName);
                searchByGPS();
                showProgressDialog();

            } catch (Exception e) {

            }


        }


    }

    private boolean checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            return false;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkInternet()) {
            try {
                String val = getIntent().getStringExtra("place");
                if (val.length() != 0) {
                    mEtAreaOfCity.setText(val);
                    String _location = mEtAreaOfCity.getText().toString();
                    searchByPlaceName(_location);
                    showProgressDialog();

                }
            } catch (Exception e) {
                Log.e("#### EXc", e.toString());
            }
        } else        {
            Toast.makeText(MainActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();

            final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setMessage("No Internet Connection !!! Do you want to enable your internet?");
            dialog.setPositiveButton(this.getResources().getString(R.string.sett), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    finish();
                }
            });
            dialog.show();
        }
    }


    public void getCellTowerInfo() {

        AppLocationService appLocationService = new AppLocationService(MainActivity.this);


        nwLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);


        if (nwLocation != null) {
            double latitude = nwLocation.getLatitude();
            double longitude = nwLocation.getLongitude();
            String provoide = nwLocation.getProvider();


            try {
                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);


                String cityName = addresses.get(0).getLocality();
                String countryName = addresses.get(0).getCountryName();


                CITYNAME = cityName;


               /* Toast.makeText(
                        getApplicationContext(),
                        "Mobile Location (NW): \nLatitude: " + latitude
                                + "\nLongitude: " + longitude + "\ncityName:" + cityName + "\ncountryName:" + countryName,
                        Toast.LENGTH_LONG).show();
*/


                      /*  Toast.makeText(
                                getApplicationContext(),
                                "Your Current city is :- "+CITYNAME,
                                Toast.LENGTH_LONG).show();
*/

                mEtAreaOfCity.setText(CITYNAME);


            } catch (Exception e) {
                mEtAreaOfCity.setText("Delhi");
                CITYNAME = "Delhi";
                Log.e("exc in taking n/w", e.toString());
            }


            String _location = CITYNAME;
            searchByPlaceName(_location);
            showProgressDialog();

        } else {
            mEtAreaOfCity.setText("Delhi");
            CITYNAME = "Delhi";
            String _location = CITYNAME;
            searchByPlaceName(_location);
            showProgressDialog();
            //Toast.makeText(MainActivity.this,"Network error !!!",Toast.LENGTH_SHORT).show();
        }


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


         /*   if (mYahooWeather.getSearchMode() == YahooWeather.SEARCH_MODE.GPS) {
                mEtAreaOfCity.setText("YOUR CURRENT LOCATION");
            }
*/
            mWeatherInfosLayout.removeAllViews();


            txtWeather.setText(weatherInfo.getCurrentText());
            mainTitle.setText(mEtAreaOfCity.getText().toString().trim());

            if (flagTemp == 1) {
                txtTemp.setText("+" + C_To_F(weatherInfo.getCurrentTemp()) + "\u2109");
            } else {
                txtTemp.setText("+" + weatherInfo.getCurrentTemp() + "\u2103");
            }

            txtWind.setText("" + weatherInfo.getWindSpeed()+" km/h");


            txtWindDirection.setText(weatherInfo.getCurrentConditionDate());
            txtWindSpeed.setText("" + weatherInfo.getWindDirection());
            txtVisibility.setText("" + weatherInfo.getAtmosphereVisibility()+" km/h");
            txtHumidty.setText("" + weatherInfo.getAtmosphereHumidity()+" %");
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
                String number = "" + weatherInfo.getCurrentCode();
                setImage(mIvWeather0, number);
                Log.e("###NUM", "" + number);
                //mIvWeather0.setImageBitmap(weatherInfo.getCurrentConditionIcon());
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
                String temp1 = "+" + forecastInfo.getForecastTempHigh();
                String temp2 = "+" + forecastInfo.getForecastTempLow();

                txtDate.setText(date);



                if (flagTemp == 1) {
                    txtTemp1.setText("+" + C_To_F(forecastInfo.getForecastTempHigh()) + "\u2109");
                    txtTemp2.setText("+" + C_To_F(forecastInfo.getForecastTempLow()) + "\u2109");

                } else {
                    txtTemp1.setText(temp1 + "\u2103");
                    txtTemp2.setText(temp2 + "\u2103");

                }




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


                    String number = "" + forecastInfo.getForecastCode();
                    setImage(ivForecast, number);

                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.weight = 1.0f;
                params.gravity = Gravity.CENTER;

                mWeatherInfosLayout.setLayoutParams(params);

                mWeatherInfosLayout.addView(forecastInfoLayout);

            }

            db.open();

           /* String col1Tile,col1TempCode,col1HighTemp,col1LowTemp;
            for(int i=0;i<mWeatherInfosLayout.getChildCount();i++) {

                final LinearLayout forecastInfoLayout = (LinearLayout)mWeatherInfosLayout.getChildAt(i);

                final TextView txtDate = (TextView) forecastInfoLayout.findViewById(R.id.txtDate);
                final ImageView ivForecast = (ImageView) forecastInfoLayout.findViewById(R.id.imageview_forecast_info);
                final TextView txtTemp1 = (TextView) forecastInfoLayout.findViewById(R.id.txtTemp1);
                final TextView txtTemp2 = (TextView) forecastInfoLayout.findViewById(R.id.txtTemp2);


                if(i==0){
                    col1Tile = txtDate.getText().toString();
                    col1TempCode = txtDate.getText().toString();
                    col1HighTemp = txtDate.getText().toString();
                    col1HighTemp = txtDate.getText().toString();
                }

                db.insertOLDDATARecord(mEtAreaOfCity.getText().toString().trim(), txtTemp.getText().toString(), weatherInfo.getCurrentConditionIcon(),
                        weatherInfo.getCurrentConditionDate(), weatherInfo.getWindSpeed(), weatherInfo.getWindDirection(), weatherInfo.getAtmosphereVisibility(), weatherInfo.getAtmosphereHumidity(),
                        txtDate.getText(), forecastInfo.getForecastConditionIcon(), txtTemp1.getText(), txtTemp2.getText());
            }
            db.close();*/


            //end of for loop

        } else {

            setNoResultLayout();
        }
    }


    private int C_To_F(int celsius) {
        Log.e("### Val C", "" + celsius);
        double fahrenheit = (celsius * 9 / 5.0) + 32;
        int val = (int) Math.round(fahrenheit);

        Log.e("### converteed C", "" + val);
        return val;
    }




    private void setImage(ImageView img_weather, String no) {
        int ii = Integer.parseInt(no);
        switch (ii) {
            case 0:
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

        try {
            Toast.makeText(MainActivity.this, "No Data Found !!!", Toast.LENGTH_SHORT).show();
            mWeatherInfosLayout.setVisibility(View.INVISIBLE);
            mProgressDialog.cancel();
        } catch (Exception e) {

        }
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