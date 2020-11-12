package com.gavelier.gavelierplus;

import static java.util.stream.Collectors.toList;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.channels.AcceptPendingException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.gavelier.gavelierplus.domain.Auction;
import com.gavelier.gavelierplus.domain.Buyer;
import com.gavelier.gavelierplus.domain.Lot;
import com.gavelier.gavelierplus.domain.Seller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

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

        model.addAttribute("name", principal.getName());

        model.addAttribute("allAuctionsForUser", dynamoDBService.getAllAuctionsForUserInDateOrder(principal.getName()));

        model.addAttribute("error", queryParameters.get("error"));

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

            if (currentAuction != null && currentAuction.getUserId().equals(principal.getName())) { // if the auction
                                                                                                    // exists, we can
                                                                                                    // accept new lots
                                                                                                    // for it, and show
                                                                                                    // the existing
                // ones.

                // get all lots for this auction and sort the scan
                List<Lot> allLotsForAuction = dynamoDBService.getAllLotsForAuction(currentAuctionId).stream()
                        .sorted((lot1, lot2) -> Integer.compare(lot2.getLotNumber(), lot1.getLotNumber()))
                        .collect(toList());

                model.addAttribute("name", principal.getName());

                if (allLotsForAuction.size() == 0) {
                    model.addAttribute("nextLotNumber", 1);
                } else {

                    Lot highestLotNumber = allLotsForAuction.stream()
                            .max(Comparator.comparing(lot -> lot.getLotNumber())).get();
                    if (highestLotNumber.getLotNumber() == 1) {
                        model.addAttribute("nextLotNumber", 2);
                    } else {
                        model.addAttribute("nextLotNumber", highestLotNumber.getLotNumber() + 1);
                    }

                }
                model.addAttribute("currentAuctionId", currentAuctionId);
                model.addAttribute("currentAuction", currentAuction);
                model.addAttribute("lotsForCurrentAuction", allLotsForAuction);

                return "lots";

            } else {

                // there is no valid auction provided
                // so we show a page with no form, only the option to select an auction
                return "emptylots";

            }
        } else {
            // there is no valid auction provided
            // so we show a page with no form, only the option to select an auction
            return "emptylots";
        }

    }

    @GetMapping("/editlot")
    public String editLot(@RequestParam String lotId, Principal principal, Model model,
            @RequestParam Map<String, String> queryParameters) {

        LOGGER.info("Access to update lot page. Lot id " + lotId);

        model.addAttribute("name", principal.getName());
        model.addAttribute("error", queryParameters.get("error"));

        Lot oldLotState = dynamoDBService.getOneLotById(lotId);

        model.addAttribute("oldLot", oldLotState);
        model.addAttribute("currentAuctionId", oldLotState.getAuctionId());

        return "editlot";

    }

    @GetMapping("/editseller")
    public String editSeller(@RequestParam String sellerId, Principal principal, Model model,
            @RequestParam Map<String, String> queryParameters) {

        LOGGER.info("Access to update seller page. Seller id " + sellerId);

        model.addAttribute("name", principal.getName());
        model.addAttribute("error", queryParameters.get("error"));

        Seller oldSellerState = dynamoDBService.getOneSeller(sellerId);

        model.addAttribute("oldSeller", oldSellerState);
        model.addAttribute("currentAuctionId", oldSellerState.getAuctionId());

        return "editseller";

    }

    @GetMapping("/editbuyer")
    public String editBuyer(@RequestParam String buyerId, Principal principal, Model model,
            @RequestParam Map<String, String> queryParameters) {

        LOGGER.info("Access to update buyer page. Buyer id " + buyerId);

        model.addAttribute("name", principal.getName());
        model.addAttribute("error", queryParameters.get("error"));

        Buyer oldBuyerState = dynamoDBService.getOneBuyer(buyerId);

        model.addAttribute("oldBuyer", oldBuyerState);
        model.addAttribute("currentAuctionId", oldBuyerState.getAuctionId());

        return "editbuyer";

    }

    @GetMapping("/deletelotconfirmation")
    public String deleteLot(@RequestParam String lotId, Principal principal, Model model,
            @RequestParam Map<String, String> queryParameters) {

        LOGGER.info("Access to delete lot page. Lot id " + lotId);

        model.addAttribute("name", principal.getName());
        model.addAttribute("error", queryParameters.get("error"));

        Lot oldLotState = dynamoDBService.getOneLotById(lotId);

        model.addAttribute("oldLot", oldLotState);
        model.addAttribute("currentAuctionId", oldLotState.getAuctionId());

        return "deletelotconfirmation";

    }

    @GetMapping("/deletesellerconfirmation")
    public String deleteSellerConfirmation(@RequestParam String sellerId, Principal principal, Model model,
            @RequestParam Map<String, String> queryParameters) {

        LOGGER.info("Access to delete seller page. Seller id " + sellerId);

        model.addAttribute("name", principal.getName());
        model.addAttribute("error", queryParameters.get("error"));

        Seller oldSellerState = dynamoDBService.getOneSeller(sellerId);

        if (oldSellerState != null && oldSellerState.getId() != null) {

            model.addAttribute("oldSeller", oldSellerState);
            model.addAttribute("currentAuctionId", oldSellerState.getAuctionId());

            return "deletesellerconfirmation";

        } else {

            return "redirect:/sellers";
        }

    }

    @GetMapping("/deletebuyerconfirmation")
    public String deleteBuyerConfirmation(@RequestParam String buyerId, Principal principal, Model model,
            @RequestParam Map<String, String> queryParameters) {

        LOGGER.info("Access to delete buyer page. buyer id " + buyerId);

        model.addAttribute("name", principal.getName());
        model.addAttribute("error", queryParameters.get("error"));

        Buyer oldBuyerState = dynamoDBService.getOneBuyer(buyerId);

        if (oldBuyerState != null && oldBuyerState.getId() != null) {

            model.addAttribute("oldBuyer", oldBuyerState);
            model.addAttribute("currentAuctionId", oldBuyerState.getAuctionId());

            return "deletebuyerconfirmation";

        } else {

            return "redirect:/buyers";
        }

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

            if (currentAuction != null && principal.getName().equals(currentAuction.getUserId())) { // if the auction
                                                                                                    // exists, we can
                                                                                                    // accept new
                                                                                                    // sellers for it,
                                                                                                    // and show the
                // existing ones.

                // get all sellers for this auction and sort the scan
                List<Seller> allSellers = dynamoDBService.getAllSellersForAuction(currentAuctionId);

                model.addAttribute("name", principal.getName());

                if (allSellers.size() == 0) {
                    model.addAttribute("nextSellerNumber", 1);
                } else {

                    Seller highestSellerNumber = allSellers.stream()
                            .max(Comparator.comparing(seller -> seller.getSellerNumber())).get();
                    if (highestSellerNumber.getSellerNumber() == 1) {
                        model.addAttribute("nextSellerNumber", 2);
                    } else {
                        model.addAttribute("nextSellerNumber", highestSellerNumber.getSellerNumber() + 1);
                    }

                }
                model.addAttribute("currentAuctionId", currentAuctionId);
                model.addAttribute("currentAuction", currentAuction);
                model.addAttribute("sellersForCurrentAuction", allSellers);

                return "sellers";

            } else {

                // there is no valid auction provided
                // so we show a page with no form, only the option to select an auction
                return "emptysellers";

            }
        } else {
            // there is no valid auction provided
            // so we show a page with no form, only the option to select an auction
            return "emptysellers";
        }

    }

    @GetMapping("/buyers")
    public String buyers(Principal principal, Model model, @RequestParam Map<String, String> queryParameters) {

        LOGGER.info("Access to buyers page by " + principal.getName());

        String currentAuctionId = queryParameters.get("auctionId");

        LOGGER.info("Called buyers page, auction ID = " + currentAuctionId);

        model.addAttribute("allAuctionsForUser", dynamoDBService.getAllAuctionsForUserInDateOrder(principal.getName()));

        model.addAttribute("error", queryParameters.get("error"));

        if (currentAuctionId != null && currentAuctionId != "null") {
            Auction currentAuction = dynamoDBService.getOneAuctionById(currentAuctionId, principal.getName());

            if (currentAuction != null && currentAuction.getUserId().equals(principal.getName())) { // if the auction
                                                                                                    // exists, we can
                                                                                                    // accept new buyers
                                                                                                    // for it, and show
                                                                                                    // the
                // existing ones.

                // get all buyers for this auction and sort the scan
                List<Buyer> allBuyers = dynamoDBService.getAllBuyersForAuction(currentAuctionId);

                model.addAttribute("name", principal.getName());

                if (allBuyers.size() == 0) {
                    model.addAttribute("nextBuyerNumber", 1);
                } else {

                    Buyer highestSellerNumber = allBuyers.stream()
                            .max(Comparator.comparing(buyer -> buyer.getBuyerNumber())).get();
                    if (highestSellerNumber.getBuyerNumber() == 1) {
                        model.addAttribute("nextBuyerNumber", 2);
                    } else {
                        model.addAttribute("nextBuyerNumber", highestSellerNumber.getBuyerNumber() + 1);
                    }

                }
                model.addAttribute("currentAuctionId", currentAuctionId);
                model.addAttribute("currentAuction", currentAuction);
                model.addAttribute("buyersForCurrentAuction", allBuyers);

                return "buyers";

            } else {

                // there is no valid auction provided
                // so we show a page with no form, only the option to select an auction
                return "emptybuyers";

            }
        } else {
            // there is no valid auction provided
            // so we show a page with no form, only the option to select an auction
            return "emptybuyers";
        }

    }

    @GetMapping("/auctioneering")
    public String auctioneering(Principal principal, Model model, @RequestParam Map<String, String> queryParameters) {

        LOGGER.info("Access to auctioneering page");

        String currentAuctionId = queryParameters.get("auctionId");

        LOGGER.info("Called auctioneering page, auction ID = " + currentAuctionId);

        model.addAttribute("allAuctionsForUser", dynamoDBService.getAllAuctionsForUserInDateOrder(principal.getName()));

        model.addAttribute("error", queryParameters.get("error"));

        if (currentAuctionId != null && currentAuctionId != "null") {

            Auction currentAuction = dynamoDBService.getOneAuctionById(currentAuctionId, principal.getName());

            if (currentAuction != null && currentAuction.getUserId().equals(principal.getName())) { // if the auction
                                                                                                    // exists, we can
                                                                                                    // sell the lots

                // get all lots for this auction and sort the scan in ascending lot number order
                List<Lot> allLotsForAuction = dynamoDBService.getAllLotsForAuction(currentAuctionId).stream()
                        .sorted((lot1, lot2) -> Integer.compare(lot1.getLotNumber(), lot2.getLotNumber()))
                        .collect(toList());

                if (allLotsForAuction.size()!=0) {

                String page = queryParameters.get("page");

                int currentPage = 1;
                int fromIndex = 0;
                int toIndex = 10;

                if (page != null) {
                    try {
                        currentPage = Integer.parseInt(page);
                        fromIndex = (currentPage - 1) * 10;
                        toIndex = fromIndex + 10;
                    } catch (NumberFormatException e) {

                    }
                }

                int totalPages = allLotsForAuction.size() / 10;
                int pagesMod = allLotsForAuction.size() % 10;
                if (pagesMod > 0) {
                    totalPages++;
                }

                if (toIndex > allLotsForAuction.size()) {
                    toIndex = allLotsForAuction.size();
                }

                List<Lot> lotsToPass = allLotsForAuction.subList(fromIndex, toIndex);

                
                model.addAttribute("totalPages", totalPages);
                model.addAttribute("currentPage", currentPage);
                model.addAttribute("lotsForCurrentAuction", lotsToPass);
                model.addAttribute("lotsPassedCount", lotsToPass.size() - 1);

            } else {
                model.addAttribute("totalPages", 1);
                model.addAttribute("currentPage", 1);
                model.addAttribute("lotsPassedCount", 0);

                model.addAttribute("name", principal.getName());

                
                
            }

                model.addAttribute("currentAuctionId", currentAuctionId);

                model.addAttribute("currentAuction", currentAuction);

                return "auctioneering";

            } else {

                // there is no valid auction provided
                // so we show a page with no form, only the option to select an auction
                return "emptyauctioneering";

            }
        } else {
            // there is no valid auction provided
            // so we show a page with no form, only the option to select an auction
            return "emptyauctioneering";
        }

    }

    @GetMapping("/documents")
    public String documents(Principal principal, Model model, @RequestParam Map<String, String> queryParameters) {

        LOGGER.info("Access to documents page");

        String currentAuctionId = queryParameters.get("auctionId");
        model.addAttribute("currentAuctionId", currentAuctionId);

        if (currentAuctionId != null && currentAuctionId != "null"){ 

            Auction currentAuction = dynamoDBService.getOneAuctionById(currentAuctionId, principal.getName());

            if (currentAuction != null) {
                model.addAttribute("currentAuction", currentAuction);
            }

        }

        model.addAttribute("allAuctionsForUser", dynamoDBService.getAllAuctionsForUserInDateOrder(principal.getName()));
    

        model.addAttribute("name", principal.getName());

        return "documents";

    }

    @GetMapping("/auctioneerssheet")
    public String auctioneersSheet(Principal principal, Model model, @RequestParam Map<String, String> queryParameters) {

        LOGGER.info("Access to auctioneer's sheet");

        String currentAuctionId = queryParameters.get("auctionId");

        if (currentAuctionId != null && currentAuctionId != "null") {
            Auction currentAuction = dynamoDBService.getOneAuctionById(currentAuctionId, principal.getName());

            if (currentAuction != null && currentAuction.getUserId().equals(principal.getName())) {
                LOGGER.info(currentAuction.getInputCompanyName());

                List<Lot> allLotsForAuction = dynamoDBService.getAllLotsForAuction(currentAuctionId).stream()
                        .sorted((lot1, lot2) -> Integer.compare(lot1.getLotNumber(), lot2.getLotNumber()))
                        .collect(toList());

                model.addAttribute("allLots", allLotsForAuction);
            }

            

        }

        
        model.addAttribute("name", principal.getName());
        model.addAttribute("userId", principal.getName());

        return "auctioneerssheet";

    }

    @GetMapping("/catalogue")
    public String catalogue(Principal principal, Model model, @RequestParam Map<String, String> queryParameters) {

        LOGGER.info("Access to catalogue");

        String currentAuctionId = queryParameters.get("auctionId");

        if (currentAuctionId != null && currentAuctionId != "null") {
            Auction currentAuction = dynamoDBService.getOneAuctionById(currentAuctionId, principal.getName());

            if (currentAuction != null && currentAuction.getUserId().equals(principal.getName())) {
                LOGGER.info(currentAuction.getInputCompanyName());

                List<Lot> allLotsForAuction = dynamoDBService.getAllLotsForAuction(currentAuctionId).stream()
                        .sorted((lot1, lot2) -> Integer.compare(lot1.getLotNumber(), lot2.getLotNumber()))
                        .collect(toList());

                model.addAttribute("allLots", allLotsForAuction);
            }

            

        }

        
        model.addAttribute("name", principal.getName());
        model.addAttribute("userId", principal.getName());

        return "catalogue";

    }

    @GetMapping("/sellerreport")
    public String sellerReport(Principal principal, Model model, @RequestParam Map<String, String> queryParameters)
            throws UnsupportedEncodingException {

        LOGGER.info("Access to seller report page");

        String currentAuctionId = queryParameters.get("auctionId");
        int sellerNumber = 0;

        model.addAttribute("allAuctionsForUser", dynamoDBService.getAllAuctionsForUserInDateOrder(principal.getName()));

        try {

            if (queryParameters.containsKey("sellerNumber")) {
                String sellerNumberString = queryParameters.get("sellerNumber");
                sellerNumber = Integer.parseInt(sellerNumberString);
            } else {
                return "redirect:/sellers?auctionId=" + currentAuctionId + "&error="
                        + URLEncoder.encode("Not able to find that seller in this auction.", "UTF-8");
            }

        } catch (NumberFormatException e) {
            return "redirect:/sellers?auctionId=" + currentAuctionId + "&error="
                    + URLEncoder.encode("Not able to find that seller in this auction.", "UTF-8");
        }

        if (sellerNumber != 0) {

            model.addAttribute("currentAuctionId", currentAuctionId);

            model.addAttribute("name", principal.getName());

            Auction auction = dynamoDBService.getOneAuctionById(currentAuctionId, principal.getName());

            if (auction != null && auction.getUserId().equals(principal.getName())) {

                List<Seller> allSellers = dynamoDBService.getAllSellersForAuction(currentAuctionId);

                boolean foundSeller = false;

                for (Seller seller : allSellers) {
                    if (seller.getSellerNumber() == sellerNumber) {
                        model.addAttribute("seller", seller);
                        foundSeller = true;
                    }
                }
                
                if (!foundSeller) {
                    return "redirect:/sellers?auctionId=" + currentAuctionId + "&error="
                            + URLEncoder.encode("Not able to find that seller in this auction.", "UTF-8");
                }

                List<Lot> allLotsForAuction = dynamoDBService.getAllLotsForAuction(currentAuctionId).stream()
                        .sorted((lot1, lot2) -> Integer.compare(lot1.getLotNumber(), lot2.getLotNumber()))
                        .collect(toList());

                List<Lot> subListOfLots = new ArrayList<>();

                for (Lot lot : allLotsForAuction) {
                    if (lot.getSellerNumber() == sellerNumber) {
                        if (lot.getSalePrice() != null) {
                            lot.setSalePrice(lot.getSalePrice().setScale(2, RoundingMode.HALF_UP));
                        }
                        subListOfLots.add(lot);
                    }
                }

                model.addAttribute("lotsForSeller", subListOfLots);
                model.addAttribute("totalSalesForSeller", calculateTotalSoldBySeller(subListOfLots));
                model.addAttribute("totalSellerFees", calculateTotalSellerFees(subListOfLots));
                model.addAttribute("sellerFinalTotal", calculateFinalPaymentToSeller(subListOfLots));
                model.addAttribute("auction", auction);

               return "sellerreport";

            } else {
                return "redirect:/sellers?auctionId=" + currentAuctionId + "&error=" + URLEncoder
                        .encode("There was an error finding the auction, please log out and back in.", "UTF-8");
            }

        } else {
            return "redirect:/sellers?auctionId=" + currentAuctionId + "&error="
                    + URLEncoder.encode("No seller number provided.", "UTF-8");
        }

    }

    private BigDecimal calculateTotalSoldBySeller(List<Lot> subListOfLots) {
        BigDecimal totalSoldBySeller = new BigDecimal("0.00");
        totalSoldBySeller.setScale(2, RoundingMode.HALF_UP);

        for (Lot lot: subListOfLots) {
            if (lot.getSalePrice() != null) {
                totalSoldBySeller = totalSoldBySeller.add(lot.getSalePrice()).setScale(2, RoundingMode.HALF_UP);
            }
        }

        return totalSoldBySeller;
    }

    private BigDecimal calculateTotalSellerFees(List<Lot> subListOfLots) {
        BigDecimal totalSellerFees = new BigDecimal("0.00");
        totalSellerFees.setScale(2, RoundingMode.HALF_UP);

        for (Lot lot: subListOfLots) {
            if (lot.getSellerFees()!=null) {
                totalSellerFees = totalSellerFees.add(lot.getSellerFees());
                lot.setSellerFees(lot.getSellerFees().setScale(2, RoundingMode.HALF_UP));
            }
        }

        return totalSellerFees.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateFinalPaymentToSeller(List<Lot> subListOfLots) {
        BigDecimal finalPaymentToSeller = new BigDecimal("0.00");
        finalPaymentToSeller.setScale(2, RoundingMode.HALF_UP);

        for (Lot lot: subListOfLots) {
            if (lot.getPaymentToSeller() != null) {
                finalPaymentToSeller = finalPaymentToSeller.add(lot.getPaymentToSeller());
                lot.setPaymentToSeller(lot.getPaymentToSeller().setScale(2, RoundingMode.HALF_UP));
            }
        }

        return finalPaymentToSeller.setScale(2, RoundingMode.HALF_UP);
    }

    

    @GetMapping("/buyerreport")
    public String buyerReport(Principal principal, Model model, @RequestParam Map<String, String> queryParameters)
            throws UnsupportedEncodingException {

        LOGGER.info("Access to buyer report page");

        String currentAuctionId = queryParameters.get("auctionId");
        int buyerNumber = 0;

        model.addAttribute("allAuctionsForUser", dynamoDBService.getAllAuctionsForUserInDateOrder(principal.getName()));

        try {

            if (queryParameters.containsKey("buyerNumber")) {
                String buyerNumberString = queryParameters.get("buyerNumber");
                buyerNumber = Integer.parseInt(buyerNumberString);
            } else {
                return "redirect:/buyers?auctionId=" + currentAuctionId + "&error="
                        + URLEncoder.encode("Not able to find that buyer in this auction.", "UTF-8");
            }

        } catch (NumberFormatException e) {
            return "redirect:/buyers?auctionId=" + currentAuctionId + "&error="
                    + URLEncoder.encode("Not able to find that buyer in this auction.", "UTF-8");
        }

        if (buyerNumber != 0) {

            model.addAttribute("currentAuctionId", currentAuctionId);

            model.addAttribute("name", principal.getName());

            Auction auction = dynamoDBService.getOneAuctionById(currentAuctionId, principal.getName());

            if (auction != null && auction.getUserId().equals(principal.getName())) {

                List<Buyer> allBuyers = dynamoDBService.getAllBuyersForAuction(currentAuctionId);

                boolean foundBuyer = false;

                for (Buyer buyer : allBuyers) {
                    if (buyer.getBuyerNumber() == buyerNumber) {
                        model.addAttribute("buyer", buyer);
                        foundBuyer = true;
                    }
                }

                if (!foundBuyer) {
                    return "redirect:/buyers?auctionId=" + currentAuctionId + "&error="
                            + URLEncoder.encode("Not able to find that buyer in this auction.", "UTF-8");
                }

                List<Lot> allLotsForAuction = dynamoDBService.getAllLotsForAuction(currentAuctionId).stream()
                        .sorted((lot1, lot2) -> Integer.compare(lot1.getLotNumber(), lot2.getLotNumber()))
                        .collect(toList());

                List<Lot> subListOfLots = new ArrayList<>();

                for (Lot lot : allLotsForAuction) {
                    if (lot.getBuyerNumber() == buyerNumber) {
                        if (lot.getSalePrice() != null) {
                            lot.setSalePrice(lot.getSalePrice().setScale(2, RoundingMode.HALF_UP));
                        }
                        subListOfLots.add(lot);
                    }
                }

                BigDecimal totalPurchasesByBuyer = calculateTotalPurchases(subListOfLots);

                model.addAttribute("lotsForBuyer", subListOfLots);
                model.addAttribute("totalPurchasesByBuyer", totalPurchasesByBuyer);
                model.addAttribute("totalBuyerFees", calculateTotalBuyerFees(subListOfLots));
                model.addAttribute("totalCost", calculateTotalCost(subListOfLots));
                model.addAttribute("auction", auction);

               return "buyerreport";

            } else {
                return "redirect:/buyers?auctionId=" + currentAuctionId + "&error=" + URLEncoder
                        .encode("There was an error finding the auction, please log out and back in.", "UTF-8");
            }

        } else {
            return "redirect:/buyers?auctionId=" + currentAuctionId + "&error="
                    + URLEncoder.encode("No buyer number provided.", "UTF-8");
        }

    }

    private BigDecimal calculateTotalPurchases(List<Lot> subListOfLots) {
        BigDecimal totalPurchases = new BigDecimal("0.00");
        totalPurchases.setScale(2, RoundingMode.HALF_UP);
        
        for (Lot lot: subListOfLots) {
            if (lot.getSalePrice() != null) {
                totalPurchases = totalPurchases.add(lot.getSalePrice());
            }
        }

        return totalPurchases.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalBuyerFees(List<Lot> subListOfLots) {
        BigDecimal totalBuyerFees = new BigDecimal("0.00");
        totalBuyerFees.setScale(2, RoundingMode.HALF_UP);

        for (Lot lot: subListOfLots) {
            if (lot.getBuyerFees()!=null) {
                totalBuyerFees = totalBuyerFees.add(lot.getBuyerFees()).setScale(2, RoundingMode.HALF_UP);
                lot.setBuyerFees(lot.getBuyerFees().setScale(2, RoundingMode.HALF_UP));
            }
        }

        return totalBuyerFees.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalCost(List<Lot> subListOfLots) {
        BigDecimal totalCost = new BigDecimal("0.00");
        totalCost.setScale(2, RoundingMode.HALF_UP);

        for (Lot lot: subListOfLots) {
            if (lot.getCostToBuyer() != null) {
                totalCost = totalCost.add(lot.getCostToBuyer()).setScale(2, RoundingMode.HALF_UP);
                lot.setCostToBuyer(lot.getCostToBuyer().setScale(2, RoundingMode.HALF_UP));
            }
        }

        return totalCost.setScale(2, RoundingMode.HALF_UP);
    }

    

    @GetMapping("/archive")
    public String archive(Principal principal, Model model, @RequestParam Map<String, String> queryParameters) {

        LOGGER.info("Access to auctioneer's sheet");

        String currentAuctionId = queryParameters.get("auctionId");

        model.addAttribute("allAuctionsForUser", dynamoDBService.getAllAuctionsForUserInDateOrder(principal.getName()));

        if (currentAuctionId != null && currentAuctionId != "null") {
            Auction currentAuction = dynamoDBService.getOneAuctionById(currentAuctionId, principal.getName());

            if (currentAuction != null && currentAuction.getUserId().equals(principal.getName())) {
                LOGGER.info(currentAuction.getInputCompanyName() + " archive accessed.");

                model.addAttribute("currentAuction", currentAuction);

                List<Lot> allLotsForAuction = dynamoDBService.getAllLotsForAuction(currentAuctionId).stream()
                        .sorted((lot1, lot2) -> Integer.compare(lot1.getLotNumber(), lot2.getLotNumber()))
                        .collect(toList());

                for (Lot lot: allLotsForAuction) {
                    if (lot.getSalePrice() != null) {
                        lot.setSalePrice(lot.getSalePrice().setScale(2, RoundingMode.HALF_UP));
                    }
                }

                model.addAttribute("allLots", allLotsForAuction);


                BigDecimal totalSellerFees = calculateTotalSellerFees(allLotsForAuction);
                BigDecimal totalBuyerFees = calculateTotalBuyerFees(allLotsForAuction);


                model.addAttribute("totalRevenue", totalSellerFees.add(totalBuyerFees).setScale(2, RoundingMode.HALF_UP));
                model.addAttribute("totalSellerFees", totalSellerFees.setScale(2, RoundingMode.HALF_UP));
                model.addAttribute("totalBuyerFees", totalBuyerFees.setScale(2, RoundingMode.HALF_UP));
            }

            

        }

        
        model.addAttribute("name", principal.getName());
        model.addAttribute("userId", principal.getName());
        model.addAttribute("currentAuctionId", currentAuctionId);

        return "archive";

    }

}