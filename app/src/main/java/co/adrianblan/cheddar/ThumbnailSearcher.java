package co.adrianblan.cheddar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Adrian on 2015-07-26.
 */

// This class takes an URL to a site and returns an url to the best thumbnail to use
public class ThumbnailSearcher {

    public static String getThumbnailUrl(String site) {

        Document doc = null;
        String baseUrl = null;
        String fullUrl = null;

        try {
            URL siteUrl = new URL(site);
            baseUrl = siteUrl.getHost().replace("www.", "");
            fullUrl = siteUrl.getProtocol() + "://" + baseUrl;

            doc = Jsoup.connect(site).get();
        } catch (IOException e) {
            System.err.println(e);
            return null;
        }

        Elements links;
        links = doc.getElementsByAttributeValueStarting("rel", "apple-touch-icon");
        for (Element link : links) {

            // We make sure to return a full absolute URL
            if(!link.attr("href").startsWith("/")){
                return link.attr("href");
            } else {
                return fullUrl + link.attr("href");
            }
        }

        links = doc.getElementsByAttributeValueStarting("property", "og:image");
        for (Element link : links) {

            // We make sure to return a full absolute URL
            if(!link.attr("content").startsWith("/")){
                return link.attr("content");
            } else {
                return fullUrl + link.attr("content");
            }
        }

        /*
        links = doc.getElementsByTag("img");
        for (Element link : links) {

            // Basic sanity checking
            if(link.attr("src").contains(".png") || link.attr("src").contains(".jpg") || link.attr("src").contains(".jpeg")) {

                // We make sure to return a full absolute URL
                if (!link.attr("src").startsWith("/")) {
                    return link.attr("src");
                } else {
                    return fullUrl + link.attr("src");
                }
            }
        }*/

        return null;
    }
}
