package co.adrianblan.cheddar.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.List;

import co.adrianblan.cheddar.R;
import co.adrianblan.cheddar.activities.CommentActivity;
import co.adrianblan.cheddar.activities.WebViewActivity;
import co.adrianblan.cheddar.models.FeedItem;
import co.adrianblan.cheddar.views.listeners.FeedItemClickListener;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private List<FeedItem> items;

    public FeedAdapter(List<FeedItem> items) {
        this.items = items;
    }

    @Override
    public FeedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //Inflate the custom layout
        View itemView = inflater.inflate(R.layout.feed_item, parent, false);

        //Return a new holder instance
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FeedAdapter.ViewHolder holder, int position) {
        FeedItem item = items.get(position);

        holder.titleView.setText(item.getTitle());
        holder.shortUrlView.setText(item.getShortUrl());
        holder.scoreView.setText(Long.toString(item.getScore()));
        holder.commentsView.setText(Long.toString(item.getDescendants()));
        holder.timeView.setText(item.getTime());

        //If we have a high resolution thumbnail, display it
        if (item.getThumbnail() != null) {
            holder.thumbnailView.setImageBitmap(item.getThumbnail());
        } else if (item.getTextDrawable() != null) {
            // Otherwise, just use the TextDrawable
            holder.thumbnailView.setImageDrawable(item.getTextDrawable());
        } else {
            // Generate new TextDrawable thumbnail
            TextDrawable.IShapeBuilder builder = TextDrawable.builder().beginConfig().bold().toUpperCase().endConfig();
            TextDrawable drawable = builder.buildRect(item.getLetter(), item.getColor());
            item.setTextDrawable(drawable);
        }

        // Click on body -> Comment Activity
        View.OnClickListener commentListener = new FeedItemClickListener(CommentActivity.class, item);
        holder.bodyView.setOnClickListener(commentListener);

        // Click on thumbnail -> Web view
        // If link points to Hacker News go to comments then.
        if (item.getShortUrl().equals(holder.itemView.getContext().getString(R.string.hacker_news_url_placeholder))) {
            holder.thumbnailView.setOnClickListener(commentListener);
        } else {
            holder.thumbnailView.setOnClickListener(new FeedItemClickListener(WebViewActivity.class, item));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    /**
     * Provide direct reference to the views within the data of each item
     * Used to cache views
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleView;
        public TextView shortUrlView;
        public TextView scoreView;
        public TextView commentsView;
        public TextView timeView;
        public ImageView thumbnailView;
        public LinearLayout bodyView;

        public ViewHolder(View itemView) {
            super(itemView);

            titleView = (TextView) itemView.findViewById(R.id.feed_item_title);
            shortUrlView = (TextView) itemView.findViewById(R.id.feed_item_shortUrl);
            scoreView = (TextView) itemView.findViewById(R.id.feed_item_score);
            commentsView = (TextView) itemView.findViewById(R.id.feed_item_comments);
            timeView = (TextView) itemView.findViewById(R.id.feed_item_time);
            thumbnailView = (ImageView) itemView.findViewById(R.id.feed_item_thumbnail);
            bodyView = (LinearLayout) itemView.findViewById(R.id.feed_item_text);
            
        }
    }
}
