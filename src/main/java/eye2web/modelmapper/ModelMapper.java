package eye2web.modelmapper;

import eye2web.modelmapper.annotations.MapValue;
import eye2web.modelmapper.annotations.MapValues;
import eye2web.modelmapper.exception.NoArgsConstructorException;
import eye2web.modelmapper.model.FromField;
import eye2web.modelmapper.model.Options;
import eye2web.modelmapper.value.map.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ModelMapper implements ModelMapperI {

    private final Options options;

    public ModelMapper() {
        options = Options.builder().build();
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

    @Override
    public void dispose() {
        ValueMapperContainer.getInstance().dispose();
        MultiValueMapperContainer.getInstance().dispose();
    }

    private void mapUsingFieldInjection(final Object source, final Object destinationObj) throws Exception {

        //todo remove Testing only
        mapUsingGetterSetter(source, destinationObj);

        final List<Map.Entry<String, Object>> sourceFieldValues = getFieldValues(source);

        for (final Field field : destinationObj.getClass().getDeclaredFields()) {

            if (Objects.nonNull(field.getAnnotation(MapValues.class))) {
                tryMapMultiValueField(field, sourceFieldValues, destinationObj);
            } else {
                tryMapSingleValueField(field, sourceFieldValues, destinationObj);
            }
        }
    }

    private void mapUsingGetterSetter(final Object source, final Object destinationObj) {

        final var METHOD_GET_PREFIX = "get";
        final var METHOD_SET_PREFIX = "set";
        final var METHOD_IS_PREFIX = "is";

        // Map fields to corresponding getter methods
        final var getters = Arrays.stream(source.getClass().getDeclaredFields())
                .map(field -> {
                    final var methodOpt =
                            Arrays.stream(source.getClass().getMethods())
                                    .filter(method -> method.getName().startsWith(METHOD_GET_PREFIX) ||
                                            method.getName().startsWith(METHOD_IS_PREFIX))
                                    .filter(
                                            method -> method.getName()
                                                    .endsWith(StringUtils.capitalize(field.getName())) ||
                                                    method.getName().equals(field.getName())
                                    ).findAny();

                    return methodOpt.map(method -> Pair.of(field, method));
                }).filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        // Map fields to corresponding setter methods
        final var setters = Arrays.stream(destinationObj.getClass().getDeclaredFields())
                .map(field -> {
                    final var methodOpt = Arrays.stream(destinationObj.getClass().getMethods())
                            .filter(method -> method.getName().startsWith(METHOD_SET_PREFIX))
                            .filter(
                                    method -> method.getName()
                                            .endsWith(StringUtils.capitalize(field.getName())) ||
                                            (field.getName().startsWith(METHOD_IS_PREFIX) &&
                                                    method.getName()
                                                            .endsWith(field.getName().substring(2))
                                            )
                            ).findAny();

                    return methodOpt.map(method -> Pair.of(field, method));
                }).filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        mapGettersToSetters(source, getters, destinationObj, setters);
    }

    private void mapGettersToSetters(final Object source,
                                     final List<Pair<Field, Method>> getters,
                                     final Object destination,
                                     final List<Pair<Field, Method>> setters) {


        setters.forEach(setter -> {

            // Multimap many to one
            // todo refactor into smaller parts
            if (setter.getKey().isAnnotationPresent(MapValues.class)) {

                final var mapValuesAnnotation = setter.getKey().getAnnotation(MapValues.class);

                //
                final Set<FromField> fieldValues = getters.stream().filter(getter -> Arrays.stream(mapValuesAnnotation.fieldNames())
                        .anyMatch(setterFieldName -> setterFieldName.equals(getter.getKey().getName())))
                        .map(getter -> {

                            try {
                                final var value = getter.getValue().invoke(source);

                                return FromField.builder()
                                        .fieldValue(value)
                                        .fieldName(getter.getKey().getName())
                                        .build();
                            } catch (Exception ex) {
                                System.out.println(
                                        ex.getMessage()
                                );
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(LinkedHashSet::new));


                try {
                    final var multiValueMapper = getMultiValueMapper(setter.getKey());
                    setter.getValue().invoke(destination, multiValueMapper.mapToValue(fieldValues));

                } catch (Exception ex) {
                    System.out.println(
                            ex.getMessage()
                    );
                }

                return;
            }

            // Map one to one
            // todo refactor into smaller parts
            final var setterFieldName = getFieldName(setter.getKey());

            getters.stream().filter(getter -> getter.getKey().getName().equals(setterFieldName))
                    .findAny()
                    .ifPresent(getter -> {

                        try {
                            final var value = getter.getValue().invoke(source);

                            if (!shouldIgnoreFieldValue(setter.getKey().getAnnotation(MapValue.class), value)) {

                                final var
                                        objectValueMapper = getSingleValueMapper(setter.getKey());

                                final var mapFromField = FromField.builder()
                                        .fieldValue(value)
                                        .fieldName(getter.getKey().getName())
                                        .build();

                                setter.getValue().invoke(destination, objectValueMapper.mapToValue(mapFromField));
                            }
                        } catch (Exception ex) {
                            System.out.println(
                                    ex.getMessage()
                            );
                        }
                    });
        });
    }

    private ValueMapper getSingleValueMapper(final Field field)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        final Class<? extends ValueMapper> valueMapperClass = field.isAnnotationPresent(MapValue.class) ?
                field.getAnnotation(MapValue.class).valueMapper() : DefaultValueMapper.class;

        return ValueMapperContainer.getInstance().getValueMapperInstance(valueMapperClass);
    }

    private MultiValueMapper getMultiValueMapper(final Field field)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        final var valueMapperClass = field.getAnnotation(MapValues.class).multiValueMapper();
        return MultiValueMapperContainer.getInstance().getValueMapperInstance(valueMapperClass);
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

        final var mapFromField = FromField.builder()
                .fieldName(fieldName)
                .fieldValue(fieldValue)
                .build();

        final var objectValueMapper = getSingleValueMapper(field);

        value = objectValueMapper.mapToValue(mapFromField);

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

    private void mapFieldValues(final Object destinationObj, final Field field, final Set<FromField> fromFieldSet)
            throws Exception {

        final var value = field.isAnnotationPresent(MapValues.class) ?
                getMultiValueMapper(field).mapToValue(fromFieldSet) :
                null;

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
