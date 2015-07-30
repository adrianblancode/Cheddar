package co.adrianblan.cheddar;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Adrian on 2015-07-30.
 */
public class CommentAdapter extends BaseAdapter {

    private ArrayList<Comment> comments = new ArrayList<Comment>();

    public CommentAdapter() {}

    public void add (Comment c){
        comments.add(c);
    }

    public void clear() {
        comments.clear();
    }

    public int getPosition(Comment c) {

        for(int i = 0; i < getCount(); i++) {
            if(comments.get(i).equals(c)){
                return i;
            }
        }

        return -1;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Comment c = comments.get(position);

        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
        View comment_view = inflater.inflate(R.layout.comment, parent, false);

        TextView title = (TextView) comment_view.findViewById(R.id.comment_title);
        title.setText(c.getTitle());

        TextView body = (TextView) comment_view.findViewById(R.id.comment_body);
        body.setText(Html.fromHtml(c.getBody()));

        TextView time = (TextView) comment_view.findViewById(R.id.comment_time);
        time.setText(c.getTime());

        return comment_view;
    }
}
