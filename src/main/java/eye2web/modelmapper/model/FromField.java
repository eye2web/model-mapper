package eye2web.modelmapper.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Builder
@Getter
@RequiredArgsConstructor
public class FromField {
    private final String fieldName;
    private final Object fieldValue;

    public boolean containsValue() {
        return Objects.nonNull(fieldValue);
    }

    public Class<? extends Object> getType() {
        return fieldValue.getClass();
    }
}
