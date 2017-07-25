package com.example.vinayak.hw07;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Vinayaka on 11/22/2016.
 */
public class CustomUsersAdapter extends ArrayAdapter<UserProfile> {

    List<UserProfile> mData;
    Context mContext;
    int mResource;
    int unread=0;

    public CustomUsersAdapter(Context context, int resource, List<UserProfile> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mData = objects;
        this.mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, parent, false);
        }

        UserProfile user = mData.get(position);

        final TextView tvfnmae = (TextView) convertView.findViewById(R.id.msgcustomconfname);
        final TextView tvlnmae = (TextView) convertView.findViewById(R.id.msgcustomconlname);

        final CircleImageView ivimage = (CircleImageView) convertView.findViewById(R.id.msgcustomconimage);
        final CircleImageView ivimageunread = (CircleImageView) convertView.findViewById(R.id.msgcustomconunread);



        DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("Messages").child(CreateProfile.myuuid).child(user.getUuid());

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    unread=0;

                    EachMessage tempmsgg = postSnapshot.getValue(EachMessage.class);

                    if(tempmsgg.getRead()==0)
                        unread++;
                }

                if(unread==1)
                {
                    ivimageunread.setImageResource(R.mipmap.one);
                }
                else if(unread==2)
                {
                    ivimageunread.setImageResource(R.mipmap.two);
                }
                else if(unread==3)
                {
                    ivimageunread.setImageResource(R.mipmap.three);
                }
                else if(unread==4)
                {
                    ivimageunread.setImageResource(R.mipmap.four);
                }
                else if(unread>=5)
                {
                    ivimageunread.setImageResource(R.mipmap.five);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        tvfnmae.setText(user.getFname());
        tvlnmae.setText(user.getLname());

        if(user.getImage()==null)
        {

            ivimage.setImageResource(R.mipmap.noimage);

        }else {


            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReferenceFromUrl("gs://mychatapplication-e21dc.appspot.com/");
            StorageReference storageref = storageReference.child(user.getImage());

            storageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    Picasso.with(mContext).load(uri).into(ivimage);
                }
            });


        }





        return convertView;
    }
}

