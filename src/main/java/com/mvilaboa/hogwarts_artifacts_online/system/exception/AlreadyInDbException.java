package com.mvilaboa.hogwarts_artifacts_online.system.exception;

public class AlreadyInDbException extends RuntimeException {

    public AlreadyInDbException(String objectName, String propertyName, Object propertyValue) {
        super("Already exists a %s with %s %s :("
                .formatted(objectName, propertyName, propertyValue));
    }
}
