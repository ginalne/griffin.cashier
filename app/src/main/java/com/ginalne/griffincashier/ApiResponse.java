package com.ginalne.griffincashier;

import java.util.List;

public class ApiResponse {
    public class Bill {
        public Receipt data;
        public String message;
    }

    public class ProductHeader{
        public List<com.ginalne.griffincashier.ProductHeader> data;
        public String message;
    }

    public class Receipt {
        public Integer id;
        public Integer index;
        public Integer paid;
        public Integer receipt_id;
    }
}
