package com.gavelier.gavelierplus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.gavelier.gavelierplus.domain.Auction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DynamoDBRepository {

    private final static Logger LOGGER = Logger.getLogger(Pages.class.getName());

    @Autowired
    private DynamoDBMapper mapper;

    public void insertIntoDynamoDB(Auction auction) {
        mapper.save(auction);
    }

    public List<Auction> allAuctionsForUserId(String userId) {

        LOGGER.info("Attempting to scan db for all auctions for user " + userId);
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS(userId));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression("userId = :val1").withExpressionAttributeValues(eav);

        List<Auction> scanResult = mapper.scan(Auction.class, scanExpression);
        
        return scanResult;


    }

}