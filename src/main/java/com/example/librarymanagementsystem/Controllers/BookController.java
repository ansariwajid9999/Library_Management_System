package com.example.librarymanagementsystem.Controllers;


import com.example.librarymanagementsystem.Enums.Genre;
import com.example.librarymanagementsystem.Models.Book;
import com.example.librarymanagementsystem.Repositories.BookRepository;
import com.example.librarymanagementsystem.RequestDto.AddBookRequestDto;
import com.example.librarymanagementsystem.ResponseDto.AddBookResponseDto;
import com.example.librarymanagementsystem.ResponseDto.BookResponseDto;
import com.example.librarymanagementsystem.ResponseDto.bookContainMostFineAmount;
import com.example.librarymanagementsystem.Services.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book")
@Slf4j
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @PostMapping("/add")
    public ResponseEntity addBook(@RequestBody AddBookRequestDto addBookRequestDto){

        try{
            String result = bookService.addBook(addBookRequestDto);
            return new ResponseEntity(result,HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/getByGenre")
    public ResponseEntity getBookListByGenre(@RequestParam("genre")Genre genre){
        List<BookResponseDto> responseDtoList = bookService.getBookListByGenre(genre);
        return new ResponseEntity(responseDtoList,HttpStatus.OK);
    }
    @GetMapping("/countBook-By-Genre/{genre}")
    public ResponseEntity<Integer>countOfBook(@PathVariable("genre") Genre genre) {
        try{
            Integer count = bookService.countOfBooksOfGenre(genre);
            return new ResponseEntity<>(count,HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(0,HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/get-book-amt")
    public bookContainMostFineAmount findBookFine(){
        try{
            bookContainMostFineAmount getMaxFineBookDto= bookService.findMaxBook();
            return getMaxFineBookDto;
        }
        catch (Exception e){
            log.error("Unable to Proceed Your Reqeust{}"+e.getMessage());
            return new bookContainMostFineAmount();
        }
    }


}