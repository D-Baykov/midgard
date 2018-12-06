package com.rbkmoney.midgard.data;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MtsXmlHeader {

    /**  */
    private static final String FORMAT_VERSION = "2";
    /**  */
    private String fileOriginator;
    /**  */
    private int fileNumber;

    public MtsXmlHeader(String fileOriginator, int fileNumber) {
        this.fileOriginator = fileOriginator;
        this.fileNumber = fileNumber;
    }

    public static String getFormatVersion() {
        return FORMAT_VERSION;
    }

    public String getFileOriginator() {
        return fileOriginator;
    }

    public String getFileId() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return "rbcm" + dateFormat.format(new Date()) + "_" + fileNumber;
    }

    public String getFileDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date());
    }

}