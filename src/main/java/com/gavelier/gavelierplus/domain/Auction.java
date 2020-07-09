package com.gavelier.gavelierplus.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import org.springframework.format.annotation.DateTimeFormat;

@DynamoDBTable(tableName = "auctions")
public class Auction implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    private String id;
    private String userId;
    private String inputCompanyName;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date date;

    private int inputSellerFeePercentage;
    private BigDecimal inputSellerFeeMinimum;
    private BigDecimal inputSellerFeeFixed;
    private int inputBuyerFeePercentage;
    private BigDecimal inputBuyerFeeMinimum;
    private BigDecimal inputBuyerFeeFixed;

    public Auction() {
    }

    public Auction(String userId, String inputCompanyName, Date date, 
            int inputSellerFeePercentage,
            BigDecimal inputSellerFeeMinimum, BigDecimal inputSellerFeeFixed, 
            int inputBuyerFeePercentage,
            BigDecimal inputBuyerFeeMinimum, BigDecimal inputBuyerFeeFixed) {
        this.userId=userId;
        this.inputCompanyName = inputCompanyName;
        this.inputSellerFeePercentage = inputSellerFeePercentage;
        this.inputSellerFeeMinimum = inputSellerFeeMinimum;
        this.inputSellerFeeFixed = inputSellerFeeFixed;
        this.inputBuyerFeePercentage = inputBuyerFeePercentage;
        this.inputBuyerFeeMinimum = inputBuyerFeeMinimum;
        this.inputBuyerFeeFixed = inputBuyerFeeFixed;
    }

    public Auction(String id, String userId, String inputCompanyName, Date date, 
            int inputSellerFeePercentage,
            BigDecimal inputSellerFeeMinimum, BigDecimal inputSellerFeeFixed, 
            int inputBuyerFeePercentage,
            BigDecimal inputBuyerFeeMinimum, BigDecimal inputBuyerFeeFixed) {
        this.id=id;
        this.userId=userId;
        this.inputCompanyName = inputCompanyName;
        this.inputSellerFeePercentage = inputSellerFeePercentage;
        this.inputSellerFeeMinimum = inputSellerFeeMinimum;
        this.inputSellerFeeFixed = inputSellerFeeFixed;
        this.inputBuyerFeePercentage = inputBuyerFeePercentage;
        this.inputBuyerFeeMinimum = inputBuyerFeeMinimum;
        this.inputBuyerFeeFixed = inputBuyerFeeFixed;
    }


    @DynamoDBHashKey(attributeName="id")
    @DynamoDBAutoGeneratedKey
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id=id;
    }

    @DynamoDBRangeKey
    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId=userId;
    }

    @DynamoDBAttribute
    public String getInputCompanyName() {
        return this.inputCompanyName;
    }

    public void setInputCompanyName(String inputCompanyName) {
        this.inputCompanyName = inputCompanyName;
    }

    @DynamoDBAttribute
    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @DynamoDBAttribute
    public int getInputSellerFeePercentage() {
        return this.inputSellerFeePercentage;
    }

    public void setInputSellerFeePercentage(int inputSellerFeePercentage) {
        this.inputSellerFeePercentage = inputSellerFeePercentage;
    }

    @DynamoDBAttribute
    public BigDecimal getInputSellerFeeMinimum() {
        return this.inputSellerFeeMinimum;
    }

    public void setInputSellerFeeMinimum(BigDecimal inputSellerFeeMinimum) {
        this.inputSellerFeeMinimum = inputSellerFeeMinimum;
    }

    @DynamoDBAttribute
    public BigDecimal getInputSellerFeeFixed() {
        return this.inputSellerFeeFixed;
    }

    public void setInputSellerFeeFixed(BigDecimal inputSellerFeeFixed) {
        this.inputSellerFeeFixed = inputSellerFeeFixed;
    }

    @DynamoDBAttribute
    public int getInputBuyerFeePercentage() {
        return this.inputBuyerFeePercentage;
    }

    public void setInputBuyerFeePercentage(int inputBuyerFeePercentage) {
        this.inputBuyerFeePercentage = inputBuyerFeePercentage;
    }

    @DynamoDBAttribute
    public BigDecimal getInputBuyerFeeMinimum() {
        return this.inputBuyerFeeMinimum;
    }

    public void setInputBuyerFeeMinimum(BigDecimal inputBuyerFeeMinimum) {
        this.inputBuyerFeeMinimum = inputBuyerFeeMinimum;
    }

    @DynamoDBAttribute
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
            " userId='" + getUserId() + "'" +
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