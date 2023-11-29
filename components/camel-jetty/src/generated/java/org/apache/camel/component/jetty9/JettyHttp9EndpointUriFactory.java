/* Generated by camel build tools - do NOT edit this file! */
package org.apache.camel.component.jetty9;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.camel.spi.EndpointUriFactory;

/**
 * Generated by camel build tools - do NOT edit this file!
 */
public class JettyHttp9EndpointUriFactory extends org.apache.camel.support.component.EndpointUriFactorySupport implements EndpointUriFactory {

    private static final String BASE = ":httpUri";

    private static final Set<String> PROPERTY_NAMES;
    private static final Set<String> SECRET_PROPERTY_NAMES;
    private static final Set<String> MULTI_VALUE_PREFIXES;
    static {
        Set<String> props = new HashSet<>(37);
        props.add("async");
        props.add("bridgeErrorHandler");
        props.add("chunked");
        props.add("continuationTimeout");
        props.add("disableStreamCache");
        props.add("eagerCheckContentAvailable");
        props.add("enableCORS");
        props.add("enableJmx");
        props.add("enableMultipartFilter");
        props.add("exceptionHandler");
        props.add("exchangePattern");
        props.add("fileSizeThreshold");
        props.add("filesLocation");
        props.add("filterInitParameters");
        props.add("filters");
        props.add("handlers");
        props.add("headerFilterStrategy");
        props.add("httpBinding");
        props.add("httpMethodRestrict");
        props.add("httpUri");
        props.add("mapHttpMessageBody");
        props.add("mapHttpMessageFormUrlEncodedBody");
        props.add("mapHttpMessageHeaders");
        props.add("matchOnUriPrefix");
        props.add("maxFileSize");
        props.add("maxRequestSize");
        props.add("multipartFilter");
        props.add("muteException");
        props.add("optionsEnabled");
        props.add("responseBufferSize");
        props.add("sendDateHeader");
        props.add("sendServerVersion");
        props.add("sessionSupport");
        props.add("sslContextParameters");
        props.add("traceEnabled");
        props.add("transferException");
        props.add("useContinuation");
        PROPERTY_NAMES = Collections.unmodifiableSet(props);
        SECRET_PROPERTY_NAMES = Collections.emptySet();
        Set<String> prefixes = new HashSet<>(1);
        prefixes.add("filter.");
        MULTI_VALUE_PREFIXES = Collections.unmodifiableSet(prefixes);
    }

    @Override
    public boolean isEnabled(String scheme) {
        return "jetty".equals(scheme);
    }

    @Override
    public String buildUri(String scheme, Map<String, Object> properties, boolean encode) throws URISyntaxException {
        String syntax = scheme + BASE;
        String uri = syntax;

        Map<String, Object> copy = new HashMap<>(properties);

        uri = buildPathParameter(syntax, uri, "httpUri", null, true, copy);
        uri = buildQueryParameters(uri, copy, encode);
        return uri;
    }

    @Override
    public Set<String> propertyNames() {
        return PROPERTY_NAMES;
    }

    @Override
    public Set<String> secretPropertyNames() {
        return SECRET_PROPERTY_NAMES;
    }

    @Override
    public Set<String> multiValuePrefixes() {
        return MULTI_VALUE_PREFIXES;
    }

    @Override
    public boolean isLenientProperties() {
        return true;
    }
}

