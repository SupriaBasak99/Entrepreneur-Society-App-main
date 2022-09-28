package com.example.entrepreneursociety.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.entrepreneursociety.Fragments.PostDetailFragment;
import com.example.entrepreneursociety.Model.Post;
import com.example.entrepreneursociety.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private Context eContext;
    private List<Post> ePosts;

    public PhotoAdapter(Context mContext, List<Post> mPosts) {
        this.eContext = mContext;
        this.ePosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(eContext).inflate(R.layout.photo_item, parent, false);
        return  new PhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Post post = ePosts.get(position);
        Picasso.get().load(post.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.postImage);

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid", post.getPostid()).apply();

                ((FragmentActivity)eContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return ePosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView postImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.post_image);
        }
    }

}
