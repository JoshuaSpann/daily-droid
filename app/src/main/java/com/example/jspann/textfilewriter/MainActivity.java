package com.example.jspann.textfilewriter;

import android.content.Context;
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
    private String StrCorePath = (Environment.getExternalStorageDirectory()).toString();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            setTextFieldToLatestFile();
        }
        catch(Exception e){
            popup(e);
        }

        //TODO - ADD FILE SELECTION DROPDOWN TO ALLOW DYNAMIC EDITING!!!
    }

    /*/  BUTTON CLICK FUNCTIONS /*/

    public void createNewTextFile(View view){
        File newFile = new File(getDirectoryPathToString(),getCurrentFormattedDateAsString()+".txt");

        if(newFile.exists()) {
            return;
        }

        try {
            FileWriter fwriter = new FileWriter(newFile);
            fwriter.append("# " + getCurrentFormattedDateAsString() + "\n\n---\n\n");
            fwriter.flush();
            fwriter.close();
            setTextFieldToLatestFile();
        }
        catch (Exception e){
            popup(e);
        }
    }
    public void saveToFile(View view) throws Exception{
        String[] strFilenames = getListOfAllFilenamesInDir(getDirectoryPathToString());
        String strLatestFilename = strFilenames[0];
        //((TextView) findViewById(R.id.debug_text)).setText(strLatestFilename);

        String strDataBody = ((EditText)findViewById(R.id.editText)).getText().toString();
        try {
            String dteToday = getCurrentFormattedDateAsString();

            File rootPath = new File(getDirectoryPathToString());
            rootPath.mkdir();

            File file = new File(rootPath, strLatestFilename);

            boolean blnAppendToFile = true;
            if (!file.exists()){
                blnAppendToFile = false;
                strDataBody = dteToday+"\n\n---\n\n"+strDataBody;
            }

            FileWriter fwriter = new FileWriter(file);
            fwriter.append(strDataBody+"\n\n");
            fwriter.flush();
            fwriter.close();

            setTextFieldToLatestFile();
        }
        catch (Exception e){
            popup(e);
        }
    }


    /*/  TEXT FIELD FUNCTIONS /*/

    public void setTextFieldToFile(File file) throws Exception{
        String strOriginalText = readFileContentsToString(file);
        ((EditText)findViewById(R.id.editText)).setText(strOriginalText);
        Context ctx = getApplicationContext();
        ((TextView)findViewById(R.id.debug_text)).setText((ctx.getFilesDir()).toString()+file.getName());
    }
    public void setTextFieldToLatestFile() throws Exception{
        File[] files = getListOfAllFilesInDir(getDirectoryPathToString());
        File latestFile = files[0];
        setTextFieldToFile(latestFile);
    }


    /*/  HELPER FUNCTIONS /*/

    public String getCurrentFormattedDateAsString(){
        Date dteCurrentDate = new Date();
        DateFormat dteFormat = new SimpleDateFormat("yyyyMMdd");
        return dteFormat.format(dteCurrentDate).toString();
    }

    public String getDirectoryPathToString(){
        //String strDefaultDir = (Environment.getExternalStorageDirectory()).toString()+"/DailyDroid/";
        Context ctx = getApplicationContext();
        String strDefaultDir = (ctx.getFilesDir()).toString()+"/DailyDroid/";
        File projectDir = new File(strDefaultDir);
        projectDir.mkdir();
        return strDefaultDir;
    }

    public String[] getListOfAllFilenamesInDir(String pathString){
        File[] filesInDir = getListOfAllFilesInDir(pathString);

        int intFileCounter = 0;
        String[] filenamesInDir = new String[filesInDir.length];

        for(File currFile : filesInDir){
            filenamesInDir[intFileCounter] = currFile.getName().toString();
            intFileCounter++;
        }

        return filenamesInDir;
    }

    public File[] getListOfAllFilesInDir(String pathString){
        File dir = new File(pathString);

        File[] filesInDir = dir.listFiles();
        Arrays.sort(filesInDir,Collections.reverseOrder());
        return filesInDir;
    }

    public void popup(Object data){
        android.widget.Toast.makeText(getApplicationContext(), data.toString(), Toast.LENGTH_LONG).show();
    }

    public String readFileContentsToString(File file) throws Exception{
        String data = new Scanner(file).useDelimiter("\\A").next();
        return data;
    }
}
