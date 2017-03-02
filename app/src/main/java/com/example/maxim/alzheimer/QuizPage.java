package com.example.maxim.alzheimer;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class QuizPage extends AppCompatActivity {

    public List<String> pictures = new ArrayList<String>();
    public List<String[]> answers = new ArrayList<String[]>();
    public List<Integer> used = new ArrayList<Integer>();
    String text="";
    ImageButton ans1, ans2;
    TextView word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_page);
         ans1 = (ImageButton) findViewById(R.id.btn_answer1);
         ans2 = (ImageButton) findViewById(R.id.btn_answer2);
         word = (TextView) findViewById(R.id.word);
        final Field[] fields = R.drawable.class.getDeclaredFields();
        String regex ="[a-z]+";
        for (int i = 0; i < fields.length; i++) {
            if(fields[i].getName().matches(regex))
                pictures.add(fields[i].getName());
        }
        newWord();
        Runnable runnable = new Runnable() {
            public void run() {
                synchronized (this) {
                    try {
                        wait(StartPage.secondsPerQuestion * 1000);
                        inBetween(0);
                    } catch (Exception e) {
                    }
                }
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();
    }
    //-1 beenden
    //0 skippen
    //1 richtig
    //2 falsch
    public void inBetween(int state){
        if(state==0) {
            String[] temp = {text, "NB", "" + 0};
            answers.add(temp);
            newWord();
        }
    }
    int random(){
        Random rand=new Random();
        int r=0;
        do{
            r = rand.nextInt(pictures.size());
        }while(used.contains(r));
        used.add(r);
        return r;
    }

    public void newWord() {

        Log.d("hallo", "in Se Mehod");
        Random rand = new Random();
        int pic1= random(), pic2 = random();
        ans1 = (ImageButton) findViewById(R.id.btn_answer1);
        ans2 = (ImageButton) findViewById(R.id.btn_answer2);
        Context contextAns1 = ans1.getContext();
        int idAns1 = contextAns1.getResources().getIdentifier(pictures.get(pic1), "drawable", contextAns1.getPackageName());
        ans1.setImageResource(idAns1);
        Context contextAns2 = ans2.getContext();
        int idAns2 = contextAns1.getResources().getIdentifier(pictures.get(pic2), "drawable", contextAns2.getPackageName());
        ans2.setImageResource(idAns2);


        boolean correctAnswer =  rand.nextBoolean();
        if(correctAnswer) {
            word.setText("" + pictures.get(pic1).toUpperCase());
            text=pictures.get(pic1);
        }
        else{
            word.setText("" + pictures.get(pic2).toUpperCase());
            text=pictures.get(pic2);
        }
        Log.d("newWord", "nein!");
    }

    public void answer (String answer) {

    }
}



