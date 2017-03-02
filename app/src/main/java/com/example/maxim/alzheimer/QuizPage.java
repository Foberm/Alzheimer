package com.example.maxim.alzheimer;

import android.content.Context;
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

    public List<String> pictures = new ArrayList<String>();
    public List<String[]> answers = new ArrayList<String[]>();
    public List<Integer> used = new ArrayList<Integer>();
    String searchedWord = "";
    boolean isRated = false;
    long time = 0;
    ImageButton btn_ans1, btn_ans2;
    TextView lbl_searchedWord;
    int trueButton=0;

    Runnable runnable = new Runnable() {
        public void run() {
            String sW=searchedWord;
            synchronized (this) {
                try {
                    wait(StartPage.secondsPerQuestion * 1000);
                    if(sW.equals(searchedWord))writeToAnswers(0);
                } catch (Exception e) {
                }
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

        final Field[] fields = R.drawable.class.getDeclaredFields();
        String regex = "[a-z]+";
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().matches(regex))
                pictures.add(fields[i].getName());
        }

        /*
        String photoPath = Environment.getExternalStorageDirectory()+ "/" + StartPage.main_directory + "/Almost.jpg";
        Drawable d = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(photoPath));
        btn_ans1.setImageDrawable(d);
        */
        findViewById(R.id.btn_answer1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(trueButton==1)writeToAnswers(1);
                else writeToAnswers(2);
            }
        });

        findViewById(R.id.btn_answer2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(trueButton==2)writeToAnswers(1);
                else writeToAnswers(2);
            }
        });

        newWord();
    }


    public void writeToAnswers(int state) {
        String[] temp = new String[3];
        temp[0] = searchedWord;
        switch (state) {
            case -1:    //beenden
                while (answers.size()<StartPage.numberOfQuestions-1){
                    String[] tmp2 ={"NB","NB","0"};
                    answers.add(tmp2);
                }
                temp[1] = "NB";
                temp[2] = "" + 0;
                break;
            case 0:     //skippen
                temp[1] = "NB";
                temp[2] = "" + 0;
                break;
            case 1:     //richtig
                Log.d("switch", "richtig");
                temp[1] = "Richtig";
                temp[2] = "" + (System.currentTimeMillis() - time / 1000);
                break;
            case 2:     //falsch
                Log.d("switch", "falsch");
                temp[1] = "Falsch";
                temp[2] = "" + (System.currentTimeMillis() - time / 1000);
                break;
        }
        answers.add(temp);

        //Tutorial
        if (answers.size() == StartPage.numberOfTutorials && !isRated) {
            answers.clear();
            isRated = true;
        }
        if(answers.size() == StartPage.numberOfQuestions && isRated){
            lbl_searchedWord.setText("Game Ended");
            mythread.interrupt();
        }
        else newWord();
    }


    Thread mythread = new Thread(runnable);
    public void newWord() {
        mythread.interrupt();
        Random rand = new Random();
        int pic1 = 0;
        do {
            pic1 = rand.nextInt(pictures.size());
        } while (used.contains(pic1));

        int pic2 = 0;
        do {
            pic2 = rand.nextInt(pictures.size());
        } while (used.contains(pic2) || pic1 == pic2);

        btn_ans1 = (ImageButton) findViewById(R.id.btn_answer1);
        btn_ans2 = (ImageButton) findViewById(R.id.btn_answer2);
        Context contextAns1 = btn_ans1.getContext();
        int idAns1 = contextAns1.getResources().getIdentifier(pictures.get(pic1), "drawable", contextAns1.getPackageName());
        btn_ans1.setImageResource(idAns1);
        Context contextAns2 = btn_ans2.getContext();
        int idAns2 = contextAns1.getResources().getIdentifier(pictures.get(pic2), "drawable", contextAns2.getPackageName());
        btn_ans2.setImageResource(idAns2);


        boolean correctAnswer = rand.nextBoolean();
        if (correctAnswer) {
            lbl_searchedWord.setText("" + pictures.get(pic1).toUpperCase());
            searchedWord = pictures.get(pic1);
            trueButton=1;
            used.add(pic1);
        } else {
            lbl_searchedWord.setText("" + pictures.get(pic2).toUpperCase());
            searchedWord = pictures.get(pic2);
            used.add(pic2);
            trueButton=2;
        }
        mythread = new Thread(runnable);
        mythread.start();
        time = System.currentTimeMillis();
    }
}