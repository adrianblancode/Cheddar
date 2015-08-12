package co.adrianblan.cheddar;

import java.io.Serializable;

/**
 * Created by Adrian on 2015-07-30.
 */
public class Comment implements Serializable {
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
}
