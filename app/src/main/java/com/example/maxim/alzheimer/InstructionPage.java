package com.example.maxim.alzheimer;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;


public class InstructionPage extends AppCompatActivity {

    String activity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instruction_page);

        ImageView img = ((ImageView) findViewById(R.id.img_instructions));
        String path = Environment.getExternalStorageDirectory()+ "/" +
                StartPage.main_directory + "/";

        Intent intent = getIntent();
        activity = intent.getStringExtra("activity");

        if(activity.equals("StartPage"))
            path = path + StartPage.sub_directory + "/Tutorial/" + StartPage.instructionFile;
        else
            path = path + StartPage.informationFile;

        Drawable draw_img = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(path));
        img.setImageDrawable(draw_img);

        if(StartPage.numberOfTutorials != 0 && activity.equals("StartPage"))
            ((Button)findViewById(R.id.btn_StartRun)).setText("Weiter");
        else
            ((Button)findViewById(R.id.btn_StartRun)).setText("Beginnen");

        findViewById(R.id.btn_StartRun).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.equals("StartPage")) {
                    if (StartPage.numberOfTutorials != 0)
                        startActivity(new Intent(InstructionPage.this, TutorialPage.class));
                    else
                        startActivity(new Intent(InstructionPage.this, QuizPage.class));
                }
                else
                    startActivity(new Intent(InstructionPage.this, QuizPage.class));
                }
            });


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
}
