package eye2web.modelmapper.strategy;

import eye2web.modelmapper.ModelMapperI;

public interface Strategy {


    void mapObjects(final Object source, final Object destinationObj, final ModelMapperI modelMapper) throws Exception;

}
