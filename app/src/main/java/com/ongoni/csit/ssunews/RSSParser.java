package com.ongoni.csit.ssunews;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class RSSParser {

    public List<Article> parse(String inUrl) {
        String res = null;
        try {
            URL url = new URL(inUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            try {
                InputStream istream = conn.getInputStream();
                ByteArrayOutputStream ostream = new ByteArrayOutputStream();

                try {
                    byte[] buf = new byte[32 * 1024];
                    while (true) {
                        int bytesRead = istream.read(buf);
                        if (bytesRead < 0) {
                            break;
                        }
                        ostream.write(buf, 0, bytesRead);
                    }
                    res = ostream.toString("UTF-8");
                    if (res.startsWith("\n")) {
                        res = res.substring(1);
                    }
                    if (res.startsWith("\uFEFF")) {
                        res = res.substring(1);
                    }
                } finally {
                    istream.close();
                    ostream.close();
                }
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        NewsXmlParser parser = new NewsXmlParser();
        List<Article> data = null;
        try {
            data = parser.parse(res);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i + 1 < data.size();) {
            if (data.get(i).guid == data.get(i + 1).guid) {
                data.remove(i);
            } else {
                i++;
            }
        }
        return data;
    }
}
