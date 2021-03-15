package com.gavelier.gavelierplus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Date;

import com.gavelier.gavelierplus.domain.Auction;
import com.gavelier.gavelierplus.domain.Lot;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class FeesTest {
        
        private EndpointUtils endpointUtils;

        @Test
        public void testBasicApplyFees() throws Exception {

                // test that the formula for calculating seller fees works for a given set of
                // auction fees

                endpointUtils = new EndpointUtils();

                Lot lot = new Lot("auctionId_8217833p", 1, 2, "A mixed box of interesting items.", "20", "", 1,
                                new BigDecimal("10.00"), new BigDecimal("0.00"), new BigDecimal("0.00"));

                Lot copyLotWithFees = new Lot("auctionId_8217833p", 1, 2, "A mixed box of interesting items.", "20", "", 1,
                                new BigDecimal("10.00"), new BigDecimal("0.00"), new BigDecimal("0.00"));

                Auction auction = new Auction("userId_333", "Test Company", new Date(), 10, new BigDecimal("0.00"), new BigDecimal("0.50"), 10, new BigDecimal("1.00"), new BigDecimal("0.00"));

                copyLotWithFees.setBuyerFees(new BigDecimal("1.00"));
                copyLotWithFees.setCostToBuyer(new BigDecimal("11.00"));
                copyLotWithFees.setSellerFees(new BigDecimal("1.50"));
                copyLotWithFees.setPaymentToSeller(new BigDecimal("8.50"));

                lot = endpointUtils.applySellerFees(lot, auction);
                lot = endpointUtils.applyBuyerFees(lot, auction);

                assertEquals(copyLotWithFees, lot);

        }

        @Test
        public void testApplyFeesHighPrice() throws Exception {

                // test that the formula for calculating seller fees works for a given set of
                // auction fees

                endpointUtils = new EndpointUtils();

                Lot lot = new Lot("auctionId_8217833p", 1, 2, "A mixed box of interesting items.", "20", "", 1,
                                new BigDecimal("222227.99"), new BigDecimal("0.00"), new BigDecimal("0.00"));

                Lot copyLotWithFees = new Lot("auctionId_8217833p", 1, 2, "A mixed box of interesting items.", "20", "", 1,
                                new BigDecimal("222227.99"), new BigDecimal("0.00"), new BigDecimal("0.00"));

                Auction auction = new Auction("userId_333", "Test Company", new Date(), 10, new BigDecimal("0.00"), new BigDecimal("0.50"), 10, new BigDecimal("1.00"), new BigDecimal("0.00"));

                copyLotWithFees.setBuyerFees(new BigDecimal("22222.80"));
                copyLotWithFees.setCostToBuyer(new BigDecimal("244450.79"));
                copyLotWithFees.setSellerFees(new BigDecimal("22223.30"));
                copyLotWithFees.setPaymentToSeller(new BigDecimal("200004.69"));

                lot = endpointUtils.applySellerFees(lot, auction);
                lot = endpointUtils.applyBuyerFees(lot, auction);

                assertEquals(copyLotWithFees, lot);

        }

}