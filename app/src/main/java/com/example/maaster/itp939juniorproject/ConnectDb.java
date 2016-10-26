package com.example.maaster.itp939juniorproject;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.Socket;
import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;

import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class ConnectDb extends AppCompatActivity {

    private  String mlabURL = "mongodb://hciadmin:cs374admin@ds053136.mlab.com:53136/student";
    private MongoDBConnection mongoDBConnection;
    private DBCursor cursor;
    private DBObject object;
    private ArrayList<Teacher> teacherList;
    private ArrayList<Course> courseList;
    private ArrayList<LinearLayout> layouts;
    private ArrayAdapter<LinearLayout> adapter;
    private Student student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_db);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        teacherList = new ArrayList<>();
        courseList = new ArrayList<>();

        getTeacherFromDB();

        student = (Student) getIntent().getExtras().get("data");
        TextView textView = (TextView) findViewById(R.id.textname);
        String course=" ";


        textView.setText(student.getName()+course);
        getImage();

        for (int i = 0; i < student.getCourse().size(); i++) {
            for (int j = 0; j <teacherList.size() ; j++) {
                for (int k = 0; k <teacherList.get(k).getCountCourse() ; k++) {
                    if(student.getCourse().get(i).equalsIgnoreCase(teacherList.get(j).getCourse().get(k).getName())
                            && student.getSection().get(i).equalsIgnoreCase(teacherList.get(j).getCourse().get(k).getSection())) {
                        Log.v("cc"," " +teacherList.get(j).getName());


                    }
                }
            }
        }

    }

    public void getTeacherFromDB() {
        mongoDBConnection = new MongoDBConnection(ConstantProject. IP_PUBLIC_ADDRESS, "Teacher", "test");
        cursor = mongoDBConnection.getCursor();
        object = cursor.next();

        try {

            while(object!=null) {

                BasicDBList courses = (BasicDBList) object.get("course");

                for (int i = 0; i <courses.size() ; i++) {
                    DBObject courseObject = (DBObject) courses.get(i);
                    courseList.add(new Course((String) courseObject.get("section"),(String) courseObject.get("name")));

                }

                Teacher teacher = new Teacher();
                teacher.setName((String)object.get("name"));
                teacher.setCountCourse(courses.size());
                teacher.setImageName((String) object.get("image"));

                for (int i = 0; i < courses.size() ; i++) {
                    teacher.addCourse(courseList.get(i));
                }

                teacherList.add(teacher);
                object = cursor.next();
                courseList.clear();

            }



        } catch (Exception e) {
            //e.printStackTrace();

            return;
        }

    }

    public void getImage() {

        try {
            layouts = new ArrayList<>();

            MongoClient mongoClient = new MongoClient(new MongoClientURI(ConstantProject.IP_PUBLIC_ADDRESS));
            DB db = mongoClient.getDB("test");
            GridFS gfsPhoto = new GridFS(db, "photo");

            for (int i = 0; i < student.getCourse().size(); i++) {
                for (int j = 0; j <teacherList.size() ; j++) {
                    for (int k = 0; k <teacherList.get(k).getCountCourse() ; k++) {
                        if(student.getCourse().get(i).equalsIgnoreCase(teacherList.get(j).getCourse().get(k).getName())
                                && student.getSection().get(i).equalsIgnoreCase(teacherList.get(j).getCourse().get(k).getSection())) {
                            Log.v("cc"," " +teacherList.get(j).getImageName());

                            GridFSDBFile imageForOutput = gfsPhoto.findOne(teacherList.get(j).getImageName());

                            File outFile;
                            ImageView iv= (ImageView)findViewById(R.id.t1);
                            Context context =  iv.getContext();
                            outFile = File.createTempFile("xyz", null, context.getCacheDir());
                            imageForOutput.writeTo(outFile);

                            InputStream is = new FileInputStream(outFile);

                            iv.setImageBitmap(BitmapFactory.decodeStream(is));
                            outFile.delete();


                        }
                    }
                }
            }







        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
