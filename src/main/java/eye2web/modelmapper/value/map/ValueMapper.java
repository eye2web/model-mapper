package eye2web.modelmapper.value.map;

import eye2web.modelmapper.model.MapFromField;

public interface ValueMapper {

    Object mapToValue(final MapFromField<?> mapFromField);

}
