package com.capstone.notekeepers.QuizModule;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.capstone.notekeepers.QuizModule.DbHelper.DbHelper;
import com.capstone.notekeepers.QuizModule.Model.QuestionHardWare;
import com.capstone.notekeepers.R;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class QuizSec3Activity extends AppCompatActivity {

    List<QuestionHardWare> quesList1;
    public int score=0;
    int ctr1=1;
    QuestionHardWare currentQ1;
    TextView txtQuestion1;
    RadioGroup grp;
    RadioButton rda1, rdb1, rdc1, rdd1;
    Button butNext1;
    Random random1 = new Random();
    ArrayList<Integer> list = new ArrayList<Integer>();
    TextView textViewTime1;
    public ArrayList<String> wrongQuestListOS = new ArrayList<String>();
    public ArrayList<String> selectedAnsOS = new ArrayList<String>();
    public ArrayList<String> actualAnswerOS= new ArrayList<String>();
    int number;
    ProgressBar progressBar;
    int progress = 1;
    String tableName="",catName="";
    TextView qstnNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2_sec3);
        qstnNo = (TextView)findViewById(R.id.qstnNo);
        Intent iin=getIntent();
        Bundle b=iin.getExtras();

        if(b!=null){
            tableName=(String)b.get("table_name");
            catName=(String)b.get("level_name");
            Log.d("Table_Name",tableName);
            Log.d("Level_Name",catName);
        }
        number=0;
        DbHelper db= new DbHelper(this);
        textViewTime1 = (TextView)findViewById(R.id.textViewTime);
        final QuizSec3Activity.CounterClass timer = new CounterClass(1800000, 1000);
        timer.start();
        quesList1=db.getAllQuestions2(tableName,catName);

        for(int i=0;i<50;i++){
            while(true){
                int next = random1.nextInt(50);
                if(!list.contains(next))
                {
                    list.add(next);
                    break;
                }
            }
        }
        Log.d("ListSize check",""+quesList1.size());
        currentQ1=quesList1.get(list.get(0));
        txtQuestion1=(TextView)findViewById(R.id.textView1);
        rda1=(RadioButton)findViewById(R.id.radio0);
        rdb1=(RadioButton)findViewById(R.id.radio1);
        rdc1=(RadioButton)findViewById(R.id.radio2);
        rdd1=(RadioButton)findViewById(R.id.radio3);
        butNext1=(Button)findViewById(R.id.button1);
        setQuestionView();
        grp = (RadioGroup) findViewById(R.id.radioGroup1);
        butNext1.setEnabled(false);
        grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i== R.id.radio0 || i == R.id.radio1 || i==R.id.radio2 || i == R.id.radio3)
                    butNext1.setEnabled(true);
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(30);
        progressBar.setProgress(1);
        butNext1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = progress+1;
                progressBar.setProgress(progress);
                RadioButton answer = (RadioButton) findViewById(grp.getCheckedRadioButtonId());
                //Log.d("yourans", currentQ1.getANSWER1() + " " + answer.getText());
                if (currentQ1.getANSWER2().equals(answer.getText())) {
                    score++;
                    //Log.d("score", "Your score" + score1);
                }
                else
                {
                    wrongQuestListOS.add(number, currentQ1.getQUESTION2());
                    selectedAnsOS.add(number, answer.getText().toString());
                    actualAnswerOS.add(number, currentQ1.getANSWER2());
                    number++;
                }
                grp.clearCheck();
                butNext1.setEnabled(false);
                if (ctr1 < 31) {
                    if (ctr1 == 30) {
                        butNext1.setText("End Test");
                    }
                    currentQ1 = quesList1.get(list.get(ctr1));
                    setQuestionView();
                } else {
                    timer.onFinish();
                    timer.cancel();
                }
            }
        });
    }

    public class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String hms = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            textViewTime1.setText(hms);
        }

        @Override
        public void onFinish() {
            showResult();
        }
    }

    public void showResult(){
        Intent intent = new Intent(QuizSec3Activity.this, ResultActivity.class);
        Bundle b = new Bundle();
        b.putInt("scoreHardware", score);//Your score
        b.putString("section",tableName);//Your table name
        b.putString("category",catName);//Your category name
        intent.putStringArrayListExtra("wrongQuestions", wrongQuestListOS);
        intent.putStringArrayListExtra("selectedAnswer", selectedAnsOS);
        intent.putStringArrayListExtra("actualAnswer", actualAnswerOS);
        intent.putExtras(b); //Put your score to your next Intent
        startActivity(intent);
        finish();
    }

    private void setQuestionView(){
        txtQuestion1.setText(currentQ1.getQUESTION2());
        rda1.setText(currentQ1.getOPTA2());
        rdb1.setText(currentQ1.getOPTB2());
        rdc1.setText(currentQ1.getOPTC2());
        rdd1.setText(currentQ1.getOPTD2());
        if(ctr1<10)
            qstnNo.setText("0" + ctr1 + "/30");
        else
            qstnNo.setText("" + ctr1+ "/30");
        ctr1++;
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("If you close all your progress would not be saved... Do you wish to exit ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();

        alert.show();
    }

}
