package com.cloudurable.kafka;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;

public class KafkaMelding {
    private String status;
    private String jobbId;
    private String beskrivelse;
    private String tid;
    private Integer index;
    private String key;

    public String serializeObject2Json() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        //DateFormat df = new SimpleDateFormat("yyyy.MM.dd hh24:mm:ss:ssss");
        //objectMapper.setDateFormat(df);
        String meldingAsString = objectMapper.writeValueAsString(this);
        return meldingAsString;
    }

    public KafkaMelding serializeJson2Object(String jsonMsg) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        KafkaMelding msg = objectMapper.readValue(jsonMsg, KafkaMelding.class);
        return msg;
    }

    public KafkaMelding(String status, String jobbId, String beskrivelse, Integer index,
        String key, String debugInfo) {
        this.status = status;
        this.jobbId = jobbId;
        this.beskrivelse = beskrivelse;
        //this.tid = tid;
        this.index = index;
        this.key = key;
        this.debugInfo = debugInfo;

        final ZonedDateTime now = ZonedDateTime.now();
        this.tid = now.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public enum MbStatus {
        NY("NY"),
        KLAR("KLAR"),
        FEILET("FEILET"),
        PARKERT("PARKERT");

        public final String label;

        private MbStatus(String label) {
            this.label = label;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KafkaMelding)) {
            return false;
        }
        KafkaMelding that = (KafkaMelding) o;
        return status.equals(that.status) &&
            jobbId.equals(that.jobbId) &&
            beskrivelse.equals(that.beskrivelse) &&
            tid.equals(that.tid) &&
            index.equals(that.index) &&
            key.equals(that.key) &&
            Objects.equals(debugInfo, that.debugInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, jobbId, beskrivelse, tid, index, key, debugInfo);
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    private String debugInfo;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setJobbId(String jobbId) {
        this.jobbId = jobbId;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setDebugInfo(String debugInfo) {
        this.debugInfo = debugInfo;
    }

    public String getJobbId() {
        return jobbId;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public Integer getIndex() {
        return index;
    }

    public String getKey() {
        return key;
    }

    public String getDebugInfo() {
        return debugInfo;
    }

    public String getTid() {
        return tid;
    }

}
