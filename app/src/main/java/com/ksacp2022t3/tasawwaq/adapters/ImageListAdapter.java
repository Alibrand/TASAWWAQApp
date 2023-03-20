package com.ksacp2022t3.tasawwaq.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.ksacp2022t3.tasawwaq.R;
import com.ksacp2022t3.tasawwaq.models.GlideApp;

import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageItem> {
    List<Uri> image_list;
    Context context;

    public ImageListAdapter(List<Uri> image_list, Context context) {
        this.image_list = image_list;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image,parent,false);
        return new ImageItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageItem holder, int position) {
        int pos=position;
        Uri image= image_list.get(pos);


        GlideApp.with(context)
                .load(image)
                .transform(new CenterCrop(),new RoundedCorners(25))
                .into(holder.image);

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_list.remove(image);

                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, image_list.size());

            }
        });
    }

    @Override
    public int getItemCount() {
        return image_list.size();
    }
}

class  ImageItem extends RecyclerView.ViewHolder{
    ImageView image;
    ImageButton btn_delete;

    public ImageItem(@NonNull View itemView) {
        super(itemView);
        image=itemView.findViewById(R.id.image);
        btn_delete=itemView.findViewById(R.id.btn_delete);
    }
}
