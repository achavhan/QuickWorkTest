package amol.quickworktest.rest;

import java.util.List;

import amol.quickworktest.model.Movie;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("json/movies.json")
    Call<List<Movie>> getAllMovies();

}
