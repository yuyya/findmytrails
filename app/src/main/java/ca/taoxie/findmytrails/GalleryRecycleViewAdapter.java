package ca.taoxie.findmytrails;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Max on 2016/3/29.
 */
public class GalleryRecycleViewAdapter extends RecyclerView.Adapter<ImageViewHolder>{
    private List<Image> imageList;
    private Context context;


    private String LOG_TAG = GalleryRecycleViewAdapter.class.getSimpleName();



    public GalleryRecycleViewAdapter(List<Image> imageList, Context context) {
        this.imageList = imageList;
        this.context = context;

    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, null);
        ImageViewHolder imageViewHolder=new ImageViewHolder(view);
        return imageViewHolder;
        //return null;
    }


    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Image image = imageList.get(position);
        String url = image.getImgUrl();
        if(url.equalsIgnoreCase("addicon")){
            Picasso.with(context).load(R.drawable.add_photo)
                    .error(R.drawable.placeholder)
                    .resize(140, 140)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(holder.thumbnail);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    MainActivity main = (MainActivity) context;
                    main.selectImage();
                }
            });
        }
        else if (url != null && !url.equalsIgnoreCase("null")) {
            Picasso.with(context).load(url)
                    .error(R.drawable.placeholder)
                    .resize(140, 140)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(holder.thumbnail);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "item clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }




    }





    @Override
    public int getItemCount() {
        return (null!= imageList ? imageList.size():0);
    }
}
