package com.ongoni.csit.ssunews;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsXmlParser {

    private static final String LOG_TAG = "SSUNewsParser";
    private static final String namespaces = null;

    public List<Article> parse(String response) throws XmlPullParserException, IOException {
        Log.d(LOG_TAG, "parse");
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(new StringReader(response));
        parser.nextTag();

        return readChannel(parser);
    }

    private List readChannel(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Article> articles = new ArrayList<>();
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, namespaces, "channel");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("item")) {
                articles.add(readArticle(parser));
            } else {
                skip(parser);
            }
        }
        return articles;
    }

    private Article readArticle(XmlPullParser parser) throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, namespaces, "item");

        Article article = new Article();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                article.title = readTitle(parser);
            } else if (name.equals("description")) {
                article.description = readDescription(parser);
            } else if (name.equals("pubDate")) {
                article.pubDate = readPubDate(parser);
            } else if (name.equals("link")) {
                article.link = readLink(parser);
            } else if (name.equals("guid")) {
                article.guid = Long.parseLong(readGuid(parser));
            } else if (name.equals("enclosure")) {
                article.imageUrl = readImageUrl(parser);
            } else {
                skip(parser);
            }
        }

        return article;
    }

    private String readTitle(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, namespaces, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, namespaces, "title");
        return title;
    }

    private String readDescription(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, namespaces, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, namespaces, "description");
        return description;
    }

    private String readPubDate(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, namespaces, "pubDate");
        String pubDate = readText(parser);

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(pubDate);
            pubDate = new SimpleDateFormat("HH:mm dd.MM.yy").format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        parser.require(XmlPullParser.END_TAG, namespaces, "pubDate");
        return pubDate;
    }

    private String readLink(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, namespaces, "link");
        String pubDate = readText(parser);
        parser.require(XmlPullParser.END_TAG, namespaces, "link");
        return pubDate;
    }

    private String readGuid(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, namespaces, "guid");
        String guid = readText(parser);
        parser.require(XmlPullParser.END_TAG, namespaces, "guid");
        return guid;
    }

    private String readImageUrl(XmlPullParser parser) throws  XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, namespaces, "enclosure");
        String url = parser.getAttributeValue(null, "url");
        while (parser.getEventType() != XmlPullParser.END_TAG) {
            parser.next();
        }
        parser.require(XmlPullParser.END_TAG, namespaces, "enclosure");
        return url;
    }

    private String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            result = result.replace("&quot;", "\"").replace("&amp;", "&");
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
