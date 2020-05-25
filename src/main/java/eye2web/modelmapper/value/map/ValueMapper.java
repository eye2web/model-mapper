package eye2web.modelmapper.value.map;

import eye2web.modelmapper.ModelMapperI;
import eye2web.modelmapper.model.FromField;

public interface ValueMapper {

    Object mapToValue(final FromField fromField, final ModelMapperI modelMapper);

}
