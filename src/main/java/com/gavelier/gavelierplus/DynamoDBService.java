package com.gavelier.gavelierplus;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.gavelier.gavelierplus.domain.Auction;
import com.gavelier.gavelierplus.domain.Buyer;
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
        .sorted((seller1, seller2) -> Integer.compare(seller2.getSellerNumber(), seller1.getSellerNumber()))
        .collect(toList());
    }
    
    public List<Buyer> getAllBuyersForAuction(String currentAuctionId) {
		return dynamoDBRepository.getAllBuyersFromAuction(currentAuctionId).stream()
        .sorted((buyer1, buyer2) -> Integer.compare(buyer2.getBuyerNumber(), buyer1.getBuyerNumber()))
        .collect(toList());
	}

	public Auction saveAuction(Auction auction) {
        return dynamoDBRepository.saveAuction(auction);
    }
    
    public Lot getOneLotById(String lotId) {
        return dynamoDBRepository.getOneLotById(lotId);
    }

	public void createLot(Lot lot) {
        //Creating a new lot: in case of double form submission, we have to filter the existing lots
        //to check that we are not saving a lot with a duplicate number
        List<Lot> allExistingLots = getAllLotsForAuction(lot.getAuctionId());
        

        if(!allExistingLots.stream().filter(existingLot -> lot.getLotNumber()==existingLot.getLotNumber()).findFirst().isPresent()) {
            dynamoDBRepository.saveLot(lot);
        } else {
            LOGGER.info("lot number repeated " + lot);
        }


    }

    public void updateLot(Lot lot) {
        //In the case of updating the lot, it already exists with this number, so we just pass the object on
        //to the same method
        dynamoDBRepository.saveLot(lot);
    }

	public void setRepository(DynamoDBRepository dynamoDBRepository2) {
        this.dynamoDBRepository=dynamoDBRepository2;
	}

	public void deleteLot(Lot lot) {

        dynamoDBRepository.deleteLot(lot);
        
	}

	public void createSeller(Seller seller) {
        //Creating a new seller: in case of double form submission, we have to filter the existing sellers
        //to check that we are not saving a seller with a duplicate number
        List<Seller> allExistingSellers = getAllSellersForAuction(seller.getAuctionId());

        if(!allExistingSellers.stream().filter(existingSeller -> seller.getSellerNumber()==existingSeller.getSellerNumber()).findFirst().isPresent()) {
            dynamoDBRepository.save(seller);
        } else {
            LOGGER.info("Seller number repeated " + seller);
        }


    }

    public void updateBuyer(Buyer buyer) {
        //In the case of updating the buyer, it already exists with this number, so we just pass the object on
        //to the same method. Replacing is handled by the repository/mapper
        dynamoDBRepository.save(buyer);
    }

	public Buyer getOneBuyer(String buyerId) {
		return dynamoDBRepository.getOneBuyer(buyerId);
	}

	public void deleteBuyer(Buyer buyer) {
        LOGGER.info("Calling repository to delete buyer " + buyer);
        dynamoDBRepository.deleteBuyer(buyer);
    }
    
    public void createBuyer(Buyer buyer) {
        //Creating a new buyer: in case of double form submission, we have to filter the existing buyers
        //to check that we are not saving a buyer with a duplicate number
        List<Buyer> allExistingBuyers = getAllBuyersForAuction(buyer.getAuctionId());

        if(!allExistingBuyers.stream().filter(existingBuyer -> buyer.getBuyerNumber()==existingBuyer.getBuyerNumber()).findFirst().isPresent()) {
            dynamoDBRepository.save(buyer);
        } else {
            LOGGER.info("Buyer number repeated " + buyer);
        }


    }

    public void updateSeller(Seller seller) {
        //In the case of updating the seller, it already exists with this number, so we just pass the object on
        //to the same method
        dynamoDBRepository.save(seller);
    }

	public Seller getOneSeller(String sellerId) {
		return dynamoDBRepository.getOneSeller(sellerId);
	}

	public void deleteSeller(Seller seller) {
        LOGGER.info("Calling repository to delete seller " + seller);
        dynamoDBRepository.deleteSeller(seller);
	}
    


}