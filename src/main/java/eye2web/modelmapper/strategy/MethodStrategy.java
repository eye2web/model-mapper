package eye2web.modelmapper.strategy;

import eye2web.modelmapper.annotations.MapValue;
import eye2web.modelmapper.annotations.MapValues;
import eye2web.modelmapper.model.FromField;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class MethodStrategy implements Strategy {

    private static Strategy strategy;

    private MethodStrategy() {
    }

    public static Strategy getInstance() {

        if (Objects.isNull(strategy)) {
            strategy = new MethodStrategy();
        }

        return strategy;
    }

    @Override
    public void mapObjects(Object source, Object destinationObj) {

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
                    final var multiValueMapper = StrategyUtil.getMultiValueMapper(setter.getKey());
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
            final var setterFieldName = StrategyUtil.getFieldName(setter.getKey());

            getters.stream().filter(getter -> getter.getKey().getName().equals(setterFieldName))
                    .findAny()
                    .ifPresent(getter -> {

                        try {
                            final var value = getter.getValue().invoke(source);

                            if (!StrategyUtil.shouldIgnoreFieldValue(setter.getKey().getAnnotation(MapValue.class), value)) {

                                final var
                                        objectValueMapper = StrategyUtil.getSingleValueMapper(setter.getKey());

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
}
