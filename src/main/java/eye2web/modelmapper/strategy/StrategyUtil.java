package eye2web.modelmapper.strategy;

import eye2web.modelmapper.ModelMapperI;
import eye2web.modelmapper.annotations.MapValue;
import eye2web.modelmapper.annotations.MapValues;
import eye2web.modelmapper.exception.ModelMapperException;
import eye2web.modelmapper.model.FieldProperties;
import eye2web.modelmapper.model.FromField;
import eye2web.modelmapper.value.map.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class StrategyUtil {

    public static String getFieldName(final Field field) {
        final MapValue mapValue = field.getAnnotation(MapValue.class);
        return Objects.isNull(mapValue) ? field.getName() : mapValue.fieldName();
    }

    public static String[] getFieldNames(final Field field) {
        final MapValues mapValues = field.getAnnotation(MapValues.class);
        return Objects.nonNull(mapValues) ? mapValues.fieldNames() : null;
    }

    public static List<Map.Entry<String, Object>> getFieldValues(final Object obj) {

        final List<Map.Entry<String, Object>> fieldValues = new ArrayList<>();

        for (Field field : obj.getClass().getDeclaredFields()) {

            boolean isPrivate = setFieldPublic(field, obj);

            final String name = getFieldName(field);

            try {
                final Object value = field.get(obj);
                fieldValues.add(new AbstractMap.SimpleEntry<>(name, value));
            } catch (final IllegalAccessException ex) {
                throw new ModelMapperException(ex);
            }
            
            if (isPrivate) {
                field.setAccessible(false);
            }

        }

        return fieldValues;
    }

    public static boolean shouldIgnoreFieldValue(final MapValue mapValue, final Object fieldValue) {
        return (Objects.nonNull(mapValue) &&
                Arrays.asList(mapValue.properties()).contains(FieldProperties.IGNORE_NULL_VALUES) &&
                Objects.isNull(fieldValue));
    }

    public static ValueMapper getSingleValueMapper(final Field field)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        final Class<? extends ValueMapper> valueMapperClass = field.isAnnotationPresent(MapValue.class) ?
                field.getAnnotation(MapValue.class).valueMapper() : DefaultValueMapper.class;

        return ValueMapperContainer.getInstance().getValueMapperInstance(valueMapperClass);
    }

    public static MultiValueMapper getMultiValueMapper(final Field field)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        final var valueMapperClass = field.getAnnotation(MapValues.class).multiValueMapper();
        return MultiValueMapperContainer.getInstance().getValueMapperInstance(valueMapperClass);
    }

    public static boolean setFieldPublic(final Field field, final Object object) {
        final boolean didSetPublic;

        if (!field.canAccess(object)) {
            field.setAccessible(true);
            didSetPublic = true;
        } else {
            didSetPublic = false;
        }

        return didSetPublic;
    }

    public static List<Object> iterateElements(final Object value,
                                               final String fieldName,
                                               final ValueMapper valueMapper,
                                               final ModelMapperI modelMapper) {

        final var results = new ArrayList<>();

        for (Object val : (Iterable<Object>) value) {

            final var mapFromField = FromField.builder()
                    .fieldValue(val)
                    .fieldName(fieldName)
                    .isIterableItem(true)
                    .build();

            results.add(valueMapper.mapToValue(mapFromField, modelMapper));
        }

        return results;
    }

}
