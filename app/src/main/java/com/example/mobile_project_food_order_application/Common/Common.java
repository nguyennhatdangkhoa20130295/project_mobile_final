package com.example.mobile_project_food_order_application.Common;

import com.example.mobile_project_food_order_application.Model.User;

public class Common {
    public static User currentUser;
    public static final String DELETE = "Delete";
    public static String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Chờ xác nhận";
        else if(status.equals("1"))
            return "Đang chuẩn bị hàng";
        else if(status.equals("2"))
            return "Chờ lấy hàng";
        else
            return "Đang giao";
    }
}
