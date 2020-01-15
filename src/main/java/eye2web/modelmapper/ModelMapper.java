package eye2web.modelmapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import eye2web.modelmapper.annotations.MapValueFromField;
import eye2web.modelmapper.annotations.MapValuesFromFields;
import eye2web.modelmapper.exception.NoArgsConstructorException;
import eye2web.modelmapper.value.map.DefaultValueMapper;
import eye2web.modelmapper.value.map.MultiValueMapper;
import eye2web.modelmapper.value.map.ValueMapper;

public class ModelMapper implements ModelMapperI {

    private final DefaultValueMapper defaultHandlerMapper;

    public ModelMapper() {
        defaultHandlerMapper = new DefaultValueMapper();
    }

    @Override
    public <D> D map(final Object source, final Class<D> destinationType)
            throws Exception {

        final D destinationObj = destinationType.cast(createEmptyInstanceOfNoArgsClass(destinationType));

        mapUsingFieldInjection(source, destinationObj);

        return destinationObj;
    }

    @Override
    public <D> void map(final Object source, final D destinationObj)
            throws Exception {
        mapUsingFieldInjection(source, destinationObj);
    }

    private void mapUsingFieldInjection(final Object source, final Object destinationObj) throws Exception {
        final List<Map.Entry<String, Object>> sourceFieldValues = getFieldValues(source);

        for (final Field field : destinationObj.getClass().getDeclaredFields()) {

            if (Objects.nonNull(field.getAnnotation(MapValuesFromFields.class))) {
                tryMapMultiValueField(field, sourceFieldValues, destinationObj);
            } else {
                tryMapSingleValueField(field, sourceFieldValues, destinationObj);
            }
        }
    }

    private Object createEmptyInstanceOfNoArgsClass(final Class<?> classType)
            throws NoArgsConstructorException, InstantiationException, IllegalAccessException, InvocationTargetException {
        try {
            return classType.getConstructor()
                    .newInstance();
        } catch (NoSuchMethodException ex) {
            throw new NoArgsConstructorException(classType.getName());
        }
    }

    private void tryMapMultiValueField(final Field field, final List<Map.Entry<String, Object>> sourceFieldValues,
                                       final Object destinationObj) throws Exception {

        final String[] multiValueFieldNames = getFieldNames(field);

        if (Objects.isNull(multiValueFieldNames)) {
            return;
        }

        final Object[] objectValues = sourceFieldValues.stream()
                .filter(s ->
                        Arrays.stream(multiValueFieldNames).anyMatch(mv -> mv.equals(s.getKey()))
                ).map(Map.Entry::getValue).toArray();

        if (objectValues.length == multiValueFieldNames.length) {
            mapFieldValues(destinationObj, field, multiValueFieldNames, objectValues);
        }
    }


    private void tryMapSingleValueField(final Field field, final List<Map.Entry<String, Object>> sourceFieldValues,
                                        final Object destinationObj) throws Exception {

        final String fieldName = getFieldName(field);

        final Optional<Map.Entry<String, Object>> vOpt = sourceFieldValues.stream()
                .filter(s -> s.getKey().equals(fieldName))
                .findFirst();

        if (vOpt.isPresent()) {
            mapFieldValue(destinationObj, field, fieldName, vOpt.get().getValue());
        }
    }

    private String getFieldName(final Field field) {
        final MapValueFromField mapValueFromField = field.getAnnotation(MapValueFromField.class);
        return Objects.isNull(mapValueFromField) ? field.getName() : mapValueFromField.fieldName();
    }

    private String[] getFieldNames(final Field field) {
        final MapValuesFromFields mapValuesFromFields = field.getAnnotation(MapValuesFromFields.class);
        return Objects.nonNull(mapValuesFromFields) ? mapValuesFromFields.fieldNames() : null;
    }


    private List<Map.Entry<String, Object>> getFieldValues(final Object obj) throws IllegalAccessException {

        final List<Map.Entry<String, Object>> fieldValues = new ArrayList<>();

        for (Field field : obj.getClass().getDeclaredFields()) {

            boolean isPrivate = setFieldPublic(field, obj);

            final String name = getFieldName(field);
            final Object value = field.get(obj);

            fieldValues.add(new AbstractMap.SimpleEntry<>(name, value));
            if (isPrivate) {
                field.setAccessible(false);
            }

        }

        return fieldValues;
    }

    private void mapFieldValue(final Object destinationObj, final Field field, final String fieldName,
                               final Object fieldValue)
            throws Exception {

        final Object value;

        final MapValueFromField mapValueFromField = field.getAnnotation(MapValueFromField.class);

        if (Objects.nonNull(mapValueFromField) &&
                shouldIgnoreFieldValue(mapValueFromField.properties(), fieldValue)) {
            return;
        }

        if (Objects.nonNull(mapValueFromField) &&
                !mapValueFromField.valueMapper().equals(DefaultValueMapper.class)) {

            // TODO make singleton? // DI Container
            final ValueMapper
                    objectValueMapper =
                    (ValueMapper) mapValueFromField.valueMapper().getConstructor().newInstance();

            value = objectValueMapper.mapToValue(fieldName, fieldValue);
        } else {
            value = defaultHandlerMapper.mapToValue(fieldName, fieldValue);
        }

        boolean isPrivate = setFieldPublic(field, destinationObj);

        field.set(destinationObj, value);

        if (isPrivate) {
            field.setAccessible(false);
        }
    }

    private boolean shouldIgnoreFieldValue(final FieldProperties[] fieldProperties, final Object fieldValue) {
        return (
                Arrays.asList(fieldProperties).contains(FieldProperties.IGNORE_NULL_VALUES) &&
                        Objects.isNull(fieldValue));
    }

    private void mapFieldValues(final Object destinationObj, final Field field, final String[] fieldNames,
                                final Object[] fieldValues)
            throws Exception {

        final Object value;

        final MapValuesFromFields mapValuesFromFields = field.getAnnotation(MapValuesFromFields.class);

        if (Objects.nonNull(mapValuesFromFields)) {

            // TODO make singleton?
            final MultiValueMapper
                    objectValueMapper =
                    (MultiValueMapper) mapValuesFromFields.multiValueMapper().getConstructor().newInstance();

            value = objectValueMapper.mapToValue(fieldNames, fieldValues);
        } else {
            value = null;
        }

        boolean isPrivate = setFieldPublic(field, destinationObj);

        field.set(destinationObj, value);

        if (isPrivate) {
            field.setAccessible(false);
        }
    }

    private boolean setFieldPublic(final Field field, final Object object) {
        final boolean didSetPublic;

        if (!field.canAccess(object)) {
            field.setAccessible(true);
            didSetPublic = true;
        } else {
            didSetPublic = false;
        }

        return didSetPublic;
    }
}
