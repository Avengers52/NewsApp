package com.financeapp.newsfeed.service;

import com.financeapp.newsfeed.model.NewsArticle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


@Service
public class NewsService {
    @Value("${newsapi.key}")
    private String apiKey;

    @Value("${news.keywords}")
    private String keywordList;

    private final RestTemplate restTemplate = new RestTemplate();
    public List<NewsArticle> fetchFinanceNews() {
        String url = "https://newsapi.org/v2/top-headlines?category=business&apiKey=" + apiKey;

        var response = restTemplate.getForObject(url, NewsApiResponse.class);
        if (response != null && response.getArticles() != null) {
            return response.getArticles().stream()
                    .map(article -> {
                        NewsArticle news = new NewsArticle();
                        news.setTitle(article.getTitle());
                        news.setDescription(article.getDescription());
                        news.setUrl(article.getUrl());
                        news.setPublishedAt(article.getPublishedAt());
                        return news;
                    }).collect(Collectors.toList());
        }
        return new ArrayList<>(); // return empty if null
    }

    public List<NewsArticle> filterByKeywords(List<NewsArticle> articles, List<String> keywords) {
        return articles.stream()
                .filter(article ->
                        keywords.stream().anyMatch(keyword ->
                                (article.getTitle() != null && article.getTitle().toLowerCase().contains(keyword.toLowerCase())) ||
                                        (article.getDescription() != null && article.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                        )
                ).collect(Collectors.toList());
    }

    public String generateFormattedSummary(List<NewsArticle> articles) {
        StringBuilder summary = new StringBuilder();
        summary.append("📊 Today's Financial News Summary:\n\n");

        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_DATE_TIME;
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a z");

        for (int i = 0; i < Math.min(5, articles.size()); i++) {
            NewsArticle article = articles.get(i);

            String formattedDate = "N/A";
            try {
                if (article.getPublishedAt() != null) {
                    ZonedDateTime utcTime = ZonedDateTime.parse(article.getPublishedAt(), inputFormatter);
                    ZonedDateTime localTime = utcTime.withZoneSameInstant(ZoneId.of("America/Chicago")); // your local time zone
                    formattedDate = outputFormatter.format(localTime);
                }
            } catch (DateTimeParseException e) {
                // fallback to raw publishedAt or "N/A"
                formattedDate = article.getPublishedAt();
            }

            summary.append("• ").append(article.getTitle()).append("\n");
            summary.append("  🕒 Published: ").append(formattedDate).append("\n");
            summary.append("  ").append(article.getDescription() != null ? article.getDescription() : "No description").append("\n");
            summary.append("  🔗 ").append(article.getUrl()).append("\n\n");
        }

        return summary.toString();
    }

    public String getKeywordList() {
        return keywordList;
    }


    private static class NewsApiResponse {
        private List<Article> articles;

        public List<Article> getArticles() {
            return articles;
        }

        public void setArticles(List<Article> articles) {
            this.articles = articles;
        }

        private static class Article {
            private String title;
            private String description;
            private String url;
            private String publishedAt;

            public String getTitle() { return title; }
            public void setTitle(String title) { this.title = title; }

            public String getDescription() { return description; }
            public void setDescription(String description) { this.description = description; }

            public String getUrl() { return url; }
            public void setUrl(String url) { this.url = url; }
            public String getPublishedAt() { return publishedAt; }
            public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }
        }
    }

}
