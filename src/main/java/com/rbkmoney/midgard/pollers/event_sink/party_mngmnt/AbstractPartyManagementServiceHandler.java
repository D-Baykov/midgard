package com.rbkmoney.midgard.pollers.event_sink.party_mngmnt;

import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.midgard.handlers.ServiceHandler;

public abstract class AbstractPartyManagementServiceHandler implements ServiceHandler<PartyChange, Event> {
}
