package com.gavelier.gavelierplus;

import static java.util.stream.Collectors.toList;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.gavelier.gavelierplus.domain.Auction;
import com.gavelier.gavelierplus.domain.Lot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Pages {

    private final static Logger LOGGER = Logger.getLogger(Pages.class.getName());

    @Autowired
    DynamoDBService dynamoDBService;


    @GetMapping("/")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name,
            Model model) {

        model.addAttribute("name", name);

        return "login";
    }

    @GetMapping("/logout")
    public String logout(@RequestParam(name = "name", required = false, defaultValue = "World") String name,
            Model model, Principal principal, HttpServletRequest httpServletRequest) throws ServletException {

        model.addAttribute("name", name);

        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);

        httpServletRequest.logout();

        SecurityContextHolder.clearContext();

        HttpSession session = httpServletRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model, @RequestParam Map<String, String> queryParameters) {

        LOGGER.info("Access to dashboard page");

        String currentAuctionId = queryParameters.get("auctionId");
        model.addAttribute("currentAuctionId", currentAuctionId);

        model.addAttribute("name", principal.getName());

        return "dashboard";

    }

    @GetMapping("/newauction")
    public String newAuction(Principal principal, Model model) {

        LOGGER.info("Access to new auction page");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        model.addAttribute("today", sdf.format(date));
        model.addAttribute("name", principal.getName());
        model.addAttribute("userId", principal.getName());

        return "newauction";

    }

    @GetMapping("/lots")
    public String lots(Principal principal, Model model, @RequestParam Map<String, String> queryParameters) {

        LOGGER.info("Access to lots page");

        String currentAuctionId = queryParameters.get("auctionId");

        LOGGER.info("Called lots page, auction ID = " + currentAuctionId);

        model.addAttribute("allAuctionsForUser", dynamoDBService.getAllAuctionsForUserInDateOrder(principal.getName()));



        model.addAttribute("error", queryParameters.get("error"));

        

        if (currentAuctionId != null && currentAuctionId != "null") {
            Auction currentAuction = dynamoDBService.getOneAuctionById(currentAuctionId, principal.getName());

            if (currentAuction != null) { //if the auction exists, we can accept new lots for it, and show the existing ones.


                //get all lots for this auction and sort the scan
                List<Lot> allLotsForAuction = dynamoDBService.getAllLotsForAuction(currentAuctionId).stream()
                        .sorted((lot1, lot2) -> Integer.compare(lot2.getLotNumber(), lot1.getLotNumber()))
                        .collect(toList());

                model.addAttribute("name", principal.getName());

                if (allLotsForAuction.size() == 0) {
                    model.addAttribute("nextLotNumber", 1);
                } else if (allLotsForAuction.size() == 1) {
                    model.addAttribute("nextLotNumber", 2);
                } else {
                    model.addAttribute("nextLotNumber", allLotsForAuction.size() + 1);
                }
                model.addAttribute("currentAuctionId", currentAuctionId);
                model.addAttribute("currentAuctionName", currentAuction.getInputCompanyName() + " - " + currentAuction.getDate());
                model.addAttribute("lotsForCurrentAuction", allLotsForAuction);

                return "lots";

            } else {

                //there is no valid auction provided
                //so we show a page with no form, only the option to select an auction
                return "emptylots";

            }
        } else {
            //there is no valid auction provided
                //so we show a page with no form, only the option to select an auction
            return "emptylots";
        }

    }

    @GetMapping("/editlot")
    public String editLot(@RequestParam String lotId, Principal principal, Model model, @RequestParam Map<String, String> queryParameters) {

        LOGGER.info("Access to update lot page. Lot id " + lotId);

        model.addAttribute("name", principal.getName());
        model.addAttribute("error", queryParameters.get("error"));

        Lot oldLotState = dynamoDBService.getOneLotById(lotId);

        model.addAttribute("oldLot", oldLotState);

        return "editlot";

    }

    @GetMapping("/deletelotconfirmation")
    public String deleteLot(@RequestParam String lotId, Principal principal, Model model, @RequestParam Map<String, String> queryParameters) {

        LOGGER.info("Access to delete lot page. Lot id " + lotId);

        model.addAttribute("name", principal.getName());
        model.addAttribute("error", queryParameters.get("error"));

        Lot oldLotState = dynamoDBService.getOneLotById(lotId);

        model.addAttribute("oldLot", oldLotState);

        return "deletelotconfirmation";

    }

    @GetMapping("/sellers")
    public String sellers(Principal principal, Model model, @RequestParam Map<String, String> queryParameters) {

        LOGGER.info("Access to sellers page by " + principal.getName());

        String currentAuctionId = queryParameters.get("auctionId");

        LOGGER.info("Called sellers page, auction ID = " + currentAuctionId);

        model.addAttribute("allAuctionsForUser", dynamoDBService.getAllAuctionsForUserInDateOrder(principal.getName()));



        model.addAttribute("error", queryParameters.get("error"));

        

        if (currentAuctionId != null && currentAuctionId != "null") {
            Auction currentAuction = dynamoDBService.getOneAuctionById(currentAuctionId, principal.getName());

            if (currentAuction != null) { //if the auction exists, we can accept new sellers for it, and show the existing ones.


                // //get all sellers for this auction and sort the scan
                // List<Seller> allLotsForAuction = dynamoDBService.getAllLotsForAuction(currentAuctionId).stream()
                //         .sorted((lot1, lot2) -> Integer.compare(lot2.getLotNumber(), lot1.getLotNumber()))
                //         .collect(toList());

                model.addAttribute("name", principal.getName());

                // if (allLotsForAuction.size() == 0) {
                //     model.addAttribute("nextLotNumber", 1);
                // } else if (allLotsForAuction.size() == 1) {
                //     model.addAttribute("nextLotNumber", 2);
                // } else {
                //     model.addAttribute("nextLotNumber", allLotsForAuction.size() + 1);
                // }
                model.addAttribute("currentAuctionId", currentAuctionId);
                model.addAttribute("currentAuctionName", currentAuction.getInputCompanyName() + " - " + currentAuction.getDate());
                // model.addAttribute("lotsForCurrentAuction", allLotsForAuction);

                return "sellers";

            } else {

                //there is no valid auction provided
                //so we show a page with no form, only the option to select an auction
                return "emptysellers";

            }
        } else {
            //there is no valid auction provided
                //so we show a page with no form, only the option to select an auction
            return "emptysellers";
        }

    }

    @GetMapping("/buyers")
    public String bueyrs(Principal principal, Model model, @RequestParam Map<String, String> queryParameters) {

        LOGGER.info("Access to buyers page");

        String currentAuctionId = queryParameters.get("auctionId");
        model.addAttribute("currentAuctionId", currentAuctionId);

        model.addAttribute("name", principal.getName());

        return "buyers";

    }

    @GetMapping("/auctioneering")
    public String auctioneering(Principal principal, Model model, @RequestParam Map<String, String> queryParameters) {

        LOGGER.info("Access to auctioneering page");

        String currentAuctionId = queryParameters.get("auctionId");
        model.addAttribute("currentAuctionId", currentAuctionId);

        model.addAttribute("name", principal.getName());

        return "auctioneering";

    }

    @GetMapping("/documents")
    public String documents(Principal principal, Model model, @RequestParam Map<String, String> queryParameters) {

        LOGGER.info("Access to documents page");

        String currentAuctionId = queryParameters.get("auctionId");
        model.addAttribute("currentAuctionId", currentAuctionId);

        model.addAttribute("name", principal.getName());

        return "documents";

    }

    @GetMapping("/archive")
    public String archive(Principal principal, Model model, @RequestParam Map<String, String> queryParameters) {

        LOGGER.info("Access to archive page");
        String currentAuctionId = queryParameters.get("auctionId");
        model.addAttribute("currentAuctionId", currentAuctionId);

        model.addAttribute("name", principal.getName());

        return "archive";

    }

    

}