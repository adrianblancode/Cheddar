package co.adrianblan.cheddar;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Adrian on 2015-07-25.
 */
public class FeedItem implements Serializable {

    private String title;
    private String subtitle;
    private long score;
    private long commentCount;
    private String time;
    private Bitmap thumbnail;
    private Drawable textDrawable;
    private String letter;
    private String shortUrl;
    private String longUrl;
    private ArrayList<Long> kids;

    public FeedItem(){
        this.title = null;
        this.subtitle = null;
        this.score = 0;
        this.commentCount = 0;
        this.time = null;
        this.thumbnail = null;
        this.textDrawable = null;
        this.letter = "?";
        this.shortUrl = null;
        this.longUrl = null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public void addCommentCount(int comments) {
        this.commentCount += comments;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Drawable getTextDrawable() {
        return textDrawable;
    }

    public void setTextDrawable(Drawable textDrawable) {
        this.textDrawable = textDrawable;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public ArrayList<Long> getKids() {
        return kids;
    }

    public void setKids(ArrayList<Long> kids) {
        this.kids = kids;
    }
}
