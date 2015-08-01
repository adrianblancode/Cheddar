package co.adrianblan.cheddar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Adrian on 2015-07-30.
 */
public class CommentAdapter extends BaseAdapter {

    private ArrayList<Comment> comments;
    private ArrayList<Integer> colors;

    public CommentAdapter() {
        comments = new ArrayList<Comment>();
        colors = null;
    }

    public void add (Comment c){
        comments.add(c);
    }

    public void add (int i, Comment c){
        comments.add(i, c);
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
    public Comment getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        class ViewHolder {
            TextView title;
            TextView body;
            TextView time;
            LinearLayout container;
            LinearLayout indicator;
        }

        if(colors == null){
            colors = initColors(parent.getContext());
        }

        final Comment com = comments.get(position);

        ViewHolder holder;

        if(convertView == null) {

            holder = new ViewHolder();

            // TODO get passed context?
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.comment, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.comment_title);
            holder.body = (TextView) convertView.findViewById(R.id.comment_body);
            holder.time = (TextView) convertView.findViewById(R.id.comment_time);
            holder.container = (LinearLayout) convertView.findViewById(R.id.comment);
            holder.indicator = (LinearLayout) convertView.findViewById(R.id.comment_indicator);

            // Store the holder with the view.
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.title.setText(com.getTitle());

        if(com.getBody() != null) {
            // We can't show the text as is, but have to parse it as html
            holder.body.setText(trimTrailingWhitespace(Html.fromHtml(com.getBody())));

            // We make links clickable
            holder.body.setMovementMethod(LinkMovementMethod.getInstance());
        }

        holder.time.setText(com.getTime());

        // Adds padding based on hierarchy, and adds indicator
        holder.container.setPadding((int) dpToPixels(4, parent.getContext()) * (com.getHierarchy() - 1), 0, 0, 0);

        // We don't need the indicator for top level comments
        if(com.getHierarchy() == 0){
            holder.indicator.setVisibility(View.GONE);
        } else {
            // Use modulo to get the appropriate color
            int color = colors.get((com.getHierarchy() - 1) % colors.size());
            holder.indicator.setBackgroundColor(color);
            holder.indicator.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    // Initializes the list of colours which will be used to display comment hierarchy
    public ArrayList<Integer> initColors(Context context){
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(context.getResources().getColor(R.color.colorPrimary));
        colors.add(context.getResources().getColor(R.color.materialPink));
        colors.add(context.getResources().getColor(R.color.materialDeepPurple));
        colors.add(context.getResources().getColor(R.color.materialBlue)); // Not really material blue but who cares
        colors.add(context.getResources().getColor(R.color.materialGreen));
        colors.add(context.getResources().getColor(R.color.materialAmber));


        return colors;
    }

    public static float dpToPixels(float dp, Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    // Trims trailing whitespace.
    public static CharSequence trimTrailingWhitespace(CharSequence source) {

        if(source == null)
            return "";

        int i = source.length();

        // loop back to the first non-whitespace character
        while(--i >= 0 && Character.isWhitespace(source.charAt(i))) {
        }

        return source.subSequence(0, i+1);
    }
}
