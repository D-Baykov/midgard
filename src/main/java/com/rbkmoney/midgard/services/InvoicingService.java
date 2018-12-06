package com.rbkmoney.midgard.services;

import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.midgard.exception.DaoException;
import com.rbkmoney.midgard.pollers.event_sink.invoices.AbstractInvoicingServiceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InvoicingService implements EventService<Event, EventPayload> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final List<AbstractInvoicingServiceHandler> invoicingHandlers;

    public InvoicingService(List<AbstractInvoicingServiceHandler> invoicingHandlers) {
        this.invoicingHandlers = invoicingHandlers;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(Event processingEvent, EventPayload payload) {
        if (payload.isSetInvoiceChanges()) {
            payload.getInvoiceChanges().forEach(cc -> invoicingHandlers.forEach(ph -> {
                if (ph.accept(cc)) {
                    ph.handle(cc, processingEvent);
                }
            }));
        }
    }

    @Override
    public Optional<Long> getLastEventId() throws DaoException {
            Optional<Long> lastEventId = null;/*Optional.ofNullable(invoiceDao.getLastEventId());
        log.info("Last invoicing eventId={}", lastEventId);*/
        return lastEventId;
    }
}
