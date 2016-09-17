package amol.quickworktest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import amol.quickworktest.R;
import amol.quickworktest.model.Movie;

/**
 * Created by Amol on 17-09-2016.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private List<Movie> movies;
    private int rowLayout;
    private Context context;


    public MoviesAdapter(List<Movie> movies, int rowLayout, Context context) {
        this.movies = movies;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public MoviesAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, final int position) {
        Picasso.with(context).load(movies.get(position).getImage()).into(holder.imageView);
        holder.movieTitle.setText(movies.get(position).getTitle());
        holder.releaseYear.setText("Release Year : " + movies.get(position).getReleaseYear());
        StringBuilder s = new StringBuilder();
        for (String z : movies.get(position).getGenre()) {
            s.append(z);
            s.append(",");
        }
        holder.genre.setText("Genre : " + s.toString());
        holder.rating.setText(movies.get(position).getRating().toString());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        LinearLayout moviesLayout;
        TextView movieTitle;
        TextView releaseYear;
        TextView genre;
        TextView rating;
        ImageView imageView;

        public MovieViewHolder(View v) {
            super(v);
            moviesLayout = (LinearLayout) v.findViewById(R.id.movies_layout);
            movieTitle = (TextView) v.findViewById(R.id.title);
            releaseYear = (TextView) v.findViewById(R.id.releaseYear);
            genre = (TextView) v.findViewById(R.id.genre);
            rating = (TextView) v.findViewById(R.id.rating);
            imageView = (ImageView) v.findViewById(R.id.movies_Image);
        }
    }
}
