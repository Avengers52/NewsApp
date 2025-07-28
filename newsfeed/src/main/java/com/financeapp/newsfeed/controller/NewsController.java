package com.financeapp.newsfeed.controller;

import com.financeapp.newsfeed.model.NewsArticle;
import com.financeapp.newsfeed.service.MailService;
import com.financeapp.newsfeed.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/news")
public class NewsController {
    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @Autowired
    private MailService mailService;

    @Value("${MAIL_FROM_ADDRESS}")
    private String fromAddress;

    @GetMapping("/email")
    public ResponseEntity<String> sendNewsSummaryEmail() {
        List<String> keywords = Arrays.stream(newsService.getKeywordList().split(","))
                .map(String::trim)
                .toList();

        List<NewsArticle> rawNews = newsService.fetchFinanceNews();
        List<NewsArticle> filteredNews = newsService.filterByKeywords(rawNews, keywords);
        String summary = newsService.generateFormattedSummary(filteredNews);

        try {
            mailService.sendNewsSummary(
                    fromAddress,
                    "📩 Daily Finance News Summary",
                    summary
            );
            return ResponseEntity.ok("Email sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error sending email: " + e.getMessage());
        }
    }

    @GetMapping
    public List<NewsArticle> getFinanceNews() {
        return newsService.fetchFinanceNews();
    }

    @GetMapping("/summary")
    public ResponseEntity<String> getFormattedNewsSummary() {
        List<String> keywords = Arrays.stream(newsService.getKeywordList().split(","))
                .map(String::trim)
                .collect(Collectors.toList());
        List<NewsArticle> rawNews = newsService.fetchFinanceNews();
        List<NewsArticle> filteredNews = newsService.filterByKeywords(rawNews, keywords);
        String summary = newsService.generateFormattedSummary(filteredNews);

        return ResponseEntity.ok(summary);
    }
}
