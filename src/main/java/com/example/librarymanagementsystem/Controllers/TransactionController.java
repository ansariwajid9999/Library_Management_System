package com.example.librarymanagementsystem.Controllers;


import com.example.librarymanagementsystem.Models.Student;
import com.example.librarymanagementsystem.Services.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@Slf4j
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/issueBook")
    public ResponseEntity issueBook(@RequestParam("bookId")Integer bookId,@RequestParam("cardId")Integer cardId){

        try{
            String result = transactionService.issueBook(bookId,cardId);
            return new ResponseEntity(result,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/returnBook")
    public ResponseEntity returnBook(@RequestParam("bookId")Integer bookId,@RequestParam("cardId")Integer cardId){
        try{
            String result = transactionService.returnBook(bookId,cardId);
            return new ResponseEntity(result,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/ totalFineCollected")
    public ResponseEntity totalFineCollected(){

        try{
            Integer Amount = transactionService.totalFineCollected();
            return new ResponseEntity(Amount,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getStudentWhoReadMoreBooks")
    public ResponseEntity getStudentWhoReadMoreBookds(){
        try{
            Student student=transactionService.findWhoReadMoreBooks();
            return new ResponseEntity(student,HttpStatus.OK);
        }
        catch (Exception e){
            log.error("Oops Something Went Wrong {}"+e.getMessage());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }



}