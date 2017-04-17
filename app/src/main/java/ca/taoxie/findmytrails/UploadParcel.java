package ca.taoxie.findmytrails;

import android.net.Uri;

/**
 * Created by TaoX on 2016-10-22.
 */

public class UploadParcel {

    private Uri file;
    private Trail trail;
    private String username;

    public UploadParcel() {
    }

    public UploadParcel(Uri file, Trail trail, String username) {
        this.file = file;
        this.trail = trail;
        this.username = username;
    }

    public Trail getTrail() {
        return trail;
    }

    public void setTrail(Trail trail) {
        this.trail = trail;
    }

    public Uri getFile() {
        return file;
    }

    public void setFile(Uri file) {
        this.file = file;
    }
}
