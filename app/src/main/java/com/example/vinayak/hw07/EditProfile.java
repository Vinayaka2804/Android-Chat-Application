package com.example.vinayak.hw07;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class EditProfile extends AppCompatActivity {

    ImageView ivimage;
    ImageButton ibgallery;
    Button btncancel;
    Button btnupdate;
    EditText etfname;
    EditText etlname;
    EditText etemail;
    RadioGroup rggender;
    RadioButton rbmale;
    RadioButton rbfemale;

    UserProfile tempprof;

    int earlierimage=0;

    String imageurl=null;
    String uuid=null;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ivimage = (ImageView) findViewById(R.id.epivimage);
        ibgallery = (ImageButton) findViewById(R.id.epibgallery);
        btncancel = (Button) findViewById(R.id.epbtncancel);
        btnupdate = (Button) findViewById(R.id.epbtnupdate);
        etfname = (EditText) findViewById(R.id.epetfname);
        etlname = (EditText) findViewById(R.id.epetlname);
        etemail = (EditText) findViewById(R.id.epetemail);
        rggender = (RadioGroup) findViewById(R.id.eprgender);
        rbfemale = (RadioButton) findViewById(R.id.eprbfemale);
        rbmale = (RadioButton) findViewById(R.id.eprbmale);

        storageref = storage.getReferenceFromUrl("gs://mychatapplication-e21dc.appspot.com/");

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(),"Update Cancelled",Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(),Messages.class);
                finish();
                startActivity(i);

                return;
            }
        });

        DatabaseReference mrefcheckprofile = FirebaseDatabase.getInstance().getReference().child("users");

        mrefcheckprofile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int flag=0;

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    tempprof = postSnapshot.getValue(UserProfile.class);

                    if(tempprof.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                    {
                        flag=1;

                        etemail.setText(tempprof.getEmail());
                        etemail.setEnabled(false);
                        etfname.setText(tempprof.getFname());
                        etlname.setText(tempprof.getLname());

                        if(tempprof.getGender().equals("male"))
                        {
                            rbmale.setChecked(true);
                            rbfemale.setChecked(false);
                        }
                        else {

                            rbmale.setChecked(false);
                            rbfemale.setChecked(true);
                        }

                        uuid = tempprof.getUuid();

                        if(tempprof.getImage()!=null)
                        {

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageReference = storage.getReferenceFromUrl("gs://mychatapplication-e21dc.appspot.com/");
                            StorageReference storageref = storageReference.child(tempprof.getImage());
                            imageurl=  tempprof.getImage();
                            earlierimage=1;

                            storageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Picasso.with(getApplicationContext()).load(uri).into(ivimage);
                                }
                            });

                        }
                        else {
                            ivimage.setImageResource(R.mipmap.noimage);
                            imageurl=null;
                            earlierimage=0;
                        }


                        break;
                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ibgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
            }
        });


        btnupdate.setOnClickListener(new View.OnClickListener() {
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

                    if(rggender.getCheckedRadioButtonId()==R.id.eprbmale)
                        profile.setGender("male");
                    else
                        profile.setGender("female");

                    profile.setImage(imageurl);
                    profile.setUuid(uuid);

                    if(imageurl!=null){

                        if(earlierimage==1)
                        {
                            if(tempprof.getImage().equals(imageurl))
                            {
                                FirebaseDatabase.getInstance().getReference().child("users").child(profile.getUuid()).setValue(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(),"Profile Updated successfully",Toast.LENGTH_LONG).show();

                                        Intent i = new Intent(getApplicationContext(),Messages.class);
                                        finish();
                                        startActivity(i);

                                    }
                                });

                            }
                            else
                            {


                                final StorageReference ImagesRef = storageref.child(tempprof.getImage());
                                ImagesRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        final StorageReference ImagesRef2 = storageref.child("profileImages/"+ UUID.randomUUID()+".jpg");

                                        ImagesRef2.putFile(Uri.parse(imageurl)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                profile.setImage(ImagesRef2.getPath());


                                                FirebaseDatabase.getInstance().getReference().child("users").child(profile.getUuid()).setValue(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getApplicationContext(),"Profile Updated successfully",Toast.LENGTH_LONG).show();

                                                        Intent i = new Intent(getApplicationContext(),Messages.class);
                                                        finish();
                                                        startActivity(i);

                                                    }
                                                });

                                            }
                                        });

                                    }
                                });
                            }
                        }
                        else {


                            final StorageReference ImagesRef2 = storageref.child("profileImages/"+ UUID.randomUUID()+".jpg");

                            ImagesRef2.putFile(Uri.parse(imageurl)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    profile.setImage(ImagesRef2.getPath());


                                    FirebaseDatabase.getInstance().getReference().child("users").child(profile.getUuid()).setValue(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(),"Profile Updated successfully",Toast.LENGTH_LONG).show();

                                            Intent i = new Intent(getApplicationContext(),Messages.class);
                                            finish();
                                            startActivity(i);

                                        }
                                    });

                                }
                            });

                        }



                    }else {

                        FirebaseDatabase.getInstance().getReference().child("users").child(profile.getUuid()).setValue(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"Profile Updated successfully",Toast.LENGTH_LONG).show();

                                Intent i = new Intent(getApplicationContext(),Messages.class);
                                finish();
                                startActivity(i);

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
                        imageurl = selectedImageUri.toString();

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

    public void onBackPressed()
    {
        Intent i = new Intent(getApplicationContext(),Messages.class);
        finish();
        startActivity(i);

        return;
    }

}
