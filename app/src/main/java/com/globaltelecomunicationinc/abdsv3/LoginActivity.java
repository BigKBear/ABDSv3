package com.globaltelecomunicationinc.abdsv3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences prefs;
    EditText etLoginPassword;
    TextView tvLoginNotice, tvLoginForgotPassword;
    Button btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        File folder= new File(Environment.getExternalStorageDirectory() + "/map");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            // Do something on success
            Toast.makeText(getApplicationContext(),"SDcard Folder created "+ folder.getPath(),Toast.LENGTH_LONG).show();
        } else {
            // Do something else on failure
            Toast.makeText(getApplicationContext(),"SDcard Folder ALREADY exists " + folder.getPath(),Toast.LENGTH_LONG).show();
        }

        prefs = this.getSharedPreferences("com.globaltelecomunicationinc.abdsv3", MODE_PRIVATE);

        tvLoginNotice = (TextView) findViewById(R.id.tvLoginNotice);
        etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);
        tvLoginForgotPassword = (TextView) findViewById(R.id.tvLoginForgotPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        String LoginPassword = etLoginPassword.getText().toString();
        tvLoginNotice.setText("Welcome back " + prefs.getString("username", ""));

        etLoginPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etLoginPassword.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });

        etLoginPassword.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent) {
                etLoginPassword.setBackgroundColor(getResources().getColor(R.color.white));
                return false;
            }
        });

        etLoginPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    //the editText has just been left
                    etLoginPassword.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });

        tvLoginForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SecurityQuestionsActivity.class);

                String username = prefs.getString("username", "");
                String q1 = prefs.getString("question_one", "");
                String q2 = prefs.getString("question_two", "");
                String q3 = prefs.getString("question_three", "");

                //Add your data to bundle
                i.putExtra("username", username);
                i.putExtra("q1", q1);
                i.putExtra("q2", q2);
                i.putExtra("q3", q3);

                startActivity(i);

                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String LoginPassword = etLoginPassword.getText().toString();
                //TODO:use the secure RSA encryption to make the pasword check not shared preferences which is currently being used
                if (prefs.getString("password", "").matches(LoginPassword)) {
                    //Give user access
                    Intent i = new Intent(LoginActivity.this, FileListActivity.class);
                    String u_username = i.getStringExtra("u_username");
                    //Add data to bundle
                    i.putExtra("u_username", prefs.getString("username", ""));
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                } else {
                    Toast.makeText(getApplicationContext(), " Passwords did not match", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);
        etLoginPassword.getText().clear();
    }
}
