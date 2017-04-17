package ca.taoxie.findmytrails;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by TaoX on 2016-10-23.
 */

public interface AsyncImagesResponse {

    void processFinish(List<Image> results);
    void processFinishForBitmap(List<Bitmap> bitmaps);

}
