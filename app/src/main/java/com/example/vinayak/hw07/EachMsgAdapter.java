package com.example.vinayak.hw07;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Vinayaka on 11/22/2016.
 */
public class EachMsgAdapter extends ArrayAdapter<EachMessage> {

    List<EachMessage> mData;
    Context mContext;
    int mResource;

    public EachMsgAdapter(Context context, int resource, List<EachMessage> objects) {
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

        final EachMessage msg = mData.get(position);



        final TextView tvtext = (TextView) convertView.findViewById(R.id.cusmsgtvtext);
        TextView tvtime = (TextView) convertView.findViewById(R.id.cusmsgtvtime);
        final ImageView ivmsgimage =  (ImageView) convertView.findViewById(R.id.cusmsgivthumb);

        tvtime.setText(msg.getTime());

        if(msg.getText()!=null)
        {
            tvtext.setText(msg.getText());
            ivmsgimage.setVisibility(View.GONE);
        }
        else {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReferenceFromUrl("gs://mychatapplication-e21dc.appspot.com/");
            StorageReference storageref = storageReference.child(msg.getImage());

            storageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    Picasso.with(mContext).load(uri).into(ivmsgimage);
                    tvtext.setVisibility(View.GONE);
                }
            });
        }



//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) convertView.getLayoutParams();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ivmsgimage.getLayoutParams());



        tvtext.setTextColor(Color.BLACK);
        tvtime.setTextColor(Color.BLACK);

        if(CreateProfile.myuuid.equals(msg.getFrom()))
        {
            params.weight = 1.0f;
            params.gravity = Gravity.RIGHT;

            params2.weight = 1.0f;
            params2.gravity = Gravity.RIGHT;

            if(msg.getText()!=null)
                tvtext.setLayoutParams(params);
            else
                ivmsgimage.setLayoutParams(params2);

            tvtime.setLayoutParams(params);

        }else {

            params.weight = 1.0f;
            params.gravity = Gravity.LEFT;

            params2.weight = 1.0f;
            params2.gravity = Gravity.LEFT;

            if(msg.getText()!=null)
                tvtext.setLayoutParams(params);
            else
                ivmsgimage.setLayoutParams(params2);

            tvtime.setLayoutParams(params);

        }

        if(msg.getRead()==0)
        {
            convertView.setBackgroundColor(Color.GRAY);

            msg.setRead(1);

            CountDownTimer count = new CountDownTimer(2000, 1000) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {



                }
            }.start();

            DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("Messages").child(CreateProfile.myuuid).child(msg.getFrom()).child(msg.getPushid());
            mref.setValue(msg);

        }

        return convertView;
    }
}

