package co.adrianblan.cheddar.utils;

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
import android.view.View;
import android.widget.TextView;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import co.adrianblan.cheddar.activities.WebViewActivity;

public class StringUtils {

    // Converts the difference between two dates into a pretty date
    // There's probably a joke in there somewhere
    public static String getPrettyDate(Long time) {

        Date past = new Date(time * 1000);
        Date now = new Date();

        if (TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) > 0) {
            return TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + "d";
        } else if (TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) > 0) {
            return TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) + "h";
        }
        if (TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) > 0) {
            return TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + "m";
        } else {
            return TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime()) + "s";
        }
    }

    // Removes trailing double whitespace, reduces the size of other double whitespace
    public static SpannableStringBuilder trimWhitespace(CharSequence source) {

        int i = source.length();

        // loop back to the first non-whitespace character
        while (--i >= 0 && Character.isWhitespace(source.charAt(i))) {
        }

        // Removes two trailing newlines
        source = source.subSequence(0, i + 1);

        SpannableStringBuilder ssb = new SpannableStringBuilder(source);

        for (i = 0; i + 1 < source.length(); i++) {
            if (Character.isWhitespace(source.charAt(i)) && Character.isWhitespace(source.charAt(i + 1))) {

                // Reduces the size of double whitespace
                ssb.setSpan(new RelativeSizeSpan(0.4f), i, i + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                i++;
            }
        }
        return ssb;
    }

    // Works some magic with converting the html to a proper text view
    public static void setTextViewHTML(TextView text, String html) {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            StringUtils.makeLinkClickable(strBuilder, span, text.getContext());
        }

        // In the end we trim the whitespace
        text.setText(StringUtils.trimWhitespace(strBuilder));
    }

    public static void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span, final Context context) {
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
}
