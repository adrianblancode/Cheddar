package co.adrianblan.cheddar;

import android.graphics.BitmapFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Adrian on 2015-07-26.
 */

// This class takes an URL to a site and extracts an thumbnail url
public class ThumbnailSearcher {

    public static String getThumbnailUrl(String site) {

        Document doc = null;

        try {
            doc = Jsoup.connect(site).ignoreContentType(true).get();
        } catch (IOException e) {
            System.err.println(e);
            return null;
        }

        //Really lazy regex to extract image url from JSON
        Pattern pattern = Pattern.compile("https?:\\/\\/[^&]*\\.png");
        Matcher matcher = pattern.matcher(doc.toString());

        if (matcher.find()) {
            return matcher.group(0);
        }

        return null;
    }
}
