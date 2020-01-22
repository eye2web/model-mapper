package eye2web.modelmapper.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Options {

    @Builder.Default
    private final InjectionMode injectionMode = InjectionMode.Method;

}
