package com.coding.challenge.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class UploadProgressEventProcessor implements ApplicationListener<UploadProgressEvent> {

    private final Map<String, FluxSink<String>> sinks = new ConcurrentHashMap<>();

    public Flux<String> start(String id) {
        return Flux.create((FluxSink<String> fluxSink) -> sinks.put(id, fluxSink)).share();
    }

    @Override
    public void onApplicationEvent(UploadProgressEvent uploadProgressEvent) {
        publish(uploadProgressEvent.getRequestId(), uploadProgressEvent.getSource().toString());
        if (uploadProgressEvent.getComplete()) {
            if (sinks.containsKey(uploadProgressEvent.getRequestId())) {
                sinks.get(uploadProgressEvent.getRequestId()).complete();
                sinks.remove(uploadProgressEvent.getRequestId());
            }
        }
    }

    private void publish(String id, String item) {
        if (sinks.containsKey(id)) {
            sinks.get(id).next(item);
        } else {
            log.debug("No subscriber to status of upload file : [RequestId={}] [Status={}]", id, item);
        }
    }
}
