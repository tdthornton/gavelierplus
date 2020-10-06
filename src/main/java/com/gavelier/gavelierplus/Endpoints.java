package com.gavelier.gavelierplus;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.validation.Valid;

import com.gavelier.gavelierplus.domain.Auction;
import com.gavelier.gavelierplus.domain.Buyer;
import com.gavelier.gavelierplus.domain.Lot;
import com.gavelier.gavelierplus.domain.LotListWrapper;
import com.gavelier.gavelierplus.domain.Seller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

            bindingResult.getFieldErrors()
                    .forEach(error -> errors.append(error.getField() + ": " + error.getDefaultMessage() + ". "));

            return "redirect:/lots?auctionId=" + lot.getAuctionId() + "&error="
                    + URLEncoder.encode(errors.toString(), "UTF-8");
        }

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

            bindingResult.getFieldErrors()
                    .forEach(error -> errors.append(error.getField() + ": " + error.getDefaultMessage() + ". "));

            return "redirect:/sellers?auctionId=" + seller.getAuctionId() + "&error="
                    + URLEncoder.encode(errors.toString(), "UTF-8");
        }

        LOGGER.info("seller =  " + seller.toString());

        dynamoDBService.createSeller(seller);

        return "redirect:/sellers?auctionId=" + seller.getAuctionId();

    }

    @PostMapping(value = "/createbuyer", produces = "application/html")
    public String createBuyer(@Valid Buyer buyer, BindingResult bindingResult, Model model, Principal principal)
            throws UnsupportedEncodingException {

        LOGGER.info("called create buyer");

        if (bindingResult.hasErrors()) {
            LOGGER.info("Entered error branch in /createseller");

            StringBuilder errors = new StringBuilder();

            bindingResult.getFieldErrors()
                    .forEach(error -> errors.append(error.getField() + ": " + error.getDefaultMessage() + ". "));

            return "redirect:/buyers?auctionId=" + buyer.getAuctionId() + "&error="
                    + URLEncoder.encode(errors.toString(), "UTF-8");
        }

        LOGGER.info("buyer =  " + buyer.toString());

        dynamoDBService.createBuyer(buyer);

        return "redirect:/buyers?auctionId=" + buyer.getAuctionId();

    }

    @PostMapping(value = "/updatelot", produces = "application/html")
    public String updateLot(@Valid Lot lot, BindingResult bindingResult, Model model, Principal principal)
            throws UnsupportedEncodingException {

        LOGGER.info("called update lot");

        if (bindingResult.hasErrors()) {
            LOGGER.info("Entered error branch in /updatelot");

            StringBuilder errors = new StringBuilder();

            bindingResult.getFieldErrors()
                    .forEach(error -> errors.append(error.getField() + ": " + error.getDefaultMessage() + ". "));

            return "redirect:/editlot?lotId=" + lot.getId() + "&error=" + URLEncoder.encode(errors.toString(), "UTF-8");
        }

        LOGGER.info("updated lot =  " + lot.toString());

        dynamoDBService.updateLot(lot);

        return "redirect:/lots?auctionId=" + lot.getAuctionId();

    }

    @PostMapping(value = "/updateseller", produces = "application/html")
    public String updateSeller(@Valid Seller seller, BindingResult bindingResult, Model model, Principal principal)
            throws UnsupportedEncodingException {

        LOGGER.info("called update seller");

        if (bindingResult.hasErrors()) {
            LOGGER.info("Entered error branch in /updateseller");

            StringBuilder errors = new StringBuilder();

            bindingResult.getFieldErrors()
                    .forEach(error -> errors.append(error.getField() + ": " + error.getDefaultMessage() + ". "));

            return "redirect:/editseller?sellerId=" + seller.getId() + "&error="
                    + URLEncoder.encode(errors.toString(), "UTF-8");
        }

        LOGGER.info("updated seller =  " + seller.toString());

        dynamoDBService.updateSeller(seller);

        return "redirect:/sellers?auctionId=" + seller.getAuctionId();

    }

    @PostMapping(value = "/updatebuyer", produces = "application/html")
    public String updateBuyer(@Valid Buyer buyer, BindingResult bindingResult, Model model, Principal principal)
            throws UnsupportedEncodingException {

        LOGGER.info("called update buyer");

        if (bindingResult.hasErrors()) {
            LOGGER.info("Entered error branch in /updatebuyer");

            StringBuilder errors = new StringBuilder();

            bindingResult.getFieldErrors()
                    .forEach(error -> errors.append(error.getField() + ": " + error.getDefaultMessage() + ". "));

            return "redirect:/editbuyer?buyerId=" + buyer.getId() + "&error="
                    + URLEncoder.encode(errors.toString(), "UTF-8");
        }

        LOGGER.info("updated buyer =  " + buyer.toString());

        dynamoDBService.updateBuyer(buyer);

        return "redirect:/buyers?auctionId=" + buyer.getAuctionId();

    }

    @PostMapping(value = "/deletelot")
    public String deleteLot(Lot lot) {

        LOGGER.info("called delete lot for lot " + lot);

        dynamoDBService.deleteLot(lot);

        return "redirect:/lots?auctionId=" + lot.getAuctionId();

    }

    @PostMapping(value = "/deleteseller")
    public String deleteSeller(Seller seller) {

        LOGGER.info("called delete seller for seller " + seller);

        dynamoDBService.deleteSeller(seller);

        return "redirect:/sellers?auctionId=" + seller.getAuctionId();

    }

    @PostMapping(value = "/deletebuyer")
    public String deleteBuyer(Buyer buyer) {

        LOGGER.info("called delete buyer for buyer " + buyer);

        dynamoDBService.deleteBuyer(buyer);

        return "redirect:/buyers?auctionId=" + buyer.getAuctionId();

    }

    @PostMapping(value = "/recordsalesmultiple", produces = "application/html")
    public String recordSales(LotListWrapper lotListWrapper, BindingResult bindingResult, Model model,
            Principal principal, @RequestParam Map<String, String> queryParameters)
            throws UnsupportedEncodingException {

        String page = queryParameters.get("page");

        List<Integer> errors = new ArrayList<Integer>();

        for (Lot lot : lotListWrapper.getLots()) {
            if (isValidSale(lot)) {
                
                dynamoDBService.updateLotSaleOnly(lot);
            } else {
                errors.add(lot.getLotNumber());
            }
        }

        LOGGER.info(errors.toString());
        
        if (errors.size() > 0) {
            return "redirect:/auctioneering?auctionId=" + lotListWrapper.getLots().get(0).getAuctionId() + "&page="
                    + page + "&error=" + URLEncoder.encode("Lots not saved: " + errors.toString(), "UTF-8");
        } else {
            return "redirect:/auctioneering?auctionId=" + lotListWrapper.getLots().get(0).getAuctionId() + "&page="
                    + page;
        }

    }

    private boolean isValidSale(Lot lot) {

        if (lot.getBuyerNumber() < 1 && (lot.getSalePrice() != null && lot.getSalePrice() != new BigDecimal("0.00"))) {
            return false;
        }

        return true;
    }

}