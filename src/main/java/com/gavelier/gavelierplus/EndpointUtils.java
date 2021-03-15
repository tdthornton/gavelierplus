package com.gavelier.gavelierplus;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.gavelier.gavelierplus.domain.Auction;
import com.gavelier.gavelierplus.domain.Lot;


public class EndpointUtils {

    public EndpointUtils() {

    }

    public Lot applyBuyerFees(Lot lot, Auction auction) {

        BigDecimal totalBuyerFees = new BigDecimal("0.00");

        BigDecimal feeUnderTen = new BigDecimal("0.00");

        if (auction.getInputBuyerFeeMinimum() != null) {
            feeUnderTen = auction.getInputBuyerFeeMinimum().setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal fixedAdditionalFee = new BigDecimal("0.00");

        if (auction.getInputBuyerFeeFixed() != null) {
            fixedAdditionalFee = auction.getInputBuyerFeeFixed().setScale(2, RoundingMode.HALF_UP);
        } else {
            fixedAdditionalFee = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_UP);
        }

        totalBuyerFees = totalBuyerFees.add(fixedAdditionalFee);

        if (lot.getSalePrice() != null) {

            BigDecimal percentageFee = lot.getSalePrice()
                    .multiply(new BigDecimal("0." + auction.getInputBuyerFeePercentage()))
                    .setScale(2, RoundingMode.HALF_UP);

            if (lot.getSalePrice().compareTo(new BigDecimal("10.00")) > 0) {
                totalBuyerFees = totalBuyerFees.add(percentageFee);
            } else {
                totalBuyerFees = totalBuyerFees.add(feeUnderTen);
            }

            lot.setCostToBuyer(lot.getSalePrice().add(totalBuyerFees).setScale(2, RoundingMode.HALF_UP));
            lot.setSalePrice(lot.getSalePrice().setScale(2, RoundingMode.HALF_UP));

        }

        lot.setBuyerFees(totalBuyerFees.setScale(2, RoundingMode.HALF_UP));

        return lot;
    }

    public Lot applySellerFees(Lot lot, Auction auction) {

        BigDecimal finalPaymentToSeller;

        BigDecimal minimumFee = new BigDecimal("0.00");
        BigDecimal totalFees = new BigDecimal("0.00");

        if (auction.getInputSellerFeeMinimum() != null) {
            minimumFee = auction.getInputSellerFeeMinimum().setScale(2, RoundingMode.HALF_UP);
        }

        if (auction.getInputSellerFeeFixed() != null) {
            totalFees = totalFees.add(auction.getInputSellerFeeFixed());
        }

        if (lot.getSalePrice() != null) {

            finalPaymentToSeller = lot.getSalePrice().setScale(2, RoundingMode.HALF_UP);

            BigDecimal percentageFee = lot.getSalePrice()
                    .multiply(new BigDecimal("0." + auction.getInputSellerFeePercentage()))
                    .setScale(2, RoundingMode.HALF_UP);

            if (percentageFee.compareTo(minimumFee) > 0) {
                totalFees = totalFees.add(percentageFee);
            } else {
                totalFees = totalFees.add(minimumFee);
            }

        } else {

            finalPaymentToSeller = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_UP);
        }

        lot.setSellerFees(totalFees);

        lot.setPaymentToSeller(finalPaymentToSeller.subtract(totalFees));

        return lot;
    }

    public boolean isValidSale(Lot lot) {

        if (lot.getBuyerNumber() < 1 && (lot.getSalePrice() != null && lot.getSalePrice() != new BigDecimal("0.00")) && lot.getSalePrice() != new BigDecimal("0")) {
            return false;
        }

        if (lot.getBuyerNumber() > 0 && (lot.getSalePrice() == null || lot.getSalePrice() == new BigDecimal("0.00")) || lot.getSalePrice() == new BigDecimal("0") || lot.getSalePrice().equals(new BigDecimal("0.00")) || lot.getSalePrice().equals(new BigDecimal("0"))) {
            return false;
        }

        return true;
    }
}