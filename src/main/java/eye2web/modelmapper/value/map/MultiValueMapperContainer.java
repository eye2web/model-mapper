package eye2web.modelmapper.value.map;

import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class MultiValueMapperContainer {

    private static MultiValueMapperContainer instance;

    private final Map<Class<? extends MultiValueMapper>, MultiValueMapper> valueMappers;

    @Setter
    private List<? extends MultiValueMapper> extManagedMultiValueMappers;

    private MultiValueMapperContainer() {
        valueMappers = new HashMap<>();
    }

    public static MultiValueMapperContainer getInstance() {
        if (Objects.isNull(instance)) {
            instance = new MultiValueMapperContainer();
        }
        return instance;
    }

    public MultiValueMapper getValueMapperInstance(final Class<? extends MultiValueMapper> clazz)
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

    private Optional<? extends MultiValueMapper> getExternalManagedValueMapper(final Class<? extends MultiValueMapper> clazz) {
        if (Objects.nonNull(extManagedMultiValueMappers)) {
            return extManagedMultiValueMappers.stream()
                    .filter(extValMapper -> extValMapper.getClass().equals(clazz))
                    .findFirst();
        }
        return Optional.empty();
    }

    private MultiValueMapper newInstance(final Class<? extends MultiValueMapper> clazz)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        final var newInstance = clazz.getConstructor().newInstance();
        valueMappers.put(clazz, newInstance);
        return newInstance;
    }

}
