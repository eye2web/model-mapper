package eye2web.modelmapper.handler;

import eye2web.modelmapper.model.FromField;
import eye2web.modelmapper.value.map.ValueMapper;

import java.time.LocalDate;
import java.time.Period;

public class AgeValueMapper implements ValueMapper {

    @Override
    public Object mapToValue(final FromField fromField) {

        if (fromField.containsValue())
            System.out.println(fromField.getType());

        final var birthDate = (LocalDate) fromField.getFieldValue();

        final var currentTestDate = LocalDate.ofYearDay(2020, 1);

        return Period.between(birthDate, currentTestDate).getYears();
    }

}
