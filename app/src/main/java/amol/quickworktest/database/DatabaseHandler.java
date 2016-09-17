package amol.quickworktest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import amol.quickworktest.model.Movie;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "MoviewManager";

    // transactions table name
    private static final String TABLE_MOVIE = "movies";

    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_RATING = "rating";
    private static final String TAG_RELEASE_YEAR = "releaseyear";
    private static final String TAG_GENRE = "genre";

    private Context context;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_BOOK_TABLE = "CREATE TABLE " + TABLE_MOVIE + " ( "
    /*0*/ + TAG_ID + " integer primary key autoincrement, "
	/*1*/ + TAG_TITLE + " TEXT, "
	/*2*/ + TAG_IMAGE + " TEXT, "
	/*3*/ + TAG_RATING + " TEXT, "
	/*4*/ + TAG_RELEASE_YEAR + " TEXT, "
	/*5*/ + TAG_GENRE + " TEXT "
                + " ) ";

        db.execSQL(CREATE_BOOK_TABLE);


    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void addMovie(Movie movie) {

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MOVIE + " WHERE " + TAG_TITLE + " = '" + movie.getTitle().replace("'", "") + "'";
        //       Log.e("if Exists Coupon", selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //     Log.e("Count", cursor.getCount() + "");
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(TAG_TITLE, movie.getTitle().replace("'", ""));
            values.put(TAG_IMAGE, movie.getImage());
            values.put(TAG_RATING, movie.getRating());
            values.put(TAG_RELEASE_YEAR, movie.getReleaseYear());

            StringBuilder temp = new StringBuilder();
            for (String s : movie.getGenre()) {
                temp.append(s);
                temp.append(",");
            }

            values.put(TAG_GENRE, temp.toString());
            db.insert(TABLE_MOVIE, null, values);
            db.close(); // Closing database connection
        }
        cursor.close();
    }


    public ArrayList<Movie> getAllMovies() {

        ArrayList<Movie> movieList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_MOVIE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor1 = db.rawQuery(selectQuery, null);
        if (cursor1.getCount() > 0 && cursor1.moveToLast()) {

            do {

                Movie movie = new Movie();
                movie.setId(cursor1.getInt(0));
                movie.setTitle(cursor1.getString(1));
                movie.setImage(cursor1.getString(2));
                movie.setRating(Double.valueOf(cursor1.getString(3)));
                movie.setReleaseYear(Integer.valueOf(cursor1.getString(4)));
                List<String> items = Arrays.asList(cursor1.getString(5).split("\\s*,\\s*"));
                movie.setGenre(items);
                movieList.add(movie);

            } while (cursor1.moveToPrevious());
        }

        cursor1.close();
        db.close();

        return movieList;

    }

    public boolean deleteMovie(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean f = db.delete(TABLE_MOVIE, TAG_ID + "=" + id, null) > 0;
        db.close();
        return f;
    }

}
