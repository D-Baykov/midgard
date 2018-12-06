package com.rbkmoney.midgard.config;

import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.poll.PollingEventPublisherBuilder;
import com.rbkmoney.midgard.handlers.InvoicingEventStockHandler;
import com.rbkmoney.midgard.handlers.PartyMngmntEventStockHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class EventStockConfig {

    @Bean
    public EventPublisher partyManagementEventPublisher(
            PartyMngmntEventStockHandler partyMngmntEventStockHandler,
            @Value("${bm.partyManagement.url}") Resource resource,
            @Value("${bm.partyManagement.polling.delay}") int pollDelay,
            @Value("${bm.partyManagement.polling.retryDelay}") int retryDelay,
            @Value("${bm.partyManagement.polling.maxPoolSize}") int maxPoolSize,
            @Value("${bm.partyManagement.polling.maxQuerySize}") int maxQuerySize
    ) throws IOException {
        return new PollingEventPublisherBuilder()
                .withURI(resource.getURI())
                .withEventHandler(partyMngmntEventStockHandler)
                .withMaxPoolSize(maxPoolSize)
                .withEventRetryDelay(retryDelay)
                .withPollDelay(pollDelay)
                .withMaxQuerySize(maxQuerySize)
                .build();
    }

    @Bean
    public EventPublisher invoicingEventPublisher(
            InvoicingEventStockHandler invoicingEventStockHandler,
            @Value("${bm.invoicing.url}") Resource resource,
            @Value("${bm.invoicing.polling.delay}") int pollDelay,
            @Value("${bm.invoicing.polling.retryDelay}") int retryDelay,
            @Value("${bm.invoicing.polling.maxPoolSize}") int maxPoolSize,
            @Value("${bm.invoicing.polling.maxQuerySize}") int maxQuerySize
    ) throws IOException {
        return new PollingEventPublisherBuilder()
                .withURI(resource.getURI())
                .withEventHandler(invoicingEventStockHandler)
                .withMaxPoolSize(maxPoolSize)
                .withEventRetryDelay(retryDelay)
                .withPollDelay(pollDelay)
                .withMaxQuerySize(maxQuerySize)
                .build();
    }


}
