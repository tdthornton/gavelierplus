package com.gavelier.gavelierplus;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.gavelier.gavelierplus.domain.Buyer;
import com.gavelier.gavelierplus.domain.Seller;

import java.net.URLEncoder;
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
abstract class BuyerControllerTest {
}

@RunWith(SpringRunner.class)
public class CreateBuyerTest extends BuyerControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        ApplicationContext context;

        @MockBean
        DynamoDBService mockService;

        @Test
        @WithMockUser
        public void testBasicCreateValidBuyer() throws Exception {

                // post a basic form submission to /createbuyer with a valid buyer, and see that the
                // successful redirect is performed without errors
                // also see that the mocked db service is called to process our lot.

                Buyer buyer = new Buyer("auction_1922p", 1, "Mrs. C. Seller", "01822638291", "740 Evergreen Terrace");

                MvcResult result = mockMvc
                                .perform(post("/createbuyer").content(utilGetFormParamsForBuyer(buyer)).with(csrf())
                                                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                                .andExpect(status().isFound()).andReturn();

                String redirectUrl = result.getResponse().getRedirectedUrl();

                verify(mockService, times(1)).createBuyer(buyer); // DynamoDB was called once to add our buyer
                                                                //specifically, the method that protects against duplicates is called

                Assert.assertTrue(redirectUrl.contains("/buyers?auctionId=" + buyer.getAuctionId())); // we are
                                                                                                     // redirected to
                                                                                                     // the right place
                Assert.assertFalse(redirectUrl.contains("&error=")); // we are redirected without errors

        }



        @Test
        @WithMockUser
        public void testCreateInvalidBuyer() throws Exception {

                // post a buyer without a name, and check we are redirected with error.

                Buyer buyer = new Buyer("auction_1922p", 1, "", "01822638291", "740 Evergreen Terrace");

                MvcResult result = mockMvc
                                .perform(post("/createbuyer").content(utilGetFormParamsForBuyer(buyer)).with(csrf())
                                                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                                .andExpect(status().isFound()).andReturn();

                String redirectUrl = result.getResponse().getRedirectedUrl();

                verify(mockService, times(0)).createBuyer(buyer); //invalid buyer should not be saved

                Assert.assertTrue(redirectUrl.contains("/buyers?auctionId=" + buyer.getAuctionId())); // we are
                                                                                                     // redirected to
                                                                                                     // the right place
                Assert.assertTrue(redirectUrl.contains("&error=" + URLEncoder.encode("buyerName: must not be blank.", "UTF-8"))); // we are redirected with errors

        }

        @Test
        @WithMockUser
        public void testEditBuyer() throws Exception {

                // post a valid buyer and see that it is saved.
                // edit the buyer, post it again, and see that it is saved and the db service update method is called

                Buyer buyer = new Buyer("auction_1922p", 1, "Mrs. C. Seller", "01822638291", "740 Evergreen Terrace");

                MvcResult result = mockMvc
                                .perform(post("/createbuyer").content(utilGetFormParamsForBuyer(buyer)).with(csrf())
                                                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                                .andExpect(status().isFound()).andReturn();

                String redirectUrl = result.getResponse().getRedirectedUrl();

                verify(mockService, times(1)).createBuyer(buyer); // DynamoDB was called once to add our buyer
                                                                //specifically, the method that protects against duplicates is called

                Assert.assertTrue(redirectUrl.contains("/buyers?auctionId=" + buyer.getAuctionId())); // we are
                                                                                                     // redirected to
                                                                                                     // the right place
                Assert.assertFalse(redirectUrl.contains("&error=" + URLEncoder.encode("somethings", "UTF-8"))); // we are redirected without errors



                //----- update scenario

                buyer.setBuyerContactNumber("38292837564");

                MvcResult updatedResult = mockMvc
                                .perform(post("/updatebuyer").content(utilGetFormParamsForBuyer(buyer)).with(csrf())
                                                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                                .andExpect(status().isFound()).andReturn();

                String updatedRedirectUri = updatedResult.getResponse().getRedirectedUrl();

                verify(mockService, times(1)).updateBuyer(buyer); //dynamo db update was called once with this new contact number as a field

                Assert.assertTrue(updatedRedirectUri.contains("/buyers?auctionId=" + buyer.getAuctionId())); // we are
                                                                                                     // redirected to
                                                                                                     // the right place
                Assert.assertFalse(updatedRedirectUri.contains("&error=")); // we are redirected without errors

        }



        @Test
        public void testBuyerSecurity() throws Exception {

                // post a good buyer, but with no mock user. Should not be saved.

                Buyer buyer = new Buyer("auction_1922p", 1, "Mr A. Buyer", "01822638291", "740 Evergreen Terrace");

                mockMvc.perform(post("/createbuyer").content(utilGetFormParamsForBuyer(buyer)).with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                                .andExpect(status().isFound()).andReturn();


                verify(mockService, times(0)).createBuyer(buyer); //invalid buyer should not be saved

                

        }


        private String utilGetFormParamsForBuyer(Buyer buyer)
                        throws ParseException, UnsupportedEncodingException, IOException {
                return EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                                new BasicNameValuePair("id", buyer.getId()),
                                new BasicNameValuePair("auctionId", buyer.getAuctionId()),
                                new BasicNameValuePair("buyerNumber", String.valueOf(buyer.getBuyerNumber())),
                                new BasicNameValuePair("buyerName", String.valueOf(buyer.getBuyerName())),
                                new BasicNameValuePair("buyerContactNumber", buyer.getBuyerContactNumber()),
                                new BasicNameValuePair("buyerAddress", buyer.getBuyerAddress())
                )));
        }

        

}