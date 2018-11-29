package com.wso2.finance.open.banking.pisp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import java.util.ArrayList;
import java.util.List;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;

/**
 * Information that locates and identifies a specific address,
 * as defined by postal services or in free format text.
 **/

@ApiModel(description = "Information that locates and identifies a specific address," +
        " as defined by postal services or in free format text.")
public class PaymentInitRequestDeliveryAddressDTO {

    @NotNull
    private String country = null;

    private String streetName = null;

    @NotNull
    private String townName = null;

    private List<String> countrySubDivision = new ArrayList<String>();

    private String buildingNumber = null;

    private String postCode = null;

    private List<String> addressLine = new ArrayList<String>();

    /**
     * Nation with its own government, occupying a particular territory.
     **/
    @ApiModelProperty(required = true, value = "Nation with its own government," +
            "occupying a particular territory.")
    @JsonProperty("country")
    public String getCountry() {

        return country;
    }

    public void setCountry(String country) {

        this.country = country;
    }

    /**
     * Name of a street or thoroughfare.
     **/
    @ApiModelProperty(value = "Name of a street or thoroughfare")
    @JsonProperty("streetName")
    public String getStreetName() {

        return streetName;
    }

    public void setStreetName(String streetName) {

        this.streetName = streetName;
    }

    /**
     * Name of a built-up area, with defined boundaries, and a local government.
     **/
    @ApiModelProperty(required = true, value = "Name of a built-up area.")
    @JsonProperty("townName")
    public String getTownName() {

        return townName;
    }

    public void setTownName(String townName) {

        this.townName = townName;
    }

    /**
     * Identifies a subdivision of a country, for instance state, region county.
     **/
    @ApiModelProperty(value = "Identifies a subdivision of a country, for instance state, region county.")
    @JsonProperty("countrySubDivision")
    public List<String> getCountrySubDivision() {

        return countrySubDivision;
    }

    public void setCountrySubDivision(List<String> countrySubDivision) {

        this.countrySubDivision = countrySubDivision;
    }

    /**
     * Number that identifies the position of a building on a street.
     **/
    @ApiModelProperty(value = "Number that identifies the position of a building on a street.")
    @JsonProperty("buildingNumber")
    public String getBuildingNumber() {

        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {

        this.buildingNumber = buildingNumber;
    }

    /**
     * Identifier consisting of a group of letters and/or numbers that is added.
     * to a postal address to assist the sorting of mail
     **/
    @ApiModelProperty(value = "Identifier consisting of a group of letters and/or " +
            "numbers that is added to a postal address to assist the sorting of mail")
    @JsonProperty("postCode")
    public String getPostCode() {

        return postCode;
    }

    public void setPostCode(String postCode) {

        this.postCode = postCode;
    }

    /**
     * Information that locates and identifies a specific address, as defined by postal services,
     * that is presented in free format text.
     **/
    @ApiModelProperty(value = "Information that locates and identifies a specific address, " +
            "as defined by postal services, that is presented in free format text.")
    @JsonProperty("addressLine")
    public List<String> getAddressLine() {

        return addressLine;
    }

    public void setAddressLine(List<String> addressLine) {

        this.addressLine = addressLine;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class PaymentInitRequestDeliveryAddressDTO {\n");

        sb.append("  country: ").append(country).append("\n");
        sb.append("  streetName: ").append(streetName).append("\n");
        sb.append("  townName: ").append(townName).append("\n");
        sb.append("  countrySubDivision: ").append(countrySubDivision).append("\n");
        sb.append("  buildingNumber: ").append(buildingNumber).append("\n");
        sb.append("  postCode: ").append(postCode).append("\n");
        sb.append("  addressLine: ").append(addressLine).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
