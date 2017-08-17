package com.pachirrys.nasaproyect;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    //SDK variables
    private  CollapsingToolbarLayout toolbarLayout ;
    private ProgressDialog progressDialog;
    private Calendar calendar;
    private TextView descTxt;
    private ImageView imageView;

    //general variables
    private String  url         = "";
    private String  patternDate = "";
    private int     year ;
    private int     month;
    private int     day;
    private int widthImageView ;
    private int heightImageView;
    /*+--------------------------------------------------------+
      | get you api_key in  https://api.nasa.gov/api.html#apod |
      |  and replace it in APY_KEY value.                      |
      +--------------------------------------------------------+
     */
    //Constants
    final String APY_KEY = "YOUR KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        //get ToolbarLayout form xml
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle("");
        //parsing views from xml
        descTxt         = (TextView) findViewById(R.id.description);
        imageView       = (ImageView)findViewById(R.id.imageView);

        //get current date
        calendar        = Calendar.getInstance();
        year            = calendar.get(Calendar.YEAR);
        month           = calendar.get(Calendar.MONTH);
        day             = calendar.get(Calendar.DAY_OF_MONTH);

        //start progress bar
        progressDialog  = ProgressDialog.show(this,"Please wait..","Searching Image..",true,false);

        //set URL query to the Api
        patternDate     = "" + year + "-" + (month+1 )+ "-" + day;
        url             = "https://api.nasa.gov/planetary/apod?date="+patternDate+"&api_key="+APY_KEY;
        apiRequest(url);



        //set Button floating
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this,
                        new DateListener(), year, month, day);
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
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

    private void apiRequest(String url){
        /*
        * Response Jason from Api Nasa
        *
        * {
        *   "copyright" : owner of the picture,
        *   "date":      yyyy-mm-dd  date of the picture,
        *   "explanation": description of the picture,
        *   "hdurl":  url image HD of the picture,
        *   "media_type" : type of resource,
        *   "service_version" : unknown what is that,
        *   "title": title of the picture,
        *   "url": url of the picture
        * }
        * */
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String title = "Name :" + response.getString("title")
                                                    +"\nDate  : "+ response.getString("date")
                                                    +"\n\n";
                            descTxt.setText(title + "Description: " + response.getString("explanation"));

                             widthImageView = imageView.getWidth();
                             heightImageView = imageView.getHeight();

                            Picasso.with(getApplicationContext()).load(response.getString("url"))
                                    .resize(widthImageView,heightImageView)
                                    .centerCrop()
                                    .error(R.drawable.ic_nasa_logo)
                                    .into(imageView);
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Picasso.with(getApplicationContext()).load(R.drawable.ic_nasa_logo)
                                .into(imageView);
                        descTxt.setText("Resource not found");
                        progressDialog.dismiss();

                        if (  ! isOnline() ){
                            Snackbar.make(findViewById(android.R.id.content),"No Connection", Snackbar.LENGTH_LONG)
                                .show();
                        }

                    }
                });

        // Access the RequestQueue through your singleton class.
        SingletonVolley.getInstance(this).addToRequestQueue(jsObjRequest);
    }//end apiRequest method

    public boolean isOnline(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        return isWifiConn;
    }
    //inner Class
    class DateListener implements DatePickerDialog.OnDateSetListener{

        /*
        * params:
        * y : year selected
        * m : month selected
        * d : day selected
        * */
        @Override
        public void onDateSet(DatePicker datePicker, int y, int m, int d) {
            //start progress bar
            progressDialog  = ProgressDialog.show(MainActivity.this,"Please wait..","Searching Image..",true,false);
            patternDate = "" + y + "-" + (m+1) + "-" + d;
            url = "https://api.nasa.gov/planetary/apod?date="+patternDate+"&api_key="+APY_KEY;
            apiRequest(url);

        }
    }//end DateListener class
}
