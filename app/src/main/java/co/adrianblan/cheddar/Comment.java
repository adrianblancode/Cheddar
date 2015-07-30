package co.adrianblan.cheddar;

/**
 * Created by Adrian on 2015-07-30.
 */
public class Comment {
    public String title;
    public String body;
    public String time;

    public Comment(){
        this.title = null;
        this.body = null;
    }

    public Comment(String title, String body){
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
