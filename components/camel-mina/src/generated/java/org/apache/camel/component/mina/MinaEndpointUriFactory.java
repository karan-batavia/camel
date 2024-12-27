/* Generated by camel build tools - do NOT edit this file! */
package org.apache.camel.component.mina;

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
public class MinaEndpointUriFactory extends org.apache.camel.support.component.EndpointUriFactorySupport implements EndpointUriFactory {

    private static final String BASE = ":protocol:host:port";

    private static final Set<String> PROPERTY_NAMES;
    private static final Set<String> SECRET_PROPERTY_NAMES;
    private static final Set<String> MULTI_VALUE_PREFIXES;
    static {
        Set<String> props = new HashSet<>(30);
        props.add("allowDefaultCodec");
        props.add("bridgeErrorHandler");
        props.add("cachedAddress");
        props.add("clientMode");
        props.add("codec");
        props.add("decoderMaxLineLength");
        props.add("disconnect");
        props.add("disconnectOnNoReply");
        props.add("encoderMaxLineLength");
        props.add("encoding");
        props.add("exceptionHandler");
        props.add("exchangePattern");
        props.add("filters");
        props.add("host");
        props.add("lazySessionCreation");
        props.add("lazyStartProducer");
        props.add("maximumPoolSize");
        props.add("minaLogger");
        props.add("noReplyLogLevel");
        props.add("objectCodecPattern");
        props.add("orderedThreadPoolExecutor");
        props.add("port");
        props.add("protocol");
        props.add("sslContextParameters");
        props.add("sync");
        props.add("textline");
        props.add("textlineDelimiter");
        props.add("timeout");
        props.add("transferExchange");
        props.add("writeTimeout");
        PROPERTY_NAMES = Collections.unmodifiableSet(props);
        SECRET_PROPERTY_NAMES = Collections.emptySet();
        MULTI_VALUE_PREFIXES = Collections.emptySet();
    }

    @Override
    public boolean isEnabled(String scheme) {
        return "mina".equals(scheme);
    }

    @Override
    public String buildUri(String scheme, Map<String, Object> properties, boolean encode) throws URISyntaxException {
        String syntax = scheme + BASE;
        String uri = syntax;

        Map<String, Object> copy = new HashMap<>(properties);

        uri = buildPathParameter(syntax, uri, "protocol", null, true, copy);
        uri = buildPathParameter(syntax, uri, "host", null, true, copy);
        uri = buildPathParameter(syntax, uri, "port", null, true, copy);
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
        return false;
    }
}

