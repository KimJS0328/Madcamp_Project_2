package com.example.madcamp_project_2;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private ArrayList<String> imgList = null;
    private OnImgClickListener imgClkListener = null;
    private OnImgLongClickListener imgLongClkListener = null;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgView ;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (imgClkListener != null && imgList != null) {

                            imgClkListener.onImgClick(imgList.get(pos));
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (imgLongClkListener != null && imgList != null) {
                            imgLongClkListener.onImgLongClick(imgList.get(pos));
                            return true;
                        }
                    }
                    return false;
                }
            });
            imgView = itemView.findViewById(R.id.image_view);
        }
    }

    GalleryAdapter (ArrayList<String> list, Context context) {
        imgList = list; this.context = context;
    }

    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.galleryview_item, parent, false) ;
        GalleryAdapter.ViewHolder vh = new GalleryAdapter.ViewHolder(view) ;

        return vh ;
    }


    @Override
    public void onBindViewHolder(GalleryAdapter.ViewHolder holder, int position) {
        Picasso.with(context)
                .load("http://192.249.19.250:7680/upload/" + imgList.get(position))
                .into(holder.imgView);

    }

    @Override
    public int getItemCount() {
        return imgList.size() ;
    }

    public interface OnImgClickListener {
        void onImgClick(String image);
    }

    public interface OnImgLongClickListener {
        void onImgLongClick(String image);
    }


    public void setOnImgClickListener(OnImgClickListener listener) {
        this.imgClkListener = listener;
    }

    public void setOnImgLongClickListener(OnImgLongClickListener listener) {
        this.imgLongClkListener = listener;
    }

}
