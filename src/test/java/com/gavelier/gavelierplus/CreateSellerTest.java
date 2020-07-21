package com.gavelier.gavelierplus;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.logging.Logger;
import com.gavelier.gavelierplus.domain.Seller;

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
abstract class SellerControllerTest {
}

@RunWith(SpringRunner.class)
public class CreateSellerTest extends SellerControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        ApplicationContext context;

        private final static Logger LOGGER = Logger.getLogger(Pages.class.getName());

        @MockBean
        DynamoDBService mockService;

        @Test
        @WithMockUser
        public void testBasicCreateValidLot() throws Exception {

                // post a basic form submission to /createseller with a valid lot, and see that the
                // successful redirect is performed without errors

                Seller seller = new Seller("auction_19292", 1, "Mr. A. Seller", "01826638291", "742 Evergreen Terrace");

                MvcResult result = mockMvc
                                .perform(post("/createseller").content(utilGetFormParamsForSeller(seller)).with(csrf())
                                                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                                .andExpect(status().isFound()).andReturn();

                String redirectUrl = result.getResponse().getRedirectedUrl();

                verify(mockService, times(1)).createSeller(seller); // DynamoDB was called once to add our seller

                Assert.assertTrue(redirectUrl.contains("/sellers?auctionId=" + seller.getAuctionId())); // we are
                                                                                                     // redirected to
                                                                                                     // the right place
                Assert.assertFalse(redirectUrl.contains("&error=")); // we are redirected without errors

        }

        @Test
        @WithMockUser
        public void testInvalidCreateLot() throws Exception {

                // post a basic form submission to /createseller with an invalid lot
                // we should be redirected with errors, and the db should not be called

                Seller seller = new Seller("", 1, "Mr. A. Seller", "01826638291", "742 Evergreen Terrace");

                MvcResult result = mockMvc
                                .perform(post("/createseller").content(utilGetFormParamsForSeller(seller)).with(csrf())
                                                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                                .andExpect(status().isFound()).andReturn();

                String redirectUrl = result.getResponse().getRedirectedUrl();

                verify(mockService, times(0)).createSeller(seller); // DynamoDB was called once to add our seller

                Assert.assertTrue(redirectUrl.contains("/sellers?auctionId=" + seller.getAuctionId())); // we are
                                                                                                     // redirected to
                                                                                                     // the right place
                Assert.assertTrue(redirectUrl.contains("&error=")); // and the url parameters will be displayed to tell the user about the validation error.

        }

        @Test
        public void testCreateLotNotLoggedIn() throws Exception {

                // post a seller to /createseller without the user being logged in
                // spring security should not allow this.

                //(Difference is no @WithMockUser annotation on this method.)

                Seller seller = new Seller("auction_19292", 1, "Mr. A. Seller", "01826638291", "742 Evergreen Terrace");

                MvcResult result = mockMvc
                                .perform(post("/createseller").content(utilGetFormParamsForSeller(seller)).with(csrf())
                                                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                                .andExpect(status().isFound()).andReturn();

                verify(mockService, times(0)).createSeller(seller); // DynamoDB was not asked to save the seller

        }

        private String utilGetFormParamsForSeller(Seller seller)
                        throws ParseException, UnsupportedEncodingException, IOException {
                return EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                                new BasicNameValuePair("id", seller.getId()),
                                new BasicNameValuePair("auctionId", seller.getAuctionId()),
                                new BasicNameValuePair("sellerNumber", String.valueOf(seller.getSellerNumber())),
                                new BasicNameValuePair("sellerName", String.valueOf(seller.getSellerName())),
                                new BasicNameValuePair("sellerContactNumber", seller.getSellerContactNumber()),
                                new BasicNameValuePair("sellerAddress", seller.getSellerAddress())
                )));
        }

}