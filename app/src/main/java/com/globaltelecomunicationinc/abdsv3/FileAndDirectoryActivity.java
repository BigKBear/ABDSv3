package com.globaltelecomunicationinc.abdsv3;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileAndDirectoryActivity extends ListActivity {

    private List<String> fileList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        ListDir(root);
        //setContentView(R.layout.activity_file_and_directory);
    }

    void ListDir(File f){
        File[] files = f.listFiles();
        fileList.clear();
        for (File file : files){
            fileList.add(file.getPath());
        }
        ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileList);
        setListAdapter(directoryList);
    }
}