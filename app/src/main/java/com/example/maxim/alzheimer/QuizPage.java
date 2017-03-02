package com.example.maxim.alzheimer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
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

    Runnable runnable = new Runnable() {
        public void run() {
            synchronized (this) {
                try {
                    wait(StartPage.secondsPerQuestion * 1000);
                    writeToAnswers(0);
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
        String regex ="[a-z]+";
        for (int i = 0; i < fields.length; i++) {
            if(fields[i].getName().matches(regex))
                pictures.add(fields[i].getName());
        }

        /*
        String photoPath = Environment.getExternalStorageDirectory()+ "/" + StartPage.main_directory + "/Almost.jpg";
        Drawable d = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(photoPath));
        btn_ans1.setImageDrawable(d);
        */

        newWord();

    }


    public void writeToAnswers(int state){
        String[] temp = new String[3];
        temp[0] = searchedWord;

        switch (state) {
            case -1:    //beenden
                break;
            case 0:     //skippen
                temp[1] = "NB";
                temp[2] = "" + 0;
                break;
            case 1:     //richtig
                temp[1] = "Richtig";
                temp[2] = "" + (System.currentTimeMillis() - time / 1000);
                break;
            case 2:     //falsch
                temp[1] = "Falsch";
                temp[2] = "" + (System.currentTimeMillis() - time / 1000);
                break;
        }
        answers.add(temp);

        //Tutorial
        if(answers.size() == StartPage.numberOfTutorials && !isRated){
            answers.clear();
            isRated = true;
        }

        newWord();
    }
    int random(){
        Random rand=new Random();
        int r=0;
        do{
            r = rand.nextInt(pictures.size());
        }while(used.contains(r));
        return r;
    }

    public void newWord() {

        Log.d("hallo", "in Se Mehod");
        Random rand = new Random();
        int pic1= random(), pic2 = random();
        btn_ans1 = (ImageButton) findViewById(R.id.btn_answer1);
        btn_ans2 = (ImageButton) findViewById(R.id.btn_answer2);
        Context contextAns1 = btn_ans1.getContext();
        int idAns1 = contextAns1.getResources().getIdentifier(pictures.get(pic1), "drawable", contextAns1.getPackageName());
        btn_ans1.setImageResource(idAns1);
        Context contextAns2 = btn_ans2.getContext();
        int idAns2 = contextAns1.getResources().getIdentifier(pictures.get(pic2), "drawable", contextAns2.getPackageName());
        btn_ans2.setImageResource(idAns2);


        boolean correctAnswer =  rand.nextBoolean();
        if(correctAnswer) {
            lbl_searchedWord.setText("" + pictures.get(pic1).toUpperCase());
            searchedWord = pictures.get(pic1);
            used.add(pic1);
        }
        else{
            lbl_searchedWord.setText("" + pictures.get(pic2).toUpperCase());
            searchedWord = pictures.get(pic2);
            used.add(pic2);
        }
        Log.d("newWord", "nein!");

        Thread mythread = new Thread(runnable);
        mythread.start();
        time = System.currentTimeMillis();
    }

    public void answer (String answer) {

    }
}



