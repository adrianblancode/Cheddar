package co.adrianblan.cheddar;

import android.content.Context;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.RelativeSizeSpan;
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
    private final Context context;
    private String author;

    public CommentAdapter(Context c, String author) {
        comments = new ArrayList<Comment>();
        colors = null;
        context = c;
        this.author = author;
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
        /*
        int i = 0;
        for(Comment c : comments){
            if(!c.isHidden()){
                i++;
            }
        }
        return i;
        */
    }

    @Override
    public Comment getItem(int position) {
        return comments.get(position);
        /*
        int i = 0;
        for(Comment c : comments){
            if(i == position){return c;}
            if(!c.isHidden()){i++;}
        }
        return null;
        */
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
            LinearLayout text_container;
            LinearLayout indicator;
            LinearLayout indicator_color;
            TextView hidden_children;
        }

        if(colors == null){
            colors = initColors(parent.getContext());
        }

        final Comment com = comments.get(position);

        if(com.isHidden()){
            // Inflate and return an empty layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.empty, parent, false);
        }

        ViewHolder holder;

        if(convertView == null || convertView.getTag() == null) {

            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.comment, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.comment_title);
            holder.body = (JellyBeanCompatTextView) convertView.findViewById(R.id.comment_body);
            holder.time = (TextView) convertView.findViewById(R.id.comment_time);
            holder.container = (LinearLayout) convertView.findViewById(R.id.comment);
            holder.text_container = (LinearLayout) convertView.findViewById(R.id.comment_text_container);
            holder.indicator = (LinearLayout) convertView.findViewById(R.id.comment_indicator);
            holder.indicator_color = (LinearLayout) convertView.findViewById(R.id.comment_indicator_color);
            holder.hidden_children = (TextView) convertView.findViewById(R.id.comment_hidden_children);

            // Store the holder with the view.
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(com.getBy());

        // If the writer of the comment is also the author of the submission
        if(com.getBy().equals(author)){
            holder.title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.title.setText(holder.title.getText() + " [OP]");
        } else {
            holder.title.setTextColor(context.getResources().getColor(R.color.abc_secondary_text_material_light));
        }

        if(com.getBody() != null) {
            // We can't show the text as is, but have to parse it as html
            holder.body.setText(trimWhitespace(Html.fromHtml(com.getBody())));

            // We make links clickable
            holder.body.setMovementMethod(LinkMovementMethod.getInstance());
        }

        holder.time.setText(com.getTime());

        // Adds padding based on hierarchy, and adds indicator
        holder.indicator.setPadding((int) dpToPixels(4, parent.getContext()) * (com.getHierarchy() - 1), 0, 0, 0);

        // We don't need the indicator for top level commentCount
        if(com.getHierarchy() == 0){
            holder.indicator.setVisibility(View.GONE);
            holder.indicator_color.setVisibility(View.GONE);
        } else {
            holder.indicator.setVisibility(View.VISIBLE);
            holder.indicator_color.setVisibility(View.VISIBLE);

            // Use modulo to get the appropriate color
            int color = colors.get((com.getHierarchy() - 1) % colors.size());
            holder.indicator_color.setBackgroundColor(color);
        }

        if(com.hasHideChildren()){
            holder.hidden_children.setText("+" + Integer.toString(com.getHiddenChildren()));
            holder.hidden_children.setVisibility(View.VISIBLE);
        } else {
            holder.hidden_children.setVisibility(View.GONE);
        }

        holder.text_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hierarchy = com.getHierarchy();
                int position = getPosition(com);

                if(!com.hasHideChildren()){
                    int i;

                    // Find all child comments with higher hierarchy and hide them
                    // We do the direct access since we want to be able to access hidden comments
                    for(i = position + 1; comments.get(i).getHierarchy() > hierarchy && i < comments.size(); i++){
                        getItem(i).setIsHidden(true);
                    }

                    int hiddenChildren = i - position - 1;
                    com.setHiddenChildren(hiddenChildren);

                    if(hiddenChildren > 0){
                        com.setHideChildren(true);
                    }
                } else {

                    com.setHideChildren(false);

                    // Find all child comments with higher hierarchy and show them
                    for(int i = position + 1; comments.get(i).getHierarchy() > hierarchy && i < comments.size(); i++){
                        getItem(i).setIsHidden(false);

                    }
                }

                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    // Initializes the list of colours which will be used to display comment hierarchy
    public ArrayList<Integer> initColors(Context context) {
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

    // Removes trailing double whitespace, reduces the size of other double whitespace
    public static SpannableStringBuilder trimWhitespace(CharSequence source) {

        int i = source.length();

        // loop back to the first non-whitespace character
        while(--i >= 0 && Character.isWhitespace(source.charAt(i))) {
        }

        // Removes two trailing newlines
        source = source.subSequence(0, i + 1);

        SpannableStringBuilder ssb = new SpannableStringBuilder(source);

        for(i = 0; i + 1 < source.length(); i++){
            if(Character.isWhitespace(source.charAt(i)) && Character.isWhitespace(source.charAt(i + 1))) {

                // Reduces the size of double whitespace
                ssb.setSpan(new RelativeSizeSpan(0.4f), i, i + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                i++;
            }
        }

        return ssb;
    }
}
