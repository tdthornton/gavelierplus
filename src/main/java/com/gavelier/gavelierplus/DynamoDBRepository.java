package com.gavelier.gavelierplus;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.gavelier.gavelierplus.domain.NewAuctionRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DynamoDBRepository {

    @Autowired
    private DynamoDBMapper mapper;

    public void insertIntoDynamoDB(NewAuctionRequest newAuctionRequest) {
        mapper.save(newAuctionRequest);
    }

}