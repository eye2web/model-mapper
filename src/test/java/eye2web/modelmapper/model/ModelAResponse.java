package eye2web.modelmapper.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import eye2web.modelmapper.FieldProperties;
import eye2web.modelmapper.annotations.MapFromField;
import eye2web.modelmapper.annotations.MapFromFields;
import eye2web.modelmapper.handler.ConcatMultiValueMapper;
import eye2web.modelmapper.handler.FirstNameValueMapper;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelAResponse {


    private int id;

    private String firstName;

    @MapFromField(fieldName = "firstName",
        valueMapper = FirstNameValueMapper.class,
        properties = {FieldProperties.IGNORE_NULL_VALUES})
    private String fName;

    @MapFromField(fieldName = "lastName")
    private String lName;

    @MapFromFields(fieldNames = {"firstName", "lastName"}, multiValueMapper = ConcatMultiValueMapper.class)
    private String fullName;

    private String doesNotMap;

}
