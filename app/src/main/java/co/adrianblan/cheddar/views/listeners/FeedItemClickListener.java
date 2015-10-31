package co.adrianblan.cheddar.views.listeners;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import co.adrianblan.cheddar.models.FeedItem;

public class FeedItemClickListener implements View.OnClickListener {

    private Class intentClass;
    private FeedItem item;

    public FeedItemClickListener(Class intent, FeedItem item) {
        this.intentClass = intent;
        this.item = item;
    }

    @Override
    public void onClick(View v) {

        Bitmap thumbnail = item.getThumbnail();

        if (thumbnail != null) {

            final int maxThumbnailSize = 144;

            // We can't pass through too much data through intents (terrible)
            // Wordpress has something silly like 512x512 px
            if (thumbnail.getHeight() > maxThumbnailSize || thumbnail.getWidth() > maxThumbnailSize) {
                thumbnail = Bitmap.createScaledBitmap(thumbnail, maxThumbnailSize, maxThumbnailSize, false);
            }
        }

        Intent intent = new Intent(v.getContext(), intentClass);
        Bundle b = new Bundle();
        b.putParcelable("feedItem", item);
        intent.putExtra("thumbnail", thumbnail);
        intent.putExtras(b);

        v.getContext().startActivity(intent);
    }
}
