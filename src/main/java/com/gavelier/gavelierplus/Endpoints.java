package com.gavelier.gavelierplus;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.List;
import java.util.logging.Logger;

import javax.validation.Valid;

import com.gavelier.gavelierplus.domain.Auction;
import com.gavelier.gavelierplus.domain.Lot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class Endpoints {

    @Autowired
    DynamoDBService dynamoDBService;

    private final static Logger LOGGER = Logger.getLogger(Pages.class.getName());

    @PostMapping("/createauction")
    public String greeting(Auction auction, BindingResult bindingResult) {

        List<FieldError> errorFields = bindingResult.getFieldErrors();

        errorFields.forEach(ef -> {
            LOGGER.info("Unable to bind: " + ef.getField());
        });

        dynamoDBService.insertIntoDynamoDB(auction);

        return "redirect:/newauction";

    }

    @PostMapping(value = "/createlot", produces = "application/html")
    public String createLot(@Valid Lot lot, BindingResult bindingResult, Model model, Principal principal)
            throws UnsupportedEncodingException {

        LOGGER.info("called create lot");

        if (bindingResult.hasErrors()) {
            LOGGER.info("Entered error branch in /createlot");
           
            StringBuilder errors = new StringBuilder();

            bindingResult.getFieldErrors().forEach(error -> errors.append(error.getField() + ": " + error.getDefaultMessage() + ". "));


			return "redirect:/lots?auctionId=" + lot.getAuctionId() + "&error=" + URLEncoder.encode(errors.toString(), "UTF-8");
		}


        LOGGER.info("lot =  " + lot.toString());

        dynamoDBService.createLot(lot);
        

        return "redirect:/lots?auctionId=" + lot.getAuctionId();
        
    }


}