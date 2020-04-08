package eye2web.modelmapper.spring.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Article {

    private final int id;
    private final String name;

}
