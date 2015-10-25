package demo.tuning.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import demo.tuning.model.Article;

@Repository
public class ArticleDao {
    public List<Article> getArticles() {
        try {
            // Killing time
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        ArrayList<Article> list = new ArrayList<Article>();
        list.add(new Article());
        list.add(new Article());
        list.add(new Article());
        list.add(new Article());
        return list;
    }
}
