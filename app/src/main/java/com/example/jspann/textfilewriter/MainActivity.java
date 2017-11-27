package com.example.jspann.textfilewriter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import android.os.Environment;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void createNewTextFile(View view){
        File newFile = getFileStreamPath(".txt");
        if(!newFile.exists()){
            //newFile.createNewFile();
        }
    }
    public void saveToFile(View view) throws Exception{
        String strDataBody = ((EditText)findViewById(R.id.editText)).getText().toString();
        Date dteCurrentDate = new Date();
        DateFormat dteFormat = new SimpleDateFormat("yyyyMMdd");
        String strCurrentDate = dteFormat.format(dteCurrentDate).toString();
        //throw new Exception("CURRDATE:: "+strCurrentDate);
        ((TextView)findViewById(R.id.debug_text)).setText(strCurrentDate);
        //String strDirectoryPath = "/storage/emulated/0/";
        try {
            String strDirectoryPath = (Environment.getExternalStorageDirectory()).toString();
            //File rootPath = new File(DIRECTORY_PATH);
            File rootPath = new File(strDirectoryPath);
            File file = new File(rootPath, strCurrentDate + ".txt");
            FileWriter fwriter = new FileWriter(file);
            fwriter.append(strDataBody);
            fwriter.flush();
            fwriter.close();
        }
        catch (Exception e){
            android.widget.Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
