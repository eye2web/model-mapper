package eye2web.modelmapper.model;

import eye2web.modelmapper.annotations.MapValue;
import eye2web.modelmapper.annotations.MapValues;
import eye2web.modelmapper.mapper.AgeValueMapper;
import eye2web.modelmapper.mapper.ConcatMultiValueMapper;
import eye2web.modelmapper.mapper.FirstNameValueMapper;
import eye2web.modelmapper.mapper.UpperCaseValueMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    private boolean isActive;

    private boolean valid;

    @MapValue(fieldName = "birthday", valueMapper = AgeValueMapper.class)
    private int age;
    
    @MapValue(fieldName = "names", valueMapper = UpperCaseValueMapper.class, iterate = true)
    private List<String> upperCaseNames;
}
