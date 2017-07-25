package com.example.vinayak.hw07;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnlyMessages.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class OnlyMessages extends Fragment {

    private OnFragmentInteractionListener mListener;

    ListView listView;
    ArrayList<UserProfile> profilelisttemp = new ArrayList<UserProfile>();
    ArrayList<UserProfile> profilelistfinal = new ArrayList<UserProfile>();
    CustomUsersAdapter adapter;

    public OnlyMessages() {
        // Required empty public constructor
    }

    private int mPage;

    public static OnlyMessages newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("ARG_PAGE", page);
        OnlyMessages fragment = new OnlyMessages();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        final DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("users");

        profilelisttemp.removeAll(profilelisttemp);
        profilelistfinal.removeAll(profilelistfinal);

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    UserProfile tempuser = postSnapshot.getValue(UserProfile.class);

                    ///

                    if (!tempuser.getEmail().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                        profilelisttemp.add(tempuser);
                    }
                }


                final DatabaseReference mref2 = FirebaseDatabase.getInstance().getReference().child("Messages").child(CreateProfile.myuuid);

                mref2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        profilelistfinal.removeAll(profilelistfinal);

                        for(int i=0;i<profilelisttemp.size();i++)
                        {
                            if(dataSnapshot.hasChild(profilelisttemp.get(i).getUuid()))
                                profilelistfinal.add(profilelisttemp.get(i));
                        }

                        listView.setAdapter(adapter);
                        adapter.setNotifyOnChange(true);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new CustomUsersAdapter(getContext(), R.layout.custommsgusers,profilelistfinal);

        return inflater.inflate(R.layout.fragment_only_messages, container, false);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView = (ListView) getView().findViewById(R.id.listViewContactsMessages);

        listView.setAdapter(adapter);
        adapter.setNotifyOnChange(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent ii = new Intent(getActivity(),MyChat.class);
                ii.putExtra("chatwith",profilelistfinal.get(i));
                getActivity().finish();
                startActivity(ii);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
