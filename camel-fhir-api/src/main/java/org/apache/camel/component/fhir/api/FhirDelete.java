package org.apache.camel.component.fhir.api;

import java.util.Map;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IDeleteTyped;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;

/**
 * API for the "delete" operation, which performs a logical delete on a server resource.
 */
public class FhirDelete {

    private final IGenericClient client;

    public FhirDelete(IGenericClient client) {
        this.client = client;
    }

    /**
     * Deletes the given resource
     *
     * @param resource the {@link IBaseResource} to delete
     * @param extraParameters see {@link ExtraParameters} for a full list of parameters that can be passed, may be NULL
     * @return the {@link IBaseOperationOutcome}
     */
    public IBaseOperationOutcome resource(IBaseResource resource, Map<ExtraParameters, Object> extraParameters) {
        IDeleteTyped deleteTyped = client.delete().resource(resource);
        ExtraParameters.process(extraParameters, deleteTyped);
        return deleteTyped.execute();
    }

    /**
     * * Deletes the given resource by {@link IIdType}
     *
     * @param id the {@link IIdType} referencing the resource
     * @param extraParameters see {@link ExtraParameters} for a full list of parameters that can be passed, may be NULL
     * @return the {@link IBaseOperationOutcome}
     */
    public IBaseOperationOutcome resourceById(IIdType id, Map<ExtraParameters, Object> extraParameters) {
        IDeleteTyped deleteTyped = client.delete().resourceById(id);
        ExtraParameters.process(extraParameters, deleteTyped);
        return deleteTyped.execute();
    }

    /**
     * Deletes the resource by resource type e.g "Patient" and it's id
     * @param type the resource type e.g "Patient"
     * @param stringId it's id
     * @param extraParameters see {@link ExtraParameters} for a full list of parameters that can be passed, may be NULL
     * @return the {@link IBaseOperationOutcome}
     */
    public IBaseOperationOutcome resourceById(String type, String stringId, Map<ExtraParameters, Object> extraParameters) {
        IDeleteTyped deleteTyped = client.delete().resourceById(type, stringId);
        ExtraParameters.process(extraParameters, deleteTyped);
        return deleteTyped.execute();
    }

    /**
     * Specifies that the delete should be performed as a conditional delete against a given search URL.
     * @param url The search URL to use. The format of this URL should be of the form
     *            <code>[ResourceType]?[Parameters]</code>,
     *            for example: <code>Patient?name=Smith&amp;identifier=13.2.4.11.4%7C847366</code>
     * @param extraParameters see {@link ExtraParameters} for a full list of parameters that can be passed, may be NULL
     * @return the {@link IBaseOperationOutcome}
     */
    public IBaseOperationOutcome resourceConditionalByUrl(String url, Map<ExtraParameters, Object> extraParameters) {
        IDeleteTyped deleteTyped = client.delete().resourceConditionalByUrl(url);
        ExtraParameters.process(extraParameters, deleteTyped);
        return deleteTyped.execute();
    }

}
