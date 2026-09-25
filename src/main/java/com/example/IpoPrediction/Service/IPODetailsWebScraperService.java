package com.example.IpoPrediction.Service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class IPODetailsWebScraperService {

    public String scrape(String url) throws IOException {
        Document document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(10_000)
                .followRedirects(false)
                .get();

        return document.body().text();
    }
}
