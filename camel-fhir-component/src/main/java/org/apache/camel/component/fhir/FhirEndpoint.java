package org.apache.camel.component.fhir;

import java.util.HashMap;
import java.util.Map;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.component.fhir.api.ExtraParameters;
import org.apache.camel.component.fhir.api.FhirCapabilities;
import org.apache.camel.component.fhir.api.FhirCreate;
import org.apache.camel.component.fhir.api.FhirDelete;
import org.apache.camel.component.fhir.api.FhirHistory;
import org.apache.camel.component.fhir.api.FhirLoadPage;
import org.apache.camel.component.fhir.api.FhirMeta;
import org.apache.camel.component.fhir.api.FhirOperation;
import org.apache.camel.component.fhir.api.FhirPatch;
import org.apache.camel.component.fhir.api.FhirRead;
import org.apache.camel.component.fhir.api.FhirSearch;
import org.apache.camel.component.fhir.api.FhirTransaction;
import org.apache.camel.component.fhir.api.FhirUpdate;
import org.apache.camel.component.fhir.api.FhirValidate;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriPath;
import org.apache.camel.util.component.AbstractApiEndpoint;
import org.apache.camel.util.component.ApiMethod;
import org.apache.camel.util.component.ApiMethodPropertiesHelper;

import org.apache.camel.component.fhir.internal.FhirApiCollection;
import org.apache.camel.component.fhir.internal.FhirApiName;
import org.apache.camel.component.fhir.internal.FhirConstants;
import org.apache.camel.component.fhir.internal.FhirPropertiesHelper;

/**
 * Represents a FHIR endpoint.
 */
@UriEndpoint(firstVersion = "1.0-SNAPSHOT", scheme = "fhir", title = "FHIR", syntax="fhir:apiName/methodName",
             consumerClass = FhirConsumer.class, label = "api")
public class FhirEndpoint extends AbstractApiEndpoint<FhirApiName, FhirConfiguration> {

    @UriPath @Metadata(required = "true")
    private String name;

    private Object apiProxy;
    private static final String EXTRA_PARAMETERS_PROPERTY = "extraParameters";

    public FhirEndpoint(String uri, FhirComponent component,
                        FhirApiName apiName, String methodName, FhirConfiguration endpointConfiguration) {
        super(uri, component, apiName, methodName, FhirApiCollection.getCollection().getHelper(apiName), endpointConfiguration);

    }

    public Producer createProducer() throws Exception {
        return new FhirProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        // make sure inBody is not set for consumers
        if (inBody != null) {
            throw new IllegalArgumentException("Option inBody is not supported for consumer endpoint");
        }
        final FhirConsumer consumer = new FhirConsumer(this, processor);
        // also set consumer.* properties
        configureConsumer(consumer);
        return consumer;
    }

    @Override
    protected ApiMethodPropertiesHelper<FhirConfiguration> getPropertiesHelper() {
        return FhirPropertiesHelper.getHelper();
    }

    protected String getThreadProfileName() {
        return FhirConstants.THREAD_PROFILE_NAME;
    }

    @Override
    protected void afterConfigureProperties() {
        initRestClient();
        IGenericClient client = getConfiguration().getGenericClient();
        switch(apiName){
            case CAPABILITIES:
                apiProxy = new FhirCapabilities(client);
                break;
            case CREATE:
                apiProxy = new FhirCreate(client);
                break;
            case DELETE:
                apiProxy = new FhirDelete(client);
                break;
            case HISTORY:
                apiProxy = new FhirHistory(client);
                break;
            case LOAD_PAGE:
                apiProxy = new FhirLoadPage(client);
                break;
            case META:
                apiProxy = new FhirMeta(client);
                break;
            case OPERATION:
                apiProxy = new FhirOperation(client);
                break;
            case PATCH:
                apiProxy = new FhirPatch(client);
                break;
            case READ:
                apiProxy = new FhirRead(client);
                break;
            case SEARCH:
                apiProxy = new FhirSearch(client);
                break;
            case TRANSACTION:
                apiProxy = new FhirTransaction(client);
                break;
            case UPDATE:
                apiProxy = new FhirUpdate(client);
                break;
            case VALIDATE:
                apiProxy = new FhirValidate(client);
                break;
            default:
                throw new IllegalArgumentException("Invalid API name " + apiName);
        }
    }

    @Override
    public void interceptProperties(Map<String, Object> properties) {
        Map<ExtraParameters, Object> extraProperties = getExtraParameters(properties);
        for (ExtraParameters extraParameter : ExtraParameters.values()) {
            Object value = properties.get(extraParameter.getParam());
            if (value != null) {
                extraProperties.put(extraParameter, value);
            }
        }
        properties.put(EXTRA_PARAMETERS_PROPERTY, extraProperties);

    }

    private Map<ExtraParameters, Object> getExtraParameters(Map<String, Object> properties) {
        Object extraParameters = properties.get(EXTRA_PARAMETERS_PROPERTY);
        if (extraParameters == null) {
            return new HashMap<>();
        }
        return (Map<ExtraParameters, Object>) extraParameters;
    }

    private void initRestClient() {
        if (configuration.getGenericClient() == null) {
            if (configuration.getFhirBase() == null) {
                throw new RuntimeCamelException("FhirBase URL must be set!");
            }
            if (configuration.getFhirContext() == null) {
                FhirVersionEnum version = FhirVersionEnum.valueOf(configuration.getFhirVersion());
                configuration.setFhirContext(new FhirContext(version));
            }
            configuration.setGenericClient(configuration.getFhirContext().newRestfulGenericClient(configuration.getFhirBase()));
        }
    }

    @Override
    public Object getApiProxy(ApiMethod method, Map<String, Object> args) {
        return apiProxy;
    }

    /**
     * Some description of this option, and what it does
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
