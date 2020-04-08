package eye2web.modelmapper.mapper;

import eye2web.modelmapper.model.FromField;
import eye2web.modelmapper.value.map.ValueMapper;

import java.time.LocalDate;
import java.time.Period;

public class AgeValueMapper implements ValueMapper {

    @Override
    public Object mapToValue(final FromField fromField) {

        if (!fromField.containsValue())
            return 0;

        final var birthDate = (LocalDate) fromField.getFieldValue();
        final var currentDate = LocalDate.now();

        return Period.between(birthDate, currentDate).getYears();
    }

}
