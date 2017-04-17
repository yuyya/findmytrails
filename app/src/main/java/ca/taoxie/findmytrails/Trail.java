package ca.taoxie.findmytrails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TaoX on 2016-10-03.
 */

public class Trail {

    private String city;
    private String state;
    private String country;
    private String name;
    private String parent_id;
    private String unique_id;
    private String directions;
    private double lat;
    private double lon;
    private String description;
    private String date_created;
    private float avg_rating;
    private String url;
    private List<ActivityDetail> activitiesDetails = new ArrayList<>();

    public Trail(Trail t)
    {
        if (t != null) {
            this.city = t.getCity();
            this.state = t.getState();
            this.country = t.getCountry();
            this.name = t.getName();
            this.parent_id = t.getParent_id();
            this.unique_id = t.getUnique_id();
            this.directions = t.getDirections();
            this.lat = t.getLat();
            this.lon = t.getLon();
            this.description = t.getDescription();
            this.date_created = t.getDate_created();
        }
    }

    public List<ActivityDetail> getActivitiesDetails() {
        return activitiesDetails;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public float getAvg_rating() {
        return avg_rating;
    }

    public void setAvg_rating(float avg_rating) {
        this.avg_rating = avg_rating;
    }

    public void setActivitiesDetails(List<ActivityDetail> activitiesDetails) {
        this.activitiesDetails = activitiesDetails;
    }

    public Trail(String city, String state, String country, String name, String parent_id,
                 String unique_id, String directions, double lat, double lon,
                 String description, String date_created) {
        this.city = city;
        this.state = state;
        this.country = country;
        this.name = name;
        this.parent_id = parent_id;
        this.unique_id = unique_id;
        this.directions = directions;
        this.lat = lat;
        this.lon = lon;
        this.description = description;
        this.date_created = date_created;

    }

    public Trail() {
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }

    public String getParent_id() {
        return parent_id;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public String getDirections() {
        return directions;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getDescription() {
        return description;
    }

    public String getDate_created() {
        return date_created;
    }
}
