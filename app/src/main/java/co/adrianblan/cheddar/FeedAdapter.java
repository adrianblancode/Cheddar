package co.adrianblan.cheddar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;

/**
 * Created by Adrian on 2015-07-25.
 */
public class FeedAdapter extends BaseAdapter {

    private ArrayList<FeedItem> feedItems;
    private final Context context;

    public FeedAdapter(Context c) {
        feedItems = new ArrayList<FeedItem>();
        context = c;
    }

    public FeedAdapter(ArrayList<FeedItem> fi, Context c) {
        feedItems = fi;
        context = c;
    }

    public ArrayList<FeedItem> getFeedItems(){
        return feedItems;
    }

    public void add (FeedItem f){
        feedItems.add(f);
    }

    public void clear() {
        feedItems.clear();
    }

    public int getPosition(FeedItem f) {

        for(int i = 0; i < getCount(); i++) {
            if(feedItems.get(i).equals(f)){
                return i;
            }
        }

        return -1;
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public FeedItem getItem(int position) {
        return feedItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        class ViewHolder {
            TextView title;
            TextView shortUrl;
            TextView score;
            TextView comments;
            TextView time;
            ImageView thumbnail;
            LinearLayout body;
        }

        final FeedItem item = feedItems.get(position);

        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.feed_item, parent, false);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.feed_item_title);
            holder.shortUrl = (TextView) convertView.findViewById(R.id.feed_item_shortUrl);
            holder.score = (TextView) convertView.findViewById(R.id.feed_item_score);
            holder.comments = (TextView) convertView.findViewById(R.id.feed_item_comments);
            holder.time = (TextView) convertView.findViewById(R.id.feed_item_time);
            holder.thumbnail = (ImageView) convertView.findViewById(R.id.feed_item_thumbnail);
            holder.body = (LinearLayout) convertView.findViewById(R.id.feed_item_text);

            // Store the holder with the view.
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(item.getTitle());
        holder.shortUrl.setText(item.getShortUrl());
        holder.score.setText(Long.toString(item.getScore()));
        holder.comments.setText(Long.toString(item.getDescendants()));
        holder.time.setText(item.getTime());

        // If we have a high resolution thumbnail, display it
        if(item.getThumbnail() != null){
            holder.thumbnail.setImageBitmap(item.getThumbnail());
        } else if (item.getTextDrawable() != null) {
            // Otherwise, just use the TextDrawable
            holder.thumbnail.setImageDrawable(item.getTextDrawable());
        } else {
            // Generate TextDrawable thumbnail
            TextDrawable.IShapeBuilder builder = TextDrawable.builder().beginConfig().bold().toUpperCase().endConfig();
            TextDrawable drawable = builder.buildRect(item.getLetter(), item.getColor());
            item.setTextDrawable(drawable);
        }



        // Comment listener
        View.OnClickListener commentOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap thumbnail = item.getThumbnail();

                if(thumbnail != null) {

                    final int maxThumbnailSize = 144;

                    // We can't pass through too much data through intents (terrible)
                    // Wordpress has something silly like 512x512 px
                    if (thumbnail.getHeight() > maxThumbnailSize || thumbnail.getWidth() > maxThumbnailSize) {
                        thumbnail = Bitmap.createScaledBitmap(thumbnail, maxThumbnailSize, maxThumbnailSize, false);
                    }
                }

                Intent intent = new Intent(v.getContext(), CommentActivity.class);
                Bundle b = new Bundle();
                b.putParcelable("feedItem", item);
                intent.putExtra("thumbnail", thumbnail);
                intent.putExtras(b);
                context.startActivity(intent);
            }
        };

        // TODO move onclick listeners to somewhere that makes sense?
        // Comment, click on body
        holder.body.setOnClickListener(commentOnClickListener);

        // Webview, click on thumbnail
        if(item.getShortUrl().equals(context.getResources().getString(R.string.hacker_news_url_placeholder))){

            // If it points to hacker news, we need to go to the commentCount instead
            holder.thumbnail.setOnClickListener(commentOnClickListener);
        } else {
            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bitmap thumbnail = item.getThumbnail();
                    if(thumbnail != null) {
                        // We can't pass through too much data through intents (terrible)
                        if (thumbnail.getHeight() > 100 || thumbnail.getWidth() > 100) {
                            thumbnail = Bitmap.createScaledBitmap(thumbnail, 100, 100, false);
                        }
                    }

                    Intent intent = new Intent(v.getContext(), WebViewActivity.class);
                    Bundle b = new Bundle();
                    b.putParcelable("feedItem", item);
                    intent.putExtra("thumbnail", thumbnail);
                    intent.putExtras(b);
                    context.startActivity(intent);
                }
            });
        }

        return convertView;
    }
}
