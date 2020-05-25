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
    public void shouldMapArticleIdToArticleObject() {

        final var articleGroupRequest = ArticleGroupRequest.builder()
                .groupId(1)
                .groupName("test")
                .build();

        final var articleGroup = modelMapper.map(articleGroupRequest, ArticleGroup.class);

        Assert.assertEquals("test", articleGroup.getGroupName());
        Assert.assertTrue(Objects.nonNull(articleGroup.getArticles()));
        Assert.assertEquals(2, articleGroup.getArticles().size());
        Assert.assertEquals(1, articleGroup.getArticles().get(0).getId());
        Assert.assertEquals("Test article 1", articleGroup.getArticles().get(0).getName());
        Assert.assertEquals(2, articleGroup.getArticles().get(1).getId());
        Assert.assertEquals("Test article 2", articleGroup.getArticles().get(1).getName());
    }

}
