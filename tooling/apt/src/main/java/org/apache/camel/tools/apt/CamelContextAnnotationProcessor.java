/**
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
package org.apache.camel.tools.apt;

import java.io.PrintWriter;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.camel.spi.Metadata;
import org.apache.camel.tools.apt.helper.JsonSchemaHelper;
import org.apache.camel.tools.apt.helper.Strings;

import static org.apache.camel.tools.apt.helper.JsonSchemaHelper.sanitizeDescription;
import static org.apache.camel.tools.apt.helper.Strings.canonicalClassName;
import static org.apache.camel.tools.apt.helper.Strings.isNullOrEmpty;
import static org.apache.camel.tools.apt.helper.Strings.safeNull;

/**
 * Process camel-spring's <camelContext> and generate json schema documentation
 */
@SupportedAnnotationTypes({"javax.xml.bind.annotation.*", "org.apache.camel.spi.Label"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CamelContextAnnotationProcessor extends AbstractAnnotationProcessor {

    private static final String ONE_OF_TYPE_NAME = "";
    private boolean skipUnwanted = true;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (roundEnv.processingOver()) {
                return true;
            }

            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(XmlRootElement.class);
            for (Element element : elements) {
                if (element instanceof TypeElement) {
                    processModelClass(roundEnv, (TypeElement) element);
                }
            }
        } catch (Throwable e) {
            dumpExceptionToErrorFile("camel-apt-error.log", "Error processing CamelContext model", e);
        }
        return true;
    }

    protected void processModelClass(final RoundEnvironment roundEnv, final TypeElement classElement) {
        // must be from camel-spring or camel-core-xml
        final String javaTypeName = canonicalClassName(classElement.getQualifiedName().toString());
        String packageName = javaTypeName.substring(0, javaTypeName.lastIndexOf("."));
        boolean valid = javaTypeName.startsWith("org.apache.camel.spring") || javaTypeName.startsWith("org.apache.camel.core.xml");
        if (!valid) {
            return;
        }

        // skip abstract classes
        if (classElement.getModifiers().contains(Modifier.ABSTRACT)) {
            return;
        }

        // skip unwanted classes which are "abstract" holders
        if (skipUnwanted) {
            if (classElement.getQualifiedName().toString().equals(ONE_OF_TYPE_NAME)) {
                return;
            }
        }

        final XmlRootElement rootElement = classElement.getAnnotation(XmlRootElement.class);
        if (rootElement == null) {
            return;
        }

        String aName = rootElement.name();
        if (isNullOrEmpty(aName) || "##default".equals(aName)) {
            XmlType typeElement = classElement.getAnnotation(XmlType.class);
            aName = typeElement.name();
        }
        final String name = aName;

        // lets use the xsd name as the file name
        String fileName;
        if (isNullOrEmpty(name) || "##default".equals(name)) {
            fileName = classElement.getSimpleName().toString() + ".json";
        } else {
            fileName = name + ".json";
        }

        // write json schema
        Func1<PrintWriter, Void> handler = new Func1<PrintWriter, Void>() {
            @Override
            public Void call(PrintWriter writer) {
                writeJSonSchemeDocumentation(writer, roundEnv, classElement, rootElement, javaTypeName, name);
                return null;
            }
        };
        processFile(packageName, fileName, handler);
    }

    protected void writeJSonSchemeDocumentation(PrintWriter writer, RoundEnvironment roundEnv, TypeElement classElement, XmlRootElement rootElement,
                                                String javaTypeName, String modelName) {
        // gather eip information
        EipModel eipModel = findEipModelProperties(roundEnv, classElement, javaTypeName, modelName);

        // collect eip information
        Set<EipOption> eipOptions = new TreeSet<EipOption>(new EipOptionComparator(eipModel));
        findClassProperties(writer, roundEnv, eipOptions, classElement, classElement, "", modelName);

        String json = createParameterJsonSchema(eipModel, eipOptions);
        writer.println(json);
    }

    public String createParameterJsonSchema(EipModel eipModel, Set<EipOption> options) {
        StringBuilder buffer = new StringBuilder("{");
        // eip model
        buffer.append("\n \"model\": {");
        buffer.append("\n    \"kind\": \"").append("model").append("\",");
        buffer.append("\n    \"name\": \"").append(eipModel.getName()).append("\",");
        if (eipModel.getTitle() != null) {
            buffer.append("\n    \"title\": \"").append(eipModel.getTitle()).append("\",");
        } else {
            // fallback and use name as title
            buffer.append("\n    \"title\": \"").append(asTitle(eipModel.getName())).append("\",");
        }
        buffer.append("\n    \"description\": \"").append(safeNull(eipModel.getDescription())).append("\",");
        buffer.append("\n    \"javaType\": \"").append(eipModel.getJavaType()).append("\",");
        buffer.append("\n    \"label\": \"").append(safeNull(eipModel.getLabel())).append("\"");
        buffer.append("\n  },");

        buffer.append("\n  \"properties\": {");
        boolean first = true;
        for (EipOption entry : options) {
            if (first) {
                first = false;
            } else {
                buffer.append(",");
            }
            buffer.append("\n    ");
            // as its json we need to sanitize the docs
            String doc = entry.getDocumentation();
            doc = sanitizeDescription(doc, false);
            buffer.append(JsonSchemaHelper.toJson(entry.getName(), entry.getKind(), entry.isRequired(), entry.getType(), entry.getDefaultValue(), doc,
                    entry.isDeprecated(), false, null, null, entry.isEnumType(), entry.getEnums(), entry.isOneOf(), entry.getOneOfTypes(), null, null, false));
        }
        buffer.append("\n  }");

        buffer.append("\n}\n");
        return buffer.toString();
    }

    protected EipModel findEipModelProperties(RoundEnvironment roundEnv, TypeElement classElement, String javaTypeName, String name) {
        EipModel model = new EipModel();
        model.setJavaType(javaTypeName);
        model.setName(name);

        Metadata metadata = classElement.getAnnotation(Metadata.class);
        if (metadata != null) {
            if (!Strings.isNullOrEmpty(metadata.label())) {
                model.setLabel(metadata.label());
            }
            if (!Strings.isNullOrEmpty(metadata.title())) {
                model.setTitle(metadata.title());
            }
        }

        // favor to use class javadoc of component as description
        if (model.getJavaType() != null) {
            Elements elementUtils = processingEnv.getElementUtils();
            TypeElement typeElement = findTypeElement(roundEnv, model.getJavaType());
            if (typeElement != null) {
                String doc = elementUtils.getDocComment(typeElement);
                if (doc != null) {
                    // need to sanitize the description first (we only want a summary)
                    doc = sanitizeDescription(doc, true);
                    // the javadoc may actually be empty, so only change the doc if we got something
                    if (!Strings.isNullOrEmpty(doc)) {
                        model.setDescription(doc);
                    }
                }
            }
        }

        return model;
    }

    protected void findClassProperties(PrintWriter writer, RoundEnvironment roundEnv, Set<EipOption> eipOptions,
                                       TypeElement originalClassType, TypeElement classElement, String prefix, String modelName) {
        while (true) {
            List<VariableElement> fieldElements = ElementFilter.fieldsIn(classElement.getEnclosedElements());
            for (VariableElement fieldElement : fieldElements) {

                String fieldName = fieldElement.getSimpleName().toString();

                XmlAttribute attribute = fieldElement.getAnnotation(XmlAttribute.class);
                if (attribute != null) {
                    boolean skip = processAttribute(roundEnv, originalClassType, classElement, fieldElement, fieldName, attribute, eipOptions, prefix, modelName);
                    if (skip) {
                        continue;
                    }
                }

                XmlElements elements = fieldElement.getAnnotation(XmlElements.class);
                if (elements != null) {
                    processElements(roundEnv, classElement, elements, fieldElement, eipOptions, prefix);
                }

                XmlElementRef elementRef = fieldElement.getAnnotation(XmlElementRef.class);
                if (elementRef != null) {
                    processElement(roundEnv, classElement, null, elementRef, fieldElement, eipOptions, prefix);
                }

                XmlElement element = fieldElement.getAnnotation(XmlElement.class);
                if (element != null) {
                    if ("rests".equals(fieldName)) {
                        processRests(roundEnv, classElement, element, fieldElement, fieldName, eipOptions, prefix);
                    } else if ("routes".equals(fieldName)) {
                        processRoutes(roundEnv, classElement, element, fieldElement, fieldName, eipOptions, prefix);
                    } else {
                        processElement(roundEnv, classElement, element, null, fieldElement, eipOptions, prefix);
                    }
                }
            }

            // check super classes which may also have fields
            TypeElement baseTypeElement = null;
            TypeMirror superclass = classElement.getSuperclass();
            if (superclass != null) {
                String superClassName = canonicalClassName(superclass.toString());
                baseTypeElement = findTypeElement(roundEnv, superClassName);
            }
            if (baseTypeElement != null) {
                classElement = baseTypeElement;
            } else {
                break;
            }
        }
    }

    private boolean processAttribute(RoundEnvironment roundEnv, TypeElement originalClassType, TypeElement classElement, VariableElement fieldElement,
                                     String fieldName, XmlAttribute attribute, Set<EipOption> eipOptions, String prefix, String modelName) {
        Elements elementUtils = processingEnv.getElementUtils();

        String name = attribute.name();
        if (isNullOrEmpty(name) || "##default".equals(name)) {
            name = fieldName;
        }

        // lets skip some unwanted attributes
        if (skipUnwanted) {
        }

        name = prefix + name;
        TypeMirror fieldType = fieldElement.asType();
        String fieldTypeName = fieldType.toString();
        TypeElement fieldTypeElement = findTypeElement(roundEnv, fieldTypeName);

        String defaultValue = findDefaultValue(fieldElement, fieldTypeName);
        String docComment = findJavaDoc(elementUtils, fieldElement, fieldName, name, classElement, true);
        if (isNullOrEmpty(docComment)) {
            Metadata metadata = fieldElement.getAnnotation(Metadata.class);
            docComment = metadata != null ? metadata.description() : null;
        }
        boolean required = attribute.required();
        // metadata may overrule element required
        required = findRequired(fieldElement, required);

        // gather enums
        Set<String> enums = new TreeSet<String>();
        boolean isEnum = fieldTypeElement != null && fieldTypeElement.getKind() == ElementKind.ENUM;
        if (isEnum) {
            TypeElement enumClass = findTypeElement(roundEnv, fieldTypeElement.asType().toString());
            // find all the enum constants which has the possible enum value that can be used
            List<VariableElement> fields = ElementFilter.fieldsIn(enumClass.getEnclosedElements());
            for (VariableElement var : fields) {
                if (var.getKind() == ElementKind.ENUM_CONSTANT) {
                    String val = var.toString();
                    enums.add(val);
                }
            }
        }

        boolean deprecated = fieldElement.getAnnotation(Deprecated.class) != null;

        // special for id as its inherited from camel-core
        if ("id".equals(name) && isNullOrEmpty(docComment)) {
            if ("CamelContextFactoryBean".equals(originalClassType.getSimpleName().toString())) {
                docComment = "Sets the id (name) of this CamelContext";
            } else {
                docComment = "Sets the id of this node";
            }
        }

        EipOption ep = new EipOption(name, "attribute", fieldTypeName, required, defaultValue, docComment, deprecated, isEnum, enums, false, null);
        eipOptions.add(ep);

        return false;
    }

    /**
     * Special for processing an @XmlElement routes field
     */
    private void processRoutes(RoundEnvironment roundEnv, TypeElement originalClassType, XmlElement element,
                               VariableElement fieldElement, String fieldName, Set<EipOption> eipOptions, String prefix) {

        TypeMirror fieldType = fieldElement.asType();
        String fieldTypeName = fieldType.toString();

        Set<String> oneOfTypes = new TreeSet<String>();
        oneOfTypes.add("route");

        EipOption ep = new EipOption("route", "element", fieldTypeName, false, "", "Contains the Camel routes", false, false, null, true, oneOfTypes);
        eipOptions.add(ep);
    }

    /**
     * Special for processing an @XmlElement rests field
     */
    private void processRests(RoundEnvironment roundEnv, TypeElement originalClassType, XmlElement element,
                              VariableElement fieldElement, String fieldName, Set<EipOption> eipOptions, String prefix) {

        TypeMirror fieldType = fieldElement.asType();
        String fieldTypeName = fieldType.toString();

        Set<String> oneOfTypes = new TreeSet<String>();
        oneOfTypes.add("rest");

        EipOption ep = new EipOption("rest", "element", fieldTypeName, false, "", "Contains the rest services defined using the rest-dsl", false, false, null, true, oneOfTypes);
        eipOptions.add(ep);
    }

    private void processElement(RoundEnvironment roundEnv, TypeElement classElement, XmlElement element, XmlElementRef elementRef, VariableElement fieldElement,
                                Set<EipOption> eipOptions, String prefix) {
        Elements elementUtils = processingEnv.getElementUtils();

        String fieldName;
        fieldName = fieldElement.getSimpleName().toString();
        if (element != null || elementRef != null) {

            String kind = "element";
            String name = element != null ? element.name() : elementRef.name();
            if (isNullOrEmpty(name) || "##default".equals(name)) {
                name = fieldName;
            }
            name = prefix + name;
            TypeMirror fieldType = fieldElement.asType();
            String fieldTypeName = fieldType.toString();
            TypeElement fieldTypeElement = findTypeElement(roundEnv, fieldTypeName);

            String defaultValue = findDefaultValue(fieldElement, fieldTypeName);
            String docComment = findJavaDoc(elementUtils, fieldElement, fieldName, name, classElement, true);
            if (isNullOrEmpty(docComment)) {
                Metadata metadata = fieldElement.getAnnotation(Metadata.class);
                docComment = metadata != null ? metadata.description() : null;
            }
            boolean required = element != null ? element.required() : elementRef.required();
            // metadata may overrule element required
            required = findRequired(fieldElement, required);

            // gather enums
            Set<String> enums = new LinkedHashSet<String>();
            boolean isEnum = fieldTypeElement != null && fieldTypeElement.getKind() == ElementKind.ENUM;
            if (isEnum) {
                TypeElement enumClass = findTypeElement(roundEnv, fieldTypeElement.asType().toString());
                // find all the enum constants which has the possible enum value that can be used
                List<VariableElement> fields = ElementFilter.fieldsIn(enumClass.getEnclosedElements());
                for (VariableElement var : fields) {
                    if (var.getKind() == ElementKind.ENUM_CONSTANT) {
                        String val = var.toString();
                        enums.add(val);
                    }
                }
            }

            // is it a definition/factory-bean type then its a oneOf
            TreeSet oneOfTypes = new TreeSet<String>();
            if (fieldTypeName.endsWith("Definition") || fieldTypeName.endsWith("FactoryBean")) {
                TypeElement definitionClass = findTypeElement(roundEnv, fieldTypeElement.asType().toString());
                if (definitionClass != null) {
                    XmlRootElement rootElement = definitionClass.getAnnotation(XmlRootElement.class);
                    if (rootElement != null) {
                        String childName = rootElement.name();
                        if (childName != null) {
                            oneOfTypes.add(childName);
                        }
                    }
                }
            } else if (fieldTypeName.endsWith("Definition>") || fieldTypeName.endsWith("FactoryBean>")) {
                // its a list so we need to load the generic type
                String typeName = Strings.between(fieldTypeName, "<", ">");
                TypeElement definitionClass = findTypeElement(roundEnv, typeName);
                if (definitionClass != null) {
                    XmlRootElement rootElement = definitionClass.getAnnotation(XmlRootElement.class);
                    if (rootElement != null) {
                        String childName = rootElement.name();
                        if (childName != null) {
                            oneOfTypes.add(childName);
                        }
                    }
                }
            }
            boolean oneOf = !oneOfTypes.isEmpty();

            boolean deprecated = fieldElement.getAnnotation(Deprecated.class) != null;

            EipOption ep = new EipOption(name, kind, fieldTypeName, required, defaultValue, docComment, deprecated, isEnum, enums, oneOf, oneOfTypes);
            eipOptions.add(ep);
        }
    }

    private void processElements(RoundEnvironment roundEnv, TypeElement classElement, XmlElements elements, VariableElement fieldElement,
                                 Set<EipOption> eipOptions, String prefix) {
        Elements elementUtils = processingEnv.getElementUtils();

        String fieldName;
        fieldName = fieldElement.getSimpleName().toString();
        if (elements != null) {
            String kind = "element";
            String name = fieldName;
            name = prefix + name;

            TypeMirror fieldType = fieldElement.asType();
            String fieldTypeName = fieldType.toString();

            String defaultValue = findDefaultValue(fieldElement, fieldTypeName);
            String docComment = findJavaDoc(elementUtils, fieldElement, fieldName, name, classElement, true);
            if (isNullOrEmpty(docComment)) {
                Metadata metadata = fieldElement.getAnnotation(Metadata.class);
                docComment = metadata != null ? metadata.description() : null;
            }
            boolean required = false;
            required = findRequired(fieldElement, required);

            // gather oneOf of the elements
            Set<String> oneOfTypes = new TreeSet<String>();
            for (XmlElement element : elements.value()) {
                String child = element.name();
                oneOfTypes.add(child);
            }

            EipOption ep = new EipOption(name, kind, fieldTypeName, required, defaultValue, docComment, false, false, null, true, oneOfTypes);
            eipOptions.add(ep);
        }
    }

    private String findDefaultValue(VariableElement fieldElement, String fieldTypeName) {
        String defaultValue = null;
        Metadata metadata = fieldElement.getAnnotation(Metadata.class);
        if (metadata != null) {
            if (!Strings.isNullOrEmpty(metadata.defaultValue())) {
                defaultValue = metadata.defaultValue();
            }
        }
        if (defaultValue == null) {
            // if its a boolean type, then we use false as the default
            if ("boolean".equals(fieldTypeName) || "java.lang.Boolean".equals(fieldTypeName)) {
                defaultValue = "false";
            }
        }

        return defaultValue;
    }

    private boolean findRequired(VariableElement fieldElement, boolean defaultValue) {
        Metadata metadata = fieldElement.getAnnotation(Metadata.class);
        if (metadata != null) {
            if (!Strings.isNullOrEmpty(metadata.required())) {
                defaultValue = "true".equals(metadata.required());
            }
        }
        return defaultValue;
    }

    /**
     * Capitializes the name as a title
     *
     * @param name  the name
     * @return as a title
     */
    private static String asTitle(String name) {
        StringBuilder sb = new StringBuilder();
        for (char c : name.toCharArray()) {
            boolean upper = Character.isUpperCase(c);
            boolean first = sb.length() == 0;
            if (first) {
                sb.append(Character.toUpperCase(c));
            } else if (upper) {
                sb.append(' ');
                sb.append(c);
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString().trim();
    }

    private static final class EipModel {

        private String name;
        private String title;
        private String javaType;
        private String label;
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getJavaType() {
            return javaType;
        }

        public void setJavaType(String javaType) {
            this.javaType = javaType;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }

    private static final class EipOption {

        private String name;
        private String kind;
        private String type;
        private boolean required;
        private String defaultValue;
        private String documentation;
        private boolean deprecated;
        private boolean enumType;
        private Set<String> enums;
        private boolean oneOf;
        private Set<String> oneOfTypes;

        private EipOption(String name, String kind, String type, boolean required, String defaultValue, String documentation, boolean deprecated,
                          boolean enumType, Set<String> enums, boolean oneOf, Set<String> oneOfTypes) {
            this.name = name;
            this.kind = kind;
            this.type = type;
            this.required = required;
            this.defaultValue = defaultValue;
            this.documentation = documentation;
            this.deprecated = deprecated;
            this.enumType = enumType;
            this.enums = enums;
            this.oneOf = oneOf;
            this.oneOfTypes = oneOfTypes;
        }

        public String getName() {
            return name;
        }

        public String getKind() {
            return kind;
        }

        public String getType() {
            return type;
        }

        public boolean isRequired() {
            return required;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public String getDocumentation() {
            return documentation;
        }

        public boolean isDeprecated() {
            return deprecated;
        }

        public boolean isEnumType() {
            return enumType;
        }

        public Set<String> getEnums() {
            return enums;
        }

        public boolean isOneOf() {
            return oneOf;
        }

        public Set<String> getOneOfTypes() {
            return oneOfTypes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            EipOption that = (EipOption) o;

            if (!name.equals(that.name)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    private static final class EipOptionComparator implements Comparator<EipOption> {

        private final EipModel model;

        private EipOptionComparator(EipModel model) {
            this.model = model;
        }

        @Override
        public int compare(EipOption o1, EipOption o2) {
            int weigth = weigth(o1);
            int weigth2 = weigth(o2);

            if (weigth == weigth2) {
                // keep the current order
                return 1;
            } else {
                // sort according to weight
                return weigth2 - weigth;
            }
        }

        private int weigth(EipOption o) {
            String name = o.getName();

            // these should be first
            if ("expression".equals(name)) {
                return 10;
            }

            // these should be last
            if ("description".equals(name)) {
                return -10;
            } else if ("id".equals(name)) {
                return -9;
            } else if ("pattern".equals(name) && "to".equals(model.getName())) {
                // and pattern only for the to model
                return -8;
            }
            return 0;
        }
    }

}
