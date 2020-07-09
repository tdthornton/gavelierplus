package com.gavelier.gavelierplus;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.logging.Logger;

import com.gavelier.gavelierplus.domain.Auction;

@Controller
public class Endpoints {

    @Autowired
    DynamoDBRepository dynamoDBRepository;


    private final static Logger LOGGER = Logger.getLogger(Pages.class.getName());

    @PostMapping("/createauction")
    public String greeting(Auction auction, BindingResult bindingResult) {


        LOGGER.info("called");
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


}