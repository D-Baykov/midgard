package com.rbkmoney.midgard.utils;

import org.jooq.generated.tables.pojos.ClearingTransaction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.List;
import java.util.stream.Collectors;

public final class MtsXmlUtil {

    private static final String VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

    public static String createXML(String header, List<String> transactions) {
        StringBuilder builder = new StringBuilder();
        builder.append(VERSION);
        builder.append("<File>\n");
        builder.append(header).append("\n");

        builder.append("<Transactions>\n");
        builder.append(transactions.stream().collect(Collectors.joining("\n")));
        builder.append("</Transactions>\n");
        builder.append("</File>\n");
        return builder.toString();
    }


    public static Document createTransactionXmlByDOM(ClearingTransaction transaction)
            throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db  = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        Element root = doc.createElement("Transaction");

        Element apprCode = doc.createElement("ApprCode");
        //TODO: добавить в таблицы. Copy from Auth response' FLD_038
        apprCode.setTextContent("");
        root.appendChild(apprCode);

        Element cardCaptureCap = doc.createElement("CardCaptureCap");
        cardCaptureCap.setTextContent(transaction.getCardCaptureCapability());
        root.appendChild(cardCaptureCap);

        Element cardDataInputCap = doc.createElement("CardDataInputCap");
        cardDataInputCap.setTextContent(transaction.getCardDataInputCapability());
        root.appendChild(cardDataInputCap);

        Element cardDataInputMode = doc.createElement("CardDataInputMode");
        cardDataInputMode.setTextContent(transaction.getCardDataInputMode());
        root.appendChild(cardDataInputMode);

        doc.appendChild(root);
        return doc;
    }

    private MtsXmlUtil() {}

}
