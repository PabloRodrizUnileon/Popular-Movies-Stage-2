package com.example.pablo.popularmovies.data.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;
import android.arch.persistence.room.Ignore;

import com.example.pablo.popularmovies.data.room.entity.Movie;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;


@Dao
public abstract class MovieDao {


    /**
     * Inserta un item en la tabla, con onConflict = IGNORE, y devuelve el id del registro insertado.
     * Lo que quiere decir que si encuentra un conflicto al insertar, lo ignora,
     * y devuelve -1.
     *
     * @param movie     Item a ser insertado.
     * @return          id del registro insertado, o -1 si ha ignorado y no ha insertado.
     */
    @Insert(onConflict = IGNORE)
    public abstract long insertOrIgnore(Movie movie);

    @Insert(onConflict = IGNORE)
    public abstract List<Long> insertOrIgnore(List<Movie> movies);


    @Update
    public abstract void update(Movie movie);

    @Update
    public abstract void update(List<Movie> movies);


    /**
     *
     *
     * Updates all columns of the movie specified by movieId, but {@link Movie#favourite}.
     *
     * Explanation:
     * We are saving a value called {@link Movie#favourite} in the Movie Entity,
     * which is false by default.
     * Because all the movies are being stored, not only the ones set as favourite by the user,
     * When a call to the API is made, to download fresh movies, or update the data of stored movies,
     * if the normal and default method of update is used (or an upsert) all the columns/properties of the entity
     * that are not marked with {@link Ignore}, will be updated, and the column <code>favourite</code>
     * is <code>false</code> by default. This means that when the database is updated with new data from
     * the API, the favourite movies stored, would be "reset" to not favourite. <br>
     * To solve this problem, the column favourite is not updated, so if some movies are saved as favourites,
     * when the new data arrives, those movies will be updated, and will remain marked as favourite.
     *
     * @param movieId
     * @param voteCount
     * @param video
     * @param voteAverage
     * @param title
     * @param popularity
     * @param posterPath
     * @param originalLanguage
     * @param originalTitle
     * @param backdropPath
     * @param adult
     * @param overview
     * @param releaseDate
     */
    @Query("UPDATE Movie SET voteCount =:voteCount, video =:video, voteAverage =:voteAverage, title =:title, " +
            "popularity =:popularity, posterPath =:posterPath, originalLanguage =:originalLanguage, " +
            "originalTitle =:originalTitle, backdropPath =:backdropPath, adult =:adult, overview =:overview, " +
            "releaseDate =:releaseDate " +
            "WHERE id =:movieId")
    public abstract void updateFavouriteMovie(int movieId, int voteCount, boolean video, double voteAverage, String title,
                                  double popularity, String posterPath, String originalLanguage,
                                  String originalTitle, String backdropPath,
                                  boolean adult, String overview, String releaseDate);


    @Transaction
    public void updateFavouriteMovies(List<Movie> movies){
        for (Movie movie : movies) {
            updateFavouriteMovie(movie.getId(), movie.getVoteCount(), movie.isVideo(), movie.getVoteAverage(), movie.getTitle(),
                    movie.getPopularity(), movie.getPosterPath(), movie.getOriginalLanguage(), movie.getOriginalTitle(),
                    movie.getBackdropPath(), movie.isAdult(), movie.getOverview(), movie.getReleaseDate());
        }
    }


//
//    @Transaction
//    void upsert(List<Movie> movies){
//        //  Probamos a insertar todos los valores.
//        //  Devuelve una lista con los ids de los registros insertados,
//        // incluyendo -1 en los registros que no ha insertado al haber conflicto.
//        List<Long> insertResult = insertOrIgnore(movies);
//        //  Crea una nueva lista de Items
//        List<Movie> updateList = new ArrayList<>();
//        //  Itera sobre la lista de ids.
//        for (int i = 0; i < insertResult.size(); i++) {
//            // Si un id es == -1, quiere decir que no ha hecho insert, y es para hacer update
//            if(insertResult.get(i) == -1){
//                //  Añade ese item a la lista de items a actualizar
//                updateList.add(movies.get(i));
//            }
//        }
//        //  Si la lista de items a actualizar no está vacía, actualiza todos los registros que no se insertaron.
//        if(!updateList.isEmpty()){
//            update(updateList);
//        }
//
//
//    }


    @Transaction
    public void upsert(List<Movie> movies){
        //  Probamos a insertar todos los valores.
        //  Devuelve una lista con los ids de los registros insertados,
        // incluyendo -1 en los registros que no ha insertado al haber conflicto.
        List<Long> insertResult = insertOrIgnore(movies);
        //  Crea una nueva lista de Items
        List<Movie> updateList = new ArrayList<>();
        //  Itera sobre la lista de ids.
        for (int i = 0; i < insertResult.size(); i++) {
            // Si un id es == -1, quiere decir que no ha hecho insert, y es para hacer update
            if(insertResult.get(i) == -1){
                //  Añade ese item a la lista de items a actualizar
                updateList.add(movies.get(i));
            }
        }
        //  Si la lista de items a actualizar no está vacía, actualiza todos los registros que no se insertaron.
        //  Pero actualiza de forma especial, manteniendo el campo de favorita, para que no borre el cambio.
        if(!updateList.isEmpty()){
            updateFavouriteMovies(updateList);
        }


    }






    @Query("SELECT * FROM Movie WHERE id =:id")
    public abstract LiveData<Movie> getMovieById(long id);

    @Query("SELECT * FROM Movie ORDER BY popularity DESC LIMIT 20")
    public abstract LiveData<List<Movie>> getPopularMovies();


    @Query("SELECT * FROM Movie ORDER BY voteAverage DESC LIMIT 20")
    public abstract LiveData<List<Movie>> getTopRated();

    @Query("SELECT * FROM Movie WHERE favourite == 1")
    public abstract LiveData<List<Movie>> getFavouriteMovies();


    @Query("SELECT * FROM Movie")
    public abstract LiveData<List<Movie>> getAll();


    @Delete
    public abstract void delete (Movie movie);

    @Query("DELETE FROM Movie")
    public abstract void deleteAll();

    @Query("DELETE FROM Movie WHERE favourite = 0")
    public abstract void deleteAllNonFavourite();



}
