package eye2web.modelmapper.handler;

import eye2web.modelmapper.model.MapFromField;
import eye2web.modelmapper.value.map.ValueMapper;

import java.time.LocalDate;
import java.time.Period;

public class AgeValueMapper implements ValueMapper {

    @Override
    public Object mapToValue(final MapFromField<?> mapFromField) {

        if (mapFromField.containsValue())
            System.out.println(mapFromField.getType());

        final var birthDate = (LocalDate) mapFromField.getFieldValue();

        final var currentTestDate = LocalDate.ofYearDay(2020, 1);

        return Period.between(birthDate, currentTestDate).getYears();
    }

}
