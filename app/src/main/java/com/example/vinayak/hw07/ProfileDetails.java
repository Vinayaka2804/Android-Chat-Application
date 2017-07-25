package com.example.vinayak.hw07;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileDetails extends AppCompatActivity {

    UserProfile usertemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        TextView tvfname =(TextView) findViewById(R.id.profdisfname);
        TextView tvlname =(TextView) findViewById(R.id.profdislname);
        TextView tvemail =(TextView) findViewById(R.id.profdisemail);
        TextView tvgender =(TextView) findViewById(R.id.profdisgender);

        final ImageView ivimage = (ImageView) findViewById(R.id.profdisimage);


         usertemp = new UserProfile();

        usertemp = (UserProfile) getIntent().getExtras().getSerializable("profiledetails");

        tvfname.setText(usertemp.getFname());
        tvlname.setText(usertemp.getLname());
        tvgender.setText(usertemp.getGender());
        tvemail.setText(usertemp.getEmail());

        if(usertemp.getImage()!=null)
        {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReferenceFromUrl("gs://mychatapplication-e21dc.appspot.com/");
            StorageReference storageref = storageReference.child(usertemp.getImage());

            storageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    Picasso.with(getApplicationContext()).load(uri).into(ivimage);
                }
            });
        }
        else {

            ivimage.setImageResource(R.mipmap.noimage);

        }

        findViewById(R.id.profdiscancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

        Button btnmsg = (Button) findViewById(R.id.profdisbtnmessage);

        btnmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),MyChat.class);
                i.putExtra("chatwith",usertemp);
                finish();
                startActivity(i);

            }
        });

    }
}
