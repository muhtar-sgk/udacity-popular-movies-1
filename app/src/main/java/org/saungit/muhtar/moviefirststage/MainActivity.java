package org.saungit.muhtar.moviefirststage;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.saungit.muhtar.moviefirststage.adapter.MovieAdapterRecyclerView;
import org.saungit.muhtar.moviefirststage.app.Config;
import org.saungit.muhtar.moviefirststage.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Movie> movieList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MovieAdapterRecyclerView adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(MainActivity.this,2);
        recyclerView.setLayoutManager(layoutManager);
        movieList = new ArrayList<>();

        adapter = new MovieAdapterRecyclerView(movieList, MainActivity.this);
        recyclerView.setAdapter(adapter);

        fetchDataMovies("now_playing");

        setTitle("Now Playing");
    }

    private void fetchDataMovies(String movieRequest) {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest request =
                new StringRequest(Request.Method.GET,
                        "https://api.themoviedb.org/3/movie/"+ movieRequest +"?api_key=e63e74d7100975534da7c836560604e3&language=en-US",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                showJSON(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                if (volleyError instanceof NetworkError) {
                                    Toast.makeText(MainActivity.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                } else if (volleyError instanceof ServerError) {
                                    Toast.makeText(MainActivity.this, "The server could not be found. Please try again after some time!", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                } else if (volleyError instanceof AuthFailureError) {
                                    Toast.makeText(MainActivity.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                } else if (volleyError instanceof ParseError) {
                                    Toast.makeText(MainActivity.this, "Parsing error! Please try again after some time!", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                } else if (volleyError instanceof NoConnectionError) {
                                    Toast.makeText(MainActivity.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                } else if (volleyError instanceof TimeoutError) {
                                    Toast.makeText(MainActivity.this, "Connection TimeOut! Please check your internet connection!", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(request);
    }

    private void showJSON(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray results = jsonObject.getJSONArray(Config.RESULTS);
            if (results.length() > 0) {
                for (int i = 0; i < results.length(); i++) {
                    JSONObject data = results.getJSONObject(i);
                    Movie movie = new Movie();
                    movie.setIdMovie(data.getString(Config.ID_MOVIE));
                    movie.setPosterPath(data.getString(Config.POSTERPATH));
                    movie.setOverView(data.getString(Config.OVERVIEW));
                    movie.setReleaseDate(data.getString(Config.RELEASE_DATE));
                    movie.setTitle(data.getString(Config.TITLE));
                    movie.setBackdropPath(data.getString(Config.BACKDROP_PATH));
                    movie.setPopularity(data.getString(Config.POPULARITY));
                    movie.setVoteCount(data.getString(Config.VOTE_COUNT));
                    movie.setVoteAverage(data.getString(Config.VOTE_AVERAGE));

                    movieList.add(movie);

                }

                adapter.setMovies(movieList);


            } else {
                Toast.makeText(MainActivity.this, "Movie Not Available", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==R.id.popular){
            movieList.clear();
            fetchDataMovies("popular");
            setTitle("Popular");
        } else {
            movieList.clear();
            fetchDataMovies("top_rated");
            setTitle("Top Rated");
        }

        return super.onOptionsItemSelected(item);
    }
}
