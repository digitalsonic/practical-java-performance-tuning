package demo.tuning.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.tuning.biz.ArticleServices;
import demo.tuning.model.Article;

@RestController
public class DemoController {
    private ArticleServices articleServices;

    @RequestMapping("/articles")
    public List<Article> getArticles() {
        List<Article> articles = articleServices.getArticles();

        for (Article a : articles) {
            // Just a demo for profiler. No use at all
            articleServices.getArticleAuthorFromCache(a.getId());
        }
        return articles;
    }

    @Autowired
    public void setArticleServices(ArticleServices articleServices) {
        this.articleServices = articleServices;
    }
}
