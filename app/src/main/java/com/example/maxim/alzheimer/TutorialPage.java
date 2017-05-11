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

public class TutorialPage extends AppCompatActivity {

    ImageButton btn_ans1, btn_ans2;
    TextView lbl_searchedWord, lbl_tutorial;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_page);

        btn_ans1 = (ImageButton) findViewById(R.id.btn_answer1);
        btn_ans2 = (ImageButton) findViewById(R.id.btn_answer2);
        lbl_searchedWord = (TextView) findViewById(R.id.label_word);
        lbl_tutorial = (TextView) findViewById(R.id.label_tutorial);
        lbl_tutorial.setText("T");

        findViewById(R.id.btn_answer1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newWord();
            }
        });

        findViewById(R.id.btn_answer2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               newWord();
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutorialPage.this, StartPage.class));
            }
        });

        newWord();
    }

    @Override
    public void onBackPressed() {
        //Blocking the hardware Back-Button
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


    public void newWord() {

        if(count == StartPage.numberOfTutorials) {
            Intent intent = new Intent(TutorialPage.this, InstructionPage.class);
            intent.putExtra("activity","TutorialPage");
            startActivity(intent);
        }
        else {
            btn_ans1 = (ImageButton) findViewById(R.id.btn_answer1);
            btn_ans2 = (ImageButton) findViewById(R.id.btn_answer2);
            Random rand = new Random();
            boolean correctAnswer = rand.nextBoolean();
            int pic1, pic2 = 0;

            pic1 = rand.nextInt(StartPage.tutorial_pictures.size());
            do {
                pic2 = rand.nextInt(StartPage.tutorial_pictures.size());
            } while (pic1 == pic2);

            //Set the chosen Pictures to the ImageButtons
            String path_btn_ans1 = Environment.getExternalStorageDirectory() + "/" +
                    StartPage.main_directory + "/" + StartPage.sub_directory + "/Tutorial/" + StartPage.tutorial_pictures.get(pic1) + ".jpg";
            String path_btn_ans2 = Environment.getExternalStorageDirectory() + "/" +
                    StartPage.main_directory + "/" + StartPage.sub_directory + "/Tutorial/" + StartPage.tutorial_pictures.get(pic2) + ".jpg";

            Drawable draw_btn_ans1 = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(path_btn_ans1));
            Drawable draw_btn_ans2 = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(path_btn_ans2));
            btn_ans1.setImageDrawable(draw_btn_ans1);
            btn_ans2.setImageDrawable(draw_btn_ans2);


            if (correctAnswer) {
                lbl_searchedWord.setText("" + StartPage.tutorial_pictures.get(pic1).toUpperCase());
            } else {
                lbl_searchedWord.setText("" + StartPage.tutorial_pictures.get(pic2).toUpperCase());
            }

            if (pic1 > pic2) {
                StartPage.tutorial_pictures.remove(pic1);
                StartPage.tutorial_pictures.remove(pic2);
            } else {
                StartPage.tutorial_pictures.remove(pic2);
                StartPage.tutorial_pictures.remove(pic1);
            }

            count++;
        }
    }
}