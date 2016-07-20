package com.globaltelecomunicationinc.abdsv3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    SharedPreferences prefs = null;
    //initialise all variables from the view
    EditText etConfirmPassword, etUsername, etPassword;
    TextView tvRegistrationNotice, tvRegistrationConfirmPasswordLabel;
    ImageView ivRegistrationIcon;
    Button btnRegister;

    public void initViews() {
        etUsername = (EditText) findViewById(R.id.etRegistrationUsername);
        etPassword = (EditText) findViewById(R.id.etRegistrationPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etRegistrationConfirmPassword);
        btnRegister = (Button) findViewById(R.id.btnRegistrer);
        ivRegistrationIcon = (ImageView) findViewById(R.id.ivRegistrationIcon);

        tvRegistrationNotice = (TextView) findViewById(R.id.tvRegistrationNotice);
        tvRegistrationConfirmPasswordLabel = (TextView) findViewById(R.id.tvRegistrationConfirmPasswordLabel);
    }

    public void setListeners() {
        etUsername.setOnClickListener(this);
        etPassword.setOnClickListener(this);
        etConfirmPassword.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    public void RegisterUser(int message) {
        initViews();
        setListeners();
        tvRegistrationNotice.setText(message);
        tvRegistrationNotice.setVisibility(View.VISIBLE);
        tvRegistrationNotice.setGravity(Gravity.CENTER_HORIZONTAL);

        /*etUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etUsername.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });*/

        etUsername.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent) {
                etUsername.setBackgroundColor(getResources().getColor(R.color.white));
                return false;
            }
        });

        etUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    //the editText has just been left
                    etUsername.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });

        /*etPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etPassword.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });*/

        etPassword.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent) {
                etPassword.setBackgroundColor(getResources().getColor(R.color.white));
                return false;
            }
        });

        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    //the editText has just been left
                    etPassword.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });

        /*etConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etConfirmPassword.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });*/

        etConfirmPassword.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent) {
                etConfirmPassword.setBackgroundColor(getResources().getColor(R.color.white));
                return false;
            }
        });

        etConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    //the editText has just been left
                    etConfirmPassword.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });

       /*btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("I am","here");
                final String confirmPassword = etConfirmPassword.getText().toString();
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                showConfirm_password();

                if((username.matches(""))||(password.matches("")) || (confirmPassword.matches(""))){
                    Toast.makeText(getApplicationContext(), " Please enter username, password and confirm password", Toast.LENGTH_LONG).show();
                    return;
                }else if(password.matches(confirmPassword)){
                    //TODO RSA encrypt the password and save it to shared preferences
                    prefs.edit().putString("username", username).apply();
                    prefs.edit().putString("password", password).apply();
                    //Start asking security Questions
                    Intent i = new Intent(RegistrationActivity.this, SecurityQuestionsActivity.class);
                    //Add your data to bundle
                    i.putExtra("username", username);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Something went wrong please send and email", Toast.LENGTH_LONG).show();
                }
            }
        });*/
    }

    public void showConfirm_password() {
        etConfirmPassword = (EditText) findViewById(R.id.etRegistrationConfirmPassword);
        etConfirmPassword.setVisibility(View.VISIBLE);
        //tvRegistrationConfirmPasswordLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        tvRegistrationConfirmPasswordLabel.setVisibility(View.VISIBLE);
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initViews();
        setListeners();

        //check to see if this is the first time the user is using the application on their device
        prefs = this.getSharedPreferences("com.globaltelecomunicationinc.abdsv3", MODE_PRIVATE);

        //Checks in the backend in this case shared preferences to see if the user created a username and security questions
        if ((prefs.getString("username", "").matches("")) || (prefs.getString("question_one", "").matches("")) || (prefs.getString("question_two", "").matches("")) || (prefs.getString("question_three", "").matches(""))) {
            //if ((prefs.getString("username", "").matches(""))||(prefs.getString("question_one","").matches("")) ||(prefs.getString("question_two","").matches("")) ||(prefs.getString("question_three","").matches(""))) {
            RegisterUser(R.string.never_registered_user_message);
        } else if (prefs.getBoolean("changepassword", true)) {
            RegisterUser(R.string.user_forgot_password_message);
            prefs.edit().putBoolean("changepassword", false).commit();
        } else {
            //can be placed in a function
            Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
            this.startActivity(i);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.etRegistrationUsername:
                etUsername.setBackgroundColor(getResources().getColor(R.color.white));
                etConfirmPassword.setBackgroundColor(Color.TRANSPARENT);
                etPassword.setBackgroundColor(Color.TRANSPARENT);
                break;
            case R.id.etRegistrationPassword:
                etPassword.setBackgroundColor(getResources().getColor(R.color.white));
                etConfirmPassword.setBackgroundColor(Color.TRANSPARENT);
                etUsername.setBackgroundColor(Color.TRANSPARENT);
                break;
            case R.id.etRegistrationConfirmPassword:
                etConfirmPassword.setBackgroundColor(getResources().getColor(R.color.white));
                etPassword.setBackgroundColor(Color.TRANSPARENT);
                etUsername.setBackgroundColor(Color.TRANSPARENT);
                break;
            case R.id.btnRegistrer:
                final String confirmPassword = etConfirmPassword.getText().toString();
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                showConfirm_password();

                if (!password.equals("")) {

                    if (password.length() >= 8) {
                        Pattern pattern = Pattern.compile("[[^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])[0-9A-Za-z]{15,}$]]");
                        Matcher matcher = pattern.matcher(password);

                        if (matcher.matches()) {

                            if (password.equals(confirmPassword)) {
                                prefs.edit().putString("username", username).apply();
                                prefs.edit().putString("password", password).apply();
                                //SavePreferences("Password", password1);
                                Intent intent = new Intent(RegistrationActivity.this, SecurityQuestionsActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                finish();
                            } else {
                                etPassword.setText("");
                                etConfirmPassword.setText("");
                                Toast.makeText(RegistrationActivity.this, "Not equal", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            etPassword.setText("");
                            etConfirmPassword.setText("");
                            Toast.makeText(RegistrationActivity.this, "Not matched", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        etPassword.setText("");
                        etConfirmPassword.setText("");
                        Toast.makeText(RegistrationActivity.this, "Length", Toast.LENGTH_LONG).show();
                    }
                } else {
                    etPassword.setText("");
                    etConfirmPassword.setText("");
                    Toast.makeText(RegistrationActivity.this, "Nothing", Toast.LENGTH_LONG).show();
                }
                /*
                if ((username.matches("")) || (password.matches("")) || (confirmPassword.matches(""))) {
                    Toast.makeText(getApplicationContext(), " Please enter username, password and confirm password", Toast.LENGTH_LONG).show();
                    return;
                } else if (password.matches(confirmPassword)) {
                    //TODO RSA encrypt the password and save it to shared preferences
                    prefs.edit().putString("username", username).apply();
                    prefs.edit().putString("password", password).apply();
                    //Start asking security Questions
                    Intent i = new Intent(RegistrationActivity.this, SecurityQuestionsActivity.class);
                    //Add your data to bundle
                    i.putExtra("username", username);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong please send and email", Toast.LENGTH_LONG).show();
                    finish();
                }*/
                break;
            default:
                finish();
                break;
        }
    }

    private void SavePreferences(String key, String value){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check to see if this is the first ime the application is being run
        if (prefs.getBoolean("firstrun", true)) {
            RegisterUser(R.string.first_time_use_message);
            prefs.edit().putBoolean("firstrun", false).commit();
        } else if (prefs.getBoolean("changepassword", true)) {
            RegisterUser(R.string.user_forgot_password_message);
            prefs.edit().putBoolean("changepassword", false).commit();
        } else {
            RegisterUser(R.string.never_registered_user_message);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.registration_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

   /*@Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }*/
}
