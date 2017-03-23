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
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class QuizPage extends AppCompatActivity {

    ImageButton btn_ans1, btn_ans2;
    TextView lbl_searchedWord;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_page);

        btn_ans1 = (ImageButton) findViewById(R.id.btn_answer1);
        btn_ans2 = (ImageButton) findViewById(R.id.btn_answer2);
        lbl_searchedWord = (TextView) findViewById(R.id.label_word);

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
        Timer t= new Timer();
        t.execute("");
    }


    public void writeToAnswers(int state) {
        String[] tmp = new String[3];
        String searchedWordUpperCase = searchedWord.substring(0, 1).toUpperCase() + searchedWord.substring(1);
        tmp[0] = searchedWordUpperCase;

        if(state != 0)
            t.cancel(true);

        switch (state) {
            case -1:    //beenden
                tmp[1] = "Abbruch";
                tmp[2] = "-";
                /*
                while (answers.size() < StartPage.numberOfQuestions-1){
                    String[] tmp2 ={"NB","NB","-"};
                    answers.add(tmp2);
                }
                */

                break;
            case 0:     //skippen
                tmp[1] = "NB";
                tmp[2] = "0";
                break;
            case 1:     //richtig
                Log.d("switch", "richtig");
                tmp[1] = "Richtig";
                tmp[2] = "" + ((System.currentTimeMillis() - time) / 1000);
                break;
            case 2:     //falsch
                Log.d("switch", "falsch");
                tmp[1] = "Falsch";
                tmp[2] = "" + ((System.currentTimeMillis() - time) / 1000);
                break;
        }
        answers.add(tmp);

        //Tutorial
        if (answers.size() == StartPage.numberOfTutorials && !isRated) {
            answers.clear();
            isRated = true;
        }

        if(answers.size() == StartPage.numberOfQuestions && isRated || state == -1){
            t.cancel(true);
            //writeToConsole();
            writeToOutputFile();
            resetAll();
            startActivity(new Intent(QuizPage.this, StartPage.class));
        }
        else newWord();
    }

    public void writeToConsole() {
        String out = StartPage.username+": \n";
        int[] len=new int[answers.size()];
        for(int i=0;i<answers.size();i++) {
            for (String[] an : answers) {
                if (an[0].length() > an[1].length()) len[i] = an[0].length();
                else len[i] = an[1].length();
            }
        }
        for(int i=0;i<3;i++){
            int j=0;
            for (String[] an : answers) {
              out += an[i] + repeat(" ", len[j]-an[i].length())+"   ";
              j++;
            }
            out += "\n";
        }
        Log.d("out", out);
    }

    public static String repeat(String val, int count){
        StringBuilder buf = new StringBuilder(val.length() * count);
        while (count-- > 0) {
            buf.append(val);
        }
        return buf.toString();
    }

    public void writeToOutputFile(){
        try {
            File path = Environment.getExternalStorageDirectory();
            File f = new File(path.getAbsolutePath() + "/" + StartPage.main_directory);
            File b = new File(f, StartPage.outputFileName);

            Calendar c = Calendar.getInstance();
            int date = c.get(Calendar.DATE);
            int month = c.get(Calendar.MONTH) +1;
            int year = c.get(Calendar.YEAR);
            int hour = c.get(Calendar.HOUR);
            int min = c.get(Calendar.MINUTE);
            int sec = c.get(Calendar.SECOND);
            String time = "" + hour + min + sec;


            FileOutputStream fOut = new FileOutputStream(b, true);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            osw.append('\n');
            osw.append(date + "." + month + "." + year);
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

            MediaScannerConnection.scanFile(this, new String[]{b.getAbsolutePath()}, null, null);

        }
        catch(IOException e)
        {
            Log.e("o", e.getMessage());
        }
    }

    public void resetAll() {
        StartPage.pictures.clear();
        answers.clear();
        used.clear();
        isRated = false;
        //Reset to default
        StartPage.numberOfQuestions = 10;
        StartPage.secondsPerQuestion = 30;
        StartPage.username = "Nicht angegeben";
    }

    Timer t= new Timer();
    public synchronized void newWord() {

        Random rand = new Random();
        int pic1 = 0;
        do {
            pic1 = rand.nextInt(StartPage.pictures.size());
        } while (used.contains(pic1));

        int pic2 = 0;
        do {
            pic2 = rand.nextInt(StartPage.pictures.size());
        } while (used.contains(pic2) || pic1 == pic2);

        btn_ans1 = (ImageButton) findViewById(R.id.btn_answer1);
        btn_ans2 = (ImageButton) findViewById(R.id.btn_answer2);

        //Set the chosen Pictures to the ImageButtons
        String path_btn_ans1 = Environment.getExternalStorageDirectory()+ "/" + StartPage.main_directory + "/" + StartPage.pictures.get(pic1) +".jpg";
        String path_btn_ans2 = Environment.getExternalStorageDirectory()+ "/" + StartPage.main_directory + "/" + StartPage.pictures.get(pic2) +".jpg";
        Drawable draw_btn_ans1 = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(path_btn_ans1));
        Drawable draw_btn_ans2 = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(path_btn_ans2));
        btn_ans1.setImageDrawable(draw_btn_ans1);
        btn_ans2.setImageDrawable(draw_btn_ans2);


        /*
        Context contextAns1 = btn_ans1.getContext();
        int idAns1 = contextAns1.getResources().getIdentifier(StartPage.pictures.get(pic1), "drawable", contextAns1.getPackageName());
        btn_ans1.setImageResource(idAns1);
        Context contextAns2 = btn_ans2.getContext();
        int idAns2 = contextAns1.getResources().getIdentifier(StartPage.pictures.get(pic2), "drawable", contextAns2.getPackageName());
        btn_ans2.setImageResource(idAns2);
        */

        boolean correctAnswer = rand.nextBoolean();
        if (correctAnswer) {
            lbl_searchedWord.setText("" + StartPage.pictures.get(pic1).toUpperCase());
            searchedWord = StartPage.pictures.get(pic1);
            searchedButton = 1;
            used.add(pic1);
        } else {
            lbl_searchedWord.setText("" + StartPage.pictures.get(pic2).toUpperCase());
            searchedWord = StartPage.pictures.get(pic2);
            used.add(pic2);
            searchedButton = 2;
        }
        t= new Timer();
        t.execute("");
        time = System.currentTimeMillis();
    }
}