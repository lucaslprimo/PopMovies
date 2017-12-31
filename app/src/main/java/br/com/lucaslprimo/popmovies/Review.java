package br.com.lucaslprimo.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lucas Primo on 29-Dec-17.
 */

public class Review implements Parcelable{

    private String id;
    private String author;
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    //Parcelable implementation
    private Review(Parcel in)
    {
        this.id = in.readString();
        this.author = in.readString();
        this.content = in.readString();
    }

    public Review()
    {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(author);
        parcel.writeString(content);
    }

    static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>()
    {
        @Override
        public Review createFromParcel(Parcel parcel) {
            return new Review(parcel);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
