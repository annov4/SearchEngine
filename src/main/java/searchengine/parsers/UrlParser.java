package searchengine.parsers;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.dto.statistics.StatisticsPage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

@Slf4j
public class UrlParser extends RecursiveTask<List<StatisticsPage>> {
    private final String url;
    private final List<String> urlList;
    private final List<StatisticsPage> statisticsPageList;

    public UrlParser(String url, List<StatisticsPage> statisticsPageList, List<String> urlList) {
        this.url = url;
        this.statisticsPageList = statisticsPageList;
        this.urlList = urlList;
    }

    public Document getConnect(String url) {
        Document document = null;
        try {
            Thread.sleep(150);
            document = Jsoup.connect(url).userAgent(UserAgent.getUserAgent())
                    .referrer("http://www.google.com").get();
        } catch (Exception e) {
            log.debug(url + " - Unable to connect to the site");
        }
        return document;
    }

    @Override
    protected List<StatisticsPage> compute() {
        try {
            Thread.sleep(150);
            Document document = getConnect(url);
            String html = document.outerHtml();
            Connection.Response response = document.connection().response();
            int statusCode = response.statusCode();
            StatisticsPage statisticsPage = new StatisticsPage(url, html, statusCode);
            statisticsPageList.add(statisticsPage);
            Elements elements = document.select("body").select("a");
            List<UrlParser> taskList = new ArrayList<>();
            for (Element element : elements) {
                String link = element.attr("abs:href");
                if (link.startsWith(element.baseUri())
                        && !link.equals(element.baseUri())
                        && !link.contains(".png") && !urlList.contains(link)
                        && !link.contains(".jpg") && !link.contains(".JPG")
                        && !link.contains("#") && !link.contains(".pdf") ) {
                    urlList.add(link);
                    UrlParser task = new UrlParser(link, statisticsPageList, urlList);
                    task.fork();
                    taskList.add(task);
                }
            }
            taskList.forEach(ForkJoinTask::join);
        } catch (Exception e) {
            log.debug(url + " - Parsing Error");
            StatisticsPage statisticsPage = new StatisticsPage(url, "", 500);
            statisticsPageList.add(statisticsPage);
        }
        return statisticsPageList;
    }
}