package eye2web.modelmapper.annotations;

import eye2web.modelmapper.value.map.MultiValueMapper;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface MapValues {

    String[] fieldNames();

    Class<? extends MultiValueMapper> multiValueMapper();
}
