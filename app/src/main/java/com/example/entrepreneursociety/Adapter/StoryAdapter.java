package com.example.entrepreneursociety.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.entrepreneursociety.AddStoryActivity;
import com.example.entrepreneursociety.Model.Story;
import com.example.entrepreneursociety.Model.User;
import com.example.entrepreneursociety.R;
import com.example.entrepreneursociety.StoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>{

    private Context eContext;
    private List<Story> eStory;

    public StoryAdapter(Context eContext, List<Story> eStory) {
        this.eContext = eContext;
        this.eStory = eStory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==0){
            View view = LayoutInflater.from(eContext).inflate(R.layout.add_story_item, parent,false);
            return new StoryAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(eContext).inflate(R.layout.story_item, parent,false);
            return new StoryAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Story story = eStory.get(position);

        userInfo(holder,story.getUserid(),position);

        if (holder.getAdapterPosition() != 0){
            seenStory(holder,story.getUserid());
        }

        if (holder.getAdapterPosition()==0){
            myStory(holder.addstory_text,holder.story_plus,false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() == 0){
                    myStory(holder.addstory_text, holder.story_plus, true);
                } else {
                    Intent intent = new Intent(eContext, StoryActivity.class);
                    intent.putExtra("userid",story.getUserid());
                    eContext.startActivity(intent);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return eStory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView story_photo, story_plus, story_photo_seen;
        public TextView story_username, addstory_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            story_photo = itemView.findViewById(R.id.story_photo);
            story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
            story_plus = itemView.findViewById(R.id.add_story);

            story_username = itemView.findViewById(R.id.story_username);
            addstory_text = itemView.findViewById(R.id.story_add_text);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0){
            return 0;
        }
        return 1;
    }

    private void userInfo(ViewHolder viewHolder, String userId,int pos){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(eContext).load(user.getImageurl()).into(viewHolder.story_photo);
                if (pos != 0 ){
                    Glide.with(eContext).load(user.getImageurl()).into(viewHolder.story_photo_seen);
                    viewHolder.story_username.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myStory(TextView textView,ImageView imageView,boolean click){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count =0;
                long timeCurrent = System.currentTimeMillis();

                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Story story = snapshot1.getValue(Story.class);
                    if (timeCurrent > story.getTimestart() && timeCurrent < story.getTimeend()){
                        count++;
                    }
                }

                if (click){
                    if (count>0){
                        AlertDialog alertDialog = new AlertDialog.Builder(eContext).create();
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "View Story", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(eContext, StoryActivity.class);
                                intent.putExtra("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                eContext.startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Story", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(eContext, AddStoryActivity.class);
                                eContext.startActivity(intent);
                                dialog.dismiss();

                            }
                        });
                        alertDialog.show();
                    } else {
                        Intent intent = new Intent(eContext, AddStoryActivity.class);
                        eContext.startActivity(intent);
                    }

                }else {
                    if (count>0){
                        textView.setText("My Story");
                        imageView.setVisibility(View.GONE);
                    }else {
                        textView.setText("Add Story");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void seenStory(ViewHolder viewHolder, String userId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i =0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    if (!snapshot1.child("views").
                            child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()
                            && System.currentTimeMillis()<snapshot1.getValue(Story.class).getTimeend()) {
                        i++;
                    }
                }

                if (i>0){
                    viewHolder.story_photo.setVisibility(View.VISIBLE);
                    viewHolder.story_photo_seen.setVisibility(View.GONE);
                }else {
                    viewHolder.story_photo.setVisibility(View.GONE);
                    viewHolder.story_photo_seen.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}

