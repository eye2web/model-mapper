package eye2web.modelmapper.handler;

import eye2web.modelmapper.value.map.MultiValueMapper;

public class ConcatMultiValueMapper implements MultiValueMapper {

    @Override
    public Object mapToValue(final String[] fieldNames, final Object[] fieldValues) {

        final StringBuilder stringBuilder = new StringBuilder();

        for (final Object value : fieldValues) {

            stringBuilder.append(value);
            stringBuilder.append(" ");
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

}
