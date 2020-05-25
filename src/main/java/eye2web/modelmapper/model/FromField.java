package eye2web.modelmapper.model;

import lombok.*;

import java.util.Objects;

@Builder
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FromField {
    @NonNull
    private final String fieldName;
    private final Object fieldValue;

    @Builder.Default
    private final boolean isIterableItem = false;

    public boolean containsValue() {
        return Objects.nonNull(fieldValue);
    }

    public Class<? extends Object> getType() {
        return fieldValue.getClass();
    }
}
