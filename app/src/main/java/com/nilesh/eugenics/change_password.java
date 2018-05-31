package com.nilesh.eugenics;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class change_password extends AppCompatActivity {
    EditText user, Cpass, Npass, RNpass;
    String username, CurrentPassword, NewPassword, REPassword, LOG_TAG = "HTTP Error";
    ProgressDialog progress;
    Vibrator myVib;
    AlertDialog alertDialog;
    String pass_url = "http://www.eugenicspharma.in/reporting_app/change_pass.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        set();

    }

    public void set() {
        user = (EditText) findViewById(R.id.usernameTV);
        Cpass = (EditText) findViewById(R.id.Cpassword);
        Npass = (EditText) findViewById(R.id.Npassword);
        RNpass = (EditText) findViewById(R.id.RNpassword);
        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        String userid = sp.getString("username", null);
        user.setText(userid);
        username=userid;
    }

    public void Bchangepass(View v) {
        takeDataFromET();
        if(isInternet()){
            if (checkEmptyField()) {
                if (checkEqual()) {
                    submit();
                }
            }
        }
    }

    public Boolean checkEmptyField() {

        if (TextUtils.isEmpty(CurrentPassword)) {
            Cpass.setError("Current Password is required");
            return false;
        }
        if (TextUtils.isEmpty(NewPassword)) {
            Npass.setError("New Password is required");
            return false;
        }
        if (TextUtils.isEmpty(REPassword)) {
            RNpass.setError("Field is required");
            return false;
        } else
            return true;


    }

    public Boolean checkEqual() {
        if(!isPasswordValid(NewPassword)){
            Npass.setError("New Password is too short");
            return false;
        }
        if (CurrentPassword.equals(NewPassword)) {
            Npass.setError("Current Password and New Nassword can't be same");
            return false;
        }
        if (!(NewPassword.equals(REPassword))) {
            RNpass.setError("Password do not match");
            return false;
        } else
            return true;
    }

    public void submit() {
        myVib.vibrate(20);
        new changePassword().execute();

    }
    public void takeDataFromET(){

        CurrentPassword = Cpass.getText().toString();
        NewPassword = Npass.getText().toString();
        REPassword = RNpass.getText().toString();
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
            Snackbar.make(getCurrentFocus(), "No Internet Connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return false;
        }

    }
    public boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
    class changePassword extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(change_password.this).create();
            progress = new ProgressDialog(change_password.this);
            progress.setMessage("Please Wait...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();
        }

        protected String doInBackground(String... args) {
            try {

                URL url = new URL(pass_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&"
                        + URLEncoder.encode("newpass", "UTF-8") + "=" + URLEncoder.encode(NewPassword, "UTF-8") + "&"
                        + URLEncoder.encode("currentpass", "UTF-8") + "=" + URLEncoder.encode(CurrentPassword, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                // Log.d("username",result);
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            progress.dismiss();


            if (result.equals("update successful")) {
                alertDialog.setTitle("Change Password");
                alertDialog.setMessage("Changed Successful");
                alertDialog.show();
                new CountDownTimer(2000, 2000) {
                    public void onFinish() {
                        logout();
                    }

                    public void onTick(long millisUntilFinished) {
                        // millisUntilFinished    The amount of time until finished.
                    }
                }.start();
            } else if (result.equals("password doesn't match")) {
                alertDialog.setTitle("Change Password");
                alertDialog.setMessage("Entered Password Is Incorrect");
                alertDialog.show();
            } else {
                alertDialog.setTitle("Change Password");
                alertDialog.setMessage("Server error please come back later");
                alertDialog.show();
            }
        }



    }
}
