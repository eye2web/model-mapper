package eye2web.modelmapper.strategy;

import eye2web.modelmapper.ModelMapperI;
import eye2web.modelmapper.annotations.MapValue;
import eye2web.modelmapper.annotations.MapValues;
import eye2web.modelmapper.exception.ModelMapperException;
import eye2web.modelmapper.model.FromField;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class FieldStrategy implements Strategy {

    private static Strategy strategy;

    private FieldStrategy() {
    }

    public static Strategy getInstance() {

        if (Objects.isNull(strategy)) {
            strategy = new FieldStrategy();
        }

        return strategy;
    }

    @Override
    public void mapObjects(final Object source, final Object destinationObj, final ModelMapperI modelMapper) {
        final List<Map.Entry<String, Object>> sourceFieldValues = StrategyUtil.getFieldValues(source);

        for (final Field field : destinationObj.getClass().getDeclaredFields()) {

            if (Objects.nonNull(field.getAnnotation(MapValues.class))) {
                tryMapMultiValueField(field, sourceFieldValues, destinationObj);
            } else {
                tryMapSingleValueField(field, sourceFieldValues, destinationObj, modelMapper);
            }
        }
    }

    private void tryMapMultiValueField(final Field field, final List<Map.Entry<String, Object>> sourceFieldValues,
                                       final Object destinationObj) {

        final String[] multiValueFieldNames = StrategyUtil.getFieldNames(field);

        if (Objects.isNull(multiValueFieldNames)) {
            return;
        }

        final Set<FromField> fromFieldsSet = sourceFieldValues.stream()
                .filter(s ->
                        Arrays.stream(multiValueFieldNames).anyMatch(mv -> mv.equals(s.getKey()))
                ).map((sourceField) ->

                        FromField.builder()
                                .fieldName(sourceField.getKey())
                                .fieldValue(sourceField.getValue())
                                .build()
                ).collect(Collectors.toCollection(LinkedHashSet::new));

        for (final var t : fromFieldsSet) {
            System.out.println(t.getFieldValue());
        }

        if (fromFieldsSet.size() == multiValueFieldNames.length) {
            mapFieldValues(destinationObj, field, fromFieldsSet);
        }
    }


    private void tryMapSingleValueField(final Field field, final List<Map.Entry<String, Object>> sourceFieldValues,
                                        final Object destinationObj,
                                        ModelMapperI modelMapper) {

        final String fieldName = StrategyUtil.getFieldName(field);

        final Optional<Map.Entry<String, Object>> vOpt = sourceFieldValues.stream()
                .filter(s -> s.getKey().equals(fieldName))
                .findFirst();

        if (vOpt.isPresent()) {
            mapFieldValue(destinationObj, field, fieldName, vOpt.get().getValue(), modelMapper);
        }
    }


    private void mapFieldValue(final Object destinationObj, final Field field, final String fieldName,
                               final Object fieldValue,
                               final ModelMapperI modelMapper) {

        final Object value;

        final MapValue mapValue = field.getAnnotation(MapValue.class);

        if (StrategyUtil.shouldIgnoreFieldValue(mapValue, fieldValue)) {
            return;
        }

        try {
            final var objectValueMapper = StrategyUtil.getSingleValueMapper(field);

            if (Objects.isNull(mapValue)) {
                value = fieldValue;
            } else if (mapValue.iterate()) {
                value = StrategyUtil.iterateElements(fieldValue, fieldName, objectValueMapper, modelMapper);

            } else {
                final var mapFromField = FromField.builder()
                        .fieldName(fieldName)
                        .fieldValue(fieldValue)
                        .build();

                value = objectValueMapper.mapToValue(mapFromField, modelMapper);
            }

            boolean isPrivate = StrategyUtil.setFieldPublic(field, destinationObj);

            field.set(destinationObj, value);

            if (isPrivate) {
                field.setAccessible(false);
            }
        } catch (final Exception ex) {
            throw new ModelMapperException(ex);
        }
    }

    private void mapFieldValues(final Object destinationObj, final Field field, final Set<FromField> fromFieldSet) {

        try {
            final var value = field.isAnnotationPresent(MapValues.class) ?
                    StrategyUtil.getMultiValueMapper(field).mapToValue(fromFieldSet) :
                    null;

            boolean isPrivate = StrategyUtil.setFieldPublic(field, destinationObj);

            field.set(destinationObj, value);

            if (isPrivate) {
                field.setAccessible(false);
            }
        } catch (final Exception ex) {
            throw new ModelMapperException(ex);
        }
    }
}
