package amol.quickworktest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import amol.quickworktest.R;
import amol.quickworktest.adapter.MoviesAdapter;
import amol.quickworktest.database.DatabaseHandler;
import amol.quickworktest.listView.ItemClickSupport;
import amol.quickworktest.model.Movie;
import amol.quickworktest.rest.ApiClient;
import amol.quickworktest.rest.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    DatabaseHandler db;
    List<Movie> temp;
    MoviesAdapter moviesAdapter;
    RecyclerView recyclerView;
    /**
     * BroadCast Receiver for delete feature
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            int id = intent.getIntExtra("id", 0);
            int lPo = intent.getIntExtra("lPosition", 0);
            String title = intent.getStringExtra("movieName");
            Log.d("receiver", "Got message: " + title);

            if (db.deleteMovie(id)) {
                temp.remove(lPo);
                moviesAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), title + " Successfully Deleted!", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), "Failed To Delete " + title, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHandler(this);

        /**
         * Receiving delete movie request via local broadcast
         */
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("delete-movie"));

        recyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /**
         * fetch all movies
         */
        fetchMovieResponse();

    }

    /**
     * Get All movies if exists else fetch fresh
     */
    @Override
    protected void onResume() {
        temp = db.getAllMovies();
        if (temp.size() != 0)
            showMovieList();
        else
            fetchMovieResponse();
        super.onResume();
    }

    /**
     * Unregister Broadcast receiver
     */
    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }


    /**
     * Show Movies in list view
     */
    private void showMovieList() {

        moviesAdapter = new MoviesAdapter(temp, R.layout.list_item_movie, getApplicationContext());
        recyclerView.setAdapter(moviesAdapter);
        moviesAdapter.notifyDataSetChanged();


        ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, final int position, View v) {

                final int dbId = temp.get(position).getId();
                final String movieName = temp.get(position).getTitle();

                AlertDialog alertDialog = new AlertDialog.Builder(
                        MainActivity.this).create();

                // Setting Dialog Title
                alertDialog.setTitle("Delete!");

                // Setting Dialog Message
                alertDialog.setMessage("Are you sure you want to delete " + movieName + " ?");

                // Setting OK Button
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        Intent intent = new Intent("delete-movie");
                        intent.putExtra("lPosition", position);
                        intent.putExtra("id", dbId);
                        intent.putExtra("movieName", movieName);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    }
                });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                // Showing Alert Message
                alertDialog.show();
                return false;
            }
        });
    }


    /**
     * Make Service call via retrofit
     */
    private void fetchMovieResponse() {

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<List<Movie>> call = apiService.getAllMovies();
        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                for (Movie movie : response.body()) {
                    db.addMovie(movie);
                    Log.d(TAG, "DB add " + movie.getTitle());
                }
                temp = db.getAllMovies();
                showMovieList();
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
            }
        });


    }

}
