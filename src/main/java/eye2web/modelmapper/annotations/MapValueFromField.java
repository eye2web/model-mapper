package eye2web.modelmapper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import eye2web.modelmapper.FieldProperties;
import eye2web.modelmapper.value.map.DefaultValueMapper;

@Target(ElementType.FIELD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface MapValueFromField {

    String fieldName();

    Class<?> valueMapper() default DefaultValueMapper.class;

    FieldProperties[] properties() default {};
}
