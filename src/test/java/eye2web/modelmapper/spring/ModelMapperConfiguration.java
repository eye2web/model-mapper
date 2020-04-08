package eye2web.modelmapper.spring;

import eye2web.modelmapper.ModelMapper;
import eye2web.modelmapper.value.map.MultiValueMapper;
import eye2web.modelmapper.value.map.ValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ModelMapperConfiguration {

    @Autowired(required = false)
    private List<? extends ValueMapper> valueMappers;
    @Autowired(required = false)
    private List<? extends MultiValueMapper> multiValueMappers;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper(valueMappers, multiValueMappers);
    }

}
