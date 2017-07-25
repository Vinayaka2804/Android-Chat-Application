package com.example.vinayak.hw07;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Vinayaka on 11/22/2016.
 */
public class CustomContactsAdapter  extends ArrayAdapter<UserProfile> {

    List<UserProfile> mData;
    Context mContext;
    int mResource;

    public CustomContactsAdapter(Context context, int resource, List<UserProfile> objects) {
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



        TextView tvfnmae = (TextView) convertView.findViewById(R.id.customconfname);
        TextView tvlnmae = (TextView) convertView.findViewById(R.id.customconlname);

        final CircleImageView ivimage = (CircleImageView) convertView.findViewById(R.id.customconimage);

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

