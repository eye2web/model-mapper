package eye2web.modelmapper.mapper;

import eye2web.modelmapper.model.FromField;
import eye2web.modelmapper.spring.repository.ArticleRepository;
import eye2web.modelmapper.value.map.ValueMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ArticleMapper implements ValueMapper {

    private final ArticleRepository articleRepository;

    @Override
    public Object mapToValue(FromField fromField) {

        final var articleOpt = articleRepository.getArticleById((int) fromField.getFieldValue());

        return articleOpt.orElse(null);
    }
}
