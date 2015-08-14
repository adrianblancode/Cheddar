package co.adrianblan.cheddar;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by Adrian on 2015-07-25.
 */
public class FeedItem implements Serializable {

    // For info on these variables, check out the Hacker News FireBase API
    private Long submissionId;
    private String title;
    private String by;
    private String text;
    private long score;
    private long descendants;
    private String time;
    private String shortUrl;
    private String longUrl;

    // Marked transient so they won't get included in the bundle
    private transient Bitmap thumbnail;
    private transient Drawable textDrawable;

    private String letter;
    private int color;

    public FeedItem(){
        this.submissionId = 0L;
        this.title = null;
        this.score = 0;
        this.descendants = 0;
        this.time = null;
        this.shortUrl = null;
        this.longUrl = null;
        this.thumbnail = null;
        this.textDrawable = null;
        this.letter = "?";
        this.color = -39424;
        // Our primary color, hax to get around fragment lifecycles
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
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

    public long getDescendants() {
        return descendants;
    }

    public void setDescendants(long descendants) {
        this.descendants = descendants;
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
