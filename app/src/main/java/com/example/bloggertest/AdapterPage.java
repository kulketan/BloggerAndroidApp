package com.example.bloggertest;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterPage extends RecyclerView.Adapter<AdapterPage.HolderPage> {

    private Context context;
    private ArrayList<ModelPage> pageArrayList;

    //constructor

    public AdapterPage(Context context, ArrayList<ModelPage> pageArrayList) {
        this.context = context;
        this.pageArrayList = pageArrayList;
    }

    @NonNull
    @Override
    public HolderPage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_page.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_page, parent, false);
        return new HolderPage(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPage holder, int position) {
        //get data
        ModelPage modelPage = pageArrayList.get(position);
        String authorName = modelPage.getAuthorName();
        String content = modelPage.getContent();
        String id = modelPage.getId();
        String published = modelPage.getPublished();
        String selfLink = modelPage.getSelfLink();
        String title = modelPage.getTitle();
        String updated = modelPage.getUpdated();
        String url = modelPage.getUrl();

        //descritpion is in html format, format it
        Document document = Jsoup.parse(content);
        try {
            //get thumbnail
            Elements elements = document.select("img");
            String image = elements.get(0).attr("src");
            //set the image, if there is any
            Picasso.get().load(image).placeholder(R.drawable.ic_image_black).into(holder.imageIv);

        }catch (Exception e){
            //if exception occurs due to no image or any other reason set default image
            holder.imageIv.setImageResource(R.drawable.ic_image_black);
        }

        //format data from GMT to dd/MM/yyyy

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
                Intent intent = new Intent(context,PageDetailsActivity.class);
                intent.putExtra("pageId",id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pageArrayList.size();
    }


    //View holder class for row_page.xml
    class HolderPage extends RecyclerView.ViewHolder {

        //UI views of row_page.xml
        private TextView titleTv, publishInfoTv, descriptionTv;
        private ImageView imageIv;

        public HolderPage(@NonNull View itemView) {
            super(itemView);

            //init UI views
            titleTv = itemView.findViewById(R.id.titleTv);
            publishInfoTv = itemView.findViewById(R.id.publishInfoTv);
            imageIv = itemView.findViewById(R.id.imageIv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);

        }
    }
}
