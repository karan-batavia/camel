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
package org.apache.camel.jsonpath;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

public class JsonPathNullHeaderTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:start")
                        .setHeader("foo").jsonpath("$.foo")
                        .to("mock:foo");
            }
        };
    }

    @Test
    public void testHeaderValue() throws Exception {
        getMockEndpoint("mock:foo").expectedHeaderReceived("foo", "cheese");

        template.sendBody("direct:start", """
                {
                  "foo": "cheese"
                }
                """);

        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    public void testHeaderNull() throws Exception {
        getMockEndpoint("mock:foo").expectedHeaderReceived("foo", null);

        template.sendBody("direct:start", """
                {
                  "foo": null
                }
                """);

        MockEndpoint.assertIsSatisfied(context);
    }

}
