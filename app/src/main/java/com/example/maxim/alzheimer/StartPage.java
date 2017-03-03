package com.example.maxim.alzheimer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StartPage extends AppCompatActivity {

    public static String username = "Standard Username";
    public static int numberOfQuestions = 3;
    public static int secondsPerQuestion = 15;
    public static int numberOfTutorials = 1;
    public static String main_directory = "AlzheimerTestOrdner";
    public static String outputFileName = "Auswertung.csv";
    public static List<String> pictures = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.start_page);

        //Creating Directory if doesn't exist
        File directory = new File(Environment.getExternalStorageDirectory(), main_directory);
        if (!directory.exists()) {
            directory.mkdirs();
        }


        /*
        final Field[] fields = R.drawable.class.getDeclaredFields();
        String regex = "[a-z]+";
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().matches(regex))
                pictures.add(fields[i].getName());
        }
        */

        //Scanning the available pictures
        String filename = "";
        File yourDir = new File(Environment.getExternalStorageDirectory(), "/" + main_directory);
        for (File f : yourDir.listFiles()) {
            if (f.isFile()) {
                filename = f.getName();
                if(filename.endsWith(".jpg")) {
                    filename = filename.substring(0, (filename.length()-4));
                    pictures.add(filename);
                }
            }
        }

        //Let User know the maximum amount of Questions possible
        ((EditText)findViewById(R.id.numOfQuestionsInput)).setHint("Anzahl Fragen.. (Standard: 10, Maximal: " + (pictures.size()-1) + ")");

        //OnClick for "Beginnen"-Button
        findViewById(R.id.beginnen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String preusername = ((EditText)findViewById(R.id.nameInput)).getText().toString();
                String anzahl = ((EditText)findViewById(R.id.numOfQuestionsInput)).getText().toString();
                String zeit = ((EditText)findViewById(R.id.timePerQuestionInput)).getText().toString();

                if(!preusername.isEmpty())
                    username=preusername;

                if(!anzahl.isEmpty()) {
                    int a = Integer.parseInt(anzahl);
                    if(a > pictures.size()-1)
                        numberOfQuestions = pictures.size()-1;
                    else
                        numberOfQuestions = a;
                }

                if(!zeit.isEmpty())
                    secondsPerQuestion = Integer.parseInt(zeit);

                startActivity(new Intent(StartPage.this, QuizPage.class));
            }
        });

    }

}
