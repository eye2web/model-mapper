package eye2web.modelmapper.mapper;

import eye2web.modelmapper.model.FromField;
import eye2web.modelmapper.spring.repository.ArticleRepository;
import eye2web.modelmapper.value.map.ValueMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GroupArticlesMapper implements ValueMapper {

    private final ArticleRepository articleRepository;

    @Override
    public Object mapToValue(final FromField fromField) {

        return articleRepository.getAllArticlesByGroupId((int) fromField.getFieldValue());
    }
}
