package com.gavelier.gavelierplus.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

public class NewAuctionRequest {

    private String inputCompanyName;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date date;

    private int inputSellerFeePercentage;
    private BigDecimal inputSellerFeeMinimum;
    private BigDecimal inputSellerFeeFixed;
    private int inputBuyerFeePercentage;
    private BigDecimal inputBuyerFeeMinimum;
    private BigDecimal inputBuyerFeeFixed;

    public NewAuctionRequest() {
    }

    public NewAuctionRequest(String inputCompanyName, Date date, 
            int inputSellerFeePercentage,
            BigDecimal inputSellerFeeMinimum, BigDecimal inputSellerFeeFixed, 
            int inputBuyerFeePercentage,
            BigDecimal inputBuyerFeeMinimum, BigDecimal inputBuyerFeeFixed) {
        this.inputCompanyName = inputCompanyName;
        this.inputSellerFeePercentage = inputSellerFeePercentage;
        this.inputSellerFeeMinimum = inputSellerFeeMinimum;
        this.inputSellerFeeFixed = inputSellerFeeFixed;
        this.inputBuyerFeePercentage = inputBuyerFeePercentage;
        this.inputBuyerFeeMinimum = inputBuyerFeeMinimum;
        this.inputBuyerFeeFixed = inputBuyerFeeFixed;
    }

    public String getInputCompanyName() {
        return this.inputCompanyName;
    }

    public void setInputCompanyName(String inputCompanyName) {
        this.inputCompanyName = inputCompanyName;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getInputSellerFeePercentage() {
        return this.inputSellerFeePercentage;
    }

    public void setInputSellerFeePercentage(int inputSellerFeePercentage) {
        this.inputSellerFeePercentage = inputSellerFeePercentage;
    }

    public BigDecimal getInputSellerFeeMinimum() {
        return this.inputSellerFeeMinimum;
    }

    public void setInputSellerFeeMinimum(BigDecimal inputSellerFeeMinimum) {
        this.inputSellerFeeMinimum = inputSellerFeeMinimum;
    }

    public BigDecimal getInputSellerFeeFixed() {
        return this.inputSellerFeeFixed;
    }

    public void setInputSellerFeeFixed(BigDecimal inputSellerFeeFixed) {
        this.inputSellerFeeFixed = inputSellerFeeFixed;
    }

    public int getInputBuyerFeePercentage() {
        return this.inputBuyerFeePercentage;
    }

    public void setInputBuyerFeePercentage(int inputBuyerFeePercentage) {
        this.inputBuyerFeePercentage = inputBuyerFeePercentage;
    }

    public BigDecimal getInputBuyerFeeMinimum() {
        return this.inputBuyerFeeMinimum;
    }

    public void setInputBuyerFeeMinimum(BigDecimal inputBuyerFeeMinimum) {
        this.inputBuyerFeeMinimum = inputBuyerFeeMinimum;
    }

    public BigDecimal getInputBuyerFeeFixed() {
        return this.inputBuyerFeeFixed;
    }

    public void setInputBuyerFeeFixed(BigDecimal inputBuyerFeeFixed) {
        this.inputBuyerFeeFixed = inputBuyerFeeFixed;
    }

    @Override
    public String toString() {
        return "{" +
            " inputCompanyName='" + getInputCompanyName() + "'" +
            ", date='" + getDate() + "'" +
            ", inputSellerFeePercentage='" + getInputSellerFeePercentage() + "'" +
            ", inputSellerFeeMinimum='" + getInputSellerFeeMinimum() + "'" +
            ", inputSellerFeeFixed='" + getInputSellerFeeFixed() + "'" +
            ", inputBuyerFeePercentage='" + getInputBuyerFeePercentage() + "'" +
            ", inputBuyerFeeMinimum='" + getInputBuyerFeeMinimum() + "'" +
            ", inputBuyerFeeFixed='" + getInputBuyerFeeFixed() + "'" +
            "}";
    }
    
}