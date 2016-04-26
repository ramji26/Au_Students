package com.au.students;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;

public class ME extends AppCompatActivity {

    String name = "";
    String id = "";
    final int CSSE = 20;
    final int CSNW = 80;
    final int EEE = 11;
    final int CIVIL = 22;
    final int ECE = 33;
    final int MARINE = 44;

    File myPath;
    ContextWrapper cw;

    TextView idV;
    TextView namev;
    TextView Dept;
    TextView sem;
    TextView CourseDuration;
    ImageView propic;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        sharedPref = getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
        idV = (TextView) findViewById(R.id.Id);
        namev = (TextView) findViewById(R.id.textView3);
        Dept = (TextView) findViewById(R.id.textView5);
        sem = (TextView) findViewById(R.id.textView7);
        CourseDuration = (TextView) findViewById(R.id.textView9);
        propic = (ImageView) findViewById(R.id.propic);
        cw = new ContextWrapper(getApplicationContext());
        myPath = cw.getDir("imageDir", MODE_PRIVATE);
        loadContent();
    }
    public void loadContent(){

        try {
            File f=new File(myPath, "propic.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            propic.setImageBitmap(b);

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        String defaultVal = "";
        id = sharedPref.getString("ID", defaultVal);
        name = sharedPref.getString("Name", defaultVal);
        String year = "20"+id.charAt(1)+id.charAt(2);
        idV.setText(id);
        namev.setText(name);
        int curyear = Calendar.getInstance().get(Calendar.YEAR);
        int depcode = Character.getNumericValue(id.charAt(8))*10+Character.getNumericValue(id.charAt(9));

        switch (depcode){
            case CSSE:{
                Dept.setText("CSSE");
                break;
            }
            case CSNW:{
                Dept.setText("CSNW");
                break;
            }
        }
        String temp = Integer.toString(Integer.parseInt(year)+4);
        CourseDuration.setText(year+"-"+temp);
        sem.setText(Integer.toString(curyear - Integer.parseInt(year)));

    }
}
