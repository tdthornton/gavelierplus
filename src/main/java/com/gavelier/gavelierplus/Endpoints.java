package com.gavelier.gavelierplus;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.List;
import java.util.logging.Logger;

import javax.validation.Valid;

import com.gavelier.gavelierplus.domain.Auction;
import com.gavelier.gavelierplus.domain.Lot;
import com.gavelier.gavelierplus.domain.Seller;

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

    private final static Logger LOGGER = Logger.getLogger(Endpoints.class.getName());

    @PostMapping("/createauction")
    public String greeting(Auction auction, BindingResult bindingResult) {

        List<FieldError> errorFields = bindingResult.getFieldErrors();

        errorFields.forEach(ef -> {
            LOGGER.info("Unable to bind: " + ef.getField());
        });

        Auction auctionSaved = dynamoDBService.saveAuction(auction);

        return "redirect:/lots?auctionId=" + auctionSaved.getId();

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

    @PostMapping(value = "/createseller", produces = "application/html")
    public String createseller(@Valid Seller seller, BindingResult bindingResult, Model model, Principal principal)
            throws UnsupportedEncodingException {

        LOGGER.info("called create seller");

        if (bindingResult.hasErrors()) {
            LOGGER.info("Entered error branch in /createseller");
           
            StringBuilder errors = new StringBuilder();

            bindingResult.getFieldErrors().forEach(error -> errors.append(error.getField() + ": " + error.getDefaultMessage() + ". "));


			return "redirect:/sellers?auctionId=" + seller.getAuctionId() + "&error=" + URLEncoder.encode(errors.toString(), "UTF-8");
		}


        LOGGER.info("seller =  " + seller.toString());

        dynamoDBService.createSeller(seller);
        

        return "redirect:/sellers?auctionId=" + seller.getAuctionId();
        
    }

    @PostMapping(value = "/updatelot", produces = "application/html")
    public String updateLot(@Valid Lot lot, BindingResult bindingResult, Model model, Principal principal)
            throws UnsupportedEncodingException {

        LOGGER.info("called update lot");

        if (bindingResult.hasErrors()) {
            LOGGER.info("Entered error branch in /updatelot");
           
            StringBuilder errors = new StringBuilder();

            bindingResult.getFieldErrors().forEach(error -> errors.append(error.getField() + ": " + error.getDefaultMessage() + ". "));


			return "redirect:/editlot?lotId=" + lot.getId() + "&error=" + URLEncoder.encode(errors.toString(), "UTF-8");
		}


        LOGGER.info("updated lot =  " + lot.toString());

        dynamoDBService.createLot(lot);
        

        return "redirect:/lots?auctionId=" + lot.getAuctionId();
        
    }

    @PostMapping(value = "/updateseller", produces = "application/html")
    public String updateSeller(@Valid Seller seller, BindingResult bindingResult, Model model, Principal principal)
            throws UnsupportedEncodingException {

        LOGGER.info("called update seller");

        if (bindingResult.hasErrors()) {
            LOGGER.info("Entered error branch in /updateseller");
           
            StringBuilder errors = new StringBuilder();

            bindingResult.getFieldErrors().forEach(error -> errors.append(error.getField() + ": " + error.getDefaultMessage() + ". "));


			return "redirect:/editseller?sellerId=" + seller.getId() + "&error=" + URLEncoder.encode(errors.toString(), "UTF-8");
		}


        LOGGER.info("updated seller =  " + seller.toString());

        dynamoDBService.updateSeller(seller);
        

        return "redirect:/sellers?auctionId=" + seller.getAuctionId();
        
    }

    @PostMapping(value = "/deletelot")
    public String deleteLot(Lot lot) {

        LOGGER.info("called delete lot for lot " + lot);

        dynamoDBService.deleteLot(lot);
        
        return "redirect:/lots?auctionId=" + lot.getAuctionId();
        
    }


}