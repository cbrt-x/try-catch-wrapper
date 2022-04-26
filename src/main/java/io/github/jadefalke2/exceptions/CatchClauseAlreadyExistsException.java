package io.github.jadefalke2.exceptions;

import io.github.jadefalke2.interfaces.CatchClause;

public class CatchClauseAlreadyExistsException extends RuntimeException {
    private final Class<? extends Exception> handledType;
    private final CatchClause clause;

    public CatchClauseAlreadyExistsException(Class<? extends Exception> handledType, CatchClause clause) {
        super(String.format("Catch clause already exists because another clause handles exceptions of type %s.", handledType.getSimpleName()));
        this.handledType = handledType;
        this.clause = clause;
    }

    public Class<? extends Exception> getHandledType() {
        return handledType;
    }

    public CatchClause getClause() {
        return clause;
    }
}
