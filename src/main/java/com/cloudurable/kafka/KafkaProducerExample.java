
package com.cloudurable.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class KafkaProducerExample {

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

    private final static String TOPIC = MbTopics.my_example_topic.label;
    //private final static String TOPIC = "my-example-topic";
    private final static String BOOTSTRAP_SERVERS =
        "localhost:9092,localhost:9093,localhost:9094";

    public static void main(String... args) throws Exception {
        List<String> jobbList = Arrays.asList(new String[]{"11111111111", "22222222222", "33333333333", "44", "55"});
        
        runProducerSync(jobbList, 2020, "JobbID_1001");
        //runProducerAsync (jobbList, 2020, "JobbID_1001");

    }


    private static Producer<String, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
        //props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        //    LongSerializer.class.getName());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class.getName());
        // Batching, denne skal benyttes ved oprettelse av mange jobber, dvs alltid?
        props.put(ProducerConfig.BATCH_SIZE_CONFIG,1024);
        //props.put(ProducerConfig.LINGER_MS_CONFIG,500);  // Set denne i async batch mode

        return new KafkaProducer<>(props);
    }

    static void runProducerSync(List<String> jobbListe, Integer inntektsaar, String jobbId) throws Exception {
        final Producer<String, String> producer = createProducer();
        long time = System.currentTimeMillis();

        try {
            for (int rep=0; rep < 100; rep++) {
                for (int i = 0; i < jobbListe.size(); i++) {
                    String timeMs = setTime2String();
                    final ZonedDateTime now = ZonedDateTime.now();

                    final String key = jobbListe.get(i) + ":" + inntektsaar + ":" + jobbId;
                    final KafkaMelding melding = new KafkaMelding(KafkaMelding.MbStatus.NY.label, jobbId, "beskrivelse:test"
                        , i, key, "bla bla debug..");

                    final String value = melding.serializeObject2Json();

                    /*final String value = "{value: { status:" + KafkaMelding.MbStatus.NY.label + " jobbid:" + jobbId + ", beskrivelse:test" +
                        ", tid:" + timeMs + ", tidms:" + System.currentTimeMillis() + ", index: " + i + ", key:" + key
                        + "}}";*/

                    final ProducerRecord<String, String> record =
                        new ProducerRecord<>(TOPIC, key, value);

                    RecordMetadata metadata = producer.send(record).get();

                    long elapsedTime = System.currentTimeMillis() - time;
                    System.out.printf("sent record(key=%s value=%s) " +
                            "meta(partition=%d, offset=%d) time=%d\n",
                        record.key(), record.value(), metadata.partition(),
                        metadata.offset(), elapsedTime);
                }
            }
        } finally {
            producer.flush();
            producer.close();
        }
    }


    static void runProducerAsync(List<String> jobbListe, Integer inntektsaar, String jobbId) throws Exception {
        final Producer<String, String> producer = createProducer();
        long time = System.currentTimeMillis();
        final int LOOPREP = 100;
        final CountDownLatch countDownLatch = new CountDownLatch(jobbListe.size()*LOOPREP);

        try {
            for (int rep=0; rep <LOOPREP; rep++) {
                for (int i = 0; i < jobbListe.size(); i++) {
                    String timeMs = setTime2String();
                    Integer antall = rep*jobbListe.size()+i;

                    final String key = jobbListe.get(i) + ":" + inntektsaar + ":" + jobbId;
                    final String value = "{value: { status:NY," + " jobbid:" + jobbId + ", beskrivelse:test" +
                        ", tid:" + timeMs + ", tidms:" + System.currentTimeMillis() + ", antall: " + antall + ", key:" + key
                        + "}}";

                    final ProducerRecord<String, String> record =
                        new ProducerRecord<>(TOPIC, key, value);

                    producer.send(record, (metadata, exception) -> {
                        // Dette kjører i bakgrunnen via en callback
                        long elapsedTime = System.currentTimeMillis() - time;
                        if (metadata != null) {
                            System.out.printf("sent record(key=%s value=%s) " +
                                    "meta(partition=%d, offset=%d) time=%d\n",
                                record.key(), record.value(), metadata.partition(),
                                metadata.offset(), elapsedTime);
                        } else {
                            exception.printStackTrace();
                        }
                        countDownLatch.countDown();
                    });
                }
            }
            countDownLatch.await(25, TimeUnit.SECONDS);
        }finally {
            producer.flush();
            producer.close();
        }
    }

    private static String  setTime2String () {
        // set timestamp
        Date date = new Date();
        //Specifying the format of the date using SimpleDateFormat
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss:ssss");
        String dateString = sdf.format(date);
        dateString = sdf.format(date);
        System.out.println("Date in the format of dd/MM/yyyy hh:mm:ss : "+dateString);

        return dateString;
    }
}
