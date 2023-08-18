package com.example.librarymanagementsystem.Controllers;


import com.example.librarymanagementsystem.Enums.CardStatus;
import com.example.librarymanagementsystem.Models.LibraryCard;
import com.example.librarymanagementsystem.Services.LibraryCardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/card")
@Slf4j
public class CardController {

    @Autowired
    private LibraryCardService cardService;


    @PostMapping("/create")
    public String addCard(@RequestBody LibraryCard card){

        return cardService.addCard(card);

    }

    @PutMapping("/issueToStudent")
    public ResponseEntity issueToStudent(@RequestParam("cardId")Integer cardId,@RequestParam("rollNo")Integer rollNo){

        try{

            String result =  cardService.associateToStudent(cardId,rollNo);
            return new ResponseEntity(result,HttpStatus.OK);
        }catch (Exception e){
            log.error("Error in associating card to student",e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }

    }
    @GetMapping("/get-Card-status-by-studentId")
    public ResponseEntity<CardStatus> getCardStatusByStudentId(@RequestParam("id") Integer studentId){
        try{
            CardStatus cardStatus =cardService.getCardStatusByStudentId(studentId);
            return new ResponseEntity<>(cardStatus,HttpStatus.ACCEPTED);
        }
        catch(Exception e)
        {
            log.error("Unable to Process Your Request {} "+e.getMessage());
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }

    }




}