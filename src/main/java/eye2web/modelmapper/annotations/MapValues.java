package eye2web.modelmapper.annotations;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface MapValues {

    String[] fieldNames();

    Class<?> multiValueMapper();
}
