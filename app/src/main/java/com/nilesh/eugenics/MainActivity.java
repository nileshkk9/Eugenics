package com.nilesh.eugenics;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Spinner spin;
    String no_chemist,mDoc_name,mPlace,mQualification,mSample,mName_chemist1,mName_chemist2,mName_chemist3,mName_chemist4,mName_chemist5,mName_chemist6,mWorked_with,mOther;
    EditText doc_name,qualification,sample,worked_with,other,name_chemist1,name_chemist2,name_chemist3,name_chemist4,name_chemist5,name_chemist6;
    AutoCompleteTextView place;
    Vibrator myVib;
    Boolean flag;
    String LOG_TAG="error_url";
    String url_all_data="http://www.eugenicspharma.in/reporting_app/auto_complete.php?username=",username;
    LinearLayout linearLayout;
    private ArrayAdapter<String> autoCompleteAdapter;
    ArrayList<String> PlaceList = new ArrayList<String>();
    View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        username = sp.getString("username", null);

        if(username==null) {
            Intent in = new Intent(this,Login.class);
            startActivity(in);
            finish();
        }
        url_all_data +=username;
        new LoadAutoComplete().execute();//
        if(isInternet()) {
            new LoadName().execute();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v=view;
                submit(v);
               /*   Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                *        .setAction("Action", null).show();
                *
                *
                */

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        ActionBarDrawerToggle  drawerToggle = new  android.support.v7.app.ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_open) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //dismiss softkey
                imm.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);

            }
        };
        drawer.setDrawerListener(drawerToggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(1).setChecked(true);

        spin = (Spinner) findViewById(R.id.spinner);
        doc_name = (EditText) findViewById(R.id.doc_name);
        place = (AutoCompleteTextView) findViewById(R.id.place);
        qualification = (EditText) findViewById(R.id.qualification);
        sample = (EditText) findViewById(R.id.sample);
        name_chemist1 = (EditText) findViewById(R.id.name_chemist1);
        name_chemist2 = (EditText) findViewById(R.id.name_chemist2);
        name_chemist3 = (EditText) findViewById(R.id.name_chemist3);
        name_chemist4 = (EditText) findViewById(R.id.name_chemist4);
        name_chemist5 = (EditText) findViewById(R.id.name_chemist5);
        name_chemist6 = (EditText) findViewById(R.id.name_chemist6);
        worked_with = (EditText) findViewById(R.id.worked_with);
        linearLayout= (LinearLayout) findViewById(R.id.l1);
        other = (EditText) findViewById(R.id.other);
        declareAutoComplete();

        place.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                new LoadAutoComplete().execute();
                declareAutoComplete();
            }
        });


        qualification.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);




        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 no_chemist = spin.getItemAtPosition(position).toString();
                switch (no_chemist) {
                    case "0":
                        name_chemist1.setVisibility(view.GONE);
                        name_chemist2.setVisibility(view.GONE);
                        name_chemist3.setVisibility(view.GONE);
                        name_chemist4.setVisibility(view.GONE);
                        name_chemist5.setVisibility(view.GONE);
                        name_chemist6.setVisibility(view.GONE);
                        break;
                    case "1":
                        name_chemist1.setVisibility(view.VISIBLE);
                        name_chemist2.setVisibility(view.GONE);
                        name_chemist3.setVisibility(view.GONE);
                        name_chemist4.setVisibility(view.GONE);
                        name_chemist5.setVisibility(view.GONE);
                        name_chemist6.setVisibility(view.GONE);
                        break;
                    case "2":
                        name_chemist1.setVisibility(view.VISIBLE);
                        name_chemist2.setVisibility(view.VISIBLE);
                        name_chemist3.setVisibility(view.GONE);
                        name_chemist4.setVisibility(view.GONE);
                        name_chemist5.setVisibility(view.GONE);
                        name_chemist6.setVisibility(view.GONE);
                        break;
                    case "3":
                        name_chemist1.setVisibility(view.VISIBLE);
                        name_chemist2.setVisibility(view.VISIBLE);
                        name_chemist3.setVisibility(view.VISIBLE);
                        name_chemist4.setVisibility(view.GONE);
                        name_chemist5.setVisibility(view.GONE);
                        name_chemist6.setVisibility(view.GONE);
                        break;
                    case "4":
                        name_chemist1.setVisibility(view.VISIBLE);
                        name_chemist2.setVisibility(view.VISIBLE);
                        name_chemist3.setVisibility(view.VISIBLE);
                        name_chemist4.setVisibility(view.VISIBLE);
                        name_chemist5.setVisibility(view.GONE);
                        name_chemist6.setVisibility(view.GONE);
                        break;
                    case "5":
                        name_chemist1.setVisibility(view.VISIBLE);
                        name_chemist2.setVisibility(view.VISIBLE);
                        name_chemist3.setVisibility(view.VISIBLE);
                        name_chemist4.setVisibility(view.VISIBLE);
                        name_chemist5.setVisibility(view.VISIBLE);
                        name_chemist6.setVisibility(view.GONE);
                        break;
                    case "6":
                        name_chemist1.setVisibility(view.VISIBLE);
                        name_chemist2.setVisibility(view.VISIBLE);
                        name_chemist3.setVisibility(view.VISIBLE);
                        name_chemist4.setVisibility(view.VISIBLE);
                        name_chemist5.setVisibility(view.VISIBLE);
                        name_chemist6.setVisibility(view.VISIBLE);


                }
            }
            public void onNothingSelected(AdapterView<?> parent) {}

        });
}




    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume(){
        super.onResume();

        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        String username = sp.getString("username", null);
        if(username==null) {
            Intent in = new Intent(this,Login.class);
            startActivity(in);
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(1).setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.textView);
        TextView nav_headquater = (TextView)hView.findViewById(R.id.textView2);

        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        String name = sp.getString("name", null);
        String headquater = sp.getString("headquater", null);
        nav_user.setText("Current User: "+name);/////////////////////////////////////
        nav_headquater.setText("Headquater: "+headquater);

        //Log.d("USERNAME",userid);
        MenuItem mn = menu.findItem(R.id.action_settings);
        mn.setTitle("Signed In As: "+username);
        invalidateOptionsMenu ();

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
           logout();
        } else if (id == R.id.nav_changepass) {
            Intent in = new Intent(this, change_password.class);
            startActivity(in);
        } else if (id == R.id.nav_showEntries) {

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //dismiss softkey
            imm.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);
            Intent in = new Intent(this, Entries.class);
            startActivity(in);

        } else if (id == R.id.nav_submit) {

        } else if (id == R.id.nav_share) {
        shareApplication();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void submit(View v){
        myVib.vibrate(20);
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        validate();

    }


    public void cleared(View v){
       // myVib.vibrate(20);
        doc_name.setText("");
        place.setText("");
        qualification.setText("");
        sample.setText("");
        worked_with.setText("");
        other.setText("");

        name_chemist1.setText("");
        name_chemist2.setText("");
        name_chemist3.setText("");
        name_chemist4.setText("");
        name_chemist5.setText("");
        name_chemist6.setText("");
        spin.setSelection(0);
        doc_name.requestFocus();
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

    public void validate(){
        mDoc_name=doc_name.getText().toString();                //1
        mPlace=place.getText().toString();                      //2
        mQualification=qualification.getText().toString();      //3
        mSample=sample.getText().toString();                    //4
        mName_chemist1=name_chemist1.getText().toString();      //5
        mName_chemist2=name_chemist2.getText().toString();      //6
        mName_chemist3=name_chemist3.getText().toString();      //7
        mName_chemist4=name_chemist4.getText().toString();      //8
        mName_chemist5=name_chemist5.getText().toString();      //9
        mName_chemist6=name_chemist6.getText().toString();      //10
        mWorked_with=worked_with.getText().toString();          //11
        mOther=other.getText().toString();

        if(TextUtils.isEmpty(mWorked_with)) {
            worked_with.setError("You worked with field is required");
            worked_with.requestFocus();
        }
        if(TextUtils.isEmpty(mPlace)) {
            place.setError("Place is required");
            place.requestFocus();
        }
        if(TextUtils.isEmpty(mDoc_name)) {
            doc_name.setError("Doctor's name is required");
            doc_name.requestFocus();
        }


        if(!TextUtils.isEmpty(mDoc_name)&&!TextUtils.isEmpty(mPlace)&&!TextUtils.isEmpty(mWorked_with))
        {
            validateSpinner();
            if(flag==false)
            {
                ConnectivityManager cm =
                        (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                    String type = "insert";
                    BackgroundWorker backgroundWorker = new BackgroundWorker(this);
                    backgroundWorker.execute(type, mDoc_name, mPlace, mQualification, mSample, no_chemist, mName_chemist1, mName_chemist2, mName_chemist3, mName_chemist4, mName_chemist5, mName_chemist6, mWorked_with, mOther);
                    cleared(getCurrentFocus());
                }else{
                    Snackbar.make(v, "No Internet Connection", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
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
            Log.e("URL_ERROR", "Problem building the URL ", e);
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

    class LoadAutoComplete extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {

        }

        /**
         * getting All products from url
         */
        protected String doInBackground(String... args) {

            URL url = createUrl(url_all_data);

            String jsonResponse = null;
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("All Data: ", jsonResponse);




            try {
                JSONArray jArray = new JSONArray(jsonResponse);

                Set<String> hs = new HashSet<>();
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject c = jArray.getJSONObject(i);
                    // Storing each json item in variable
                    String place = c.getString("place");
                    Log.d("Place: ", place);
                    PlaceList.add(place);

                }
                hs.addAll(PlaceList);
                PlaceList.clear();
                PlaceList.addAll(hs);
                //autoCompleteAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {


        }
    }
    class LoadName extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {

        }

        /**
         * getting All products from url
         */
        protected String doInBackground(String... args) {
            String url_name="http://www.eugenicspharma.in/reporting_app/Load_name.php?username=";
            url_name +=username;
            URL url = createUrl(url_name);

            String json = null;
            String name = null;
            String headquater = null;
            try {
                json = makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (json != null) {
                for(int i=0;i<json.length();i++){
                    if(json.charAt(i)=='<')
                    {
                        name=json.substring(0,i);
                        headquater=json.substring(i+4,json.length());
                    }

                }
            }
            //Log.d("All Data: ", name);
            //Log.d("All Data: ", headquater);
            SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("name", name);
            editor.putString("headquater", headquater);
            editor.apply();
            return null;
        }


        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {


        }
    }
    public void declareAutoComplete(){
        autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,PlaceList);
        autoCompleteAdapter.setNotifyOnChange(true); // This is so I don't have to manually sync whenever changed


        place.setThreshold(2);
        place.setAdapter(autoCompleteAdapter);
    }
    public void validateSpinner(){
        flag=false;
        if(no_chemist.equals("1"))
        {
            if(TextUtils.isEmpty(name_chemist1.getText().toString())){
                flag=true;
                name_chemist1.setError("This field is required");
            }
        }
        else if(no_chemist.equals("2"))
        {
            if(TextUtils.isEmpty(name_chemist1.getText().toString())){
                flag=true;
                name_chemist1.setError("This field is required");
            }
            if(TextUtils.isEmpty(name_chemist2.getText().toString())){
                flag=true;
                name_chemist2.setError("This field is required");
            }
        }
        else if(no_chemist.equals("3"))
        {
            if(TextUtils.isEmpty(name_chemist1.getText().toString())){
                flag=true;
                name_chemist1.setError("This field is required");
            }
            if(TextUtils.isEmpty(name_chemist2.getText().toString())){
                flag=true;
                name_chemist2.setError("This field is required");
            }
            if(TextUtils.isEmpty(name_chemist3.getText().toString())){
                flag=true;
                name_chemist3.setError("This field is required");
            }
        }
        else if(no_chemist.equals("4"))
        {
            if(TextUtils.isEmpty(name_chemist1.getText().toString())){
                flag=true;
                name_chemist1.setError("This field is required");
            }
            if(TextUtils.isEmpty(name_chemist2.getText().toString())){
                flag=true;
                name_chemist2.setError("This field is required");
            }
            if(TextUtils.isEmpty(name_chemist3.getText().toString())){
                flag=true;
                name_chemist3.setError("This field is required");
            }
            if(TextUtils.isEmpty(name_chemist4.getText().toString())){
                flag=true;
                name_chemist4.setError("This field is required");
            }
        }
        else if(no_chemist.equals("5"))
        {
            if(TextUtils.isEmpty(name_chemist1.getText().toString())){
                flag=true;
                name_chemist1.setError("This field is required");
            }
            if(TextUtils.isEmpty(name_chemist2.getText().toString())){
                flag=true;
                name_chemist2.setError("This field is required");
            }
            if(TextUtils.isEmpty(name_chemist3.getText().toString())){
                flag=true;
                name_chemist3.setError("This field is required");
            }
            if(TextUtils.isEmpty(name_chemist4.getText().toString())){
                flag=true;
                name_chemist4.setError("This field is required");
            }
            if(TextUtils.isEmpty(name_chemist5.getText().toString())){
                flag=true;
                name_chemist5.setError("This field is required");
            }
        }
        else if(no_chemist.equals("6"))
        {

            if(TextUtils.isEmpty(name_chemist1.getText().toString())){
                flag=true;
                name_chemist1.setError("This field is required");
            }
            if(TextUtils.isEmpty(name_chemist2.getText().toString())){
                flag=true;
                name_chemist2.setError("This field is required");
            }
            if(TextUtils.isEmpty(name_chemist3.getText().toString())){
                flag=true;
                name_chemist3.setError("This field is required");
            }
            if(TextUtils.isEmpty(name_chemist4.getText().toString())){
                flag=true;
                name_chemist4.setError("This field is required");
            }
            if(TextUtils.isEmpty(name_chemist5.getText().toString())){
                flag=true;
                name_chemist5.setError("This field is required");
            }
            if(TextUtils.isEmpty(name_chemist6.getText().toString())){
                flag=true;
                name_chemist6.setError("This field is required");
            }
        }

    }
    public void logout(){
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
    }
    public Boolean isInternet(){

        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            return true;
        }else{

            return false;
        }

    }

}
