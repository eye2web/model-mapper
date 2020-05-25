package eye2web.modelmapper;

import eye2web.modelmapper.exception.NoArgsConstructorException;
import eye2web.modelmapper.model.Options;
import eye2web.modelmapper.strategy.FieldStrategy;
import eye2web.modelmapper.strategy.MethodStrategy;
import eye2web.modelmapper.strategy.Strategy;
import eye2web.modelmapper.value.map.MultiValueMapper;
import eye2web.modelmapper.value.map.MultiValueMapperContainer;
import eye2web.modelmapper.value.map.ValueMapper;
import eye2web.modelmapper.value.map.ValueMapperContainer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@AllArgsConstructor
public class ModelMapper implements ModelMapperI {

    @NonNull
    @Getter
    @Setter
    private Options options;

    public ModelMapper() {
        options = Options.builder().build();
    }

    public ModelMapper(final List<? extends ValueMapper> valueMappers,
                       final List<? extends MultiValueMapper> multiValueMappers) {
        initExternalMappers(valueMappers, multiValueMappers);
        options = Options.builder().build();
    }

    public ModelMapper(final List<? extends ValueMapper> valueMappers,
                       final List<? extends MultiValueMapper> multiValueMappers,
                       final Options options) {
        initExternalMappers(valueMappers, multiValueMappers);
        this.options = options;
    }

    @Override
    public <D> D map(final Object source, final Class<D> destinationType)
            throws Exception {

        final D destinationObj = destinationType.cast(createEmptyInstanceOfNoArgsClass(destinationType));

        getCurrentStrategy().mapObjects(source, destinationObj, this);

        return destinationObj;
    }

    @Override
    public <D> void map(final Object source, final D destinationObj)
            throws Exception {
        getCurrentStrategy().mapObjects(source, destinationObj, this);
    }

    @Override
    public void dispose() {
        ValueMapperContainer.getInstance().dispose();
        MultiValueMapperContainer.getInstance().dispose();
    }

    private void initExternalMappers(final List<? extends ValueMapper> valueMappers,
                                     final List<? extends MultiValueMapper> multiValueMappers) {

        ValueMapperContainer.getInstance().setExtManagedValueMappers(valueMappers);
        MultiValueMapperContainer.getInstance().setExtManagedMultiValueMappers(multiValueMappers);
    }

    private Strategy getCurrentStrategy() {

        final Strategy strategy;

        switch (options.getInjectionStrategy()) {
            case Field:
                strategy = FieldStrategy.getInstance();
                break;
            case Method:
            default:
                strategy = MethodStrategy.getInstance();
        }

        return strategy;
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
}
