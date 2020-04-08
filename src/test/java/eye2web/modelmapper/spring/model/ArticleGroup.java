package eye2web.modelmapper.spring.model;

import eye2web.modelmapper.annotations.MapValue;
import eye2web.modelmapper.mapper.ArticleMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArticleGroup {

    @MapValue(fieldName = "id", valueMapper = ArticleMapper.class)
    private Article article;

    private String groupName;
}
