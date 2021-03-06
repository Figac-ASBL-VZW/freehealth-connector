package org.taktik.connector.business.domain.civics;

import java.util.Date;

import org.taktik.connector.business.domain.civics.*;
import org.taktik.connector.business.domain.civics.Ampp;

public class Copayment {

	org.taktik.connector.business.domain.civics.Atm atm;
	org.taktik.connector.business.domain.civics.Ampp ampp;

	String chapterName;
	String paragraphName;
	String deliveryEnvironment;
	Long regimeType;
	Date startDate;
	Date createdTms;
	String createdUserId;
	Date endDate;
	Double copayAmnt;
	Double solidFlatRateAmnt;
	String modificationStatus;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public org.taktik.connector.business.domain.civics.Atm getAtm() {
        return atm;
    }

    public void setAtm(org.taktik.connector.business.domain.civics.Atm atm) {
        this.atm = atm;
    }

    public org.taktik.connector.business.domain.civics.Ampp getAmpp() {
        return ampp;
    }

    public void setAmpp(Ampp ampp) {
        this.ampp = ampp;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getParagraphName() {
        return paragraphName;
    }

    public void setParagraphName(String paragraphName) {
        this.paragraphName = paragraphName;
    }

    public String getDeliveryEnvironment() {
        return deliveryEnvironment;
    }

    public void setDeliveryEnvironment(String deliveryEnvironment) {
        this.deliveryEnvironment = deliveryEnvironment;
    }

    public Long getRegimeType() {
        return regimeType;
    }

    public void setRegimeType(Long regimeType) {
        this.regimeType = regimeType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getCreatedTms() {
        return createdTms;
    }

    public void setCreatedTms(Date createdTms) {
        this.createdTms = createdTms;
    }

    public String getCreatedUserId() {
        return createdUserId;
    }

    public void setCreatedUserId(String createdUserId) {
        this.createdUserId = createdUserId;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Double getCopayAmnt() {
        return copayAmnt;
    }

    public void setCopayAmnt(Double copayAmnt) {
        this.copayAmnt = copayAmnt;
    }

    public Double getSolidFlatRateAmnt() {
        return solidFlatRateAmnt;
    }

    public void setSolidFlatRateAmnt(Double solidFlatRateAmnt) {
        this.solidFlatRateAmnt = solidFlatRateAmnt;
    }

    public String getModificationStatus() {
        return modificationStatus;
    }

    public void setModificationStatus(String modificationStatus) {
        this.modificationStatus = modificationStatus;
    }
}

