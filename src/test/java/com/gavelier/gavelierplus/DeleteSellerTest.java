package com.gavelier.gavelierplus;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gavelier.gavelierplus.domain.Seller;

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
abstract class SellerDeleteControllerTest {
}

@RunWith(SpringRunner.class)
public class DeleteSellerTest extends SellerDeleteControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        ApplicationContext context;
        @MockBean
        DynamoDBService mockService;

        @Test
        @WithMockUser
        public void testBasicCreateValidLot() throws Exception {

                // post a basic form submission to /createseller with a valid lot, and see that the
                // successful redirect is performed without errors

                Seller seller = new Seller("auction_1s3392", 1, "Mr. A. Seller", "01826638291", "742 Evergreen Terrace");

                MvcResult result = mockMvc
                                .perform(post("/createseller").content(CreateSellerTest.utilGetFormParamsForSeller(seller)).with(csrf())
                                                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                                .andExpect(status().isFound()).andReturn();

                String redirectUrl = result.getResponse().getRedirectedUrl();

                verify(mockService, times(1)).createSeller(seller); // DynamoDB was called once to add our seller

                Assert.assertTrue(redirectUrl.contains("/sellers?auctionId=" + seller.getAuctionId())); // we are
                                                                                                     // redirected to
                                                                                                     // the right place
                Assert.assertFalse(redirectUrl.contains("&error=")); // we are redirected without errors



                //delete phase

                MvcResult deleteResult = mockMvc
                                .perform(post("/deleteseller").content(CreateSellerTest.utilGetFormParamsForSeller(seller)).with(csrf())
                                                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                                .andExpect(status().isFound()).andReturn();

                String deleteRedirectUrl = deleteResult.getResponse().getRedirectedUrl();

                verify(mockService, times(1)).deleteSeller(seller); // Delete seller called once

                Assert.assertTrue(deleteRedirectUrl.contains("/sellers?auctionId=" + seller.getAuctionId())); // we are
                                                                                                        // redirected to
                                                                                                        // the right place
                Assert.assertFalse(deleteRedirectUrl.contains("&error=")); // we are redirected without errors

        }


}