package com.example.jspann.textfilewriter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;

import java.io.*;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import android.os.Environment;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

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
    }

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

    public void setTextFieldToFile(File file) throws Exception{
        String strOriginalText = readFileContentsToString(file);
        ((EditText)findViewById(R.id.editText)).setText(strOriginalText);
    }
    public void setTextFieldToLatestFile() throws Exception{
        File[] files = getListOfAllFilesInDir(getDirectoryPathToString());
        File latestFile = files[0];
        setTextFieldToFile(latestFile);
    }



    public String getCurrentFormattedDateAsString(){
        Date dteCurrentDate = new Date();
        DateFormat dteFormat = new SimpleDateFormat("yyyyMMdd");
        return dteFormat.format(dteCurrentDate).toString();
    }

    public String getDirectoryPathToString(){
        return (Environment.getExternalStorageDirectory()).toString()+"/tst/";
    }

    public void popup(Object data){
        android.widget.Toast.makeText(getApplicationContext(), data.toString(), Toast.LENGTH_LONG).show();
    }

    public File[] getListOfAllFilesInDir(String pathString){
        File dir = new File(pathString);

        File[] filesInDir = dir.listFiles();
        Arrays.sort(filesInDir,Collections.reverseOrder());
        return filesInDir;
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



    public String readFileContentsToString(File file) throws Exception{
        //try{
            /*
            InputStream inputStreamFromFile = getApplicationContext().openFileInput(file.getAbsolutePath().toString());
            BufferedReader br = new BufferedReader(new FileReader(file));
            String strFileContent="";
            String line;
            while(line=br.readLine()!=null){
                strFileContent += line;
            }
            */
            //FileInputStream fis = new FileInputStream(file);
            //Context context = App.instance.getApplicationContext();
            //InputStream
            String data = new Scanner(file).useDelimiter("\\A").next();
            return data;
        //}
    }
}
