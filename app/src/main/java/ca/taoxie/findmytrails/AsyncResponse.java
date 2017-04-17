package ca.taoxie.findmytrails;

import android.location.Location;

import java.util.HashMap;
import java.util.List;

/**
 * Created by TaoX on 2016-10-03.
 */

public interface AsyncResponse {

    void processFinish(List<Trail> results);
    void processFinish(Location location);
    void processFinish(HashMap<String, Trail> trailSuggestions);
}
