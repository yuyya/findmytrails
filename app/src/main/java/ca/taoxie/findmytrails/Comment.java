package ca.taoxie.findmytrails;

/**
 * Created by TaoX on 2016-10-28.
 */

public class Comment {

    private String comment;
    private String trailId;
    private String userEmail;

    public Comment(String comment, String trailId, String userEmail) {
        this.comment = comment;
        this.trailId = trailId;
        this.userEmail = userEmail;
    }

    public Comment() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTrailId() {
        return trailId;
    }

    public void setTrailId(String trailId) {
        this.trailId = trailId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUser(String user) {
        this.userEmail = user;
    }

    @Override
    public String toString() {
        return userEmail +": "+comment;
    }
}
