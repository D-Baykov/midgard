package com.rbkmoney.midgard.pollers.dominant;

public interface DominantHandler<T> {

    boolean accept(T change);

    void handle(T change, Long versionId);
}
