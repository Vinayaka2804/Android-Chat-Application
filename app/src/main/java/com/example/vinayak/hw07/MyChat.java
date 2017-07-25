package com.example.vinayak.hw07;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class MyChat extends AppCompatActivity {

    ArrayList<EachMessage> mgslist = new ArrayList<EachMessage>();
    UserProfile chatwith;
    DatabaseReference mref;
    EachMsgAdapter adapter;
    ListView mychatlist;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chat);

        adapter = new EachMsgAdapter(getApplicationContext() ,R.layout.custommsg, mgslist);
        chatwith = (UserProfile) getIntent().getExtras().getSerializable("chatwith");
        mref = FirebaseDatabase.getInstance().getReference().child("Messages");

        storageref = storage.getReferenceFromUrl("gs://mychatapplication-e21dc.appspot.com/");

        //getActionBar().setTitle(chatwith.getFname()+" "+chatwith.getLname());

        TextView tvfullname = (TextView) findViewById(R.id.mychattvfullname);

        ImageButton btnsend = (ImageButton) findViewById(R.id.mychatbtnsend);
        ImageButton btngallery = (ImageButton) findViewById(R.id.mychatbtngallery);

        mychatlist =(ListView) findViewById(R.id.mychatlist);

        final EditText ettext = (EditText) findViewById(R.id.mychatettext);



        mref.child(CreateProfile.myuuid).child(chatwith.getUuid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mgslist.removeAll(mgslist);

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    EachMessage singlemsg = postSnapshot.getValue(EachMessage.class);

                    mgslist.add(singlemsg);
                }

                mychatlist.setAdapter(adapter);
                adapter.setNotifyOnChange(true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        mychatlist.setAdapter(adapter);
        adapter.setNotifyOnChange(true);


        tvfullname.setText("Chat With "+chatwith.getFname()+" "+chatwith.getLname());


        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String text= ettext.getText().toString().trim();

                if(text.length()>0)
                {

                    final EachMessage tempmsg = new EachMessage();
                    tempmsg.setText(text);
                    tempmsg.setFrom(CreateProfile.myuuid);
                    tempmsg.setTo(chatwith.getUuid());
                    tempmsg.setImage(null);


                    try {

                        SimpleDateFormat inputformatter = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
                        tempmsg.setTime(inputformatter.format(new Date()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    tempmsg.setRead(1);

                    DatabaseReference pushid = mref.child(CreateProfile.myuuid).child(chatwith.getUuid()).push();

                    tempmsg.setPushid(pushid.getKey());

                    pushid.setValue(tempmsg).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {


                            tempmsg.setRead(0);

                            DatabaseReference pushid2 = mref.child(chatwith.getUuid()).child(CreateProfile.myuuid).push();

                            tempmsg.setPushid(pushid2.getKey());

                            pushid2.setValue(tempmsg).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    ettext.setText("");

                                    Toast.makeText(getApplicationContext(),"Message sent successfully",Toast.LENGTH_LONG).show();

                                }
                            });
                        }
                    });



                }else {
                    Toast.makeText(getApplicationContext(),"Enter a proper text to send", Toast.LENGTH_LONG).show();
                }

            }
        });


            btngallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);

                }
            });

        mychatlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                final EachMessage delmessage = mgslist.get(position);

                    mref.child(CreateProfile.myuuid).child(chatwith.getUuid()).child(delmessage.getPushid()).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            Toast.makeText(getApplicationContext(),"Message Deleted Successfully",Toast.LENGTH_LONG).show();
                        }
                    });


                return false;
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

                        final EachMessage eachmsg  = new EachMessage();

                        final StorageReference ImagesRef2 = storageref.child("messageimages/"+ UUID.randomUUID()+".jpg");


                        ImagesRef2.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                eachmsg.setImage(ImagesRef2.getPath());
                                eachmsg.setText(null);
                                eachmsg.setFrom(CreateProfile.myuuid);
                                eachmsg.setTo(chatwith.getUuid());
                                try {

                                    SimpleDateFormat inputformatter = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
                                    eachmsg.setTime(inputformatter.format(new Date()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                eachmsg.setRead(1);

                                DatabaseReference pushid = mref.child(CreateProfile.myuuid).child(chatwith.getUuid()).push();

                                eachmsg.setPushid(pushid.getKey());

                                pushid.setValue(eachmsg).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {


                                        eachmsg.setRead(0);

                                        DatabaseReference pushid2 = mref.child(chatwith.getUuid()).child(CreateProfile.myuuid).push();

                                        eachmsg.setPushid(pushid2.getKey());

                                        pushid2.setValue(eachmsg).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {


                                                Toast.makeText(getApplicationContext(),"Message sent successfully",Toast.LENGTH_LONG).show();

                                            }
                                        });
                                    }
                                });



                            }
                        });




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
