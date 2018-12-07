package eye2web.modelmapper.value.map;

public class DefaultValueMapper implements ValueMapper {

    @Override
    public Object mapToValue(final String fieldName, final Object fieldValue) {
        return fieldValue;
    }
}
