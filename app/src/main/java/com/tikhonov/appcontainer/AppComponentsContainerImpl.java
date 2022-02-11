package com.tikhonov.appcontainer;

import com.tikhonov.appcontainer.api.AppComponent;
import com.tikhonov.appcontainer.api.AppComponentsContainer;
import com.tikhonov.appcontainer.api.AppComponentsContainerConfig;
import com.tikhonov.facades.impl.ClassFacade;
import com.tikhonov.facades.impl.PackageFacade;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.*;

public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();
    private final Reflections reflections;

    public AppComponentsContainerImpl(Class<?> ...initialConfigClasses) {
        reflections = new ClassFacade(initialConfigClasses).getReflections();
        processConfig();
    }

    public AppComponentsContainerImpl(String packageName) {
        reflections = new PackageFacade(packageName).getReflections();
        processConfig();
    }

    private void processConfig() {
        final List<Class<?>> configs = new ArrayList<>(
                reflections.getTypesAnnotatedWith(AppComponentsContainerConfig.class)
        );

        if (configs.isEmpty()) {
            throw new IllegalArgumentException("Configuration classes not found");
        }

        sortConfigsByOrder(configs);

        configs.forEach(config -> {
            final Object appConfig = initAppConfig(config);
            final List<Method> methods = getAnnotatedMethods(config);

            if (methods.isEmpty()) {
                throw new IllegalArgumentException("Components not found");
            }

            sortComponentsByOrder(methods);
            fillComponentStore(appConfig, methods);
        });
    }

    private void sortConfigsByOrder(List<Class<?>> configs) {
        configs.sort((c1, c2) -> {
            final int order1 = c1.getDeclaredAnnotation(AppComponentsContainerConfig.class).order();
            final int order2 = c2.getDeclaredAnnotation(AppComponentsContainerConfig.class).order();

            return Integer.compare(order1, order2);
        });
    }

    private void sortComponentsByOrder(List<Method> methods) {
        methods.sort((m1, m2) -> {
            final int order1 = m1.getDeclaredAnnotation(AppComponent.class).order();
            final int order2 = m2.getDeclaredAnnotation(AppComponent.class).order();

            return Integer.compare(order1, order2);
        });
    }

    private Object initAppConfig(Class<?> config) {
        try {
            return config.getConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Configuration class must have a default constructor", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Method> getAnnotatedMethods(Class<?> config) {
        return new ArrayList<>(
                ReflectionUtils.getMethods(config,
                        method -> Objects.requireNonNull(method).isAnnotationPresent(AppComponent.class))
        );
    }

    private void fillComponentStore(Object config, List<Method> methods) {
        for (Method method : methods) {
            final String componentName = method.getDeclaredAnnotation(AppComponent.class).name();
            final Object[] args = getArguments(method);
            final Object result = invokeMethod(config, method, args);

            appComponents.add(result);
            appComponentsByName.put(componentName, result);
        }
    }

    private Object[] getArguments(Method method) {
        return Arrays.stream(method.getParameters())
                .map(param ->
                    appComponents.stream().filter(component ->
                        param.getType().isAssignableFrom(component.getClass())
                    ).findFirst().orElse(null)
                ).toArray();
    }

    private Object invokeMethod(Object owner, Method method, Object ...args) {
        try {
            return method.invoke(owner, args);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Method execution error. Method: %s", method.getName()), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(Class<C> componentClass) {
        return (C) appComponents.stream()
                .filter(component -> componentClass.isAssignableFrom(component.getClass()))
                .findFirst()
                .orElse(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(String componentName) {
        return (C) appComponentsByName.get(componentName);
    }
}
