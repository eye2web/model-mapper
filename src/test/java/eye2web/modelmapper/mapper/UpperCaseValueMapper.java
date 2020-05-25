package eye2web.modelmapper.mapper;

import eye2web.modelmapper.ModelMapperI;
import eye2web.modelmapper.model.FromField;
import eye2web.modelmapper.value.map.ValueMapper;

public class UpperCaseValueMapper implements ValueMapper {
    @Override
    public Object mapToValue(FromField fromField, final ModelMapperI modelMapper) {

        if (fromField.getType().equals(String.class)) {
            return ((String) fromField.getFieldValue()).toUpperCase();
        }
        return null;
    }
}
