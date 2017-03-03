package com.example.maxim.alzheimer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
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

    Runnable runnable = new Runnable() {
        public void run() {
            String sW = searchedWord;
            synchronized (this) {
                try {
                    wait(StartPage.secondsPerQuestion * 1000);
                    if(sW.equals(searchedWord))writeToAnswers(0);
                }
                catch (Exception e) {}
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_page);

        btn_ans1 = (ImageButton) findViewById(R.id.btn_answer1);
        btn_ans2 = (ImageButton) findViewById(R.id.btn_answer2);
        lbl_searchedWord = (TextView) findViewById(R.id.label_word);

        /*
        String photoPath = Environment.getExternalStorageDirectory()+ "/" + StartPage.main_directory + "/Almost.jpg";
        Drawable d = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(photoPath));
        btn_ans1.setImageDrawable(d);
        */

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

        newWord();
    }


    public void writeToAnswers(int state) {
        String[] tmp = new String[3];
        tmp[0] = searchedWord;
        switch (state) {
            case -1:    //beenden
                while (answers.size()<StartPage.numberOfQuestions-1){
                    String[] tmp2 ={"NB","NB","0"};
                    answers.add(tmp2);
                }
                tmp[1] = "NB";
                tmp[2] = "" + 0;
                break;
            case 0:     //skippen
                tmp[1] = "NB";
                tmp[2] = "" + 0;
                break;
            case 1:     //richtig
                Log.d("switch", "richtig");
                tmp[1] = "Richtig";
                tmp[2] = "" + ((System.currentTimeMillis() - time) / 1000);
                break;
            case 2:     //falsch
                Log.d("switch", "falsch");
                tmp[1] = "Falsch";
                tmp[2] = "" + ((System.currentTimeMillis() - time) / 100);
                break;
        }
        answers.add(tmp);

        //Tutorial
        if (answers.size() == StartPage.numberOfTutorials && !isRated) {
            answers.clear();
            isRated = true;
        }
        if(answers.size() == StartPage.numberOfQuestions && isRated){
            mythread.interrupt();
            writeToOutputFile();
            startActivity(new Intent(QuizPage.this, StartPage.class));
        }
        else newWord();
    }

    public void writeToOutputFile() {
        String out = "\n" + StartPage.username;
        for(int i=0;i<3;i++){
            for (String[] an : answers) {
              out += an[i] + "      ";
            }
            out += "\n";
        }
        Log.d("out", out);
    }


    Thread mythread = new Thread(runnable);
    public void newWord() {
        mythread.interrupt();

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
        Context contextAns1 = btn_ans1.getContext();
        int idAns1 = contextAns1.getResources().getIdentifier(StartPage.pictures.get(pic1), "drawable", contextAns1.getPackageName());
        btn_ans1.setImageResource(idAns1);
        Context contextAns2 = btn_ans2.getContext();
        int idAns2 = contextAns1.getResources().getIdentifier(StartPage.pictures.get(pic2), "drawable", contextAns2.getPackageName());
        btn_ans2.setImageResource(idAns2);


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
        mythread = new Thread(runnable);
        mythread.start();
        time = System.currentTimeMillis();
    }
}