package com.example.entrepreneursociety.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.entrepreneursociety.R;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder>{

    private Context eContext;
    private List<String> eTags;
    private List<String> eTagsCount;

    public TagAdapter(Context eContext, List<String> eTags, List<String> eTagsCount) {
        this.eContext = eContext;
        this.eTags = eTags;
        this.eTagsCount = eTagsCount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(eContext).inflate(R.layout.tag_item,parent,false);

        return new TagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tag.setText("# " + eTags.get(position));
        holder.noOfPosts.setText(eTagsCount.get(position) + " posts");

    }

    @Override
    public int getItemCount() {
        return eTags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tag;
        public TextView noOfPosts;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tag = itemView.findViewById(R.id.hash_tag);
            noOfPosts = itemView.findViewById(R.id.number_of_posts);
        }
    }
    public void filter (List<String> filterTags , List<String> filterTagsCount) {
        this.eTags = filterTags;
        this.eTagsCount = filterTagsCount;

        notifyDataSetChanged();
    }
}
