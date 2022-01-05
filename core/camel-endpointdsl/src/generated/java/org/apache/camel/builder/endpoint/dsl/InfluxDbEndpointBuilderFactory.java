/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.builder.endpoint.dsl;

import javax.annotation.Generated;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.endpoint.AbstractEndpointBuilder;

/**
 * Interact with , a time series database.
 * 
 * Generated by camel build tools - do NOT edit this file!
 */
@Generated("org.apache.camel.maven.packaging.EndpointDslMojo")
public interface InfluxDbEndpointBuilderFactory {


    /**
     * Builder for endpoint for the InfluxDB component.
     */
    public interface InfluxDbEndpointBuilder extends EndpointProducerBuilder {
        /**
         * Define if we want to auto create the database if it's not present.
         * 
         * The option is a: &lt;code&gt;boolean&lt;/code&gt; type.
         * 
         * Default: false
         * Group: producer
         * 
         * @param autoCreateDatabase the value to set
         * @return the dsl builder
         */
        default InfluxDbEndpointBuilder autoCreateDatabase(
                boolean autoCreateDatabase) {
            doSetProperty("autoCreateDatabase", autoCreateDatabase);
            return this;
        }
        /**
         * Define if we want to auto create the database if it's not present.
         * 
         * The option will be converted to a &lt;code&gt;boolean&lt;/code&gt;
         * type.
         * 
         * Default: false
         * Group: producer
         * 
         * @param autoCreateDatabase the value to set
         * @return the dsl builder
         */
        default InfluxDbEndpointBuilder autoCreateDatabase(
                String autoCreateDatabase) {
            doSetProperty("autoCreateDatabase", autoCreateDatabase);
            return this;
        }
        /**
         * Define if this operation is a batch operation or not.
         * 
         * The option is a: &lt;code&gt;boolean&lt;/code&gt; type.
         * 
         * Default: false
         * Group: producer
         * 
         * @param batch the value to set
         * @return the dsl builder
         */
        default InfluxDbEndpointBuilder batch(boolean batch) {
            doSetProperty("batch", batch);
            return this;
        }
        /**
         * Define if this operation is a batch operation or not.
         * 
         * The option will be converted to a &lt;code&gt;boolean&lt;/code&gt;
         * type.
         * 
         * Default: false
         * Group: producer
         * 
         * @param batch the value to set
         * @return the dsl builder
         */
        default InfluxDbEndpointBuilder batch(String batch) {
            doSetProperty("batch", batch);
            return this;
        }
        /**
         * Define if we want to check the database existence while starting the
         * endpoint.
         * 
         * The option is a: &lt;code&gt;boolean&lt;/code&gt; type.
         * 
         * Default: false
         * Group: producer
         * 
         * @param checkDatabaseExistence the value to set
         * @return the dsl builder
         */
        default InfluxDbEndpointBuilder checkDatabaseExistence(
                boolean checkDatabaseExistence) {
            doSetProperty("checkDatabaseExistence", checkDatabaseExistence);
            return this;
        }
        /**
         * Define if we want to check the database existence while starting the
         * endpoint.
         * 
         * The option will be converted to a &lt;code&gt;boolean&lt;/code&gt;
         * type.
         * 
         * Default: false
         * Group: producer
         * 
         * @param checkDatabaseExistence the value to set
         * @return the dsl builder
         */
        default InfluxDbEndpointBuilder checkDatabaseExistence(
                String checkDatabaseExistence) {
            doSetProperty("checkDatabaseExistence", checkDatabaseExistence);
            return this;
        }
        /**
         * The name of the database where the time series will be stored.
         * 
         * The option is a: &lt;code&gt;java.lang.String&lt;/code&gt; type.
         * 
         * Group: producer
         * 
         * @param databaseName the value to set
         * @return the dsl builder
         */
        default InfluxDbEndpointBuilder databaseName(String databaseName) {
            doSetProperty("databaseName", databaseName);
            return this;
        }
        /**
         * Whether the producer should be started lazy (on the first message).
         * By starting lazy you can use this to allow CamelContext and routes to
         * startup in situations where a producer may otherwise fail during
         * starting and cause the route to fail being started. By deferring this
         * startup to be lazy then the startup failure can be handled during
         * routing messages via Camel's routing error handlers. Beware that when
         * the first message is processed then creating and starting the
         * producer may take a little time and prolong the total processing time
         * of the processing.
         * 
         * The option is a: &lt;code&gt;boolean&lt;/code&gt; type.
         * 
         * Default: false
         * Group: producer
         * 
         * @param lazyStartProducer the value to set
         * @return the dsl builder
         */
        default InfluxDbEndpointBuilder lazyStartProducer(
                boolean lazyStartProducer) {
            doSetProperty("lazyStartProducer", lazyStartProducer);
            return this;
        }
        /**
         * Whether the producer should be started lazy (on the first message).
         * By starting lazy you can use this to allow CamelContext and routes to
         * startup in situations where a producer may otherwise fail during
         * starting and cause the route to fail being started. By deferring this
         * startup to be lazy then the startup failure can be handled during
         * routing messages via Camel's routing error handlers. Beware that when
         * the first message is processed then creating and starting the
         * producer may take a little time and prolong the total processing time
         * of the processing.
         * 
         * The option will be converted to a &lt;code&gt;boolean&lt;/code&gt;
         * type.
         * 
         * Default: false
         * Group: producer
         * 
         * @param lazyStartProducer the value to set
         * @return the dsl builder
         */
        default InfluxDbEndpointBuilder lazyStartProducer(
                String lazyStartProducer) {
            doSetProperty("lazyStartProducer", lazyStartProducer);
            return this;
        }
        /**
         * Define if this operation is an insert or a query.
         * 
         * The option is a: &lt;code&gt;java.lang.String&lt;/code&gt; type.
         * 
         * Default: insert
         * Group: producer
         * 
         * @param operation the value to set
         * @return the dsl builder
         */
        default InfluxDbEndpointBuilder operation(String operation) {
            doSetProperty("operation", operation);
            return this;
        }
        /**
         * Define the query in case of operation query.
         * 
         * The option is a: &lt;code&gt;java.lang.String&lt;/code&gt; type.
         * 
         * Group: producer
         * 
         * @param query the value to set
         * @return the dsl builder
         */
        default InfluxDbEndpointBuilder query(String query) {
            doSetProperty("query", query);
            return this;
        }
        /**
         * The string that defines the retention policy to the data created by
         * the endpoint.
         * 
         * The option is a: &lt;code&gt;java.lang.String&lt;/code&gt; type.
         * 
         * Default: default
         * Group: producer
         * 
         * @param retentionPolicy the value to set
         * @return the dsl builder
         */
        default InfluxDbEndpointBuilder retentionPolicy(String retentionPolicy) {
            doSetProperty("retentionPolicy", retentionPolicy);
            return this;
        }
    }

