package com.cloudurable.kafka;

public class Massebehandling {
    public enum MbStatus {
        NY("NY"),
        KLAR("KLAR"),
        FEILET("FEILET"),
        PARKERT ("PARKERT");

        public final String label;

        private MbStatus(String label) {
            this.label = label;
        }
    }
    public enum MbTopics {
        ARBEIDSOPPGAVE_TIL_SAKSBEHANDLER("SKATTEMELDING_ARBEIDSOPPGAVE_TIL_SAKSBEHANDLER"),
        KONTROLL ("SKATTEMELDING_KONTROLL"),
        SKATTEMELDING_UTKAST ("SKATTEMELDING_UTKAST"),
        SKATTEMELDING_PUBLISERING ("SKATTEMELDING_PUBLISERING"),
        LEVERINGSFRITAK("SKATTEMELDING_LEVERINGSFRITAK"),
        my_example_topic("my-example-topic");

        public final String label;

        private MbTopics(String label) {
            this.label = label;
        }
    }
}
