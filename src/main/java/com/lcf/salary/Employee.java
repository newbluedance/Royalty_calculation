package com.lcf.salary;

import java.math.BigDecimal;

/**
 * @author lichunfeng
 */
public class Employee {
    /**
     * 员工名称
     */
    private String name;
    /**
     * 员工部门（包含提成规则）
     */
    private Dept dept;
    /**
     * 销售金额
     */
    private BigDecimal sales;
    /**
     * 提成金额
     */
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
