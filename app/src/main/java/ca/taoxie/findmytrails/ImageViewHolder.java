package ca.taoxie.findmytrails;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Max on 2016/3/29.
 */
public class ImageViewHolder extends RecyclerView.ViewHolder {
    protected ImageView thumbnail;


    public ImageViewHolder(View itemView) {
        super(itemView);
        this.thumbnail=(ImageView)itemView.findViewById(R.id.id_index_gallery_item_image);


    }
}
