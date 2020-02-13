package eye2web.modelmapper.annotations;

import eye2web.modelmapper.model.FieldProperties;
import eye2web.modelmapper.value.map.DefaultValueMapper;
import eye2web.modelmapper.value.map.ValueMapper;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface MapValue {

    String fieldName();

    Class<? extends ValueMapper> valueMapper() default DefaultValueMapper.class;

    FieldProperties[] properties() default {};
}
