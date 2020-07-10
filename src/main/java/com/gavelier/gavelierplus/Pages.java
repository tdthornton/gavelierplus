package com.gavelier.gavelierplus;

import java.security.Principal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.gavelier.gavelierplus.domain.Auction;

@Controller
public class Pages {

    private final static Logger LOGGER = Logger.getLogger(Pages.class.getName());

    @Autowired
    DynamoDBRepository dynamoDBRepository;

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

    @GetMapping("/restricted")
    public String restricted(Principal principal, Model model) {

        LOGGER.info("Access to restricted page");

        model.addAttribute("name", principal.getName());

        return "restricted";

    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {

        LOGGER.info("Access to dashboard page");

        model.addAttribute("name", principal.getName());

        return "dashboard";

    }

    @GetMapping("/newauction")
    public String newAuction(Principal principal, Model model) {

        LOGGER.info("Access to new auction page");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        


        model.addAttribute("today", sdf.format(date));
        LOGGER.info("DATE: " + sdf.format(date));
        model.addAttribute("name", principal.getName());
        model.addAttribute("userId", principal.getName());

        return "newauction";

    }

    @GetMapping("/lots")
    public String lots(Principal principal, Model model) {

        LOGGER.info("Access to lots page");

        
        String currentAuctionId = "7a33721d-c0e8-4279-b5f7-ddcd861eaa64";

        Auction currentAuction = dynamoDBRepository.getOneAuctionById(currentAuctionId, principal.getName());

        model.addAttribute("name", principal.getName());
        model.addAttribute("currentAuctionId", currentAuctionId);
        model.addAttribute("lotsForCurrentAuction", dynamoDBRepository.getAllLotsForAuction(currentAuctionId));
        model.addAttribute("allAuctionsForUser", getAllAuctionsForUserInDateOrder(principal.getName()));

        return "lots";

    }

    @GetMapping("/sellers")
    public String sellers(Principal principal, Model model) {

        LOGGER.info("Access to sellers page");

        model.addAttribute("name", principal.getName());

        return "sellers";

    }

    @GetMapping("/buyers")
    public String bueyrs(Principal principal, Model model) {

        LOGGER.info("Access to buyers page");

        model.addAttribute("name", principal.getName());

        return "buyers";

    }

    @GetMapping("/auctioneering")
    public String auctioneering(Principal principal, Model model) {

        LOGGER.info("Access to auctioneering page");

        model.addAttribute("name", principal.getName());

        return "auctioneering";

    }

    @GetMapping("/documents")
    public String documents(Principal principal, Model model) {

        LOGGER.info("Access to documents page");

        model.addAttribute("name", principal.getName());

        return "documents";

    }

    @GetMapping("/archive")
    public String archive(Principal principal, Model model) {

        LOGGER.info("Access to archive page");

        model.addAttribute("name", principal.getName());

        return "archive";

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