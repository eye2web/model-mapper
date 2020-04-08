package eye2web.modelmapper.spring.model;

import eye2web.modelmapper.annotations.MapValue;
import eye2web.modelmapper.mapper.GroupArticlesMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ArticleGroup {

    @MapValue(fieldName = "groupId", valueMapper = GroupArticlesMapper.class)
    private List<Article> articles;

    private String groupName;
}
