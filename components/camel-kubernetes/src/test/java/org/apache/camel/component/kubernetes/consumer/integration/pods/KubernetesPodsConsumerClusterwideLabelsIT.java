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
package org.apache.camel.component.kubernetes.consumer.integration.pods;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kubernetes.consumer.integration.support.KubernetesConsumerTestSupport;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperties;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

@EnabledIfSystemProperties({
        @EnabledIfSystemProperty(named = "kubernetes.test.auth", matches = ".*", disabledReason = "Requires kubernetes"),
        @EnabledIfSystemProperty(named = "kubernetes.test.host", matches = ".*", disabledReason = "Requires kubernetes"),
        @EnabledIfSystemProperty(named = "kubernetes.test.host.k8s", matches = "true", disabledReason = "Requires kubernetes"),
})
public class KubernetesPodsConsumerClusterwideLabelsIT extends KubernetesConsumerTestSupport {
    @Test
    public void clusterWideLabelsTest() {
        createPod(ns2, "pod1", Map.of("otherKey", "otherValue"));
        createPod(ns1, "pod2", LABELS);
        createPod(ns2, "pod3", LABELS);

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            final List<String> list = result.getExchanges().stream().map(ex -> ex.getIn().getBody(String.class)).toList();
            assertThat(list, allOf(not(hasItem(containsString("pod1"))), hasItem(containsString("pod2")),
                    hasItem(containsString("pod3"))));
        });
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                fromF("kubernetes-pods://%s?oauthToken=%s&labelKey=%s&labelValue=%s",
                        host, authToken, "testkey", "testvalue")
                        .process(new KubernetesProcessor())
                        .to(result);
            }
        };

    }
}
