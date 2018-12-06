package com.rbkmoney.midgard.handlers;


import com.rbkmoney.geck.filter.Filter;

public interface ServiceHandler<T, E> {

    default boolean accept(T change) {
        return getFilter().match(change);
    }

    void handle(T change, E event);

    Filter<T> getFilter();

}
