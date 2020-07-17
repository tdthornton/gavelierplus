package com.gavelier.gavelierplus;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.gavelier.gavelierplus.domain.Auction;
import com.gavelier.gavelierplus.domain.Lot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DynamoDBService {

    @Autowired
    DynamoDBRepository dynamoDBRepository;

    public List<Auction> getAllAuctionsForUserInDateOrder(String userId) {

        List<Auction> allAuctionsForUser = dynamoDBRepository.allAuctionsForUserId(userId);

        return allAuctionsForUser.stream().distinct().sorted(new Comparator<Auction>() {
            public int compare(Auction o1, Auction o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        }).collect(Collectors.toList());

    }

	public Auction getOneAuctionById(String currentAuctionId, String userId) {
		return dynamoDBRepository.getOneAuctionById(currentAuctionId, userId);
	}

	public List<Lot> getAllLotsForAuction(String currentAuctionId) {
		return dynamoDBRepository.getAllLotsForAuction(currentAuctionId);
	}

	public void insertIntoDynamoDB(Auction auction) {
        dynamoDBRepository.insertIntoDynamoDB(auction);
	}

	public void createLot(@Valid Lot lot) {
        dynamoDBRepository.createLot(lot);
	}

	public void setRepository(DynamoDBRepository dynamoDBRepository2) {
        this.dynamoDBRepository=dynamoDBRepository2;
	}

}