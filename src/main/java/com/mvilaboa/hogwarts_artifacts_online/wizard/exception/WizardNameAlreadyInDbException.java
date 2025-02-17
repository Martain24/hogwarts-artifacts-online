package com.mvilaboa.hogwarts_artifacts_online.wizard.exception;

public class WizardNameAlreadyInDbException extends RuntimeException {
    
    public WizardNameAlreadyInDbException(String nameAlreadyInDb) {
        super("Already exists a wizard with Name " + nameAlreadyInDb + " :(");
    }

}
