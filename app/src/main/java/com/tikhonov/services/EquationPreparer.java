package com.tikhonov.services;

import com.tikhonov.models.Equation;

import java.util.List;

public interface EquationPreparer {
    List<Equation> prepareEquationsFor(int base);
}
