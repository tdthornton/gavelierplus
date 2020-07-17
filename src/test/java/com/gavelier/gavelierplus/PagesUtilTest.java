package com.gavelier.gavelierplus;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

import com.gavelier.gavelierplus.domain.Auction;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles({ "default", "local", "test" })
abstract class PagesTest {
}

@RunWith(SpringRunner.class)
public class PagesUtilTest extends PagesTest {
        

        private final static Logger LOGGER = Logger.getLogger(Pages.class.getName());

        @Autowired
        DynamoDBService service;

        @MockBean
        DynamoDBRepository mockRepository;

        @Test
        public void testGetAllAuctionsInOrder() {

                //Create list of auctions, out of date order, for the db stub to return
                //Call the service for all the auctions for that user
                //Verify the repository was called to fulfill that db scan
                //Verify that the service sorted the result for us

                Auction auction1 = new Auction();
                auction1.setDate(new GregorianCalendar(2018, Calendar.JANUARY, 6).getTime());

                Auction auction2 = new Auction();
                auction2.setDate(new GregorianCalendar(2019, Calendar.APRIL, 8).getTime());

                Auction auction3 = new Auction();
                auction3.setDate(new GregorianCalendar(2020, Calendar.SEPTEMBER, 10).getTime());


                //unordered result from db scan - returned from mock db
                List<Auction> auctionsFromDatabase = List.of(auction3, auction1, auction2);
                
                //sorted return by service - our expected result
                List<Auction> auctionsInDateOrder = List.of(auction1, auction2, auction3);


                when(mockRepository.allAuctionsForUserId("user")).thenReturn(auctionsFromDatabase);

                List<Auction> resultAuctions = service.getAllAuctionsForUserInDateOrder("user");

                verify(mockRepository, times(1)).allAuctionsForUserId("user");

                Assert.assertEquals(auctionsInDateOrder, resultAuctions);


        }

       
}