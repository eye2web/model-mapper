package eye2web.modelmapper.model;

import eye2web.modelmapper.FieldProperties;
import eye2web.modelmapper.annotations.MapValueFromField;
import eye2web.modelmapper.annotations.MapValuesFromFields;
import eye2web.modelmapper.handler.ConcatMultiValueMapper;
import eye2web.modelmapper.handler.FirstNameValueMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelAResponse {

    private int id;

    private String firstName;

    @MapValueFromField(fieldName = "firstName",
            valueMapper = FirstNameValueMapper.class,
            properties = {FieldProperties.IGNORE_NULL_VALUES})
    private String fName;

    @MapValueFromField(fieldName = "lastName")
    private String lName;

    @MapValuesFromFields(fieldNames = {"firstName", "lastName"}, multiValueMapper = ConcatMultiValueMapper.class)
    private String fullName;

    private String doesNotMap;

}
