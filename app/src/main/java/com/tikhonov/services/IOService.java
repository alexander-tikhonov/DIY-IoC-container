package com.tikhonov.services;

public interface IOService {
    void out(String message);
    String readLine(String prompt);
    int readInt(String prompt);
}
