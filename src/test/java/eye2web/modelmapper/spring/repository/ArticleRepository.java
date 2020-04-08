package eye2web.modelmapper.spring.repository;

import eye2web.modelmapper.spring.model.Article;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ArticleRepository {

    public List<Article> getAllArticlesByGroupId(final int groupId) {

        final var articles = new ArrayList<Article>();
        articles.add(Article.builder()
                .id(1)
                .name("Test article 1")
                .build());

        articles.add(Article.builder()
                .id(2)
                .name("Test article 2")
                .build());

        return articles;
    }

}
