package com.gavelier.gavelierplus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.gavelier.gavelierplus.domain.Auction;
import com.gavelier.gavelierplus.domain.Lot;

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

    public void createLot(Lot lot) {
        mapper.save(lot);
    }

    public Auction getOneAuctionById(String auctionId, String userId) {
        return mapper.load(Auction.class, auctionId, userId);
    }

    public List<Lot> getAllLotsForAuction(String auctionId) {
        List<Lot> lots = null;

        try{
            Lot partitionKey = new Lot();
            partitionKey.setAuctionId(auctionId);
            DynamoDBQueryExpression<Lot> queryExpression = new DynamoDBQueryExpression<>();
            queryExpression.setHashKeyValues(partitionKey);
            queryExpression.setIndexName("auctionId");
            queryExpression.setConsistentRead(false);
    
            lots = mapper.query(Lot.class, queryExpression);
        } catch (Exception e){
            LOGGER.info("Exception querying datasource for gsiField " +  auctionId);
            throw e;
        }

        LOGGER.info("LIST OF LOTS FOR AUCTION " + auctionId + ": ");
        LOGGER.info(lots.toString());
    
        return lots;
    
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