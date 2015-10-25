package demo.tuning.biz;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import demo.tuning.dao.ArticleDao;
import demo.tuning.model.Article;

@Service
public class ArticleServices {
    private ArticleDao articleDao;

    public List<Article> getArticles() {
        try {
            // Killing time
            Thread.sleep(400);
        } catch (InterruptedException e) {
        }
        return articleDao.getArticles();
    }

    public String getArticleAuthorFromCache(Integer articleId) {
        try {
            // Killing time
            Thread.sleep(2);
        } catch (InterruptedException e) {
        }
        return "";
    }

    @Autowired
    public void setArticleDao(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }
}
