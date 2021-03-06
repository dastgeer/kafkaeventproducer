package com.learnkafka.libraryeventsproducer.domains;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    private Integer bookId;
    private String bookName;
    private String authorName;

}
