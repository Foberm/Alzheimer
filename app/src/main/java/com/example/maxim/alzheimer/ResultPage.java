package com.example.maxim.alzheimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;


public class ResultPage extends AppCompatActivity {

    TextView lbl_time, lbl_count, lbl_correct, lbl_skipped, lbl_false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_page);

        int numberOfQuestionsDone = QuizPage.numberCorrectAnswers + QuizPage.numberSkippedAnswers + QuizPage.numberFalseAnswers;
        //Avoiding division with zero
        if(numberOfQuestionsDone == 0) numberOfQuestionsDone++;
        int min = Math.round(QuizPage.overallTime) / 60;
        int sec = Math.round(QuizPage.overallTime) % 60;


        lbl_time = (TextView) findViewById(R.id.label_result_time);
        lbl_count = (TextView) findViewById(R.id.label_result_count);
        lbl_correct = (TextView) findViewById(R.id.label_result_correct);
        lbl_skipped = (TextView) findViewById(R.id.label_result_skipped);
        lbl_false = (TextView) findViewById(R.id.label_result_false);

        lbl_time.setText("Benötigte Zeit: " + min + " Minuten " + sec + " Sekunden");
        lbl_count.setText("Anzahl der Fragen: " + numberOfQuestionsDone);
        lbl_correct.setText("" + QuizPage.numberCorrectAnswers + " (" + (QuizPage.numberCorrectAnswers *100) / numberOfQuestionsDone + "%)");
        lbl_skipped.setText("" + QuizPage.numberSkippedAnswers + " (" + (QuizPage.numberSkippedAnswers *100) / numberOfQuestionsDone + "%)");
        lbl_false.setText("" + QuizPage.numberFalseAnswers + " (" + (QuizPage.numberFalseAnswers *100) / numberOfQuestionsDone + "%)");

        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox_sameUser);

        findViewById(R.id.btn_BackToStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDefaultVariables();
                if(checkBox.isChecked()) StartPage.sameUser = true;
                else resetUser();

                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });



    }

    public void resetDefaultVariables() {
        StartPage.pictures.clear();

        //Reset to default
        StartPage.numberOfQuestions = 10;
        StartPage.numberOfTutorials = 2;
        StartPage.secondsPerQuestion = -1;
        StartPage.sub_directory = "";
        QuizPage.overallTime = 0;
        QuizPage.numberCorrectAnswers = 0;
        QuizPage.numberSkippedAnswers = 0;
        QuizPage.numberFalseAnswers = 0;
    }

    public void resetUser() {
        StartPage.username = "Nicht angegeben";
        StartPage.birthDate = "Nicht angegeben";
        StartPage.diagnosis = "Nicht angegeben";
    }

}
