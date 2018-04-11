package br.com.lucaslprimo.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lucas Primo on 07-Dec-17.
 */

public class Movie implements Parcelable{

    private int id;
    private int dbId;
    private String originalTitle;
    private String moviePoster;
    private String overview;
    private String voteAverage;
    private String popularity;
    private String releaseDate;
    private boolean favorite;

    public static final String MOVIE_INTENT = "movie";
    public static final String MOVIE_INTENT_ID = "movie_id";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public void setMoviePoster(String moviePoster) {
        this.moviePoster = moviePoster;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    //Parcelable implementation
    private Movie(Parcel in)
    {
        this.id = in.readInt();
        this.originalTitle = in.readString();
        this.moviePoster = in.readString();
        this.overview = in.readString();
        this.popularity = in.readString();
        this.voteAverage = in.readString();
        this.releaseDate = in.readString();
        this.favorite   = in.readByte() != 0;
    }

    public Movie()
    {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(originalTitle);
        parcel.writeString(moviePoster);
        parcel.writeString(overview);
        parcel.writeString(popularity);
        parcel.writeString(voteAverage);
        parcel.writeString(releaseDate);
        parcel.writeByte((byte) (favorite ? 1 : 0));
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>()
    {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
