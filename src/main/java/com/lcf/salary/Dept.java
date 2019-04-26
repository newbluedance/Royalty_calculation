package com.lcf.salary;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lichunfeng
 */
public class Dept {
    private String name;
    private List<BigDecimal> salesRules;
    private List<BigDecimal> royaltyRules;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BigDecimal> getSalesRules() {
        return salesRules;
    }

    public void setSalesRules(List<BigDecimal> salesRules) {
        this.salesRules = salesRules;
    }

    public List<BigDecimal> getRoyaltyRules() {
        return royaltyRules;
    }

    public void setRoyaltyRules(List<BigDecimal> royaltyRules) {
        this.royaltyRules = royaltyRules;
    }
}
