package com.smart.rinoiot.voice.search;

import android.util.Log;

import com.smart.rinoiot.voice.search.model.SearchResults;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @author edwin
 */
public class SearchUtils {

    private static final String TAG = "SearchUtils";
    /**
     * Searches the web for direct answers
     * @param query the search query
     * @param apiKey the api key
     * @return the direct answer
     */
    public static String search(String query, String apiKey) {
        // Create the NewsApiService
        SearchApiService service = SearchApiClient.createService();
        // Make the API call to get the requested data
        Call<SearchResults> call = service.search(
                query,
                apiKey
        );
        try {
            // Execute the API call synchronously
            Response<SearchResults> response = call.execute();
            if (!response.isSuccessful()) {
                Log.d(TAG, "search: Failed to get search results. Cause: " + response.code());
                return "Failed to get search results";
            }

            SearchResults searchResults = response.body();
            if(null == searchResults){
                Log.d(TAG, "search: Failed to get search results. Cause: no data returned");
                return "Failed to get search results";
            }
            return searchResults.getResult();
        } catch (Exception e) {
            Log.e(TAG, "search: Failed to get search results. Cause: " + e.getLocalizedMessage());
            return "Failed to get search results";
        }
    }
}
