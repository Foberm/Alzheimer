package com.example.maxim.alzheimer;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;


public class InstructionPage extends AppCompatActivity {

    String activity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instruction_page);

        ImageView img = ((ImageView) findViewById(R.id.img_instructions));
        String path = Environment.getExternalStorageDirectory()+ "/" +
                StartPage.main_directory + "/" + StartPage.sub_directory + "/Tutorial/";

        Intent intent = getIntent();
        activity = intent.getStringExtra("activity");

        if(activity.equals("StartPage"))
            path += StartPage.instructionFile;
        else
            path +=  StartPage.startRatedFile;

        Drawable draw_img = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(path));
        img.setImageDrawable(draw_img);

        findViewById(R.id.btn_StartRun).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.equals("StartPage")) {
                    if (StartPage.numberOfTutorials == 0)
                        startActivity(new Intent(InstructionPage.this, TutorialPage.class));
                    else
                        startActivity(new Intent(InstructionPage.this, QuizPage.class));
                }
                else
                    startActivity(new Intent(InstructionPage.this, QuizPage.class));
                }
            });


    }
}
