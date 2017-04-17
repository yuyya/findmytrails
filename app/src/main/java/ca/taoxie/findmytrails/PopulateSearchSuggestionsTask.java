package ca.taoxie.findmytrails;

import android.os.AsyncTask;
import android.os.Parcel;
import android.util.Log;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TaoX on 2016-11-26.
 */

public class PopulateSearchSuggestionsTask extends AsyncTask<String, Void, List<Trail>> {

    public AsyncResponse delegate;
    private FloatingSearchView floatingSearchView;
    private String target;
    private HashMap<String, Trail> trailSuggestions = new HashMap<>();

    private static final String TAG = "Background Task";
    private static final String MASHAPE_API_KEY = "d3SGrBj5iWmshcLXtHloxt8qxA5Tp1v8Fu4jsnqB4oUhDsBhgd";

    public PopulateSearchSuggestionsTask(String target, FloatingSearchView floatingSearchView) {
        this.floatingSearchView = floatingSearchView;
        this.target = target;
    }

    @Override
    protected List<Trail> doInBackground(String... params) {
        List<Trail> results = new ArrayList<>();
        URL url = null;
        HttpURLConnection hc = null;
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        JSONObject parentObject = null;

        try {
            //change this url for filtering
            for (int k = 0; k < params.length; k++) {
                url = new URL(params[k]);
                hc = (HttpURLConnection) url.openConnection();
                hc.setRequestProperty("X-Mashape-Key", MASHAPE_API_KEY);
                hc.setRequestProperty("Accept", "text/plain");
                hc.connect();
                Log.e(TAG, "" + url);
                InputStream steam = hc.getInputStream();
                reader = new BufferedReader(new InputStreamReader(steam));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                parentObject = new JSONObject(buffer.toString());
                JSONArray parentArray = parentObject.getJSONArray("places");

                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    if (!finalObject.getString("name").toLowerCase().contains(target))
                        continue;
                    Trail trail = new Trail();
                    trail.setCity(finalObject.getString("city"));
                    trail.setState(finalObject.getString("state"));
                    trail.setCountry(finalObject.getString("country"));
                    trail.setName(finalObject.getString("name"));
                    trail.setParent_id(finalObject.getString("parent_id"));
                    trail.setUnique_id(finalObject.getString("unique_id"));
                    trail.setDirections(finalObject.getString("directions"));
                    trail.setLat(Double.valueOf(finalObject.getString("lat")));
                    trail.setLon(Double.valueOf(finalObject.getString("lon")));
                    trail.setDescription(finalObject.getString("description"));
                    trail.setDate_created(finalObject.getString("date_created"));

                    ActivityDetail activityDetail = new ActivityDetail();
                    JSONArray activityDetailParentArray = finalObject.getJSONArray("activities");
                    for (int j = 0; j < activityDetailParentArray.length(); j++) {
                        JSONObject activitiesFinalObject = activityDetailParentArray.getJSONObject(j);
                        activityDetail.setName(activitiesFinalObject.getString("name"));
                        activityDetail.setUnique_id(activitiesFinalObject.getString("unique_id"));
                        activityDetail.setPlace_id(activitiesFinalObject.getString("place_id"));
                        activityDetail.setActivity_type_id(activitiesFinalObject.getInt("activity_type_id"));
                        activityDetail.setActivity_type_name(activitiesFinalObject.getString("activity_type_name"));
                        activityDetail.setUrl(activitiesFinalObject.getString("url"));
                        activityDetail.setDescription(activitiesFinalObject.getString("description"));
                        activityDetail.setLength(activitiesFinalObject.getDouble("length"));
                        activityDetail.setThumbnail(activitiesFinalObject.getString("thumbnail"));
                        activityDetail.setRank(activitiesFinalObject.getString("rank"));
                        activityDetail.setRating(activitiesFinalObject.getDouble("rating"));

                        trail.getActivitiesDetails().add(activityDetail);
                    }

                    results.add(trail);
                }
            }

        }catch (MalformedURLException e) {
            Log.e(TAG, "URL failed");
        } catch (IOException e) {
            Log.e(TAG, "http connection failed");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSON OBJECT FAILED");
        } finally {
            if (hc != null)
                hc.disconnect();
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, "Connection OK");
        return results;
    }

    @Override
    protected synchronized void onPostExecute(final List<Trail> s) {
        List<SearchSuggestion> searchSuggestions = new ArrayList<>();

        for (int i = 0; i < s.size(); i++) {
            final int index = i;
            final List<Trail> trailsCopy = s;
            if (trailsCopy.get(i).getName().toLowerCase().contains(target.toLowerCase())) {
                SearchSuggestion suggestion = new SearchSuggestion() {
                    @Override
                    public String getBody() {
                        if (index < trailsCopy.size())
                            return trailsCopy.get(index).getName();
                        else
                            return "";
                    }

                    @Override
                    public int describeContents() {
                        return 0;
                    }

                    @Override
                    public void writeToParcel(Parcel dest, int flags) {

                    }
                };
                searchSuggestions.add(suggestion);
                trailSuggestions.put(suggestion.getBody(), trailsCopy.get(i));
            }
        }
        delegate.processFinish(trailSuggestions);
        floatingSearchView.swapSuggestions(searchSuggestions);
    }

}
