package eye2web.modelmapper;

public interface ModelMapperI {

    /**
     * Map object to new instance of destination type
     *
     * @param source          Source where data should be copied from
     * @param destinationType Type of class used to map source data to.
     * @return returns a instance of destinationType including the copied data from source.
     * @throws Exception mapping error.
     */
    <D> D map(final Object source, final Class<D> destinationType)
            throws Exception;

    /**
     * Map object to existing instance
     *
     * @param source         Source where data should be copied from
     * @param destinationObj Destination object.
     * @throws Exception mapping error.
     */
    <D> void map(final Object source, final D destinationObj)
            throws Exception;


    /**
     * Releases resources like ValueMapper(s) instances
     */
    void dispose();

}
