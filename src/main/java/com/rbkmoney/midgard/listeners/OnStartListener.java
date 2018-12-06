package com.rbkmoney.midgard.listeners;

import com.rbkmoney.eventstock.client.DefaultSubscriberConfig;
import com.rbkmoney.eventstock.client.EventConstraint;
import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.SubscriberConfig;
import com.rbkmoney.eventstock.client.poll.EventFlowFilter;
import com.rbkmoney.midgard.services.PartyManagementService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OnStartListener implements ApplicationListener<ApplicationReadyEvent> {

    private final EventPublisher partyManagementEventPublisher;

    private final PartyManagementService partyManagementService;

    @Value("${bm.pollingEnabled}")
    private boolean pollingEnabled;

    public OnStartListener(EventPublisher partyManagementEventPublisher,
                           PartyManagementService partyManagementService) {
        this.partyManagementEventPublisher = partyManagementEventPublisher;
        this.partyManagementService = partyManagementService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (pollingEnabled) {
            partyManagementEventPublisher.subscribe(buildSubscriberConfig(partyManagementService.getLastEventId()));
        }
    }

    private SubscriberConfig buildSubscriberConfig(Optional<Long> lastEventIdOptional) {
        EventConstraint.EventIDRange eventIDRange = new EventConstraint.EventIDRange();
        lastEventIdOptional.ifPresent(eventIDRange::setFromExclusive);
        EventFlowFilter eventFlowFilter = new EventFlowFilter(new EventConstraint(eventIDRange));
        return new DefaultSubscriberConfig(eventFlowFilter);
    }

}
