package eye2web.modelmapper.value.map;

import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ValueMapperContainer {

    private static ValueMapperContainer instance;

    private final Map<Class<? extends ValueMapper>, ValueMapper> valueMappers;

    @Setter
    private List<? extends ValueMapper> extManagedValueMappers;

    private ValueMapperContainer() {
        valueMappers = new HashMap<>();
    }

    public static ValueMapperContainer getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ValueMapperContainer();
        }
        return instance;
    }

    public ValueMapper getValueMapperInstance(final Class<? extends ValueMapper> clazz)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        final var externalMapperOpt = getExternalManagedValueMapper(clazz);
        if (externalMapperOpt.isPresent()) {
            return externalMapperOpt.get();
        }

        return valueMappers.containsKey(clazz) ? valueMappers.get(clazz) : newInstance(clazz);
    }

    public void dispose() {
        valueMappers.clear();
    }

    private Optional<? extends ValueMapper> getExternalManagedValueMapper(final Class<? extends ValueMapper> clazz) {
        if (Objects.nonNull(extManagedValueMappers)) {
            return extManagedValueMappers.stream()
                    .filter(extValMapper -> extValMapper.getClass().equals(clazz))
                    .findFirst();
        }
        return Optional.empty();
    }

    private ValueMapper newInstance(final Class<? extends ValueMapper> clazz)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        final var newInstance = clazz.getConstructor().newInstance();
        valueMappers.put(clazz, newInstance);
        return newInstance;
    }
}
