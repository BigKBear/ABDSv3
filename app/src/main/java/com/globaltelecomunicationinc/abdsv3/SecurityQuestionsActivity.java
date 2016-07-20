package com.globaltelecomunicationinc.abdsv3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SecurityQuestionsActivity extends AppCompatActivity {

    SharedPreferences prefs = null;
    Button submit;
    TextView tvSecurityQuestionTitle;
    EditText etSecurity_q1;
    EditText etSecurity_q2;
    EditText etSecurity_q3;
    EditText etQ1_answer;
    EditText etQ2_answer;
    EditText etQ3_answer;

    public void validateAnswers(View v) {

        etQ1_answer = (EditText) findViewById(R.id.etSecurityQuestionOneAnswer);
        etQ2_answer = (EditText) findViewById(R.id.etSecurityQuestionTwoAnswer);
        etQ3_answer = (EditText) findViewById(R.id.etSecurityQuestionThreeAnswer);

        prefs = getSharedPreferences("com.globaltelecomunicationinc.abdsv3", MODE_PRIVATE);

        String Q1_answer = etQ1_answer.getText().toString();
        String Q2_answer = etQ2_answer.getText().toString();
        String Q3_answer = etQ3_answer.getText().toString();

        if (prefs.getString("question_one_answer", "").matches(Q1_answer)) {
            if (prefs.getString("question_two_answer", "").matches(Q2_answer)) {
                if (prefs.getString("question_three_answer", "").matches(Q3_answer)) {
                    prefs.edit().putBoolean("changepassword", true).commit();
                    //TODO allow the user to change their pasword
                    Intent i = new Intent(SecurityQuestionsActivity.this, RegistrationActivity.class);
                    this.startActivity(i);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                } else {
                    Toast.makeText(getApplicationContext(), "You have entered incorrect answers", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "You have entered incorrect answers", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "You have entered incorrect answers", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_questions);

        prefs = getSharedPreferences("com.globaltelecomunicationinc.abdsv3", MODE_PRIVATE);
        submit = (Button) findViewById(R.id.btnSubmitSecurityQuestions);
        etSecurity_q1 = (EditText) findViewById(R.id.etSecurityQuestionOne);
        etSecurity_q2 = (EditText) findViewById(R.id.etSecurityQuestionTwo);
        etSecurity_q3 = (EditText) findViewById(R.id.etSecurityQuestionThree);
        etQ1_answer = (EditText) findViewById(R.id.etSecurityQuestionOneAnswer);
        etQ2_answer = (EditText) findViewById(R.id.etSecurityQuestionTwoAnswer);
        etQ3_answer = (EditText) findViewById(R.id.etSecurityQuestionThreeAnswer);

        Intent mIntent = getIntent();
        if (mIntent != null) {

            String username = mIntent.getStringExtra("username");
            String q1 = mIntent.getStringExtra("q1");
            String q2 = mIntent.getStringExtra("q2");
            String q3 = mIntent.getStringExtra("q3");

            if (q1 != null) {
                tvSecurityQuestionTitle = (TextView) findViewById(R.id.tvSecurityQuestionTitle);
                tvSecurityQuestionTitle.setText("Please enter " + username + " Security Questions");

                submit.setTag(1);
                submit.setText("Check Answers");
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Button submit_change = (Button) findViewById(R.id.btnSubmitSecurityQuestions);
                        final int status = (Integer) v.getTag();
                        if (status == 1) {
                            // mPlayer.start();
                            submit_change.setText("Check Answers");
                            validateAnswers(v);
                            v.setTag(0); //check answers
                        } else {
                            submit_change.setText("Submit");
                            v.setTag(1); //submit
                        }
                    }
                });


                etSecurity_q1.setText(q1);
                etSecurity_q1.setTag(etSecurity_q1.getKeyListener());
                etSecurity_q1.setKeyListener(null);

                etSecurity_q2.setText(q2);
                etSecurity_q2.setTag(etSecurity_q2.getKeyListener());
                etSecurity_q2.setKeyListener(null);

                etSecurity_q3.setText(q3);
                etSecurity_q3.setTag(etSecurity_q3.getKeyListener());
                etSecurity_q3.setKeyListener(null);
            } else {
                Toast.makeText(getApplicationContext(), " Please enter all securiy questions and answers ", Toast.LENGTH_LONG).show();
                prefs = getSharedPreferences("com.globaltelecomunicationinc.abdsv3", MODE_PRIVATE);

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String Security_q1 = etSecurity_q1.getText().toString();
                        String Security_q2 = etSecurity_q2.getText().toString();
                        String Security_q3 = etSecurity_q3.getText().toString();
                        String Q1_answer = etQ1_answer.getText().toString();
                        String Q2_answer = etQ2_answer.getText().toString();
                        String Q3_answer = etQ3_answer.getText().toString();

                        if ((Security_q1.matches("")) || (Security_q2.matches("")) || (Security_q3.matches("")) || (Q1_answer.matches("")) || (Q2_answer.matches("")) || (Q3_answer.matches(""))) {
                            Toast.makeText(getApplicationContext(), " Please enter all securiy questions and answers ", Toast.LENGTH_LONG).show();
                            submit.setText("Resubmit");
                            return;
                        } else {
                            prefs.edit().putString("question_one", Security_q1).apply();
                            prefs.edit().putString("question_two", Security_q2).apply();
                            prefs.edit().putString("question_three", Security_q3).apply();

                            prefs.edit().putString("question_one_answer", Q1_answer).apply();
                            prefs.edit().putString("question_two_answer", Q2_answer).apply();
                            prefs.edit().putString("question_three_answer", Q3_answer).apply();

                            //Give user access
                            Intent i = new Intent(SecurityQuestionsActivity.this, FileListActivity.class);
                            String username = i.getStringExtra("username");
                            //Add data to bundle
                            i.putExtra("username", username);
                            startActivity(i);
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            finish();
                        }
                    }
                });

            }
        } else {
            Toast.makeText(getApplicationContext(), "Bad username and password", Toast.LENGTH_LONG).show();
        }
    }
}