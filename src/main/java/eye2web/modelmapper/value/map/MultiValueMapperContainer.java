package eye2web.modelmapper.value.map;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MultiValueMapperContainer {

    private static MultiValueMapperContainer instance;

    private final Map<Class<? extends MultiValueMapper>, MultiValueMapper> valueMappers;

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

        return valueMappers.containsKey(clazz) ? valueMappers.get(clazz) : newInstance(clazz);
    }

    public void dispose() {
        valueMappers.clear();
    }

    private MultiValueMapper newInstance(final Class<? extends MultiValueMapper> clazz)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        final var newInstance = clazz.getConstructor().newInstance();
        valueMappers.put(clazz, newInstance);
        return newInstance;
    }

}
