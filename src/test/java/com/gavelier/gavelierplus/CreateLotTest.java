package com.gavelier.gavelierplus;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.logging.Logger;

import com.gavelier.gavelierplus.domain.Lot;

import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles({ "default", "local", "test" })
abstract class BaseControllerTest {
}

@RunWith(SpringRunner.class)
public class CreateLotTest extends BaseControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        ApplicationContext context;

        private final static Logger LOGGER = Logger.getLogger(Pages.class.getName());

        @MockBean
        DynamoDBService mockService;

        @Test
        public void testDbRepositoryCalled() {
                verify(mockService, times(0)).createLot(any(Lot.class));
        }

        @Test
        @WithMockUser
        public void testBasicCreateValidLot() throws Exception {

                // post a basic form submission to /createlot with a valid lot, and see that the
                // successful redirect is performed without errors

                Lot lot = new Lot("auctionId_192328j", 1, 2, "A mixed box of interesting items.", "20", "",
                                new BigDecimal("0.00"), new BigDecimal("0.00"), new BigDecimal("0.00"));

                MvcResult result = mockMvc
                                .perform(post("/createlot").content(utilGetFormParamsForLot(lot)).with(csrf())
                                                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                                                .andExpect(status().isFound())
                                                .andReturn();

                String redirectUrl = result.getResponse().getRedirectedUrl();

                verify(mockService, times(1)).createLot(lot); //DynamoDB was called once to add our lot

                Assert.assertTrue(redirectUrl.contains("/lots?auctionId=" + lot.getAuctionId())); //we are redirected to the right place
                Assert.assertFalse(redirectUrl.contains("&error=")); //we are redirected without errors

        }

        @Test
        @WithMockUser
        public void testCreateLotErrorNoSellerNumber() throws Exception {

                // post a lot with a missing auctionId and check that it's rejected.

                Lot lot = new Lot("auctionId_192328j", 0, 2, "A mixed box of interesting items.", "£20", "", new BigDecimal("0.00"),
                                new BigDecimal("0.00"), new BigDecimal("0.00"));

                MvcResult result = mockMvc
                                .perform(post("/createlot").content(utilGetFormParamsForLot(lot))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED))  
                                .andExpect(status().isFound())     
                                .andReturn();

                String redirectUrl = result.getResponse().getRedirectedUrl();

                verify(mockService, times(0)).createLot(any(Lot.class));

                Assert.assertTrue(redirectUrl.contains("/lots?auctionId=" + lot.getAuctionId()));
                Assert.assertTrue(redirectUrl.contains("&error="));
                Assert.assertTrue(redirectUrl.contains(URLEncoder.encode("The seller number is too low.", "UTF-8")));

        }

        @Test
        @WithMockUser
        public void testCreateLotErrorNoDesc() throws Exception {

                // post a lot with a missing auctionId and check that it's rejected.

                Lot lot = new Lot("auctionId_192328j", 1, 2, "", "£20", "", new BigDecimal("0.00"),
                                new BigDecimal("0.00"), new BigDecimal("0.00"));

                MvcResult result = mockMvc
                                .perform(post("/createlot").content(utilGetFormParamsForLot(lot))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED))  
                                .andExpect(status().isFound())     
                                .andReturn();

                String redirectUrl = result.getResponse().getRedirectedUrl();

                verify(mockService, times(0)).createLot(any(Lot.class));

                Assert.assertTrue(redirectUrl.contains("/lots?auctionId=" + lot.getAuctionId()));
                Assert.assertTrue(redirectUrl.contains("&error="));
                Assert.assertTrue(redirectUrl.contains(URLEncoder.encode("The description cannot be empty.", "UTF-8")));
                Assert.assertFalse(redirectUrl.contains(URLEncoder.encode("The seller number is too low.", "UTF-8")));

        }

        @Test
        @WithMockUser
        public void testCreateLotErrorNullDesc() throws Exception {

                // post a lot with a missing auctionId and check that it's rejected.

                Lot lot = new Lot("auctionId_192328j", 1, 2, null, "£20", "", new BigDecimal("0.00"),
                                new BigDecimal("0.00"), new BigDecimal("0.00"));

                MvcResult result = mockMvc
                                .perform(post("/createlot").content(utilGetFormParamsForLot(lot))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED))  
                                .andExpect(status().isFound())     
                                .andReturn();

                String redirectUrl = result.getResponse().getRedirectedUrl();

                verify(mockService, times(0)).createLot(any(Lot.class));

                Assert.assertTrue(redirectUrl.contains("/lots?auctionId=" + lot.getAuctionId()));
                Assert.assertTrue(redirectUrl.contains("&error="));
                Assert.assertTrue(redirectUrl.contains(URLEncoder.encode("The description cannot be empty.", "UTF-8")));
                Assert.assertFalse(redirectUrl.contains(URLEncoder.encode("The seller number is too low.", "UTF-8")));

        }

        private String utilGetFormParamsForLot(Lot lot)
                        throws ParseException, UnsupportedEncodingException, IOException {
                return EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                                new BasicNameValuePair("auctionId", lot.getAuctionId()),
                                new BasicNameValuePair("sellerNumber", String.valueOf(lot.getSellerNumber())),
                                new BasicNameValuePair("lotNumber", String.valueOf(lot.getLotNumber())),
                                new BasicNameValuePair("desc", lot.getDesc()),
                                new BasicNameValuePair("estimate", lot.getEstimate()),
                                new BasicNameValuePair("reserve", lot.getReserve()),
                                new BasicNameValuePair("salePrice", lot.getSalePrice().toString()),
                                new BasicNameValuePair("costToBuyer", lot.getCostToBuyer().toString()),
                                new BasicNameValuePair("paymentToSeller", lot.getPaymentToSeller().toString()))));
        }
}