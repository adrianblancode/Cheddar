package co.adrianblan.cheddar;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Adrian on 2015-07-25.
 */
public class FeedItem implements Parcelable {

    // For more info on these variables, check out the Hacker News FireBase API
    private Long submissionId; // The unique ID of the submission
    private String title; // Title of the submission
    private String by; // Author of the submission
    private String text; // Submission text
    private long score; // Number of votes of the submission
    private long descendants; // Number of comments of the submission

    private String time; // Time the submission was posted
    private String shortUrl; // Domain the submission links to
    private String longUrl; // Full URL the submission links to

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.submissionId);
        dest.writeString(this.title);
        dest.writeString(this.by);
        dest.writeString(this.text);
        dest.writeLong(this.score);
        dest.writeLong(this.descendants);
        dest.writeString(this.time);
        dest.writeString(this.shortUrl);
        dest.writeString(this.longUrl);
        dest.writeString(this.letter);
        dest.writeInt(this.color);
    }

    protected FeedItem(Parcel in) {
        this.submissionId = (Long) in.readValue(Long.class.getClassLoader());
        this.title = in.readString();
        this.by = in.readString();
        this.text = in.readString();
        this.score = in.readLong();
        this.descendants = in.readLong();
        this.time = in.readString();
        this.shortUrl = in.readString();
        this.longUrl = in.readString();
        this.letter = in.readString();
        this.color = in.readInt();
    }

    public static final Creator<FeedItem> CREATOR = new Creator<FeedItem>() {
        public FeedItem createFromParcel(Parcel source) {
            return new FeedItem(source);
        }

        public FeedItem[] newArray(int size) {
            return new FeedItem[size];
        }
    };
}
