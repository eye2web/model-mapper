package eye2web.modelmapper.mapper;

import eye2web.modelmapper.model.FromField;
import eye2web.modelmapper.value.map.ValueMapper;

public class FirstNameValueMapper implements ValueMapper {
    @Override
    public Object mapToValue(final FromField fromField) {

        if (fromField.containsValue())
            System.out.println(fromField.getType());

        return fromField.getFieldValue();
    }
}
