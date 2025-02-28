package com.smart.rinoiot.voice.news;


import com.smart.rinoiot.voice.news.model.SearchResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author edwin
 */
public interface NewsApiService {

    /**
     * Fetch the data for the provided query
     *
     * @param query the query
     * @param results  the maximum number of results
     * @return the list of search results
     */
    @GET("search")
    Call<List<SearchResult>> search(@Query("q") String query, @Query("max_results") int results);
}
