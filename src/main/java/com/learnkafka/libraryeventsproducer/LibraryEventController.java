package com.learnkafka.libraryeventsproducer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.libraryeventsproducer.domains.LibraryEvent;
import com.learnkafka.libraryeventsproducer.domains.LibraryEventType;
import com.learnkafka.libraryeventsproducer.producer.LibraryEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
public class LibraryEventController {

    @Autowired
    private LibraryEventProducer libraryEventProducer;

    @PostMapping(path = "/v1/libraryEvent")
    public ResponseEntity<?> postLibraryEvent(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {
        //library event have to pass to kafka producer
        //libraryEventProducer.sendLibraryEventAsynchronous(libraryEvent);
        //SendResult<Integer, String> sendResult =  libraryEventProducer.sendLibraryEventSynchrouns(libraryEvent);
        //log.info("printing log after getting result--->",sendResult.toString());
        //System.out.println(sendResult.toString());
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        libraryEventProducer.sendLibraryEventAsynchronous_withSendOnly(libraryEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PutMapping(path = "/v1/libraryEvent")
    public ResponseEntity<?> putLibraryEvent(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {
        //library event have to pass to kafka producer
        //libraryEventProducer.sendLibraryEventAsynchronous(libraryEvent);
        //SendResult<Integer, String> sendResult =  libraryEventProducer.sendLibraryEventSynchrouns(libraryEvent);
        //log.info("printing log after getting result--->",sendResult.toString());
        //System.out.println(sendResult.toString());
        if(libraryEvent.getLibraryEventId()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("event id should not be empty");
        }
        libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
        libraryEventProducer.sendLibraryEventAsynchronous_withSendOnly(libraryEvent);
        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }
}
