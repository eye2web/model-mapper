package eye2web.modelmapper.spring.repository;

import eye2web.modelmapper.spring.model.Article;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ArticleRepository {

    public Optional<Article> getArticleById(final int id) {

        if (id == 1) {
            return Optional.of(Article.builder()
                    .id(1)
                    .name("Test article")
                    .build());
        }

        return Optional.empty();
    }

}
