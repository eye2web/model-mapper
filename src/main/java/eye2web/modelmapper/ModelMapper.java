package eye2web.modelmapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import eye2web.modelmapper.annotations.MapFromField;
import eye2web.modelmapper.annotations.MapFromFields;
import eye2web.modelmapper.value.map.DefaultValueMapper;
import eye2web.modelmapper.value.map.MultiValueMapper;
import eye2web.modelmapper.value.map.ValueMapper;

public class ModelMapper {

    private final DefaultValueMapper defaultHandlerMapper;

    public ModelMapper() {
        defaultHandlerMapper = new DefaultValueMapper();
    }


    public <D> D map(final Object source, final Class<D> destinationType)
            throws Exception {

        final Constructor<?> ctor = destinationType.getConstructor();

        final List<Map.Entry<String, Object>> sourceFieldValues = getFieldValues(source);

        final D destinationObj = (D) ctor.newInstance();

        for (final Field field : destinationObj.getClass().getDeclaredFields()) {

            if (field.getAnnotation(MapFromFields.class) != null) {
                tryMapMultiValueField(field, sourceFieldValues, destinationObj);
            } else {
                tryMapSingleValueField(field, sourceFieldValues, destinationObj);
            }
        }

        return destinationObj;
    }

    public <D> void map(final Object source, final Class<D> destinationType, final D destinationObj)
            throws Exception {

        final List<Map.Entry<String, Object>> sourceFieldValues = getFieldValues(source);

        for (final Field field : destinationObj.getClass().getDeclaredFields()) {

            if (field.getAnnotation(MapFromFields.class) != null) {
                tryMapMultiValueField(field, sourceFieldValues, destinationObj);
            } else {
                tryMapSingleValueField(field, sourceFieldValues, destinationObj);
            }
        }
    }

    private void tryMapMultiValueField(final Field field, final List<Map.Entry<String, Object>> sourceFieldValues,
                                       final Object destinationObj) throws Exception {

        final String[] multiValueFieldNames = getFieldNames(field);

        if (multiValueFieldNames == null) {
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
        final MapFromField mapFromField = field.getAnnotation(MapFromField.class);
        return ((mapFromField == null) ? field.getName() : mapFromField.fieldName());
    }

    private String[] getFieldNames(final Field field) {
        final MapFromFields mapFromFields = field.getAnnotation(MapFromFields.class);
        return ((mapFromFields == null) ? null : mapFromFields.fieldNames());
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

        final MapFromField mapFromField = field.getAnnotation(MapFromField.class);

        if (mapFromField != null &&
                shouldIgnoreFieldValue(mapFromField.properties(), fieldValue)) {
            return;
        }

        if (mapFromField != null &&
                !mapFromField.valueMapper().equals(DefaultValueMapper.class)) {

            // TODO make singleton?
            final ValueMapper
                    objectValueMapper =
                    (ValueMapper) mapFromField.valueMapper().getConstructor().newInstance();

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
                        fieldValue == null);
    }

    private void mapFieldValues(final Object destinationObj, final Field field, final String[] fieldNames,
                                final Object[] fieldValues)
            throws Exception {

        final Object value;

        final MapFromFields mapFromFields = field.getAnnotation(MapFromFields.class);

        if (mapFromFields != null) {

            // TODO make singleton?
            final MultiValueMapper
                    objectValueMapper =
                    (MultiValueMapper) mapFromFields.multiValueMapper().getConstructor().newInstance();

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
