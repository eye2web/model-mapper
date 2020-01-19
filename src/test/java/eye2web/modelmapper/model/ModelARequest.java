package eye2web.modelmapper.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ModelARequest {

    private int id;

    private LocalDate birthday;

    private String firstName;
    private String lastName;

    private boolean isActive;

    private boolean valid;

    private SimpleNestedModel simpleNestedModel;
}
