package eye2web.modelmapper.handler;

import eye2web.modelmapper.model.MapFromField;
import eye2web.modelmapper.value.map.MultiValueMapper;

import java.util.Set;

public class ConcatMultiValueMapper implements MultiValueMapper {

    @Override
    public Object mapToValue(final Set<MapFromField<?>> mapFromFieldSet) {

        final StringBuilder stringBuilder = new StringBuilder();

        for (final var mapFrom : mapFromFieldSet) {

            if (mapFrom.containsValue())
                System.out.println(mapFrom.getType());

            stringBuilder.append(mapFrom.getFieldValue());
            stringBuilder.append(" ");
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

}
