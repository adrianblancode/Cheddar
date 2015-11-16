package co.adrianblan.cheddar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devspark.robototextview.widget.RobotoTextView;

import java.util.ArrayList;

/**
 * Created by Adrian on 2015-07-30.
 */
public class CommentAdapter extends BaseAdapter {

    private ArrayList<Comment> comments;
    private ArrayList<Integer> colors;
    private final Context context;
    private FeedItem feedItem; // Feed item which is the parent of all comments

    // Constructor which creates new arraylist
    public CommentAdapter(FeedItem fi, Context c) {
        colors = null;
        comments = new ArrayList<>();
        context = c;
        feedItem = fi;
    }

    // Constructor which copies arraylist
    public CommentAdapter(ArrayList<Comment> comments, FeedItem fi, Context c) {
        colors = null;
        this.comments = comments;
        context = c;
        feedItem = fi;
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

    public ArrayList<Comment> getComments(){
        return comments;
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
            RobotoTextView title;
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
            return new View(context);
        }

        ViewHolder holder;

        if(convertView == null || convertView.getTag() == null) {

            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.comment, parent, false);
            holder.title = (RobotoTextView) convertView.findViewById(R.id.comment_title);
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

        // If the author of the feed item is also the author of the comment
        if(com.getBy().equals(feedItem.getBy())){
            holder.title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.title.setText(holder.title.getText() + " [OP]");
        } else {
            holder.title.setTextColor(context.getResources().getColor(R.color.abc_secondary_text_material_light));
        }

        // If the comment exists
        if(com.getBody() != null && !com.hasHideChildren()) {
            holder.body.setVisibility(View.VISIBLE);

            // We can't show the text as is, but have to parse it as html
            setTextViewHTML(holder.body, com.getBody());

            //This link movement method only consumes on URL clicks
            holder.body.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());
        } else {
            holder.body.setVisibility(View.GONE);
        }

        holder.time.setText(com.getTime());

        // Adds padding based on hierarchy, and adds hierarchy indicator
        holder.indicator.setPadding((int) dpToPixels(4, parent.getContext()) * (com.getHierarchy() - 1), 0, 0, 0);

        // We don't need the indicator for top level commentCount
        if(com.getHierarchy() == 0){
            holder.indicator.setVisibility(View.GONE);
            holder.indicator_color.setVisibility(View.GONE);
        } else {
            holder.indicator.setVisibility(View.VISIBLE);
            holder.indicator_color.setVisibility(View.VISIBLE);

            // Use modulo to get the appropriate color for indicator
            int color = colors.get((com.getHierarchy() - 1) % colors.size());
            holder.indicator_color.setBackgroundColor(color);
        }

        if(com.hasHideChildren()){
            holder.hidden_children.setText("+" + Integer.toString(com.getHiddenChildren()));
            holder.hidden_children.setVisibility(View.VISIBLE);
        } else {
            holder.hidden_children.setVisibility(View.GONE);
        }

        return convertView;
    }

    // Initializes the list of colours which will be used to display comment hierarchy
    public ArrayList<Integer> initColors(Context context) {
        ArrayList<Integer> colors = new ArrayList<>();
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
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
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

    protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span)
    {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {

                final View v = view;

                // We show a dialog if the user wants to open the link
                new AlertDialog.Builder(context)
                        .setTitle("Open Link")
                        .setMessage(span.getURL())
                        .setPositiveButton("Open", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // If we click links, we go to the webview
                                System.out.println("Clicked: " + span.getURL());
                                Intent intent = new Intent(v.getContext(), WebViewActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("url", span.getURL());
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    // Works some magic with converting the html to a proper text view
    protected void setTextViewHTML(TextView text, String html) {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for(URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }

        // In the end we trim the whitespace
        text.setText(trimWhitespace(strBuilder));
    }
}
