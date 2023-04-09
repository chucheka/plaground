package com.spuntik.playground.services;

import com.spuntik.playground.dao.BookRepository;
import com.spuntik.playground.entities.Book;
import com.spuntik.playground.model.GenericResponse;
import com.spuntik.playground.model.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {
    private final WebClient webClient;
    public final BookRepository bookRepository;

    public Book saveBook(Book book) {
        return this.bookRepository.save(book);
    }

    public List<Book> getBooks() {
        return this.bookRepository.findAll();
    }

    public GenericResponse purchaseBook(String id) throws URISyntaxException {

        Book sample = new Book();

        sample.setAuthor("Chinue Achebe");
        sample.setYear(1991);
        sample.setTitle("Chike And The River");
        sample.setIsbn(UUID.randomUUID().toString());
        sample.setId(Long.valueOf(id));

        Book book = bookRepository.findById(Long.valueOf(id)).orElse(sample);


        ResponseEntity<String> response = webClient.get()
                .uri("/amazon/purchase")
//                .body(Mono.just(new PaymentDetails()), PaymentDetails.class)
                .retrieve()
                .toEntity(String.class)
                .block();

        GenericResponse result = new GenericResponse<String>();

        result.setMessage("Book purchase successful");
        result.setStatus("SUCCESSFUL");
        result.setData(response.getBody());

        return result;


    }

    public GenericResponse getItem() {

        ResponseEntity<Item> response = webClient
                .get()
                .uri("/amazon/item")
//                .body(Mono.just(new PaymentDetails()), PaymentDetails.class)
                .retrieve()
                .toEntity(Item.class)
                .block();

        GenericResponse result = new GenericResponse<Item>();

        log.info("THE ITEM {}",response.getBody());

        result.setMessage("Book purchase successful");
        result.setStatus("SUCCESSFUL");
        result.setData(response.getBody());

        return result;

    }

    public GenericResponse createItem(Item item) {

        ResponseEntity<Item> response = webClient
                .post()
                .uri("/amazon/item")
                .body(Mono.just(item), Item.class)
                .retrieve()
                .toEntity(Item.class)
                .block();

        GenericResponse result = new GenericResponse<Item>();

        log.info("THE ITEM {}",response.getBody());

        result.setMessage("Item created successfully");
        result.setStatus("SUCCESSFUL");
        result.setData(response.getBody());

        return result;
    }
}