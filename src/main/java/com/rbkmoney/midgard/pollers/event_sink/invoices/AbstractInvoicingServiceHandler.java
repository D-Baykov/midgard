package com.rbkmoney.midgard.pollers.event_sink.invoices;

import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.midgard.handlers.ServiceHandler;

public abstract class AbstractInvoicingServiceHandler implements ServiceHandler<InvoiceChange, Event> {
}
