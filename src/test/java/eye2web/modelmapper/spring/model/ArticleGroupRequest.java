package eye2web.modelmapper.spring.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
public class ArticleGroupRequest {

    private final int id;

    private final String groupName;
}