    public interface InfluxDbBuilders {
        /**
         * InfluxDB (camel-influxdb)
         * Interact with , a time series database.
         * 
         * Category: database
         * Since: 2.18
         * Maven coordinates: org.apache.camel:camel-influxdb
         * 
         * Syntax: <code>influxdb:connectionBean</code>
         * 
         * Path parameter: connectionBean (required)
         * Connection to the influx database, of class InfluxDB.class
         * 
         * @param path connectionBean
         * @return the dsl builder
         */
        default InfluxDbEndpointBuilder influxdb(String path) {
            return InfluxDbEndpointBuilderFactory.endpointBuilder("influxdb", path);
        }
        /**
         * InfluxDB (camel-influxdb)
         * Interact with , a time series database.
         * 
         * Category: database
         * Since: 2.18
         * Maven coordinates: org.apache.camel:camel-influxdb
         * 
         * Syntax: <code>influxdb:connectionBean</code>
         * 
         * Path parameter: connectionBean (required)
         * Connection to the influx database, of class InfluxDB.class
         * 
         * @param componentName to use a custom component name for the endpoint
         * instead of the default name
         * @param path connectionBean
         * @return the dsl builder
         */
        default InfluxDbEndpointBuilder influxdb(
                String componentName,
                String path) {
            return InfluxDbEndpointBuilderFactory.endpointBuilder(componentName, path);
        }
    }
    static InfluxDbEndpointBuilder endpointBuilder(
            String componentName,
            String path) {
        class InfluxDbEndpointBuilderImpl extends AbstractEndpointBuilder implements InfluxDbEndpointBuilder {
            public InfluxDbEndpointBuilderImpl(String path) {
                super(componentName, path);
            }
        }
        return new InfluxDbEndpointBuilderImpl(path);
    }
}