import com.rbkmoney.midgard.data.ClearingData;
import com.rbkmoney.midgard.utils.MtsXmlUtil;
import org.jooq.generated.enums.TransactionClearingState;
import org.jooq.generated.tables.pojos.ClearingTransaction;
import org.jooq.generated.tables.pojos.Merchant;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MtsXmlUtilTest {

    @Test
    public void createXml() {
        String xml = MtsXmlUtil.createXml(getClearingData());

        //TODO: Это чисто для отладки. Полноценные тесты будут написаны позднее
        System.out.println("XML: " + xml);
    }

    private ClearingData getClearingData() {
        return new ClearingData(getClearingTransactions(), getMerchants());
    }

    private List<ClearingTransaction> getClearingTransactions() {
        List<ClearingTransaction> transactions = new ArrayList<>();
        transactions.add(getClearingTransaction());
        return transactions;
    }

    private ClearingTransaction getClearingTransaction() {
        ClearingTransaction transaction = new ClearingTransaction();
        transaction.setInvoiceId("inv_1");
        transaction.setDocId("doc_1");
        transaction.setTransactionId("tran_1");
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionAmount(100L);
        transaction.setTransactionCurrency("RUB");
        transaction.setTransactionType("00");
        transaction.setBankName("MTS");
        transaction.setTransactionClearingState(TransactionClearingState.READY);
        transaction.setAccountTypeFrom("00");
        transaction.setAccountTypeTo("00");
        transaction.setApprovalCode("appr_code_none");
        transaction.setCardCaptureCapability("0");
        transaction.setCardDataInputCapability("0");
        transaction.setCardDataInputMode("J");
        transaction.setCardDataOutputCapability("0");
        transaction.setCardPresence("5");
        transaction.setCardholderAuthCapability("0");
        transaction.setCardholderAuthEntity("0");
        transaction.setCardholderAuthMethod("0");
        transaction.setCardholderPresence("5");
        transaction.setECommerceSecurityLevel(null);
        transaction.setMcc(5734);
        transaction.setMerchantId("29003001");
        transaction.setMessageFunctionCode(200);
        transaction.setMessageReasonCode(1508);
        transaction.setMessageTypeIdentifier(1100);
        transaction.setOperationalEnvironment(null);
        transaction.setPinCaptureCapability(null);
        transaction.setTerminalDataOutputCapability(null);
        transaction.setTerminalId("29003001");
        transaction.setRrn(null);
        transaction.setResponseCode(null);
        transaction.setSystemTraceAuditNumber(null);

        return transaction;
    }

    private List<Merchant> getMerchants() {
        List<Merchant> merchants = new ArrayList<>();
        merchants.add(getMerchant());
        return merchants;
    }

    private Merchant getMerchant() {
        Merchant merchant = new Merchant();
        merchant.setMerchantId("29003001");
        merchant.setMerchantName("231285*EPS");
        merchant.setMerchantAddress("some address");
        merchant.setMerchantCity("643");
        merchant.setMerchantCountry("Moscow");
        merchant.setMerchantPostalCode("117112");
        return merchant;
    }

}
