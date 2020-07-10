package com.gavelier.gavelierplus;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.security.Principal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.gavelier.gavelierplus.domain.Auction;
import com.gavelier.gavelierplus.domain.Lot;

@Controller
public class Endpoints {

    @Autowired
    DynamoDBRepository dynamoDBRepository;


    private final static Logger LOGGER = Logger.getLogger(Pages.class.getName());

    @PostMapping("/createauction")
    public String greeting(Auction auction, BindingResult bindingResult) {


        LOGGER.info("called create auction");
        LOGGER.info("Binding errors =  " + bindingResult.hasErrors());

        List<FieldError> errorFields = bindingResult.getFieldErrors();

        errorFields.forEach(ef -> {
            LOGGER.info("Unable to bind: " + ef.getField());
        });

        dynamoDBRepository.insertIntoDynamoDB(auction);

        LOGGER.info("NAME: " + auction.getInputCompanyName());
        LOGGER.info("DEC: " + auction.toString());

        return "newauction";

    }

    @PostMapping("/createlot")
    public String createLot(Lot lot, BindingResult bindingResult, Model model, java.security.Principal principal) {


        LOGGER.info("called create lot");
        LOGGER.info("Binding errors =  " + bindingResult.hasErrors());
        LOGGER.info("lot =  " + lot.toString());

        List<FieldError> errorFields = bindingResult.getFieldErrors();

        errorFields.forEach(ef -> {
            LOGGER.info("Unable to bind: " + ef.getField());
        });

        dynamoDBRepository.createLot(lot);


        String currentAuctionId = "7a33721d-c0e8-4279-b5f7-ddcd861eaa64";

        Auction currentAuction = dynamoDBRepository.getOneAuctionById(currentAuctionId, principal.getName());

        model.addAttribute("name", principal.getName());
        model.addAttribute("currentAuctionId", currentAuctionId);
        model.addAttribute("lotsForCurrentAuction", dynamoDBRepository.getAllLotsForAuction(currentAuctionId));
        model.addAttribute("allAuctionsForUser", getAllAuctionsForUserInDateOrder(principal.getName()));
        

        return "lots";
        
    }

    public List<Auction> getAllAuctionsForUserInDateOrder(String userId) {


        List<Auction> allAuctionsForUser = dynamoDBRepository.allAuctionsForUserId(userId);


        return allAuctionsForUser.stream().distinct().sorted(new Comparator<Auction>() {
            public int compare(Auction o1, Auction o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
          })
        .collect(Collectors.toList());



    }


}