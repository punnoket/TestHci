package com.example.maaster.itp939juniorproject;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telecom.Connection;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {

    private String id;
    private String pass;
    private DBObject object;
    private Student student;
    private BasicDBList courses ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("LOGIN");
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openConnect(View view) throws UnknownHostException {
        getImage();
        Intent intent = new Intent(MainActivity.this, ConnectDb.class);
        EditText nameText = (EditText) findViewById(R.id.name_login);
        EditText passText = (EditText) findViewById(R.id.password_login);
        id = nameText.getText().toString();
        pass = passText.getText().toString();
        DBCursor cursor;

        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        Log.v("ip", ip);
        String dbURI = "mongodb://192.168.221.2:27017";
       //  String dbURI = "mongodb://172.20.10.6:27017";

        MongoDBConnection mongoDBConnection = new MongoDBConnection(dbURI, "Student", "test");
        cursor = mongoDBConnection.getCursor() ;

        object = cursor.next();

        try{
            while(object!=null) {

                if(object.get("id").equals(id) && object.get("password").equals(pass)) {
                    String name = (String) object.get("name");
                    courses = (BasicDBList) object.get("course");
                    Log.v("asdsad", "asdad");
                    student = new Student(name, pass, id);
                    for (int i = 0; i <courses.size() ; i++) {
                        DBObject courseObject = (DBObject) courses.get(i);
                        student.addCourse((String)courseObject.get("name"));
                        student.addSection((String)courseObject.get("section"));

                    }

                    intent.putExtra("data", student);

                }

            object = cursor.next();
        }

            mongoDBConnection.closeDB();

        }catch (Exception e) {

            if(student == null) {
                Log.d("error","invalid");
                return;
            }
        startActivity(intent);
       }
        //

    }

    public void getImage() {

        try {
        String newFileName = "pun";
        InputStream is = getAssets().open("boom.jpg");

        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://192.168.221.2:27017"));

        DB db = mongoClient.getDB("test");
        GridFS gfsPhoto = new GridFS(db, "photo");
        GridFSInputFile gfsFile = gfsPhoto.createFile(is);


        gfsFile.setFilename(newFileName);
        gfsFile.save();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
