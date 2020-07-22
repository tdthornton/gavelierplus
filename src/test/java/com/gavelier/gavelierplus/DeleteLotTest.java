package com.gavelier.gavelierplus;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Arrays;

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
abstract class BaseDeleteLotControllerTest {
}

@RunWith(SpringRunner.class)
public class DeleteLotTest extends BaseDeleteLotControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        ApplicationContext context;

        @MockBean
        DynamoDBService mockService;


        @Test
        @WithMockUser
        public void testBasicDeleteLot() throws Exception {

                // post a basic form submission to /deletelot with a valid lot, and see that the
                // successful redirect is performed without errors
                // and the db service is called to delete the lot by the endpoint


                Lot lot = new Lot("auctionId_8217833p", 1, 2, "A mixed box of interesting items.", "20", "",
                                new BigDecimal("0.00"), new BigDecimal("0.00"), new BigDecimal("0.00"));

                MvcResult result = mockMvc
                                .perform(post("/deletelot").content(utilGetFormParamsForLot(lot)).with(csrf())
                                                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                                                .andExpect(status().isFound())
                                                .andReturn();

                String redirectUrl = result.getResponse().getRedirectedUrl();

                verify(mockService, times(1)).deleteLot(lot); //DynamoDB was called once to add our lot

                Assert.assertTrue(redirectUrl.contains("/lots?auctionId=" + lot.getAuctionId())); //we are redirected to the right place
                Assert.assertFalse(redirectUrl.contains("&error=")); //we are redirected without errors

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