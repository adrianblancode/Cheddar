package co.adrianblan.cheddar;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by Adrian on 2015-07-25.
 */
public class FeedItem {

    private String title;
    private String subtitle;
    private String score;
    private String comments;
    private String time;
    private Bitmap thumbnail;
    private Bitmap favicon;
    private Drawable textDrawable;
    private String letter;
    private String shortUrl;
    private String longUrl;

    public FeedItem(){
        this.title = null;
        this.subtitle = null;
        this.score = null;
        this.comments = null;
        this.time = null;
        this.thumbnail = null;
        this.favicon = null;
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

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Bitmap getFavicon() {
        return favicon;
    }

    public void setFavicon(Bitmap favicon) {
        this.favicon = favicon;
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
}
