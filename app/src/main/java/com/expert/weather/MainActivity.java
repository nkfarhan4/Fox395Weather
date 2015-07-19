package com.expert.weather;

import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    private Toolbar toolbar;
    TextView toolbarTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface ttf = Typeface.createFromAsset(getAssets(), "fonts/silent_reaction.ttf");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbarTitle);


        if (toolbar != null) {

            toolbarTitle.setTypeface(ttf);
            toolbarTitle.setText("Weather Eexpert");
//            toolbar.setLogo(R.drawable.logo);
            toolbar.setTitle("");

            setSupportActionBar(toolbar);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
