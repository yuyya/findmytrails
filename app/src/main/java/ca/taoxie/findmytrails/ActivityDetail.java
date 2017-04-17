package ca.taoxie.findmytrails;

/**
 * Created by TaoX on 2016-10-15.
 */

public class ActivityDetail {

    private String name;
    private String unique_id;
    private String place_id;
    private int activity_type_id;
    private String activity_type_name;
    private String url;
    private String description;
    private double length;
    private String thumbnail;
    private String rank;
    private double rating;

    public ActivityDetail(String name, String unique_id, String place_id, int activity_type_id,
                          String activity_type_name, String url, String description,
                          int length, String thumbnail, String rank,
                          double rating) {
        this.name = name;
        this.unique_id = unique_id;
        this.place_id = place_id;
        this.activity_type_id = activity_type_id;
        this.activity_type_name = activity_type_name;
        this.url = url;
        this.description = description;
        this.length = length;
        this.thumbnail = thumbnail;
        this.rank = rank;
        this.rating = rating;
    }

    public ActivityDetail() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public int getActivity_type_id() {
        return activity_type_id;
    }

    public void setActivity_type_id(int activity_type_id) {
        this.activity_type_id = activity_type_id;
    }

    public String getActivity_type_name() {
        return activity_type_name;
    }

    public void setActivity_type_name(String activity_type_name) {
        this.activity_type_name = activity_type_name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
