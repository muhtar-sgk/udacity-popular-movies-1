package org.saungit.muhtar.moviefirststage.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.saungit.muhtar.moviefirststage.DetailActivity;
import org.saungit.muhtar.moviefirststage.MainActivity;
import org.saungit.muhtar.moviefirststage.R;
import org.saungit.muhtar.moviefirststage.app.Config;
import org.saungit.muhtar.moviefirststage.listener.ItemClickListener;
import org.saungit.muhtar.moviefirststage.model.Movie;

import java.util.List;

/**
 * Created by Muhtar on 01/07/2017.
 */

public class MovieAdapterRecyclerView extends RecyclerView.Adapter<MovieAdapterRecyclerView.ViewHolder> {
    private Context context;
    List<Movie> movieList;

    public MovieAdapterRecyclerView(List<Movie> movieList, Context context){
        super();
        //Getting all the superheroes
        this.movieList = movieList;
        this.context = context;
    }

    public void setMovies(List<Movie> movieList){
        this.movieList = movieList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_movie, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Movie movie =  movieList.get(position);
        holder.textTitle.setText(movie.getTitle());
        Context contextHolder = holder.imageMovie.getContext();
        Glide.with(contextHolder).load(Config.URL_PATH+movie.getPosterPath())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageMovie);
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailCatalog = new Intent(context, DetailActivity.class);
                detailCatalog.putExtra(Config.ID_MOVIE, movie.getIdMovie());
                context.startActivity(detailCatalog);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView imageMovie;
        public TextView textTitle;
        public CheckBox checkBoxFav;
        private ItemClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            imageMovie = (ImageView) itemView.findViewById(R.id.thumbnail);
            textTitle = (TextView) itemView.findViewById(R.id.title);
            itemView.setOnClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view);
        }
    }
}