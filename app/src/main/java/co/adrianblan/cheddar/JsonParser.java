package co.adrianblan.cheddar;

import android.os.Message;
import android.util.JsonReader;
import android.util.JsonToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adrian on 2015-07-26.
 */
public class JsonParser {

    public static Submission readJsonStream(String str) throws IOException {
        InputStream in = new ByteArrayInputStream(str.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        reader.setLenient(true);
        Submission s = null;
        try {
            s = parseSubmission(reader);
            reader.close();
        } catch (Exception e) {
            System.err.println("Json parsing failed!" + e);
        }

        return s;
    }

    /*
    public List readMessagesArray(JsonReader reader) throws IOException {
        List messages = new ArrayList();

        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readMessage(reader));
        }
        reader.endArray();
        return messages;
    }
    */

    public static Submission parseSubmission(JsonReader reader) throws IOException {

        String by = null;
        int descendants = -1;
        int id = -1;
        List<Integer> kids = null;
        int score = -1;
        long time = -1;
        String title = null;
        String type = null;
        String url = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("by")) {
                by = reader.nextString();
            } else if (name.equals("descendants")) {
                descendants = reader.nextInt();
            } else if (name.equals("id")) {
                id = reader.nextInt();
            } else if (name.equals("kids")) {
                //kids = readIntArray(reader);
            } else if (name.equals("score")) {
                score = reader.nextInt();
            } else if (name.equals("time")) {
                time = reader.nextLong();
            } else if (name.equals("title")) {
                //title = reader.nextString();
            } else if (name.equals("type")) {
                type = reader.nextString();
            } else if (name.equals("url")) {
                //url = reader.nextString();
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return new Submission(by, descendants, id, kids, score, time, title, type, url);
    }

    public static List<Integer> readIntArray(JsonReader reader) throws IOException {
        List ints = new ArrayList();

        reader.beginArray();
        while (reader.hasNext()) {
            ints.add(reader.nextInt());
        }
        reader.endArray();
        return ints;
    }
}
