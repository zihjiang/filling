package com.filling.calculation.plugin.base.flink.source.stream;

import io.pravega.client.ClientConfig;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.EventStreamWriter;
import io.pravega.client.stream.EventWriterConfig;
import io.pravega.client.stream.StreamConfiguration;
import io.pravega.client.stream.impl.UTF8StringSerializer;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

public class temp0 {
    public static void main(String[] args) throws Exception {
        createStream("tcp://10.10.14.210:9090", "random", "mystream");
        EventStreamWriter<String> writer = getWriter("tcp://10.10.14.210:9090", "random", "mystream");

        writeDate(writer, "Hello World!");
        System.out.println("Write event successfully!");
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
