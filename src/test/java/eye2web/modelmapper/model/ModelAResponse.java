package eye2web.modelmapper.model;

import eye2web.modelmapper.FieldProperties;
import eye2web.modelmapper.annotations.MapValue;
import eye2web.modelmapper.annotations.MapValues;
import eye2web.modelmapper.handler.AgeValueMapper;
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

    @MapValue(fieldName = "firstName",
            valueMapper = FirstNameValueMapper.class,
            properties = {FieldProperties.IGNORE_NULL_VALUES})
    private String fName;

    @MapValue(fieldName = "lastName")
    private String lName;

    @MapValues(fieldNames = {"firstName", "lastName"},
            multiValueMapper = ConcatMultiValueMapper.class)
    private String fullName;

    private String doesNotMap;

    private SimpleNestedModel simpleNestedModel;

    @MapValue(fieldName = "birthday", valueMapper = AgeValueMapper.class)
    private int age;
}
