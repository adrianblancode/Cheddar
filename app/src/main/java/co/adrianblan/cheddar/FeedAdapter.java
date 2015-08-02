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

    private ArrayList<FeedItem> feedItems = new ArrayList<FeedItem>();
    private final Context context;

    public FeedAdapter(Context c) {
        context = c;
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
        holder.comments.setText(Long.toString(item.getCommentCount()));
        holder.time.setText(item.getTime());

        // If we have a high resolution thumbnail, display it
        if(item.getThumbnail() != null){
            holder.thumbnail.setImageBitmap(item.getThumbnail());
        } else if (item.getTextDrawable() != null) {

            // Otherwise, just use the TextDrawable
            holder.thumbnail.setImageDrawable(item.getTextDrawable());
        }


        View.OnClickListener commentOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CommentActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("feedItem", item);
                intent.putExtra("thumbnail", item.getThumbnail());

                intent.putExtras(b);
                v.getContext().startActivity(intent);
            }
        };

        // TODO move onclick listeners to somewhere that makes sense?
        // If we click the body, get to the commentCount
        holder.body.setOnClickListener(commentOnClickListener);

        // If we click the thumbnail, get to the webview
        if(item.getShortUrl().equals(context.getResources().getString(R.string.hacker_news_url_placeholder))){

            // If it points to hacker news, we need to go to the commentCount instead
            holder.thumbnail.setOnClickListener(commentOnClickListener);
        } else {
            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), WebViewActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("feedItem", item);
                    intent.putExtra("thumbnail", item.getThumbnail());
                    intent.putExtras(b);
                    v.getContext().startActivity(intent);
                }
            });
        }

        return convertView;
    }
}
