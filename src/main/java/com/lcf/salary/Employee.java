package com.lcf.salary;

import java.math.BigDecimal;

/**
 * @author lichunfeng
 */
public class Employee {
    private String name;
    private Dept dept;
    private BigDecimal sales;
    private BigDecimal royalty;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Dept getDept() {
        return dept;
    }

    public void setDept(Dept dept) {
        this.dept = dept;
    }

    public BigDecimal getSales() {
        return sales;
    }

    public void setSales(BigDecimal sales) {
        this.sales = sales;
    }

    public BigDecimal getRoyalty() {
        return royalty;
    }

    public void setRoyalty(BigDecimal royalty) {
        this.royalty = royalty;
    }
}
