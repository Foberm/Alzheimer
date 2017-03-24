package com.example.maxim.alzheimer;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class QuizPage extends AppCompatActivity {

    ImageButton btn_ans1, btn_ans2;
    TextView lbl_searchedWord, lbl_tutorial;

    public static int numberCorrectAnswers = 0;
    public static int numberFalseAnswers = 0;
    public static int numberSkippedAnswers = 0;
    public static long overallTime = 0;


    public List<String[]> answers = new ArrayList<String[]>();
    public List<Integer> used = new ArrayList<Integer>();

    boolean isRated = false;
    long time = 0;
    int searchedButton = 0;
    String searchedWord = "";

    private class Timer extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(1000 * StartPage.secondsPerQuestion);
            } catch (InterruptedException e) {
                Thread.interrupted();
            }
            return "Executed";
        }
        @Override
        protected void onPostExecute(String result) {
            writeToAnswers(0);
        }
    }

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_page);

        btn_ans1 = (ImageButton) findViewById(R.id.btn_answer1);
        btn_ans2 = (ImageButton) findViewById(R.id.btn_answer2);
        lbl_searchedWord = (TextView) findViewById(R.id.label_word);
        lbl_tutorial = (TextView) findViewById(R.id.label_tutorial);

        findViewById(R.id.btn_answer1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchedButton == 1)writeToAnswers(1);
                else writeToAnswers(2);
            }
        });

        findViewById(R.id.btn_answer2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchedButton == 2)writeToAnswers(1);
                else writeToAnswers(2);
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToAnswers(-1);
            }
        });

        newWord();
    }


    public void writeToAnswers(int state) {
        String[] tmp = new String[3];
        String searchedWordUpperCase = searchedWord.substring(0, 1).toUpperCase() + searchedWord.substring(1);
        tmp[0] = searchedWordUpperCase;
        long timeUsed = ((System.currentTimeMillis() - time) / 1000);
        if(isRated) overallTime += timeUsed;

            timer.cancel(true);

        switch (state) {
            case -1:
                tmp[1] = "Abbruch";
                tmp[2] = "-";
                break;
            case 0:
                tmp[1] = "NB";
                tmp[2] = "" + timeUsed;
                if(isRated) numberSkippedAnswers++;

                break;
            case 1:
                tmp[1] = "Richtig";
                tmp[2] = "" + timeUsed;
                if(isRated) numberCorrectAnswers++;
                break;
            case 2:
                tmp[1] = "Falsch";
                tmp[2] = "" + timeUsed;
                if(isRated) numberFalseAnswers++;
                break;
        }
        answers.add(tmp);

        //Tutorial
        if (answers.size() == StartPage.numberOfTutorials && !isRated) {
            answers.clear();
            isRated = true;
        }
        //Finished
        if(answers.size() == StartPage.numberOfQuestions && isRated || state == -1){
            timer.cancel(true);
            writeToOutputFile();
            answers.clear();
            isRated = false;
            startActivity(new Intent(QuizPage.this, ResultPage.class));
        }
        else newWord();
    }

    public void writeToOutputFile(){
        try {
            File path = Environment.getExternalStorageDirectory();
            File absolutePath = new File(path.getAbsolutePath() + "/" + StartPage.main_directory);
            File outputFile = new File(absolutePath, StartPage.outputFileName);

            Calendar c = Calendar.getInstance();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");


            FileOutputStream fOut = new FileOutputStream(outputFile, true);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            osw.append('\n');
            osw.append(dateFormat.format(c.getTime()));
            osw.append(';');
            osw.append(StartPage.username);
            osw.append(';');
            for(int i = 0; i < answers.size(); i++){
                for(int j = 0; j < answers.get(i).length; j++){
                    osw.append(answers.get(i)[j]);
                    if(j < answers.get(i).length-1) osw.append(' ');
                }
                if(i < answers.size()-1) osw.append(';');
            }
            osw.flush();
            fOut.getFD().sync();
            osw.close();

            MediaScannerConnection.scanFile(this, new String[]{outputFile.getAbsolutePath()}, null, null);

        }
        catch(IOException e)
        {
            Log.e("o", e.getMessage());
        }
    }


    //Timer timer = new Timer();
    public synchronized void newWord() {

        lbl_tutorial.setText("");

        if(!isRated)
            lbl_tutorial.setText("T");

        Random rand = new Random();
        int pic1, pic2 = 0;

        do {
            pic1 = rand.nextInt(StartPage.pictures.size());
        } while (used.contains(pic1));



        do {
            pic2 = rand.nextInt(StartPage.pictures.size());
        } while (used.contains(pic2) ||  pic1 == pic2);

        btn_ans1 = (ImageButton) findViewById(R.id.btn_answer1);
        btn_ans2 = (ImageButton) findViewById(R.id.btn_answer2);

        //Set the chosen Pictures to the ImageButtons
        String path_btn_ans1 = Environment.getExternalStorageDirectory()+ "/" +
                StartPage.main_directory + "/" + "/" + StartPage.sub_directory + "/" +StartPage.pictures.get(pic1) +".jpg";
        String path_btn_ans2 = Environment.getExternalStorageDirectory()+ "/" +
                StartPage.main_directory + "/" + StartPage.sub_directory + "/" + StartPage.pictures.get(pic2) +".jpg";
        Drawable draw_btn_ans1 = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(path_btn_ans1));
        Drawable draw_btn_ans2 = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(path_btn_ans2));
        btn_ans1.setImageDrawable(draw_btn_ans1);
        btn_ans2.setImageDrawable(draw_btn_ans2);

        used.add(pic1);
        used.add(pic2);

        boolean correctAnswer = rand.nextBoolean();
        if (correctAnswer) {
            lbl_searchedWord.setText("" + StartPage.pictures.get(pic1).toUpperCase());
            searchedWord = StartPage.pictures.get(pic1);
            searchedButton = 1;
        } else {
            lbl_searchedWord.setText("" + StartPage.pictures.get(pic2).toUpperCase());
            searchedWord = StartPage.pictures.get(pic2);
            searchedButton = 2;
        }
        timer = new Timer();
        timer.execute("");
        time = System.currentTimeMillis();
    }
}