package eye2web.modelmapper;

public interface ModelMapperI {

    /**
     * Map object to new instance of destination type
     *
     * @param <D>             ClassType definition
     * @param source          Source where data should be copied from
     * @param destinationType Type of class used to map source data to.
     * @return returns a instance of destinationType including the copied data from source.
     * @throws eye2web.modelmapper.exception.ModelMapperException .
     */
    <D> D map(final Object source, final Class<D> destinationType);

    /**
     * Map object to existing instance
     *
     * @param <D>            ClassType definition
     * @param source         Source where data should be copied from
     * @param destinationObj Destination object.
     * @throws eye2web.modelmapper.exception.ModelMapperException .
     */
    <D> void map(final Object source, final D destinationObj);


    /**
     * Releases resources like ValueMapper(s) instances
     */
    void dispose();

}
