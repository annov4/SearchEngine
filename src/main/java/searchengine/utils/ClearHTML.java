package searchengine.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class ClearHTML {

    public static String clear(String content, String selector) {
        StringBuilder html = new StringBuilder();
        var doc = Jsoup.parse(content);
        var elements = doc.select(selector);
        for (Element element : elements) {
            html.append(element.html());
        }
        return Jsoup.parse(html.toString()).text();
    }
}