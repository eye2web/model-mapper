package eye2web.modelmapper.value.map;

public interface MultiValueMapper {
    
    Object mapToValue(final String[] fieldNames, final Object[] fieldValues);
}
