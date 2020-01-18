package eye2web.modelmapper;

import eye2web.modelmapper.annotations.MapValue;
import eye2web.modelmapper.annotations.MapValues;
import eye2web.modelmapper.exception.NoArgsConstructorException;
import eye2web.modelmapper.model.MapFromField;
import eye2web.modelmapper.value.map.DefaultValueMapper;
import eye2web.modelmapper.value.map.MultiValueMapper;
import eye2web.modelmapper.value.map.ValueMapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

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

            if (Objects.nonNull(field.getAnnotation(MapValues.class))) {
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

        final Set<MapFromField<?>> mapFromFieldsSet = sourceFieldValues.stream()
                .filter(s ->
                        Arrays.stream(multiValueFieldNames).anyMatch(mv -> mv.equals(s.getKey()))
                ).map((sourceField) ->

                        MapFromField.builder()
                                .fieldName(sourceField.getKey())
                                .fieldValue(sourceField.getValue())
                                .build()
                ).collect(Collectors.toCollection(LinkedHashSet::new));

        for (final var t : mapFromFieldsSet) {
            System.out.println(t.getFieldValue());
        }

        if (mapFromFieldsSet.size() == multiValueFieldNames.length) {
            mapFieldValues(destinationObj, field, mapFromFieldsSet);
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
        final MapValue mapValue = field.getAnnotation(MapValue.class);
        return Objects.isNull(mapValue) ? field.getName() : mapValue.fieldName();
    }

    private String[] getFieldNames(final Field field) {
        final MapValues mapValues = field.getAnnotation(MapValues.class);
        return Objects.nonNull(mapValues) ? mapValues.fieldNames() : null;
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

        final MapValue mapValue = field.getAnnotation(MapValue.class);

        if (shouldIgnoreFieldValue(mapValue, fieldValue)) {
            return;
        }

        final var mapFromField = MapFromField.builder()
                .fieldName(fieldName)
                .fieldValue(fieldValue)
                .build();

        if (Objects.nonNull(mapValue) &&
                !mapValue.valueMapper().equals(DefaultValueMapper.class)) {

            // TODO make singleton? // DI Container
            final ValueMapper
                    objectValueMapper =
                    (ValueMapper) mapValue.valueMapper().getConstructor().newInstance();

            value = objectValueMapper.mapToValue(mapFromField);
        } else {
            value = defaultHandlerMapper.mapToValue(mapFromField);
        }

        boolean isPrivate = setFieldPublic(field, destinationObj);

        field.set(destinationObj, value);

        if (isPrivate) {
            field.setAccessible(false);
        }
    }

    private boolean shouldIgnoreFieldValue(final MapValue mapValue, final Object fieldValue) {
        return (Objects.nonNull(mapValue) &&
                Arrays.asList(mapValue.properties()).contains(FieldProperties.IGNORE_NULL_VALUES) &&
                Objects.isNull(fieldValue));
    }

    private void mapFieldValues(final Object destinationObj, final Field field, final Set<MapFromField<?>> mapFromFieldSet)
            throws Exception {

        final Object value;

        final MapValues mapValues = field.getAnnotation(MapValues.class);

        if (Objects.nonNull(mapValues)) {

            // TODO make singleton?
            final MultiValueMapper
                    objectValueMapper =
                    (MultiValueMapper) mapValues.multiValueMapper().getConstructor().newInstance();

            value = objectValueMapper.mapToValue(mapFromFieldSet);
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
