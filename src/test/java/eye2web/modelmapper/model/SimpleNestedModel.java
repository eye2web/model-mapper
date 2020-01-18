package eye2web.modelmapper.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class SimpleNestedModel {
    private final String nestedModelName;
}
