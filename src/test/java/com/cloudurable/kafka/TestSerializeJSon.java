package com.cloudurable.kafka;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class TestSerializeJSon {

    @Test
    public void testUnmarshallingKafkaMeldingRequest() throws IOException {
        final ZonedDateTime now = ZonedDateTime.now();
        final KafkaMelding kafkaMelding1 =
            new KafkaMelding( KafkaMelding.MbStatus.NY.label, "1001", "bla bla", 1, "key:123", "test");

        String jsonMelding = kafkaMelding1.serializeObject2Json();

        String json = "{\"partsnummer\":1,\"partsnummerKorrespondansepart\":2,"
            + "\"skattemeldingOgNaeringsopplysninger\":{\"foo\":\"bar\"},\"meldingsID\":\"m123\","
            + "\"innsendingReferanse\":{\"referanseId\":\"r123\",\"tidspunktForInnsending\":\""
            + now.format(DateTimeFormatter.ISO_DATE_TIME)
            + "\"},\"sistGjeldendeDokumentId\":\"DOK:123\"}";

        final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

        final KafkaMelding kafkaMelding2 = objectMapper
            .readerFor(KafkaMelding.class)
            .readValue(jsonMelding);

        assert kafkaMelding2.equals(kafkaMelding1);

        String output = objectMapper.writeValueAsString(kafkaMelding2);
        System.err.println(output);

       /* final LagreSkattemeldingRequest req = objectMapper
            .readerFor(LagreSkattemeldingRequest.class)
            .readValue(json);

        assertThat(req).isEqualTo(
            new LagreSkattemeldingRequest(
                1,
                2,
                ImmutableMap.of("foo", "bar"),
                "m123",
                new InnsendingReferanse("r123", now),
                "DOK:123"));

        String output = objectMapper.writeValueAsString(req);

        System.err.println(output); */
    }
}
