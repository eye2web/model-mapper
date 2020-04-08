package eye2web.modelmapper;

import eye2web.modelmapper.model.ModelA;
import eye2web.modelmapper.model.ModelAResponse;
import eye2web.modelmapper.model.SimpleNestedModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.Period;

@RunWith(MockitoJUnitRunner.class)
public class ModelMapperTest {

    private ModelMapper modelMapper;

    @Before
    public void setup() {
        modelMapper = new ModelMapper();
    }

    @Test
    public void shouldFullyMapModelAResponseToRequest() throws Exception {

        final var age = Period.between(LocalDate.of(1990, 3, 14), LocalDate.now()).getYears();
        
        final ModelA modelA = ModelA.builder()
                .id(1)
                .isActive(true)
                .valid(true)
                .firstName("Remco")
                .lastName("van der Heijden")
                .birthday(LocalDate.of(1990, 3, 14))
                .simpleNestedModel(SimpleNestedModel.builder()
                        .nestedModelName("nested name")
                        .build())
                .build();

        final ModelAResponse modelAResponse = modelMapper.map(modelA, ModelAResponse.class);

        Assert.assertEquals(1, modelAResponse.getId());
        Assert.assertTrue(modelAResponse.isActive());
        Assert.assertTrue(modelAResponse.isValid());
        Assert.assertEquals("Remco", modelAResponse.getFirstName());
        Assert.assertEquals("van der Heijden", modelAResponse.getLName());
        Assert.assertEquals("Remco van der Heijden", modelAResponse.getFullName());
        Assert.assertEquals("nested name", modelAResponse.getSimpleNestedModel().getNestedModelName());
        Assert.assertEquals(age, modelAResponse.getAge());
    }

    @Test
    public void shouldNotMapEmptyFirstName() throws Exception {

        final ModelA modelA = ModelA.builder()
                .lastName("van der Heijden")
                .birthday(LocalDate.of(1990, 3, 14))
                .build();

        final ModelAResponse modelAResponse = modelMapper.map(modelA, ModelAResponse.class);

        Assert.assertNull(modelAResponse.getFirstName());
        Assert.assertEquals("van der Heijden", modelAResponse.getLName());
    }

    @Test
    public void shouldMapToExistingTarget() throws Exception {
        final ModelA modelA = ModelA.builder()
                .id(1)
                .firstName("Remco")
                .lastName("van der Heijden")
                .birthday(LocalDate.of(1990, 3, 14))
                .build();

        final ModelAResponse modelAResponse = ModelAResponse
                .builder()
                .doesNotMap("burp")
                .build();

        modelMapper.map(modelA, modelAResponse);

        Assert.assertEquals(1, modelAResponse.getId());
        Assert.assertEquals("Remco", modelAResponse.getFirstName());
        Assert.assertEquals("van der Heijden", modelAResponse.getLName());
        Assert.assertEquals("Remco van der Heijden", modelAResponse.getFullName());
        Assert.assertEquals("burp", modelAResponse.getDoesNotMap());
    }

    @Test
    public void shouldMapToExistingTargetIgnoringNulls() throws Exception {
        final ModelA modelA = ModelA.builder()
                .id(1)
                .firstName(null)
                .lastName("van der Heijden")
                .birthday(LocalDate.of(1990, 3, 14))
                .build();

        final ModelAResponse modelAResponse = ModelAResponse
                .builder()
                .doesNotMap("burp")
                .fName("Remco")
                .build();

        modelMapper.map(modelA, modelAResponse);

        Assert.assertEquals(1, modelAResponse.getId());
        Assert.assertNull(modelAResponse.getFirstName());
        Assert.assertEquals("van der Heijden", modelAResponse.getLName());
        Assert.assertEquals("Remco", modelAResponse.getFName());
        Assert.assertEquals("burp", modelAResponse.getDoesNotMap());
    }


}
