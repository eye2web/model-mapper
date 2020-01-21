package eye2web.modelmapper.value.map;

import eye2web.modelmapper.model.FromField;

public class DefaultValueMapper implements ValueMapper {

    @Override
    public Object mapToValue(final FromField fromField) {
        return fromField.getFieldValue();
    }
}
