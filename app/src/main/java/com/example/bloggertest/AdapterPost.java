package com.example.bloggertest;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.HolderPost> {

    private Context context;
    private ArrayList<ModelPost> postArrayList;

    //constructor

    public AdapterPost(Context context, ArrayList<ModelPost> postArrayList) {
        this.context = context;
        this.postArrayList = postArrayList;
    }

    @NonNull
    @Override
    public HolderPost onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_post.xml
        View view  = LayoutInflater.from(context).inflate(R.layout.row_post,parent, false);
        return new HolderPost(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPost holder, int position) {
        //get data, set data, handle click etc
        ModelPost model = postArrayList.get(position); //get data at specific position

        //get data
        String authorName = model.getAuthorName();
        String content = model.getContent();
        String id = model.getId();
        String published = model.getPublished();
        String selfLink = model.getSelfLink();
        String title = model.getTitle();
        String updated = model.getUpdated();
        String url = model.getUrl();

        //content/ description is in html/web form, we need to convert it to simple text using jsoup library

        Document document = Jsoup.parse(content);
        try{
            //there may be multiple images, getting first image from the post

            Elements elements = document.select("img");
            String image = elements.get(0).attr("src");
            Picasso.get().load(image).placeholder(R.mipmap.ic_launcher).into(holder.imageIv);
        }catch (Exception e){
            //exception occured while retriving image, setting to default.
            holder.imageIv.setImageResource(R.mipmap.ic_launcher);
        }

        //format date
        String gmtDate = published;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy K:mm a"); //e.g 25/10/2021 02:12 PM
        String formattedDate = "";
        try{
            Date date = dateFormat.parse(gmtDate);
            formattedDate = dateFormat2.format(date);
        }catch (Exception e){
            formattedDate = published;
            e.printStackTrace();
        }

        holder.titleTv.setText(title);
        holder.descriptionTv.setText(document.text());
        holder.publishInfoTv.setText("By " + authorName + " " + formattedDate);

        //handle click on post, start activity with post id
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //intent to start activity
                Intent intent = new Intent(context,PostDetailsActivity.class);
                intent.putExtra("postId",id);
                intent.putExtra("url", url);
                intent.putExtra("title",title);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    //ViewHOlder class that hold UI views of row_post.cxml
    class HolderPost extends RecyclerView.ViewHolder{

        //UI Views of row_post.xml
        ImageButton moreBtn;
        TextView titleTv,publishInfoTv, descriptionTv;
        ImageView imageIv;
        public HolderPost(@NonNull View itemView) {
            super(itemView);

            //init UI Views
            moreBtn = itemView.findViewById(R.id.moreBtn);
            titleTv = itemView.findViewById(R.id.titleTv);
            publishInfoTv = itemView.findViewById(R.id.publishInfoTv);
            imageIv = itemView.findViewById(R.id.imageIv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);

        }
    }
}
