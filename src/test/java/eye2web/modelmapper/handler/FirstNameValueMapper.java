package eye2web.modelmapper.handler;

import eye2web.modelmapper.model.MapFromField;
import eye2web.modelmapper.value.map.ValueMapper;

public class FirstNameValueMapper implements ValueMapper {
    @Override
    public Object mapToValue(final MapFromField<?> mapFromField) {

        if (mapFromField.containsValue())
            System.out.println(mapFromField.getType());

        return mapFromField.getFieldValue();
    }
}
