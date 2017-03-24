package com.example.maxim.alzheimer;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StartPage extends AppCompatActivity {

    public static String username = "Nicht angegeben";
    public static String birthDate = "Nicht angegeben";
    public static String diagnosis = "Nicht angegeben";
    public static int numberOfQuestions = 3;
    public static int secondsPerQuestion = 5;
    public static int numberOfTutorials = 1;
    public static String main_directory = "Alzheimer-Studie";
    public static String sub_directory = "";
    public static int sub_directoryId = 0;
    public static String outputFileName = "Auswertung_Alzheimer-Studie.csv";
    public static List<String> pictures = new ArrayList<String>();

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.start_page);

        //Creating Directory if doesn't exist
        final File directory = new File(Environment.getExternalStorageDirectory(), main_directory);
        Log.d("dir", Environment.getExternalStorageDirectory().toString());
        if (!directory.exists()) {
            directory.mkdirs();
        }


        final ArrayList<String> subDirs = new ArrayList<String>();

        //Scanning the available directories
        File yourDir = new File(Environment.getExternalStorageDirectory(), "/" + main_directory);
        for (File f : yourDir.listFiles()) {
            if (f.isDirectory())
                subDirs.add(f.getName());
        }

        //Drop-Down-List for choosing the sub-directory
        final Spinner spinner = (Spinner) findViewById(R.id.subDirInput);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subDirs);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(sub_directoryId);

        //OnClick for Drop-Down-List
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                sub_directory = spinner.getSelectedItem().toString();
                sub_directoryId = spinner.getSelectedItemPosition();

                pictures.clear();

                ((TextView) parent.getChildAt(0)).setTextSize(18);


                //Scanning the available pictures
                String filename = "";
                File yourDir = new File(Environment.getExternalStorageDirectory(), "/" + main_directory + "/" + sub_directory);
                for (File f : yourDir.listFiles()) {
                    if (f.isFile()) {
                        filename = f.getName();
                        if (filename.endsWith(".jpg")) {
                            filename = filename.substring(0, (filename.length() - 4));
                            pictures.add(filename);
                        }
                    }
                }

                //Let User know the maximum amount of Questions possible
                //((EditText)findViewById(R.id.numOfQuestionsInput)).setHint("Anzahl Fragen.. (Standard: 10, Maximal: " + (pictures.size()-1 -numberOfTutorials) + ")");
                ((EditText) findViewById(R.id.numOfQuestionsInput)).setHint("Anzahl Fragen.. (Standard: 10, Bilder: " + (pictures.size()) + ")");

            }

            public void onNothingSelected(AdapterView<?> parent) {
                sub_directory = subDirs.get(0);
            }
        });

        //Date-Picker Dialog
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel();
            }

        };

        //OnClick for BirthDate Input
        ((EditText) findViewById(R.id.birthDateInput)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(StartPage.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //OnClick for "Beginnen"-Button
        findViewById(R.id.btn_startQuiz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String preusername = ((EditText)findViewById(R.id.nameInput)).getText().toString();
                String gebDat = ((EditText)findViewById(R.id.birthDateInput)).getText().toString();
                String diagnose = ((EditText)findViewById(R.id.diagnosisInput)).getText().toString();
                String anzahl = ((EditText)findViewById(R.id.numOfQuestionsInput)).getText().toString();
                String zeit = ((EditText)findViewById(R.id.timePerQuestionInput)).getText().toString();

                if(!preusername.isEmpty())
                    username = preusername;

                if(!gebDat.isEmpty())
                    birthDate = gebDat;

                if(!diagnose.isEmpty())
                    diagnosis = diagnose;

                if(!anzahl.isEmpty()) {
                    int a = Integer.parseInt(anzahl);
                    /*
                    if(a > pictures.size()-1 -numberOfTutorials)
                        numberOfQuestions = pictures.size()-1 -numberOfTutorials;
                    else
                    */
                        numberOfQuestions = a;
                }

                if(!zeit.isEmpty())
                    secondsPerQuestion = Integer.parseInt(zeit);


                startActivity(new Intent(StartPage.this, QuizPage.class));
            }
        });



    }

    public void updateDateLabel() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        ((EditText)findViewById(R.id.birthDateInput)).setText(sdf.format(myCalendar.getTime()));
    }


}
