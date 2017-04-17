package ca.taoxie.findmytrails;

/**
 * Created by TaoX on 2016-10-23.
 */

public class Image   {

    private String trailId;
    private String imgUrl;

    public Image() {
    }

    public Image(String trailId, String imgUrl) {
        this.trailId = trailId;
        this.imgUrl = imgUrl;
    }

    public String getTrailId() {
        return trailId;
    }

    public void setTrailId(String trailId) {
        this.trailId = trailId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "Image{" +
                "trailId='" + trailId + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
