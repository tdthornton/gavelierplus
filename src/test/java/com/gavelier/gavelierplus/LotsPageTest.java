package com.gavelier.gavelierplus;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.gavelier.gavelierplus.domain.Auction;
import com.gavelier.gavelierplus.domain.Lot;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles({ "default", "local", "test" })
abstract class LotsPageTestController {
}

@RunWith(SpringRunner.class)
public class LotsPageTest extends LotsPageTestController {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        ApplicationContext context;

        @Autowired
        DynamoDBService service;

        @MockBean
        DynamoDBRepository mockRepository;

        @Test
        @WithMockUser("user")
        public void testBasicLoadLotsPage() throws Exception {

                // GET the lots page, check that it loads correctly and is populated
                // This page will call the db for a list of all lots relating to a query param,
                // auctionId

                String auctionId = "test_auction_88839";

                Auction auction1 = new Auction();
                auction1.setDate(new GregorianCalendar(2018, Calendar.JANUARY, 6).getTime());
                auction1.setId(auctionId);
                auction1.setInputCompanyName("Rome auction house");

                Auction auction2 = new Auction();
                auction2.setDate(new GregorianCalendar(2019, Calendar.APRIL, 8).getTime());

                Auction auction3 = new Auction();
                auction3.setDate(new GregorianCalendar(2020, Calendar.SEPTEMBER, 10).getTime());


                //unordered result from db scan - returned from mock db
                List<Auction> auctionsFromDatabase = new ArrayList<Auction>();
                auctionsFromDatabase.add(auction3);
                auctionsFromDatabase.add(auction1);
                auctionsFromDatabase.add(auction2);

                Auction auction = new Auction(auctionId, "user", "Rome auction house", new Date(), 10, null, null, 0, null, null);

                Lot lot1 = new Lot(auctionId, 1, 1, "Old lawnmower, no longer working.", "", "£20", null, null, null);
                Lot lot2 = new Lot(auctionId, 2, 1, "Set of watches.", "", "£20", null, null, null);
                Lot lot3 = new Lot(auctionId, 3, 1, "A mixed box, some interesting items in there.", "", "£20", null, null, null);

                List<Lot> lotsFromDatabase = new ArrayList<Lot>();
                lotsFromDatabase.add(lot1);
                lotsFromDatabase.add(lot2);
                lotsFromDatabase.add(lot3);

                when(mockRepository.getAllLotsForAuction(auctionId)).thenReturn(lotsFromDatabase);
                when(mockRepository.getOneAuctionById(auctionId, "user")).thenReturn(auction);
                when(mockRepository.allAuctionsForUserId("user")).thenReturn(auctionsFromDatabase);

                MvcResult result = mockMvc
                                .perform(get("/lots?auctionId=" + auctionId)
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andReturn();

                String pageHtml = result.getResponse().getContentAsString();

                Assert.assertTrue(pageHtml.contains("Lots for Rome auction house")); //the auction is loaded properly
                Assert.assertTrue(pageHtml.contains("<td>A mixed box, some interesting items in there.</td>"));
                Assert.assertTrue(pageHtml.contains("<td>Set of watches.</td>"));
                Assert.assertTrue(pageHtml.contains("<td>Old lawnmower, no longer working.</td>")); //lots for the auction are loaded properly
                Assert.assertTrue(pageHtml.contains("<a class=\"dropdown-item\" href=\"/lots?auctionId=test_auction_88839\">Rome auction house - 06-Jan-2018</a>")); //options to switch auction are loaded properly

        }

        @Test
        @WithMockUser("user")
        public void testLoadLotsPageNoAuction() throws Exception {

                // GET the lots page with no auction id
                // It will just show us an empty page and the option to select a new auction

                String auctionId= "";

                when(mockRepository.getOneAuctionById(auctionId, "user")).thenReturn(null);

                MvcResult result = mockMvc
                                .perform(get("/lots?auctionId=" + auctionId)
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andReturn();

                String pageHtml = result.getResponse().getContentAsString();

                //There was no auction given, so we can't show any lots. Just an empty page and a prompt to select an auction.
                Assert.assertTrue(pageHtml.contains("Select an auction from the menu to see and create lots."));

        }

        @Test
        public void testLoadLotsPageNotSignedIn() throws Exception {

                // GET the lots page without a user (this method is missing the user annotation)
                // We should be redirected to the homepage.

                mockMvc
                        .perform(get("/lots")
                        .with(csrf()))
                        .andExpect(status().isFound()) // we're not logged in, so we should be redirected, not accepted.
                        .andReturn();

        }

}