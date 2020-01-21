package eye2web.modelmapper.value.map;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ValueMapperContainer {

    private static ValueMapperContainer instance;

    private final Map<Class<? extends ValueMapper>, ValueMapper> valueMappers;

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

        return valueMappers.containsKey(clazz) ? valueMappers.get(clazz) : newInstance(clazz);
    }

    public void dispose() {
        valueMappers.clear();
    }

    private ValueMapper newInstance(final Class<? extends ValueMapper> clazz)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        final var newInstance = clazz.getConstructor().newInstance();
        valueMappers.put(clazz, newInstance);
        return newInstance;
    }
}
