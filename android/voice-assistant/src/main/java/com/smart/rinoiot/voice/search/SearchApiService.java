package com.smart.rinoiot.voice.search;
import com.smart.rinoiot.voice.search.model.SearchResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * @author edwin
 */
public interface SearchApiService {

    /**
     * Fetch the data for the provided query
     *
     * @param query the query
     * @param apiKey the search API Key
     * @return the of search results
     */
    @GET("v1/tools/search")
    Call<SearchResults> search(@Query("query") String query, @Header("X-API-Key") String apiKey);
}
