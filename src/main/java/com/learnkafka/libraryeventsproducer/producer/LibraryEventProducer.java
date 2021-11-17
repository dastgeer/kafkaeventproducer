package com.learnkafka.libraryeventsproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.libraryeventsproducer.domains.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


@Component
@Slf4j
public class LibraryEventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public static final String TOPIC_NAME="library-events";

    public void sendLibraryEventAsynchronous(LibraryEvent libraryEvent) throws JsonProcessingException {

        Integer key = libraryEvent.getLibraryEventId();
        String valueData = objectMapper.writeValueAsString(libraryEvent);

       ListenableFuture<SendResult<Integer,String>> listenableFuture = kafkaTemplate.sendDefault(key,valueData);
       listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
           @Override
           public void onFailure(Throwable ex) {
               handleFailure(key,valueData,ex);
           }

           @Override
           public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key,valueData,result);
           }
       });

    }

    public void sendLibraryEventAsynchronous_withSendOnly(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getLibraryEventId();
        String valueData = objectMapper.writeValueAsString(libraryEvent);
        List<RecordHeader> recordHeaders= new ArrayList();
        recordHeaders.add(new RecordHeader("library-event","library-type".getBytes()));
        ProducerRecord<Integer,String> producerRecord = buildProducerRecord(key,valueData,TOPIC_NAME,recordHeaders);
        ListenableFuture<SendResult<Integer,String>> sendFuture =  kafkaTemplate.send(producerRecord);
        sendFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(key,valueData,ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key,valueData,result);
            }
        });
    }

    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String valueData,String topicName,Iterable headers) {
        return new ProducerRecord<Integer,String>(topicName,null,key,valueData,headers);
    }

    public SendResult<Integer, String> sendLibraryEventSynchrouns(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getLibraryEventId();
        String valueData = objectMapper.writeValueAsString(libraryEvent);
        SendResult<Integer,String> sendResult= null;
        try {
            sendResult = (SendResult<Integer, String>) kafkaTemplate.sendDefault(key, valueData).get();
        } catch (InterruptedException | ExecutionException e) {
            log.info("InterruptedException/ExecutionException occured while send message to kafka ",e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Exception occured while send message to kafka ",e.getMessage());
        }
        return sendResult;
    }

    private void handleSuccess(Integer key, String valueData, SendResult<Integer, String> result) {
        log.info(" message sent successfully for the key :{} value :{} partition :{}",key,valueData,result.getRecordMetadata().partition());
    }

    private void handleFailure(Integer key, String valueData, Throwable ex) {
        log.error(" message sent failed exception is ",ex.getMessage());
        try {
            throw ex;
        } catch (Throwable throwable) {
            log.error(" error on failure ",throwable.getMessage());
            throwable.printStackTrace();
        }
    }
}
