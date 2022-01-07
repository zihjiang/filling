package com.filling.calculation.plugin.base.flink.source.stream;

import io.pravega.client.ClientConfig;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.admin.ReaderGroupManager;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.*;
import io.pravega.client.stream.impl.UTF8StringSerializer;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

public class temp1 {
    public static void main(String[] args) throws Exception {


        createReaderGroup("tcp://10.10.14.210:9090", "random", "mystream", "mygroup");
        EventStreamReader<String> reader = createReader("tcp://10.10.14.210:9090", "random", "mygroup", "myreader");
        readData(reader);
    }


    public static EventStreamReader<String> createReader(String url, String scope, String groupName, String readerName) throws Exception {
        URI controllerURI = new URI(url);
        ClientConfig clientConfig = ClientConfig.builder().controllerURI(controllerURI).build();

        EventStreamClientFactory clientFactory = EventStreamClientFactory.withScope(scope, clientConfig);

        ReaderConfig readerConfig = ReaderConfig.builder().build();
        EventStreamReader<String> reader = clientFactory.createReader(readerName, groupName, new UTF8StringSerializer(), readerConfig);

        return reader;
    }


    public static void createReaderGroup(String url, String scope, String stream, String groupName) throws Exception {
        URI controllerURI = new URI(url);
        ClientConfig clientConfig = ClientConfig.builder().controllerURI(controllerURI).build();

        ReaderGroupManager readerGroupManager = ReaderGroupManager.withScope(scope, clientConfig);

        ReaderGroupConfig readerGroupConfig = ReaderGroupConfig.builder().stream(scope + "/" + stream).build();

        readerGroupManager.createReaderGroup(groupName, readerGroupConfig);
    }


    public static void readData(EventStreamReader<String> reader) {
        while (true) {
            String event = reader.readNextEvent(1000).getEvent();
//            if (event == null) {
//                System.out.println("No more event");
//                break;
//            }
            System.out.println("Received Event: " + event);
        }
//        reader.close();

    }


    public static void createStream(String url, String scope, String stream) throws Exception {
        URI controllerURI = new URI(url);
        StreamManager streamManager = StreamManager.create(controllerURI);
        streamManager.createScope(scope);
        StreamConfiguration config = StreamConfiguration.builder().build();
        streamManager.createStream(scope, stream, config);

        streamManager.close();
    }


    public static void writeDate(EventStreamWriter<String> eventWriter, String message) throws Exception {
        CompletableFuture<Void> future = eventWriter.writeEvent(message);
        future.get();
        eventWriter.close();
    }

    public static EventStreamWriter<String> getWriter(String url, String scope, String stream) throws Exception {
        URI controllerURI = new URI(url);
        ClientConfig clientConfig = ClientConfig.builder().controllerURI(controllerURI).build();

        EventStreamClientFactory clientFactory = EventStreamClientFactory.withScope(scope, clientConfig);

        EventWriterConfig writerConfig = EventWriterConfig.builder().build();

        EventStreamWriter<String> eventWriter = clientFactory.createEventWriter(stream, new UTF8StringSerializer(), writerConfig);

        return eventWriter;
    }
}
