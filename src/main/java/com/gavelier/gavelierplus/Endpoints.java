package com.gavelier.gavelierplus;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.logging.Logger;

import com.gavelier.gavelierplus.domain.NewAuctionRequest;

@Controller
public class Endpoints {


    private final static Logger LOGGER = Logger.getLogger(Pages.class.getName());

    @PostMapping("/createauction")
    public String greeting(NewAuctionRequest newAuctionRequest, BindingResult bindingResult) {

        

        LOGGER.info("called");
        LOGGER.info("Binding errors =  " + bindingResult.hasErrors());

        List<FieldError> errorFields = bindingResult.getFieldErrors();

        errorFields.forEach(ef -> {
            LOGGER.info("Unable to bind: " + ef.getField());
        });

        LOGGER.info("NAME: " + newAuctionRequest.getInputCompanyName());
        LOGGER.info("DEC: " + newAuctionRequest.toString());




        return "newauction";
    }


}