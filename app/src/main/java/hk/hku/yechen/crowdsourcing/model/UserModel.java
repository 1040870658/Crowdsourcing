package hk.hku.yechen.crowdsourcing.model;

import java.sql.Date;

/**
 * Created by yechen on 2018/2/3.
 */

public class UserModel {
    private String userName;
    private String email;
    private String password;
    private Date createTime;
    private double property;
    private float credit;
    private String phone;
    private String address;

    public UserModel(String userName, String email, String password, Date createTime, double property, float credit, String phone, String address) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.createTime = createTime;
        this.property = property;
        this.credit = credit;
        this.phone = phone;
        this.address = address;
    }

    public UserModel(String phone, String userName,String password,String email, float credit, double property){
        this.phone = phone;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.credit = credit;
        this.property = property;
    }
    public UserModel(UserModel userModel){
        this.phone = userModel.getPhone();
        this.userName = userModel.getUserName();
        this.password = userModel.getPassword();
        this.email = userModel.getEmail();
        this.credit = userModel.getCredit();
        this.property = userModel.getProperty();
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setProperty(double property) {
        this.property = property;
    }

    public void setCredit(float credit) {
        this.credit = credit;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public double getProperty() {
        return property;
    }

    public float getCredit() {
        return credit;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }
}
