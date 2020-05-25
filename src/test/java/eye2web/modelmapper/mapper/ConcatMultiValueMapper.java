package eye2web.modelmapper.mapper;

import eye2web.modelmapper.model.FromField;
import eye2web.modelmapper.value.map.MultiValueMapper;

import java.util.Set;

public class ConcatMultiValueMapper implements MultiValueMapper {

    @Override
    public Object mapToValue(final Set<FromField> fromFieldSet) {
        
        return fromFieldSet.stream()
                .filter(FromField::containsValue)
                .filter(field -> field.getType().equals(String.class))
                .map(field -> (String) field.getFieldValue())
                .reduce((partial, value) ->
                        partial + " " + value
                ).get();
    }

}
