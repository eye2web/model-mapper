package eye2web.modelmapper.handler;

import eye2web.modelmapper.value.map.ValueMapper;

public class FirstNameValueMapper implements ValueMapper {
    @Override
    public Object mapToValue(final String fieldName, final Object fieldValue) {

        return fieldValue;
    }
}
