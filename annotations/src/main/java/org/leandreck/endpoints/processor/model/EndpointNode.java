/**
 * Copyright © 2016 Mathias Kowalzik (Mathias.Kowalzik@leandreck.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.leandreck.endpoints.processor.model;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 */
public class EndpointNode {

    private final String serviceName;
    private final String serviceURL;
    private final String template;
    private final List<MethodNode> methods;
    private final List<MethodNode> getMethods;
    private final List<MethodNode> headMethods;
    private final List<MethodNode> postMethods;
    private final List<MethodNode> putMethods;
    private final List<MethodNode> patchMethods;
    private final List<MethodNode> deleteMethods;
    private final List<MethodNode> optionsMethods;
    private final List<MethodNode> traceMethods;
    private final Set<TypeNode> types;

    public EndpointNode(final String serviceName, final String serviceURL, String template, final List<MethodNode> methods) {
        this.serviceName = serviceName;
        this.serviceURL = serviceURL;
        this.template = template;
        this.methods = methods;

        this.getMethods = this.getMethods().stream().filter(m -> m.getHttpMethods().contains("get")).collect(toList());
        this.headMethods = this.getMethods().stream().filter(m -> m.getHttpMethods().contains("head")).collect(toList());
        this.postMethods = this.getMethods().stream().filter(m -> m.getHttpMethods().contains("post")).collect(toList());
        this.putMethods = this.getMethods().stream().filter(m -> m.getHttpMethods().contains("put")).collect(toList());
        this.patchMethods = this.getMethods().stream().filter(m -> m.getHttpMethods().contains("patch")).collect(toList());
        this.deleteMethods = this.getMethods().stream().filter(m -> m.getHttpMethods().contains("delete")).collect(toList());
        this.optionsMethods = this.getMethods().stream().filter(m -> m.getHttpMethods().contains("options")).collect(toList());
        this.traceMethods = this.getMethods().stream().filter(m -> m.getHttpMethods().contains("trace")).collect(toList());

        this.types = collectTypes();
    }

    private Set<TypeNode> collectTypes() {
        final Map<String, TypeNode> typeMap = new HashMap<>();

        this.getMethods().stream()
                .map(MethodNode::getTypes)
                .flatMap(Collection::stream)
                .map(EndpointNode::flatten)
                .flatMap(Collection::stream)
                .filter(c -> !c.isMappedType())
                .forEach(type -> typeMap.put(type.getTypeName(), type));
        return new HashSet<>(typeMap.values());
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public List<MethodNode> getMethods() {
        return methods;
    }

    public String getTemplate() {
        return template;
    }

    public Set<TypeNode> getTypes() {
        return types;
    }

    private static Collection<TypeNode> flatten(TypeNode root) {
        final Set<TypeNode> typeSet = root.getTypes().stream()
                .map(EndpointNode::flatten)
                .flatMap(Collection::stream)
                .filter(c -> !c.isMappedType())
                .collect(toSet());
        if (root.isDeclaredComplexType()) {
            typeSet.add(root);
        }
        return typeSet;
    }

    public List<MethodNode> getGetMethods() {
        return getMethods;
    }

    public List<MethodNode> getHeadMethods() {
        return headMethods;
    }

    public List<MethodNode> getPostMethods() {
        return postMethods;
    }

    public List<MethodNode> getTraceMethods() {
        return traceMethods;
    }

    public List<MethodNode> getOptionsMethods() {
        return optionsMethods;
    }

    public List<MethodNode> getDeleteMethods() {
        return deleteMethods;
    }

    public List<MethodNode> getPatchMethods() {
        return patchMethods;
    }

    public List<MethodNode> getPutMethods() {
        return putMethods;
    }
}
