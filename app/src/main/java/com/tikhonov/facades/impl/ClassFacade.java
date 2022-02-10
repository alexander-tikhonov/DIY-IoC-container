package com.tikhonov.facades.impl;

import com.tikhonov.facades.ReflectionsFacade;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;


public class ClassFacade implements ReflectionsFacade {

    private final Class<?>[] classes;


    public ClassFacade(Class<?>[] classes) {
        this.classes = classes;
    }

    @Override
    public Reflections getReflections() {
        final var configBuilder = new ConfigurationBuilder();
        final var filterBuilder = new FilterBuilder();

        for (Class<?> clazz : classes) {
            configBuilder.setUrls(ClasspathHelper.forClass(clazz));
            filterBuilder.includePackage(clazz.getPackageName());
        }

        configBuilder.setScanners(Scanners.TypesAnnotated,
                                    Scanners.MethodsAnnotated,
                                    Scanners.SubTypes);

        return new Reflections(configBuilder);
    }
}
