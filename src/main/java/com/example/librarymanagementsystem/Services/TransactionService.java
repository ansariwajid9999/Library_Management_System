package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.CustomExceptions.BookNotAvailableException;
import com.example.librarymanagementsystem.CustomExceptions.BookNotFoundException;
import com.example.librarymanagementsystem.Enums.CardStatus;
import com.example.librarymanagementsystem.Enums.TransactionStatus;
import com.example.librarymanagementsystem.Enums.TransactionType;
import com.example.librarymanagementsystem.Models.Book;
import com.example.librarymanagementsystem.Models.LibraryCard;
import com.example.librarymanagementsystem.Models.Student;
import com.example.librarymanagementsystem.Models.Transaction;
import com.example.librarymanagementsystem.Repositories.AuthorRepository;
import com.example.librarymanagementsystem.Repositories.BookRepository;
import com.example.librarymanagementsystem.Repositories.CardRepository;
import com.example.librarymanagementsystem.Repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.example.librarymanagementsystem.Enums.TransactionStatus.SUCCESS;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;


    @Value("${book.maxLimit}")
    private Integer maxBookLimit;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CardRepository cardRepository;

    public static final Integer bookLimit = 6;


    public String issueBook(Integer bookId,Integer cardId)throws Exception{

        //Book Related Exception Handling
        Transaction transaction = new Transaction(TransactionStatus.PENDING, TransactionType.ISSUE,0);

        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if(!optionalBook.isPresent()){
            throw new BookNotFoundException("Book Id is incorrect");
        }
        Book book = optionalBook.get();
        if(book.getIsAvailable()==Boolean.FALSE){
            throw new BookNotAvailableException("Book is not Avaialble");
        }

        //Card Related Exception Handling
        Optional<LibraryCard> optionalLibraryCard = cardRepository.findById(cardId);

        if(!optionalLibraryCard.isPresent()){
            throw new Exception("Card Id entered is Invalid");
        }

        LibraryCard card = optionalLibraryCard.get();
        if(!card.getCardStatus().equals(CardStatus.ACTIVE)){

            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transaction = transactionRepository.save(transaction);

            throw new Exception("Card is not in Right status");
        }
        if(card.getNoOfBooksIssued()>=bookLimit){

            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transaction = transactionRepository.save(transaction);
            throw new Exception("Already max Limit Books are issued");
        }

        /*  All the failed cases and invalid scenarios are over */

        //We have reached at a success point

        transaction.setTransactionStatus(SUCCESS);

        //update the card and book Entity
        book.setIsAvailable(Boolean.FALSE);
        card.setNoOfBooksIssued(card.getNoOfBooksIssued()+1);

        //We need to do unidirectional mappings :-->
        transaction.setBook(book);
        transaction.setLibraryCard(card);

        Transaction newTransactionWithId = transactionRepository.save(transaction);

        //We need to do in the parent classes
        book.getTransactionList().add(newTransactionWithId);

        card.getTransactionList().add(newTransactionWithId);

        bookRepository.save(book);
        cardRepository.save(card);

        //What all needs to saved
        return "Transaction has been saved successfully";

    }
    public String returnBook(Integer bookId,Integer cardId){

        Book book = bookRepository.findById(bookId).get();
        LibraryCard card = cardRepository.findById(cardId).get();


        List<Transaction> transactionList = transactionRepository.findTransactionsByBookAndLibraryCardAndTransactionStatusAndTransactionType(book,card, SUCCESS,TransactionType.ISSUE);

        Transaction latestTransaction = transactionList.get(transactionList.size()-1);

        Date issueDate = latestTransaction.getCreatedAt();

        long milliSecondTime = Math.abs(System.currentTimeMillis() - issueDate.getTime());
        long no_of_days_issued = TimeUnit.DAYS.convert(milliSecondTime,TimeUnit.MILLISECONDS);


        int fineAmount = 0;
        if(no_of_days_issued>15){
            fineAmount = (int) ((no_of_days_issued - 15)*5);
        }

        book.setIsAvailable(Boolean.TRUE);
        card.setNoOfBooksIssued(card.getNoOfBooksIssued()-1);

        Transaction transaction = new Transaction(SUCCESS,TransactionType.RETURN,fineAmount);

        transaction.setBook(book);
        transaction.setLibraryCard(card);


        Transaction newTransactionWithId = transactionRepository.save(transaction);

        book.getTransactionList().add(newTransactionWithId);
        card.getTransactionList().add(newTransactionWithId);

        //Saving the parents
        bookRepository.save(book);
        cardRepository.save(card);

        return "Book has successfully been returned";
    }

    public int numberOfBooksRead(Student student){
        List<Transaction>transactions=transactionRepository.findByLibraryCard_Student(student);
        Set<Book> bookSet=new HashSet<>();
        for(Transaction transaction:transactions){
            if(transaction.getTransactionType().equals(TransactionType.ISSUE) && transaction.getTransactionStatus().equals(SUCCESS)){
                bookSet.add(transaction.getBook());
            }
        }
        return bookSet.size();
    }
}

    public Integer totalFineCollected()throws Exception{
        List<Transaction> transactionList = transactionRepository.findAll();
        if(transactionList==null || transactionList.isEmpty()){
            throw new Exception("There are no transactions Present");
        }
        int cnt=0;
        for(Transaction transaction:transactionList) {
            Date date=transaction.getCreatedAt();
            Integer year=date.getYear()+1900;
            if(year.equals(2023)){
                cnt+= transaction.getFineAmount();
            }
        }
        return cnt;
    }


}