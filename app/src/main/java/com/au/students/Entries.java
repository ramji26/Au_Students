package com.au.students;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;



public class Entries extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private EditText ID;
    private EditText Name;
    private ImageButton picbut;

    Intent intent;
    ContextWrapper cw;
    File directory, myPath;
    Bitmap bmp;
    String id, name;
    private static final String LOG_TAG = Entries.class.getSimpleName();

    public static int getRequestImageCapture() {
        return REQUEST_IMAGE_CAPTURE;
    }

    public static String getLogTag() {
        return LOG_TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entries);
        setID((EditText) findViewById(R.id.id_field));
        setName((EditText) findViewById(R.id.name_field));
        setPicbut((ImageButton) findViewById(R.id.imageButton));
        cw = new ContextWrapper(getApplicationContext());


    }

    public void picButClicked(View v) {
        Toast toast = Toast.makeText(getApplicationContext(), "Pic Button Clicked", Toast.LENGTH_SHORT);
        toast.show();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, getRequestImageCapture());
        }
    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data) {
        directory = cw.getDir("imageDir", MODE_PRIVATE);
        myPath = new File(directory, "propic.jpg");
        FileOutputStream fos = null;
        if (requestcode == getRequestImageCapture() && resultcode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bmp = (Bitmap) extras.get("data");
            //bmp = Bitmap.createScaledBitmap(bmp, 128, 128, true);
            picbut.setImageBitmap(bmp);
        }
    }

    public void viewClicked(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void submitClicked(View v) {

        id = getID().getText().toString();
        name = getName().getText().toString();
        Context context = getApplicationContext();

        if (id.isEmpty() || name.isEmpty()) {

            Toast toast = Toast.makeText(context, "Fields Cannot be Empty", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            SharedPreferences sharedPref = Entries.this.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("ID", id);
            editor.putString("Name", name);
            editor.commit();


            try {
                FileOutputStream fos ;
                fos = new FileOutputStream(myPath);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            new sendData().execute(id, name);
            Log.i(getLogTag(), "Saved");
            intent = new Intent(Entries.this, MainActivity.class);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    public EditText getID() {
        return ID;
    }

    public void setID(EditText ID) {
        this.ID = ID;
    }

    public EditText getName() {
        return Name;
    }

    public void setName(EditText name) {
        Name = name;
    }

    public ImageButton getPicbut() {
        return picbut;
    }

    public void setPicbut(ImageButton picbut) {
        this.picbut = picbut;
    }


    private class sendData extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection = null;
        URL url;
        String data = "";
        ProgressDialog pdialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = new ProgressDialog(Entries.this);
            pdialog.setMessage("Please Wait...");
            pdialog.setCancelable(false);
            pdialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            //id for database
            String result = "";
            String tempRno = params[0];
            String tempName = params[1];
            String tempID = "" + tempRno.charAt(10) + tempRno.charAt(11);


            try {
                HashMap<String, String> kvpairs = new HashMap<>();
                kvpairs.put("id", tempID);
                kvpairs.put("rno", tempRno);
                kvpairs.put("name", tempName);
                url = new URL("http://offmod.au-syd.mybluemix.net/addpeeps/?" + getPostDataString(kvpairs));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                int response_code = urlConnection.getResponseCode();

                if (response_code == HttpURLConnection.HTTP_OK) {

                    result = "Success";
                } else {
                    result = "failed";
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }

            return result;
        }


        protected void onPostExecute(String result) {
            pdialog.dismiss();
            Toast t = Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT);
            t.show();
            startActivity(intent);
            finish();
        }


        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            String valId = "", valName = "", valRno = "";
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getKey().equals("id")) {
                    valId = entry.getValue();
                } else if (entry.getKey().equals("name")) {
                    valName = entry.getValue();
                } else if (entry.getKey().equals("rno")) {
                    valRno = entry.getValue();
                }

            }
            result.append(URLEncoder.encode("id", "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(valId, "UTF-8"));
            result.append("&");
            result.append(URLEncoder.encode("name", "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(valName, "UTF-8"));
            result.append("&");
            result.append(URLEncoder.encode("rno", "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(valRno, "UTF-8"));

            System.out.println(result.toString());
            return result.toString();
        }
    }
}
