package eye2web.modelmapper;

import eye2web.modelmapper.model.ModelARequest;
import eye2web.modelmapper.model.ModelAResponse;
import eye2web.modelmapper.model.SimpleNestedModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ModelMapperTest {

    @InjectMocks
    private ModelMapper modelMapper;

    @Test
    public void shouldFullyMapModelAResponseToRequest() throws Exception {

        final ModelARequest modelARequest = ModelARequest.builder()
                .id(1)
                .firstName("Remco")
                .lastName("van der Heijden")
                .simpleNestedModel(SimpleNestedModel.builder()
                        .nestedModelName("nested name")
                        .build())
                .build();

        final ModelAResponse modelAResponse = modelMapper.map(modelARequest, ModelAResponse.class);

        Assert.assertEquals(1, modelAResponse.getId());
        Assert.assertEquals("Remco", modelAResponse.getFirstName());
        Assert.assertEquals("van der Heijden", modelAResponse.getLName());
        Assert.assertEquals("Remco van der Heijden", modelAResponse.getFullName());
        Assert.assertEquals("nested name", modelAResponse.getSimpleNestedModel().getNestedModelName());
    }

    @Test
    public void shouldNotMapEmptyFirstName() throws Exception {

        final ModelARequest modelARequest = ModelARequest.builder()
                .lastName("van der Heijden")
                .build();

        final ModelAResponse modelAResponse = modelMapper.map(modelARequest, ModelAResponse.class);

        Assert.assertNull(modelAResponse.getFirstName());
        Assert.assertEquals("van der Heijden", modelAResponse.getLName());
    }

    @Test
    public void shouldMapToExistingTarget() throws Exception {
        final ModelARequest modelARequest = ModelARequest.builder()
                .id(1)
                .firstName("Remco")
                .lastName("van der Heijden")
                .build();

        final ModelAResponse modelAResponse = ModelAResponse
                .builder()
                .doesNotMap("burp")
                .build();

        modelMapper.map(modelARequest, modelAResponse);

        Assert.assertEquals(1, modelAResponse.getId());
        Assert.assertEquals("Remco", modelAResponse.getFirstName());
        Assert.assertEquals("van der Heijden", modelAResponse.getLName());
        Assert.assertEquals("Remco van der Heijden", modelAResponse.getFullName());
        Assert.assertEquals("burp", modelAResponse.getDoesNotMap());
    }

    @Test
    public void shouldMapToExistingTargetIgnoringNulls() throws Exception {
        final ModelARequest modelARequest = ModelARequest.builder()
                .id(1)
                .firstName(null)
                .lastName("van der Heijden")
                .build();

        final ModelAResponse modelAResponse = ModelAResponse
                .builder()
                .doesNotMap("burp")
                .fName("Remco")
                .build();

        modelMapper.map(modelARequest, modelAResponse);

        Assert.assertEquals(1, modelAResponse.getId());
        Assert.assertNull(modelAResponse.getFirstName());
        Assert.assertEquals("van der Heijden", modelAResponse.getLName());
        Assert.assertEquals("Remco", modelAResponse.getFName());
        Assert.assertEquals("burp", modelAResponse.getDoesNotMap());
    }


}
