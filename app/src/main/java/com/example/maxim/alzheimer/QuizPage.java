package com.example.maxim.alzheimer;

import android.content.Context;
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
    String main_directory = "AlzheimerTestOrdner";
    boolean isRated = false;
    ImageButton btn_ans1, btn_ans2;
    TextView lbl_searchedWord;

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


        File f = new File(Environment.getExternalStorageDirectory(), main_directory);
        if (!f.exists()) {
            f.mkdirs();
        }

        //File fileWithinMyDir = new File(mydir, "myfile"); //Getting a file within the dir.
        //FileOutputStream out = new FileOutputStream(fileWithinMyDir); //Use the stream as usual to write into the file.

        newWord();
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
        Thread mythread = new Thread(runnable);
        mythread.start();
    }

    //-1 beenden
    //0 skippen
    //1 richtig
    //2 falsch
    public void writeToAnswers(int state){
        String[] temp = new String[3];
        temp[0] = searchedWord;

        switch (state) {
            case -1:
                break;
            case 0:
                temp[1] = "NB";
                temp[2] = "" + 0;
                break;
            case 1:
                temp[1] = "Richtig";
                temp[2] = "" + 0;
                break;
            case 2:
                temp[1] = "Falsch";
                temp[2] = "" + 0;
                break;
        }
        answers.add(temp);
        newWord();
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
        }
        else{
            lbl_searchedWord.setText("" + pictures.get(pic2).toUpperCase());
            searchedWord = pictures.get(pic2);
        }
        Log.d("newWord", "nein!");
    }

    public void answer (String answer) {

    }
}



