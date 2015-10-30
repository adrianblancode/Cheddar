package co.adrianblan.cheddar;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Adrian on 2015-07-30.
 */
public class Comment implements Parcelable {
    private String by;
    private String body;
    private String time;

    private boolean hideChildren;
    private boolean isHidden;
    private boolean toBeHidden;
    private int hiddenChildren;

    // At what level the comment should be shown
    private int hierarchy;

    public Comment(){
        this.by = null;
        this.body = null;
        this.hideChildren = false;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(int hierarchy) {
        this.hierarchy = hierarchy;
    }

    public boolean hasHideChildren() {
        return hideChildren;
    }

    public void setHideChildren(boolean hideChildren) {
        this.hideChildren = hideChildren;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setIsHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public int getHiddenChildren() {
        return hiddenChildren;
    }

    public void setHiddenChildren(int hiddenChildren) {
        this.hiddenChildren = hiddenChildren;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.by);
        dest.writeString(this.body);
        dest.writeString(this.time);
        dest.writeByte(hideChildren ? (byte) 1 : (byte) 0);
        dest.writeByte(isHidden ? (byte) 1 : (byte) 0);
        dest.writeByte(toBeHidden ? (byte) 1 : (byte) 0);
        dest.writeInt(this.hiddenChildren);
        dest.writeInt(this.hierarchy);
    }

    protected Comment(Parcel in) {
        this.by = in.readString();
        this.body = in.readString();
        this.time = in.readString();
        this.hideChildren = in.readByte() != 0;
        this.isHidden = in.readByte() != 0;
        this.toBeHidden = in.readByte() != 0;
        this.hiddenChildren = in.readInt();
        this.hierarchy = in.readInt();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
