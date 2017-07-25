package com.example.vinayak.hw07;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class CreateProfile extends AppCompatActivity {

    ImageView ivimage;
    ImageButton ibgallery;
    Button btncreate;
    EditText etfname;
    EditText etlname;
    EditText etemail;
    RadioGroup rggender;

    String imgurl=null;

    FirebaseAuth refAuth;
    FirebaseDatabase refDatabse;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageref;

    static String myuuid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        ivimage = (ImageView) findViewById(R.id.cpivimage);
        ibgallery = (ImageButton) findViewById(R.id.cpibgallery);
        btncreate = (Button) findViewById(R.id.cpbtncreate);
        etfname = (EditText) findViewById(R.id.cpetfname);
        etlname = (EditText) findViewById(R.id.cpetlname);
        etemail = (EditText) findViewById(R.id.cpetemail);
        rggender = (RadioGroup) findViewById(R.id.cprgender);

        refAuth = FirebaseAuth.getInstance();
        refDatabse = FirebaseDatabase.getInstance();

        storageref = storage.getReferenceFromUrl("gs://mychatapplication-e21dc.appspot.com/");




        DatabaseReference mrefcheckprofile = FirebaseDatabase.getInstance().getReference().child("users");

        mrefcheckprofile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int flag=0;

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    UserProfile tempprof = postSnapshot.getValue(UserProfile.class);

                    if(tempprof.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                    {
                        flag=1;
                        myuuid=tempprof.getUuid();
                        break;
                    }
                }

                if(flag==1)
                {
                    Intent i = new Intent(getApplication(), Messages.class);

                    finish();
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        etemail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        etemail.setEnabled(false);

        ibgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
            }
        });

        btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etfname.getText().toString().trim().length()<1)
                {
                    etfname.setError("Please enter the correct First name");
                }
                else if(etlname.getText().toString().trim().length()<1)
                {
                    etlname.setError("Please enter the correct Last name");
                }
                else {

                    final UserProfile profile = new UserProfile();

                    profile.setFname(etfname.getText().toString().trim());
                    profile.setLname(etlname.getText().toString().trim());
                    profile.setEmail(etemail.getText().toString().trim());

                    if(rggender.getCheckedRadioButtonId()==R.id.cprbmale)
                        profile.setGender("male");
                    else
                        profile.setGender("female");


                    final DatabaseReference pushid = refDatabse.getReference().child("users").push();

                    profile.setUuid(pushid.getKey());

                    myuuid=pushid.getKey();

                    profile.setImage(imgurl);



                    ////
                    if(imgurl!=null)
                    {
                        final StorageReference ImagesRef = storageref.child("profileImages/"+ UUID.randomUUID()+".jpg");

                        ImagesRef.putFile(Uri.parse(imgurl)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                profile.setImage(ImagesRef.getPath());



                                pushid.setValue(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                            Toast.makeText(getApplicationContext(),"Profile created successfully",Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Failure",Toast.LENGTH_LONG).show();
                            }
                        });


                    }
                    else {

                        pushid.setValue(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(getApplicationContext(),"Profile created successfully",Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }

            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == 100) {
                    // Get the url from data
                    Uri selectedImageUri = data.getData();
                    if (null != selectedImageUri) {
                        // Set the image in ImageView
                        ivimage.setImageURI(selectedImageUri);
                        imgurl = selectedImageUri.toString();

                    }

                }
            }
        }
        catch (Exception e)
        {
            Log.d("test",e.getMessage());
            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
