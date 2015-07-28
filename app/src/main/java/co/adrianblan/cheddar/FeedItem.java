package co.adrianblan.cheddar;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;

/**
 * Created by Adrian on 2015-07-25.
 */
public class FeedItem {

    private String title;
    private String subtitle1;
    private String subtitle2;
    private Bitmap thumbnail;
    private Bitmap favicon;
    private Drawable textDrawable;
    private String letter;

    public FeedItem(){
        this.title = null;
        this.subtitle1 = null;
        this.subtitle2 = null;
        this.thumbnail = null;
        this.favicon = null;
        this.textDrawable = null;
        letter = "?";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle1() {
        return subtitle1;
    }

    public void setSubtitle1(String subtitle) {
        this.subtitle1 = subtitle;
    }

    public String getSubtitle2() {
        return subtitle2;
    }

    public void setSubtitle2(String subtitle) {
        this.subtitle2 = subtitle;
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
}
