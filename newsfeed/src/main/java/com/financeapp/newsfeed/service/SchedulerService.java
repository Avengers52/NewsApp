package com.financeapp.newsfeed.service;

import com.financeapp.newsfeed.model.NewsArticle;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SchedulerService {
    private final MailService mailService;
    private final NewsService newsService;

    @Value("${MAIL_FROM_ADDRESS}")
    private String fromAddress;

    public SchedulerService(MailService mailService, NewsService newsService) {
        this.mailService = mailService;
        this.newsService = newsService;
    }

    @Scheduled(cron = "0 0 8 * * *", zone = "America/Chicago")
    public void sendDailyFinanceSummary() {
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
            System.out.println("✅ Daily email sent at 8 AM");
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send daily email: " + e.getMessage());
        }
    }
}

