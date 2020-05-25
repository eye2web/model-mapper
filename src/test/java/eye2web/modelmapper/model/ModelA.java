package eye2web.modelmapper.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ModelA {

    private int id;

    private LocalDate birthday;

    private String firstName;
    private String lastName;

    private boolean isActive;

    private boolean valid;

    private List<String> names;

    private SimpleNestedModel simpleNestedModel;
}
