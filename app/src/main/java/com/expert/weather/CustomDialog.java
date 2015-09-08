package com.expert.weather;

/**
 * Created by krishnakumar on 04-09-2015.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Android on 21-04-2015.
 */
public class CustomDialog extends Dialog {
    CustomDialogInterface customDialogInterface;
    public Activity ctx;
    public Dialog d;
    ToggleButton toggle;
    public TextView  bottomButton;
    CustomAdapter adapter;
    ArrayList<String> PLACES;
    PLACES obj;
    DBAdapter db;
    ArrayList placeList = new ArrayList<>();
    ListView listView;
    AutoCompleteTextView atvPlaces;
    PlacesTask placesTask;
    ParserTask parserTask;

    public CustomDialog(Activity a,int theme) {
        super(a,theme);


        // TODO Auto-generated constructor stub
        this.ctx = a;
    }


    public void setResponse(CustomDialogInterface customDialogInterface){
        this.customDialogInterface=customDialogInterface;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.adcity2);
        db = new DBAdapter(ctx);

        listView = (ListView)findViewById(R.id.listView);

        atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
        atvPlaces.setThreshold(1);

        atvPlaces.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                placesTask = new PlacesTask();
                placesTask.execute(s.toString());
            }
        });


        atvPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                Log.e("### Val", adapterView.getAdapter().getItem(i).toString());
                String value = adapterView.getAdapter().getItem(i).toString();
                String tempValue = value;
                db.open();
                db.insertRecord(tempValue.substring(value.lastIndexOf("=") + 1, value.length() - 1));
                db.close();

                getOldValues();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               /* Intent ii =  new Intent(ctx,MainActivity.class);

                Log.e("### Val", adapterView.getAdapter().getItem(i).toString());
                String value = adapterView.getAdapter().getItem(i).toString();

                ii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ii.putExtra("place",""+value);
                startActivity(ii);
                finish();*/
            }
        });


        getOldValues();

    }



    private void getOldValues(){
        placeList = new ArrayList<>();

        try {
            db.open();
            Cursor c = db.getALLLIST();
            if (c.moveToFirst()) {
                do {
                    FetchData(c);
                } while (c.moveToNext());
            }
            db.close();


            adapter  = new CustomAdapter(ctx, placeList);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("### Exc", e.toString());
        }



    }


    private void FetchData(Cursor c) {

        String placeNAme = c.getString(1);
        placeList.add(placeNAme);
    }


    private class CustomAdapter extends BaseAdapter {

        private Context _ctx;
        ArrayList<String> values;

        public CustomAdapter(Context ctx,ArrayList<String> place) {
            _ctx = ctx;
            values = place;
        }

        @Override
        public int getCount() {
            return values.size();
        }

        @Override
        public Object getItem(int i) {
            return values.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            LayoutInflater mInflater = (LayoutInflater)
                    _ctx.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View convertView = mInflater.inflate(R.layout.row_item, null);
            TextView txtCityName = (TextView)convertView. findViewById(R.id.txtCityName);
            ImageView imgRemove = (ImageView)convertView. findViewById(R.id.imgRemove);

            txtCityName.setText(values.get(i));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialogInterface.okButton(values.get(i));
                }
            });


            imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialogInterface.removeButton(i);

                    db.open();
                    db.deleteRecord(values.get(i));
                    db.close();

                    getOldValues1();
                }
            });

            return convertView;
        }



        private void getOldValues1(){
            placeList = new ArrayList<>();

            try {
                db.open();
                Cursor c = db.getALLLIST();
                if (c.moveToFirst()) {
                    do {
                        FetchData(c);
                    } while (c.moveToNext());
                }
                db.close();


                adapter  = new CustomAdapter(ctx, placeList);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.e("### Exc", e.toString());
            }



        }

    }


    public interface CustomDialogInterface {


        public void okButton(String place);

        public void removeButton(int pos);



    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.e("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=AIzaSyARN_U5tKb6KzyobijLgtl4SO2L3vr1Xwo";

            String input="";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            // place type to be searched
            String types = "types=geocode";

            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = input+"&"+types+"&"+sensor+"&"+key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;

            try{
                // Fetching the data from we service
                data = downloadUrl(url);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            //Log.e("#### API Result", result);

            // Creating ParserTask
            parserTask = new ParserTask();

            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
        }
    }
    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[] { "description"};
            int[] to = new int[] { android.R.id.text1 };

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(ctx, result, R.layout.adapter_item, from, to);

            // Setting the adapter
            atvPlaces.setAdapter(adapter);
        }
    }

    //end of main class
}