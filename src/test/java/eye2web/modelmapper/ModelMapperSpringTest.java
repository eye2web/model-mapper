package eye2web.modelmapper;

import eye2web.modelmapper.spring.model.ArticleGroup;
import eye2web.modelmapper.spring.model.ArticleGroupRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringStartApplication.class)
public class ModelMapperSpringTest {

    @Autowired
    private ModelMapper modelMapper;


    @Test
    public void shouldMapArticleIdToArticleObject() throws Exception {

        final var articleGroupRequest = ArticleGroupRequest.builder()
                .id(1)
                .groupName("test")
                .build();

        final var result = modelMapper.map(articleGroupRequest, ArticleGroup.class);

        Assert.assertTrue(Objects.nonNull(result.getArticle()));
        Assert.assertEquals("test", result.getGroupName());
        Assert.assertEquals(1, result.getArticle().getId());
        Assert.assertEquals("Test article", result.getArticle().getName());
    }

}
