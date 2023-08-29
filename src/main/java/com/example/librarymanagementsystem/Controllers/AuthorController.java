package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.Models.Author;
import com.example.librarymanagementsystem.Models.Book;
import com.example.librarymanagementsystem.RequestDto.UpdateNameAndPenNameRequest;
import com.example.librarymanagementsystem.Services.AuthorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/author")
@Slf4j
public class AuthorController {
    @Autowired
    private AuthorService authorService;


    @PostMapping("/add")
    public ResponseEntity addAuthor(@RequestBody Author author){

        try{

            String result = authorService.addAuthor(author);
            return new ResponseEntity(result,HttpStatus.OK);

        }catch (Exception e){
            log.error("Author couldnt be added to the db {}",e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/updateNameAndPenName")
    public String updateAuthorNameAndPenName(@RequestBody UpdateNameAndPenNameRequest updateNameAndPenNameRequest){


        //@RequestBody Author author

        //1 endpoint has become long
        //Exposed in the URL itself

        try{
            String result = authorService.updateNameAndPenName(updateNameAndPenNameRequest);
            return result;

        }catch (Exception e){
            return "Author Id is invalid"+e.getMessage();
        }
    }

    @GetMapping("/getAuthor")
    public Author getAuthor(@RequestParam("authorId")Integer authorId){
        return authorService.getAuthor(authorId);
    }
    @GetMapping("/get-book-list-by-authorId/{id}")
    private ResponseEntity<List<Book>>bookList(@PathVariable("id") Integer authorId){
        try{
            List<Book> ans = authorService.getListOfBooks(authorId);
            return new ResponseEntity<>(ans, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity(new ArrayList<>(),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-author-by-author-id")
    public ResponseEntity<Author>getAuthorByAuthorId(@RequestParam("id") Integer authorId){
        try{
            Author author=authorService.getAuthorByAuthorId(authorId);
            return new ResponseEntity(author,HttpStatus.ACCEPTED);
        }
        catch (Exception e){
            return new ResponseEntity(null,HttpStatus.BAD_REQUEST);
        }
    }

}