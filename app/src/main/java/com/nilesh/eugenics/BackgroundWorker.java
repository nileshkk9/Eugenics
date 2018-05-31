package com.nilesh.eugenics;

        import android.app.AlertDialog;

        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.AsyncTask;
        import android.os.CountDownTimer;
        import android.util.Log;
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

public class BackgroundWorker extends AsyncTask<String,Void,String> {
    Context context;
    AlertDialog alertDialog;
    String username;
    ProgressDialog progress;
    String userid;
    String type="";

    BackgroundWorker (Context ctx) {
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {
         type = params[0];
        Log.d("type",type);
        String login_url = "http://www.eugenicspharma.in/reporting_app/login.php";
        String insert_url = "http://www.eugenicspharma.in/reporting_app/insert.php";
       //
        if(type.equals("login")) {
            try {
                username = params[1];
                String password = params[2];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(username,"UTF-8")+"&"
                        +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line="";
                while((line = bufferedReader.readLine())!= null) {
                    result += line;
                }
                Log.d("username",result);
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (type.equals("insert")) {
            try {
                SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
                userid = sp.getString("username", null);
                String mDoc_name = params[1];
                String mPlace = params[2];
                String mQualification = params[3];
                String mSample = params[4];
                String no_chemist= params[5];
                String mName_chemist1 = params[6];
                String mName_chemist2 = params[7];
                String mName_chemist3 = params[8];
                String mName_chemist4 = params[9];
                String mName_chemist5 = params[10];
                String mName_chemist6 = params[11];
                String mWorked_with = params[12];
                String mOther = params[13];

                URL url = new URL(insert_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("docname","UTF-8")+"="+URLEncoder.encode(mDoc_name,"UTF-8")+"&"
                + URLEncoder.encode("place","UTF-8")+"="+URLEncoder.encode(mPlace,"UTF-8")+"&"
                + URLEncoder.encode("quali","UTF-8")+"="+URLEncoder.encode(mQualification,"UTF-8")+"&"
                + URLEncoder.encode("sample","UTF-8")+"="+URLEncoder.encode(mSample,"UTF-8")+"&"
                + URLEncoder.encode("no_chemist","UTF-8")+"="+URLEncoder.encode(no_chemist,"UTF-8")+"&"
                + URLEncoder.encode("chemist1","UTF-8")+"="+URLEncoder.encode(mName_chemist1,"UTF-8")+"&"
                + URLEncoder.encode("chemist2","UTF-8")+"="+URLEncoder.encode(mName_chemist2,"UTF-8")+"&"
                + URLEncoder.encode("chemist3","UTF-8")+"="+URLEncoder.encode(mName_chemist3,"UTF-8")+"&"
                + URLEncoder.encode("chemist4","UTF-8")+"="+URLEncoder.encode(mName_chemist4,"UTF-8")+"&"
                + URLEncoder.encode("chemist5","UTF-8")+"="+URLEncoder.encode(mName_chemist5,"UTF-8")+"&"
                + URLEncoder.encode("chemist6","UTF-8")+"="+URLEncoder.encode(mName_chemist6,"UTF-8")+"&"
                + URLEncoder.encode("worked","UTF-8")+"="+URLEncoder.encode(mWorked_with,"UTF-8")+"&"
                + URLEncoder.encode("other","UTF-8")+"="+URLEncoder.encode(mOther,"UTF-8")
                +"&"+URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(userid,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line="";
                while((line = bufferedReader.readLine())!= null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(context).create();
            progress = new ProgressDialog(context);
            progress.setMessage("Please Wait...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();

    }

    @Override
    protected void onPostExecute(String result) {
        progress.dismiss();



        if(result.equals("success")) {
                    SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username", username);
                    editor.apply();

                    Intent in = new Intent(context, MainActivity.class);
                    context.startActivity(in);
                }

                else if(result.equals("insert successful")){

                    alertDialog.setTitle("With Username: "+userid);
                    alertDialog.setMessage("Data Uploaded");
                    alertDialog.show();
                    //MainActivity mw=new MainActivity();
                    //mw.cleared();
                }

                else if(result.equals("not success")) {
                    alertDialog.setTitle("Login Status");
                    alertDialog.setMessage("Invalid Username or Password");
                    alertDialog.show();

                    new CountDownTimer(1000, 1000) {
                        public void onFinish() {
                        }

                        public void onTick(long millisUntilFinished) {
                            // millisUntilFinished    The amount of time until finished.
                        }
                    }.start();


                }
               /* else if(username==null) {
                alertDialog.setTitle("Login Status");
                alertDialog.setMessage("Login first to upload data");
                alertDialog.show();

                }*/
            else {
            alertDialog.setTitle("Status");
            alertDialog.setMessage("Server error try again later");
            alertDialog.show();
            //MainActivity mw=new MainActivity();
            //mw.cleared();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}