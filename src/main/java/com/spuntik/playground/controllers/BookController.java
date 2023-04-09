package com.spuntik.playground.controllers;

import com.spuntik.playground.entities.Book;
import com.spuntik.playground.model.GenericResponse;
import com.spuntik.playground.model.Item;
import com.spuntik.playground.services.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create/book")
    public Book createTodo(@RequestBody Book book) {
        return this.service.saveBook(book);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/fetch/books")
    public List<Book> createTodo() {
        return this.service.getBooks();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/purchase/book/{bookId}")
    public GenericResponse purchaseBook(@PathVariable("bookId") String bookId) {

        try {
            return this.service.purchaseBook(bookId);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/item")
    public GenericResponse getItem() {


        return this.service.getItem();

    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/item")
    public GenericResponse createItem(@RequestBody Item item) {

        log.info("THE REQUEST {}", item);
        return this.service.createItem(item);

    }
}