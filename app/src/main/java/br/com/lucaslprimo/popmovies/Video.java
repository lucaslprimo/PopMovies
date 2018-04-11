package br.com.lucaslprimo.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lucas Primo on 29-Dec-17.
 */

public class Video implements Parcelable{

    private String id;
    private String name;
    private String key;
    private String type;

    private static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    public String getYoutubeUrl()
    {
        return YOUTUBE_URL+this.key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    //Parcelable implementation
    private Video(Parcel in)
    {
        this.id = in.readString();
        this.name = in.readString();
        this.key = in.readString();
        this.type = in.readString();
    }

    public Video()
    {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(key);
        parcel.writeString(type);
    }

    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>()
    {
        @Override
        public Video createFromParcel(Parcel parcel) {
            return new Video(parcel);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };


}
