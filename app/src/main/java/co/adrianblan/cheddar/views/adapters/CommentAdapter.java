package co.adrianblan.cheddar.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devspark.robototextview.widget.RobotoTextView;

import java.util.List;

import co.adrianblan.cheddar.R;
import co.adrianblan.cheddar.models.Comment;
import co.adrianblan.cheddar.models.FeedItem;
import co.adrianblan.cheddar.utils.DesignUtils;
import co.adrianblan.cheddar.utils.StringUtils;
import co.adrianblan.cheddar.views.JellyBeanCompatTextView;
import co.adrianblan.cheddar.views.TextViewFixTouchConsume;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

// RecyclerViewAdapter Header Code adapted from
// https://gist.github.com/hister/d56c00fb5fd2dfaf279b
public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<Comment> comments;
    private FeedItem feedItem;
    private Context context;
    private static int[] colors = {
            R.color.colorPrimary,
            R.color.materialPink,
            R.color.materialDeepPurple,
            R.color.materialBlue,
            R.color.materialGreen,
            R.color.materialAmber
    };

    public CommentAdapter(List<Comment> comments, Context context, FeedItem feedItem) {
        this.comments = comments;
        this.feedItem = feedItem;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == TYPE_ITEM) {

            //Inflate the custom layout
            View itemView = inflater.inflate(R.layout.comment, parent, false);

            //Return a new holder instance
            return new ViewHolder(itemView);
        } else {

            View feedView = inflater.inflate(R.layout.feed_item, parent, false);
            return new FeedAdapter.ViewHolder(feedView);

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommentAdapter.ViewHolder) {
            bindCommentHolder((CommentAdapter.ViewHolder) holder, feedItem, getComment(position), context);
        } else if (holder instanceof FeedAdapter.ViewHolder) {
            FeedAdapter.bindFeedItemHolderExpanded((FeedAdapter.ViewHolder) holder, feedItem);
        }
    }

    public static void bindCommentHolder(ViewHolder commentHolder, FeedItem feedItem, Comment comment, Context context) {
        commentHolder.title.setText(comment.getBy());

        // If the author of the feed item is also the author of the comment
        if (comment.getBy().equals(feedItem.getBy())) {
            commentHolder.title.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            commentHolder.title.setText(commentHolder.title.getText() + " [OP]");
        } else {
            commentHolder.title.setTextColor(ContextCompat.getColor(context, R.color.abc_secondary_text_material_light));
        }

        // If the comment exists
        if (comment.getBody() != null && !comment.hasHideChildren()) {
            commentHolder.body.setVisibility(View.VISIBLE);

            // We can't show the text as is, but have to parse it as html
            StringUtils.setTextViewHTML(commentHolder.body, comment.getBody());

            //This link movement method only consumes on URL clicks
            commentHolder.body.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());
        } else {
            commentHolder.body.setVisibility(View.GONE);
        }

        commentHolder.time.setText(comment.getTime());

        // Adds padding based on hierarchy, and adds hierarchy indicator
        commentHolder.indicator.setPadding((int) DesignUtils.dpToPixels(4, context) * (comment.getHierarchy() - 1), 0, 0, 0);

        // We don't need the indicator for top level commentCount
        if (comment.getHierarchy() == 0) {
            commentHolder.indicator.setVisibility(View.GONE);
            commentHolder.indicator_color.setVisibility(View.GONE);
        } else {
            commentHolder.indicator.setVisibility(View.VISIBLE);
            commentHolder.indicator_color.setVisibility(View.VISIBLE);

            // Use modulo to get the appropriate color for indicator
            int color = ContextCompat.getColor(context, colors[(comment.getHierarchy() - 1) % colors.length]);
            commentHolder.indicator_color.setBackgroundColor(color);
        }

        if (comment.hasHideChildren()) {
            commentHolder.hidden_children.setText("+" + Integer.toString(comment.getHiddenChildren()));
            commentHolder.hidden_children.setVisibility(View.VISIBLE);
        } else {
            commentHolder.hidden_children.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return comments.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private Comment getComment(int position) {
        return comments.get(position - 1);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public RobotoTextView title;
        public TextView body;
        public TextView time;
        public LinearLayout container;
        public LinearLayout text_container;
        public LinearLayout indicator;
        public LinearLayout indicator_color;
        public TextView hidden_children;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (RobotoTextView) itemView.findViewById(R.id.comment_title);
            body = (JellyBeanCompatTextView) itemView.findViewById(R.id.comment_body);
            time = (TextView) itemView.findViewById(R.id.comment_time);
            container = (LinearLayout) itemView.findViewById(R.id.comment);
            text_container = (LinearLayout) itemView.findViewById(R.id.comment_text_container);
            indicator = (LinearLayout) itemView.findViewById(R.id.comment_indicator);
            indicator_color = (LinearLayout) itemView.findViewById(R.id.comment_indicator_color);
            hidden_children = (TextView) itemView.findViewById(R.id.comment_hidden_children);
        }
    }
}
