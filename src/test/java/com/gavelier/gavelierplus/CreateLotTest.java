package com.gavelier.gavelierplus;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gavelier.gavelierplus.domain.Lot;

import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

    private final static Logger LOGGER = Logger.getLogger(Pages.class.getName());

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    public void testBasicCreateValidLot() throws Exception {

        //post a basic form submission to /createlot with a valid lot, and see that the successful redirect is performed without errors

        Lot lot = new Lot("auctionId_192328j", 1, 2, "A mixed box of interesting items.", "£20", "",
                new BigDecimal("10.00"), new BigDecimal("11.00"), new BigDecimal("8.50"));

        String json = objectMapper.writeValueAsString(lot);

        MvcResult result = mockMvc
                .perform(post("/createlot").content(utilGetFormParamsForLot(lot)).with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(redirectedUrl("/lots?auctionId=" + lot.getAuctionId())).andReturn();

                //This expected redirect also encompasses error checking. If there is a binding error, the redirect url includes it under &error=

    }

    private String utilGetFormParamsForLot(Lot lot) throws ParseException, UnsupportedEncodingException, IOException {
        return EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
            new BasicNameValuePair("auctionId", lot.getAuctionId()),
            new BasicNameValuePair("sellerNumber", String.valueOf(lot.getSellerNumber())),
            new BasicNameValuePair("lotNumber", String.valueOf(lot.getLotNumber())),
            new BasicNameValuePair("desc", "a box"),
            new BasicNameValuePair("estimate", ""),
            new BasicNameValuePair("reserve", ""),
            new BasicNameValuePair("salePrice", ""),
            new BasicNameValuePair("costToBuyer", ""),
            new BasicNameValuePair("paymentToSeller", ""))));
    }
}