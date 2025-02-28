package com.smart.rinoiot.voice.news;

import android.util.Log;

import com.smart.rinoiot.voice.news.model.SearchResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @author edwin
 */
public class NewsDataUtils {

    private static final String TAG = "NewsDataUtils";

    private static final int MAX_STORIES = 5;
    private static final String BODY_KEY = "body";
    private static final String TITLE_KEY = "title";

    /**
     * Queries the top stories
     * @param query the search query
     * @return the list of top news articles
     */
    public static List<JSONObject> queryNews(String query) {
        // Create the NewsApiService
        NewsApiService service = NewsApiClient.createService();
        // Make the API call to get the requested data
        Call<List<SearchResult>> call = service.search(
               query,
                MAX_STORIES
        );
        try {
            // Execute the API call synchronously
            Response<List<SearchResult>> response = call.execute();
            if (!response.isSuccessful()) {
                Log.d(TAG, "queryNews: Failed to get news. Cause: " + response.code());
                return new ArrayList<>();
            }

            List<SearchResult> searchResults = response.body();
            if(null == searchResults){
                Log.d(TAG, "queryNews: Failed to get news. Cause: no data returned");
                return new ArrayList<>();
            }

            return searchResults.parallelStream().map(searchResult -> {
                try {
                    return new JSONObject()
                            .put(TITLE_KEY, searchResult.getTitle())
                            .put(BODY_KEY, searchResult.getBody());
                } catch (JSONException e) { return null; }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (Exception e) {
            Log.e(TAG, "queryNews: Failed to get news. Cause: " + e.getLocalizedMessage());
        }
        return new ArrayList<>();
    }
}
