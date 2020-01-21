package eye2web.modelmapper.value.map;

import eye2web.modelmapper.model.FromField;

import java.util.Set;

public interface MultiValueMapper {

    Object mapToValue(final Set<FromField> fromFieldSet);
}
