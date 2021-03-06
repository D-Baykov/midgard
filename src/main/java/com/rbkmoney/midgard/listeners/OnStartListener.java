package com.rbkmoney.midgard.listeners;

import com.rbkmoney.eventstock.client.DefaultSubscriberConfig;
import com.rbkmoney.eventstock.client.EventConstraint;
import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.SubscriberConfig;
import com.rbkmoney.eventstock.client.poll.EventFlowFilter;
import com.rbkmoney.midgard.services.InvoicingService;
import com.rbkmoney.midgard.services.PartyManagementService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OnStartListener implements ApplicationListener<ApplicationReadyEvent> {

    private final EventPublisher partyManagementEventPublisher;

    private final EventPublisher invoicingEventPublisher;

    private final PartyManagementService partyManagementService;

    private final InvoicingService invoicingService;

    @Value("${bm.pollingEnabled}")
    private boolean pollingEnabled;

    public OnStartListener(EventPublisher partyManagementEventPublisher,
                           EventPublisher invoicingEventPublisher,

                           PartyManagementService partyManagementService,
                           InvoicingService invoicingService) {
        this.partyManagementEventPublisher = partyManagementEventPublisher;
        this.invoicingEventPublisher = invoicingEventPublisher;

        this.partyManagementService = partyManagementService;
        this.invoicingService = invoicingService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (pollingEnabled) {
            //TODO: Заготовка для дальнейшей возможной реализации пулинга из эвентов
            //partyManagementEventPublisher.subscribe(buildSubscriberConfig(partyManagementService.getLastEventId()));
            //invoicingEventPublisher.subscribe(buildSubscriberConfig(invoicingService.getLastEventId()));
        }
    }

    private SubscriberConfig buildSubscriberConfig(Optional<Long> lastEventIdOptional) {
        EventConstraint.EventIDRange eventIDRange = new EventConstraint.EventIDRange();
        lastEventIdOptional.ifPresent(eventIDRange::setFromExclusive);
        EventFlowFilter eventFlowFilter = new EventFlowFilter(new EventConstraint(eventIDRange));
        return new DefaultSubscriberConfig(eventFlowFilter);
    }

}
