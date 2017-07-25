package com.example.vinayak.hw07;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    Button loginButton;
    Button createAccountButton;

    FirebaseAuth refAuth;
    FirebaseDatabase refDatabse;

    DatabaseReference mref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        refAuth = FirebaseAuth.getInstance();
        refDatabse = FirebaseDatabase.getInstance();
        ImageView myimageview = (ImageView) findViewById(R.id.myimageView);
        myimageview.setImageResource(R.drawable.chat);
        email = (EditText) findViewById(R.id.etusename);
        password = (EditText) findViewById(R.id.etpassword);
        loginButton = (Button) findViewById(R.id.btnlogin);
        createAccountButton = (Button) findViewById(R.id.btnsignup);

        FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null)
        {
            final Intent i =new Intent(getApplicationContext(),CreateProfile.class);
            final String s = user.getEmail();
            i.putExtra("useremail", s);

            finish();
            startActivity(i);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String userEmail = email.getText().toString().trim();
                String userPassword = password.getText().toString().trim();

                if(userEmail.length()<1 || userPassword.length()<1)
                {
                    Toast.makeText(getApplicationContext(),"Enter proper details", Toast.LENGTH_LONG).show();
                }
                else {
                    refAuth.signInWithEmailAndPassword(userEmail,userPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            Toast.makeText(getApplicationContext(),"Login Successfull", Toast.LENGTH_LONG).show();
                            final Intent intent = new Intent(LoginActivity.this, CreateProfile.class);
                            intent.putExtra("useremail", userEmail);

                            finish();
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                            Toast.makeText(getApplicationContext(),"Invalid Credentials",Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });

        Button createAccountButton = (Button) findViewById(R.id.btnsignup);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                finish();
                startActivity(intent);

            }
        });


    }}
