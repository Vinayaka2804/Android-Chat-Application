package com.example.vinayak.hw07;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {


    Button signup, cancel;
    EditText fullName, email, password, cpassword;
    FirebaseAuth refAuth;
    FirebaseDatabase refDatabse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signup = (Button) findViewById(R.id.button_signup);
        cancel = (Button) findViewById(R.id.button_cancel);

        email = (EditText) findViewById(R.id.editText_email);
        password = (EditText) findViewById(R.id.editText_password);
        cpassword = (EditText) findViewById(R.id.editText_password1);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String userEmail = email.getText().toString().trim();
                final String userPassword = password.getText().toString().trim();
                final String confirmPassword = password.getText().toString().trim();

                if(!userPassword.equals(confirmPassword))
                {
                    Toast.makeText(getApplicationContext(),"Passwords Do not match",Toast.LENGTH_LONG).show();
                    password.setText("");
                    cpassword.setText("");
                }
                else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(userEmail, userPassword).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Account couldnt be created", Toast.LENGTH_LONG).show();

                            Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(getApplicationContext(), "Account Created Successfully", Toast.LENGTH_LONG).show();


                            Toast.makeText(SignUpActivity.this, "Account was created.", Toast.LENGTH_SHORT).show();

                            FirebaseAuth.getInstance().signOut();

                            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                            finish();
                            startActivity(i);
                        }
                    });
                }

            }
        });




    }
}
