package com.paykun.sdk;

import com.paykun.sdk.logonsquare.Transaction;

public class PaymentMessage {
    // Event used to send message from activity to activity.

        private String message;

        public com.paykun.sdk.logonsquare.Transaction getTransactionDetail() {
            return transactionDetail;
        }


        private String id;
        private com.paykun.sdk.logonsquare.Transaction transactionDetail;
        public PaymentMessage(String message, String id, com.paykun.sdk.logonsquare.Transaction detail) {
            this.message = message;
            this.id = id;
            this.transactionDetail = detail;
        }
        public String getResults() {
            return message;
        }
        public String getTransactionId() {
            return id;
        }

}
