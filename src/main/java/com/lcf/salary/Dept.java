package com.lcf.salary;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lichunfeng
 */
public class Dept {
    /**
     * 部门名称
     */
    private String name;
    /**
     * 部门名称
     */
    private String salesRul;
    /**
     * 提成金额区间
     */
    private List<BigDecimal> salesRules;
    /**
     * 提成点数区间
     */
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

    public String getSalesRul() {
        return salesRul;
    }

    public void setSalesRul(String salesRul) {
        this.salesRul = salesRul;
    }
}
