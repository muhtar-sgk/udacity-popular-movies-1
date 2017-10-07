package org.saungit.muhtar.moviefirststage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;
import org.saungit.muhtar.moviefirststage.app.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {
    private String strIdMovie;
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private String strTitle;
    private String strPosterPath;
    private String strDateRelease;
    private String strVote;
    private String strDesccription;

    private TextView textReleaseDate;
    private TextView textVote;
    private TextView textDescription;
    private ImageView imageViewMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        strIdMovie = intent.getStringExtra(Config.ID_MOVIE);

        toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageViewMovie = (ImageView) findViewById(R.id.image_movie);
        textReleaseDate = (TextView) findViewById(R.id.text_release_date);
        textVote = (TextView) findViewById(R.id.text_vote);
        textDescription = (TextView) findViewById(R.id.text_description);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setContentScrimColor(getResources().getColor(R.color.colorPrimary));

        getMovieDetail();
    }

    private void getMovieDetail() {
        Intent intent = getIntent();
        strIdMovie = intent.getStringExtra(Config.ID_MOVIE);
        String nameURL = "https://api.themoviedb.org/3/movie/" + strIdMovie + "?api_key=e63e74d7100975534da7c836560604e3&language=en-US";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, nameURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NetworkError) {
                            Toast.makeText(DetailActivity.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(DetailActivity.this, "The server could not be found. Please try again after some time!", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(DetailActivity.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(DetailActivity.this, "Parsing error! Please try again after some time!", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(DetailActivity.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(DetailActivity.this, "Connection TimeOut! Please check your internet connection!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            strPosterPath = jsonObject.getString(Config.POSTERPATH);

            Glide.with(this).load(Config.URL_PATH+strPosterPath)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewMovie);

            strTitle = jsonObject.getString(Config.TITLE);
            collapsingToolbar.setTitle(strTitle);

            strDateRelease = jsonObject.getString(Config.RELEASE_DATE);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = formatter.parse(strDateRelease);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            formatter = new SimpleDateFormat("dd MMM yyyy");
            String convertDate = formatter.format(date);
            textReleaseDate.setText(convertDate);

            strVote = jsonObject.getString(Config.VOTE_AVERAGE);
            textVote.setText("Rating: " + strVote);

            strDesccription = jsonObject.getString(Config.OVERVIEW);
            textDescription.setText(strDesccription);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
