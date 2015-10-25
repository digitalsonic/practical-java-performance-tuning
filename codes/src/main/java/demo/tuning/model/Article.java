package demo.tuning.model;

import java.util.Random;

public class Article {
    private static final Random ID_GENERATOR = new Random();

    private Integer id = ID_GENERATOR.nextInt();
    private String title = "";

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
