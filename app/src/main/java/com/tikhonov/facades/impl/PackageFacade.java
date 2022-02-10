package com.tikhonov.facades.impl;

import com.tikhonov.facades.ReflectionsFacade;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

public class PackageFacade implements ReflectionsFacade {

    private final String packageName;

    public PackageFacade(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public Reflections getReflections() {
        return new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packageName))
                .filterInputsBy(new FilterBuilder().includePackage(packageName))
                .setScanners(
                        Scanners.TypesAnnotated,
                        Scanners.MethodsAnnotated,
                        Scanners.SubTypes.filterResultsBy(new FilterBuilder().includePackage(packageName))
                ));
    }
}
