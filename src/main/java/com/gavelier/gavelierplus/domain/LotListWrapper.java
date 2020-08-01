package com.gavelier.gavelierplus.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class LotListWrapper implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    
    private List<Lot> lots;

    public LotListWrapper() {
    }

    public LotListWrapper(List<Lot> lots) {
        this.lots = lots;
    }

    public List<Lot> getLots() {
        return this.lots;
    }

    public void setLots(List<Lot> lots) {
        this.lots = lots;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof LotListWrapper)) {
            return false;
        }
        LotListWrapper lotListWrapper = (LotListWrapper) o;
        return Objects.equals(lots, lotListWrapper.lots);
    }


    @Override
    public String toString() {
        return "{" +
            " lots='" + getLots() + "'" +
            "}";
    }

}