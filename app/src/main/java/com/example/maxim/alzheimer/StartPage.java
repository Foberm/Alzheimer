package com.example.maxim.alzheimer;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StartPage extends AppCompatActivity {

    public static String username = "Nicht angegeben";
    public static String birthDate = "Nicht angegeben";
    public static String diagnosis = "Nicht angegeben";
    public static boolean sameUser = false;
    public static int numberOfQuestions = 10;
    public static int secondsPerQuestion = -1;
    public static int numberOfTutorials = 2;
    public static String main_directory = "Alzheimer-Studie";
    public static String sub_directory = "";
    public static int sub_directoryId = 0;
    public static String outputFileName = "Auswertung_Alzheimer-Studie.csv";
    public static String instructionFile = "Instruktionen.jpg";
    public static String informationFile = "Start-Hinweis.jpg";
    public static List<String> pictures = new ArrayList<String>();
    public static List<String> tutorial_pictures = new ArrayList<String>();
    int maxNumberOfQuestions = 0;

    final Calendar myCalendar = Calendar.getInstance();
    final ArrayList<String> subDirs = new ArrayList<String>();
    Spinner spinner;


    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);

        if (ContextCompat.checkSelfPermission(StartPage.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(StartPage.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(StartPage.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        spinner = (Spinner) findViewById(R.id.subDirInput);

        createDirectory(main_directory);

        updateDropDown();

        if(sameUser) updateUserInputs();

        //OnClick for Drop-Down-List
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                sub_directory = spinner.getSelectedItem().toString();
                sub_directoryId = spinner.getSelectedItemPosition();

                pictures.clear();
                tutorial_pictures.clear();

                numberOfTutorials = 2;

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

                //Scanning the available tutorials
                String tut_filename = "";
                File tutorialDir = new File(Environment.getExternalStorageDirectory(), "/" + main_directory + "/" + sub_directory + "/Tutorial");
                for (File f : tutorialDir.listFiles()) {
                    if (f.isFile()) {
                        tut_filename = f.getName();
                        if (tut_filename.endsWith(".jpg") && !tut_filename.equals(instructionFile) && !tut_filename.equals((informationFile))) {
                            tut_filename = tut_filename.substring(0, (tut_filename.length() - 4));
                            tutorial_pictures.add(tut_filename);
                        }
                    }
                }

                if(tutorial_pictures.size()%2 != 0)
                    tutorial_pictures.remove(tutorial_pictures.size()-1);

                if(tutorial_pictures.size()/2 < numberOfTutorials)
                    numberOfTutorials = tutorial_pictures.size()/2;


                if(pictures.size()%2 == 0)
                    maxNumberOfQuestions = pictures.size() /2 ;
                else
                    maxNumberOfQuestions = (pictures.size()-1) /2;

                //Let User know the maximum amount of Questions possible
                ((EditText) findViewById(R.id.numOfQuestionsInput)).setHint("Anzahl Fragen.. (Standard: 10, Maximal: " + maxNumberOfQuestions + ")");

                ((TextView) findViewById(R.id.lbl_itemsInCategory)).setText("Items: " + pictures.size());
                if(tutorial_pictures.size() != 0)
                    ((TextView) findViewById(R.id.lbl_itemsInCategory)).append(", Tutorial");

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

        //OnClick for creating new Category Button
        findViewById(R.id.btn_newCategory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewCategory();
            }
        });

        //OnClick for BirthDate Input
        ((EditText) findViewById(R.id.birthDateInput)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(StartPage.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //OnClick for "Weiter"-Button
        findViewById(R.id.btn_ToInstructions).setOnClickListener(new View.OnClickListener() {
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

                    if(a > maxNumberOfQuestions) {
                        numberOfQuestions = maxNumberOfQuestions;
                        Toast.makeText(StartPage.this, "Die Anzahl der Fragen wurde auf den Maximalwert von " + maxNumberOfQuestions + " gesetzt!",
                                Toast.LENGTH_LONG).show();
                    }
                    else
                        numberOfQuestions = a;
                }
                else if(numberOfQuestions > maxNumberOfQuestions)
                        numberOfQuestions = maxNumberOfQuestions;

                if(!zeit.isEmpty())
                    secondsPerQuestion = Integer.parseInt(zeit);

                if(pictures.size() == 0)
                    showDialog("Keine Items vorhanden", "In dieser Kategorie befinden sich keine validen Items!\n\nFügen Sie unter    Gerätespeicher/Alzheimer-Studie/xx    Bilder im .JPG Format hinzu!");
                else {
                    Intent intent = new Intent(StartPage.this, InstructionPage.class);
                    intent.putExtra("activity","StartPage");
                    startActivity(intent);
                }
            }
        });



    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed() {
        //Blocking the hardware Back-Button
    }

    public void updateDateLabel() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        ((EditText)findViewById(R.id.birthDateInput)).setText(sdf.format(myCalendar.getTime()));
    }

    public void createNewCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Neue Kategorie hinzufügen");

        // Set up the input
        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createDirectory(input.getText().toString());
                updateDropDown();
                Toast.makeText(StartPage.this, "Kategorie " + input.getText().toString() + " hinzugefügt",
                        Toast.LENGTH_LONG).show();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void updateDropDown() {

        subDirs.clear();

        //Scanning the available directories
        File yourDir = new File(Environment.getExternalStorageDirectory(), "/" + main_directory);
        for (File f : yourDir.listFiles()) {
            if (f.isDirectory()) {
                Log.d("f", f.getName());
                subDirs.add(f.getName());
            }
        }

        if(!subDirs.isEmpty()) {
            //Drop-Down-List for choosing the sub-directory
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subDirs);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerArrayAdapter);
            spinner.setSelection(sub_directoryId);
        }
    }

    public void createDirectory(String dirName){
        File path;
        if(dirName.equals(main_directory)) path = Environment.getExternalStorageDirectory();
        else path = new File(Environment.getExternalStorageDirectory() + "/" + main_directory);

        File directory = new File(path, dirName);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        if(!dirName.equals(main_directory)) {
            path = new File(Environment.getExternalStorageDirectory() + "/" + main_directory + "/" + dirName);
            File subdir = new File(path, "Tutorial");
            if (!subdir.exists()) {
                subdir.mkdirs();
            }
        }
    }

    public void showDialog(String title, String msg) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void updateUserInputs() {
        if(!username.equals("Nicht angegeben")) ((EditText) findViewById(R.id.nameInput)).setText(username);
        if(!birthDate.equals("Nicht angegeben"))((EditText) findViewById(R.id.birthDateInput)).setText(birthDate);
        if(!diagnosis.equals("Nicht angegeben"))((EditText) findViewById(R.id.diagnosisInput)).setText(diagnosis);

        sameUser = false;
    }



}
