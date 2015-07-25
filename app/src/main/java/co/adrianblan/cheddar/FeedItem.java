package co.adrianblan.cheddar;

import android.media.Image;

/**
 * Created by Adrian on 2015-07-25.
 */
public class FeedItem {

    private String title;
    private String subtitle1;
    private String subtitle2;
    private Image thumbnail;

    public FeedItem(){}

    //TODO image
    public FeedItem(String title, String subtitle1, String subtitle2){
        this.title = title;
        this.subtitle1 = subtitle1;
        this.subtitle2 = subtitle2;
        this.thumbnail = thumbnail;
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

    public Image getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }
}
