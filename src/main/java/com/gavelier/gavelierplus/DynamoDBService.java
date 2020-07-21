package com.gavelier.gavelierplus;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.gavelier.gavelierplus.domain.Auction;
import com.gavelier.gavelierplus.domain.Lot;
import com.gavelier.gavelierplus.domain.Seller;

import static java.util.stream.Collectors.toList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DynamoDBService {

    @Autowired
    DynamoDBRepository dynamoDBRepository;

    private final static Logger LOGGER = Logger.getLogger(DynamoDBService.class.getName());

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
    
    public List<Seller> getAllSellersForAuction(String currentAuctionId) {
		return dynamoDBRepository.getAllSellersFromAuction(currentAuctionId).stream()
        .sorted((lot1, lot2) -> Integer.compare(lot2.getSellerNumber(), lot1.getSellerNumber()))
        .collect(toList());
	}

	public Auction saveAuction(Auction auction) {
        return dynamoDBRepository.saveAuction(auction);
    }
    
    public Lot getOneLotById(String lotId) {
        return dynamoDBRepository.getOneLotById(lotId);
    }

	public void createLot(Lot lot) {
        dynamoDBRepository.createLot(lot);
	}

	public void setRepository(DynamoDBRepository dynamoDBRepository2) {
        this.dynamoDBRepository=dynamoDBRepository2;
	}

	public void deleteLot(Lot lot) {

        dynamoDBRepository.deleteLot(lot);
        
	}

	public void createSeller(Seller seller) {

        List<Seller> allExistingSellers = getAllSellersForAuction(seller.getAuctionId());

        if(!allExistingSellers.stream().filter(existingSeller -> seller.getSellerNumber()==existingSeller.getSellerNumber()).findFirst().isPresent()) {
            dynamoDBRepository.save(seller);
        } else {
            LOGGER.info("Seller number repeated " + seller);
        }

        



	}

}