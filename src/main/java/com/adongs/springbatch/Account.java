package com.adongs.springbatch;

import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 账户实体
 * @Author yudong
 * @Date 2019/8/15 下午4:57
 * @Version 1.0
 */
@Component
public class Account {

    /**
     * 姓名
     */
    private String name;

    /**
     * 时间
     */
    private String date;

    /**
     * 金额
     */
    private int money;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMoney() {
        return this.money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", date=" + date +
                ", money=" + money +
                '}';
    }
}
