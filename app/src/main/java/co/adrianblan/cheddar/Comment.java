package co.adrianblan.cheddar;

/**
 * Created by Adrian on 2015-07-30.
 */
public class Comment {
    private String by;
    private String body;
    private String time;

    // At what level the comment should be shown
    private int hierarchy;

    public Comment(){
        this.by = null;
        this.body = null;
    }

    public Comment(String by, String body){
        this.by = by;
        this.body = body;
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
}
