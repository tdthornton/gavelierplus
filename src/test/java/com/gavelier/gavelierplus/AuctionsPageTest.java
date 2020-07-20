package com.gavelier.gavelierplus;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

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
abstract class AuctionsTestController {
}

@RunWith(SpringRunner.class)
public class AuctionsPageTest extends AuctionsTestController {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        ApplicationContext context;

        private final static Logger LOGGER = Logger.getLogger(Pages.class.getName());

        @MockBean
        DynamoDBService mockService;

        @Test
        @WithMockUser
        public void testBasicLoadNewAuctionPage() throws Exception {

                // GET the new auction page, check that it loads correctly and is populated

                MvcResult result = mockMvc
                                .perform(get("/newauction")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andReturn();

                String pageHtml = result.getResponse().getContentAsString();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                Assert.assertTrue(pageHtml.contains("Start a new auction"));
                Assert.assertTrue(pageHtml.contains("name=\"date\" value=\"" + sdf.format(new Date()) + "\""));

        }

        @Test
        public void testLoadAuctionsPageNotSignedIn() throws Exception {

                // GET the new auction page without a user (this method is missing the user annotation)
                // We should be redirected to the homepage.

                mockMvc
                        .perform(get("/newauction")
                        .with(csrf()))
                        .andExpect(status().isFound()) // we're not logged in, so we should be redirected, not accepted.
                        .andReturn();

        }

}