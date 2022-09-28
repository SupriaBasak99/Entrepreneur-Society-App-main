package com.example.entrepreneursociety.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.entrepreneursociety.Adapter.TagAdapter;
import com.example.entrepreneursociety.Adapter.UserAdapter;
import com.example.entrepreneursociety.Model.User;
import com.example.entrepreneursociety.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<User> eUsers;
    private UserAdapter userAdapter;

    private RecyclerView recyclerViewTags;
    private List<String> eHashTags;
    private List<String> eHashTagsCount;
    private TagAdapter tagAdapter;

    private SocialAutoCompleteTextView search_bar;

    private ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewTags = view.findViewById(R.id.recycler_view_tags);
        recyclerViewTags.setHasFixedSize(true);
        recyclerViewTags.setLayoutManager(new LinearLayoutManager(getContext()));

        eHashTags = new ArrayList<>();
        eHashTagsCount = new ArrayList<>();
        tagAdapter = new TagAdapter(getContext(), eHashTags, eHashTagsCount);
        recyclerViewTags.setAdapter(tagAdapter);

        eUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), eUsers,true);
        recyclerView.setAdapter(userAdapter);

        search_bar = view.findViewById(R.id.search_bar);

        progressBar = view.findViewById(R.id.progress_circular);

        readUsers();
        readTags();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        return view;
    }

    private void readTags() {

        FirebaseDatabase.getInstance().getReference().child("HashTags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eHashTags.clear();
                eHashTagsCount.clear();

                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    eHashTags.add(snapshot1.getKey());
                    eHashTagsCount.add(snapshot1.getChildrenCount() + "");
                }

                tagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(search_bar.getText().toString())){
                    eUsers.clear();


                    progressBar.setVisibility(View.GONE);
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        User user = snapshot1.getValue(User.class);
                        eUsers.add(user);
                    }

                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchUser(String s){
        Query query = FirebaseDatabase.getInstance().getReference().child("Users").
                orderByChild("username").startAt(s).endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eUsers.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    eUsers.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filter(String text){
        List<String> eSearchTags = new ArrayList<>();
        List<String> eSearchTagsCount = new ArrayList<>();

        for (String s : eHashTags) {
            if (s.toLowerCase().contains(text.toLowerCase())){
                eSearchTags.add(s);
                eSearchTagsCount.add(eHashTagsCount.get(eHashTags.indexOf(s)));
            }
        }

        tagAdapter.filter(eSearchTags,eSearchTagsCount);
    }
}