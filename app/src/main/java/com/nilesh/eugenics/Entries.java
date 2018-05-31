package com.nilesh.eugenics;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;


public class Entries extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> DataList;


    private static final String LOG_TAG ="error";
    TextView noEQ;
    String userid,name,headquater;
    String url_all_data = "http://www.eugenicspharma.in/reporting_app/json.php?username=";
    ListView lv;
    View parentLayout;
    int no_of_entries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entries);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lv=(ListView) findViewById(R.id.lv);
        parentLayout = findViewById(android.R.id.content);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                download();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(Entries.this);
                builder.setMessage("Do you want to download Excel file?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(3).setChecked(true);
        noEQ=(TextView) findViewById(R.id.noEQ);
        if(isInternet()) {
            new LoadAllEntries().execute();
        }
        else{
            noEQ.setText("No Internet Connection");

        }

            SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
            userid = sp.getString("username", "null");
            name = sp.getString("name", null);
            headquater= sp.getString("headquater", null);
            url_all_data = url_all_data + userid;
            //Log.d("username: ", userid);
            DataList = new ArrayList<>();
            Log.d("url: ", url_all_data);
            View hView =  navigationView.getHeaderView(0);
            TextView nav_user = (TextView)hView.findViewById(R.id.textView);
            TextView nav_headquater = (TextView)hView.findViewById(R.id.textView2);

            nav_user.setText("Current User: "+name);/////////////////////////////////////
            nav_headquater.setText("Headquater: "+headquater);



    }


    @Override
    public void onResume(){
        super.onResume();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(3).setChecked(true);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.entries, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(DataList!=null) {
            //noinspection SimplifiableIfStatement
            if (id == R.id.actionSortA_Z) {
                Collections.sort(DataList, new sortA_Z());
                ListAdapter adapter = new SimpleAdapter(Entries.this, DataList, R.layout.custom_row, new String[]{"docname", "place", "subdate", "letter", "time"}, new int[]{R.id.boldPlace, R.id.placeTxt, R.id.dateTxt, R.id.magTxt, R.id.timeTxt});
                // updating listview
                lv.setAdapter(adapter);

            } else if (id == R.id.actionSortZ_A) {
                Collections.sort(DataList, new sortZ_A());
                ListAdapter adapter = new SimpleAdapter(Entries.this, DataList, R.layout.custom_row, new String[]{"docname", "place", "subdate", "letter", "time"}, new int[]{R.id.boldPlace, R.id.placeTxt, R.id.dateTxt, R.id.magTxt, R.id.timeTxt});
                // updating listview
                lv.setAdapter(adapter);

            } else if (id == R.id.action_SortByMonth) {
                Collections.sort(DataList, new sortByMonth());
                ListAdapter adapter = new SimpleAdapter(Entries.this, DataList, R.layout.custom_row, new String[]{"docname", "place", "subdate", "letter", "time"}, new int[]{R.id.boldPlace, R.id.placeTxt, R.id.dateTxt, R.id.magTxt, R.id.timeTxt});
                // updating listview
                lv.setAdapter(adapter);

            }  else if (id == R.id.action_SortByYear) {
                Collections.sort(DataList, new sortByYear());
                ListAdapter adapter = new SimpleAdapter(Entries.this, DataList, R.layout.custom_row, new String[]{"docname", "place", "subdate", "letter", "time"}, new int[]{R.id.boldPlace, R.id.placeTxt, R.id.dateTxt, R.id.magTxt, R.id.timeTxt});
                // updating listview
                lv.setAdapter(adapter);
            } else if (id == R.id.sortDateAsc) {
                Collections.sort(DataList, new sortDateAsc());
                ListAdapter adapter = new SimpleAdapter(Entries.this, DataList, R.layout.custom_row, new String[]{"docname", "place", "subdate", "letter", "time"}, new int[]{R.id.boldPlace, R.id.placeTxt, R.id.dateTxt, R.id.magTxt, R.id.timeTxt});
                // updating listview
                lv.setAdapter(adapter);
            } else if (id == R.id.sortDateDesc) {
                Collections.sort(DataList, new sortDateDesc());
                ListAdapter adapter = new SimpleAdapter(Entries.this, DataList, R.layout.custom_row, new String[]{"docname", "place", "subdate", "letter", "time"}, new int[]{R.id.boldPlace, R.id.placeTxt, R.id.dateTxt, R.id.magTxt, R.id.timeTxt});
                // updating listview
                lv.setAdapter(adapter);
            }
        }
        if (id == R.id.refresh) {
            finish();
            startActivity(getIntent());
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("username", null);
            editor.putString("name", null);
            editor.putString("headquater", null);
            editor.apply();

            Intent intent = new Intent(this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.nav_changepass) {
            Intent in = new Intent(this, change_password.class);
            startActivity(in);
        } else if (id == R.id.nav_showEntries) {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            Intent in = new Intent(this, Entries.class);
            startActivity(in);

        } else if (id == R.id.nav_submit) {
            Intent in = new Intent(this, MainActivity.class);
            startActivity(in);
        } else if (id == R.id.nav_share) {
            shareApplication();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    class LoadAllEntries extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Entries.this);
            pDialog.setMessage("Loading data\n Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         */
        protected String doInBackground(String... args) {

            URL url = createUrl(url_all_data);
            // List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            String jsonResponse = null;
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Check your log cat for JSON reponse
            Log.d("All Data: ", jsonResponse);
            if (jsonResponse.equals("0 results")) {

                runOnUiThread(new Runnable() {
                    public void run() {

                        noEQ.setText("No Entries Found");

                    }
                });
            }

        if(!(jsonResponse.equals("0 results")))
        {
            try {
                JSONArray jArray = new JSONArray(jsonResponse);
                //Log.d("LENGTH",Integer.toString(jArray.length()));
                no_of_entries = jArray.length() - 1;
                for (int i = 1; i < jArray.length(); i++) {
                    JSONObject c = jArray.getJSONObject(i);
                    // Storing each json item in variable
                    String doctor_name = c.getString("docname");

                    String place = c.getString("place");
                    String date = c.getString("date");
                    String alpha = "";
                    if (!(doctor_name.equals(""))) {
                        char a = doctor_name.charAt(0);
                        alpha = alpha + a;
                        alpha = alpha.toUpperCase();
                    }
                    String year = date.substring(0, 4);
                    String mnth = date.substring(5, 7);
                    String day = date.substring(8, 10);
                    String subDate = "";
                    subDate = day + "/" + mnth + "/" + year;
                    String time = date.substring(11, 16);

                    try {
                        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");        //format 24hr to 12hr clock
                        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
                        Date _24HourDt = _24HourSDF.parse(time);
                        //System.out.println(_24HourDt);
                        time = _12HourSDF.format(_24HourDt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // creating new HashMap
                    //Log.d("TIME: ", time);
                    String rawDate = subDate + " @" + time;
                    Log.d("rawDate", rawDate);
                    HashMap<String, String> map = new HashMap<String, String>();
                    // adding each child node to HashMap key => value
                    map.put("docname", doctor_name);
                    map.put("place", place);
                    map.put("subdate", subDate);
                    map.put("letter", alpha);
                    map.put("time", time);
                    map.put("rawDate", rawDate);
                    // adding HashList to ArrayList
                    DataList.add(map);
                    //Collections.sort(DataList,new sortA_Z());
                   /* Set keys = map.keySet();

                    for (Iterator k = keys.iterator(); k.hasNext(); ) {  //print hash map
                        String key = (String) k.next();
                        String value =  map.get(key);
                        Log.d(key,value);
                    }*/

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

            return null;
        }


        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {// updating UI from Background Thread

                        // Updating parsed JSON data into ListView

                    ListAdapter adapter = new SimpleAdapter(Entries.this, DataList, R.layout.custom_row, new String[]{"docname","place","subdate","letter","time"}, new int[]{R.id.boldPlace, R.id.placeTxt, R.id.dateTxt,R.id.magTxt,R.id.timeTxt});
                    // updating listview
                    lv.setAdapter(adapter);
                    Toast.makeText(Entries.this,userid+" has total "+Integer.toString(no_of_entries)+" entries",Toast.LENGTH_LONG).show();
                   /* for(int k = 0; k < DataList.size(); k++) {      //print arraylist
                    *    System.out.println(DataList.get(k));
                    *}
                    */
                }
            });

        }
    }
    class sortDateAsc implements Comparator<HashMap<String, String>>{
        DateFormat f = new SimpleDateFormat("dd/MM/yyyy '@'hh:mm a");
        @Override
        public int compare(HashMap<String, String> s1, HashMap<String, String> s2) {
            try {
                String o1=s1.get("rawDate");
                String o2=s2.get("rawDate");
                return f.parse(o1).compareTo(f.parse(o2));
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
    class sortDateDesc implements Comparator<HashMap<String, String>>{
        DateFormat f = new SimpleDateFormat("dd/MM/yyyy '@'hh:mm a");
        @Override
        public int compare(HashMap<String, String> s1, HashMap<String, String> s2) {
            try {
                String o1=s1.get("rawDate");
                String o2=s2.get("rawDate");
                return -(f.parse(o1).compareTo(f.parse(o2)));
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
    class sortA_Z implements Comparator<HashMap<String, String>>{

        @Override
        public int compare(HashMap<String, String> s1, HashMap<String, String> s2) {
            String first=s1.get("docname");
            String second=s2.get("docname");

            return first.compareToIgnoreCase(second);
        }
    }
    class sortZ_A implements Comparator<HashMap<String, String>>{

        @Override
        public int compare(HashMap<String, String> s1, HashMap<String, String> s2) {
            String first=s1.get("docname");
            String second=s2.get("docname");

            return -first.compareToIgnoreCase(second);
        }
    }
    class sortByMonth implements Comparator<HashMap<String, String>>{

        @Override
        public int compare(HashMap<String, String> s1, HashMap<String, String> s2) {
            String first=s1.get("subdate");
            int one=Integer.parseInt(first.substring(3,5));
            String second=s2.get("subdate");
            int two=Integer.parseInt(second.substring(3,5));
            if(one>two)
                return 1;
            else if(one<two)
                return -1;
            else
            return 0;
        }
    }
    class sortByYear implements Comparator<HashMap<String, String>>{

        @Override
        public int compare(HashMap<String, String> s1, HashMap<String, String> s2) {
            String first=s1.get("subdate");
            Log.d("first: ", first);
            int one=Integer.parseInt(first.substring(6,10));
            Log.d("first: ",Integer.toString(one));
            String second=s2.get("subdate");
            int two=Integer.parseInt(second.substring(6,10));
            if(one>two)
                return 1;
            else if(one<two)
                return -1;
            else
                return 0;
        }
    }
    private String makeHttpRequest(URL url)throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }
    private URL createUrl(String StringUrl){

        URL url=null;
        try {
            url=new URL(StringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
    private void shareApplication() {
        ApplicationInfo app = getApplicationContext().getApplicationInfo();
        String filePath = app.sourceDir;

        Intent intent = new Intent(Intent.ACTION_SEND);

        // MIME of .apk is "application/vnd.android.package-archive".
        // but Bluetooth does not accept this. Let's use "*/*" instead.
        intent.setType("*/*");

        // Append file and send Intent
        File originalApk = new File(filePath);

        try {
            //Make new directory in new location
            File tempFile = new File(getExternalCacheDir() + "/ExtractedApk");
            //If directory doesn't exists create new
            if (!tempFile.isDirectory())
                if (!tempFile.mkdirs())
                    return;
            //Get application's name and convert to lowercase
            tempFile = new File(tempFile.getPath() + "/" + getString(app.labelRes).replace(" ","").toLowerCase() + ".apk");
            //If file doesn't exists create new
            if (!tempFile.exists()) {
                if (!tempFile.createNewFile()) {
                    return;
                }
            }
            //Copy file to new location
            InputStream in = new FileInputStream(originalApk);
            OutputStream out = new FileOutputStream(tempFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
            //Open share dialog
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));
            startActivity(Intent.createChooser(intent, "Share app via"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void download(){
        String download_url = "http://www.eugenicspharma.in/reporting_app/sql_excel.php?username=";
        download_url =download_url+userid+"&name="+name+"&headquater="+headquater;

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(download_url));
        startActivity(i);

    }
    public Boolean isInternet(){

        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            return true;
        }else{
            Snackbar.make(parentLayout, "No Internet Connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return false;
        }

    }
}
