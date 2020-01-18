package eye2web.modelmapper.value.map;

import eye2web.modelmapper.model.MapFromField;

public class DefaultValueMapper implements ValueMapper {

    @Override
    public Object mapToValue(final MapFromField<?> mapFromField) {
        return mapFromField.getFieldValue();
    }
}
