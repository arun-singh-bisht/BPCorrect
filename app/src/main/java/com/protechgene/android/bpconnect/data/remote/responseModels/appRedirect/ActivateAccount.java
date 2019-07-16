package com.protechgene.android.bpconnect.data.remote.responseModels.appRedirect;

import com.google.gson.annotations.SerializedName;
import com.protechgene.android.bpconnect.data.remote.responseModels.ResetPassword.Data;

import java.util.List;

public class ActivateAccount {

        @SerializedName("message")
        private String message;
        @SerializedName("data")
        private List<Data> data = null;
        @SerializedName("valid")
        private Boolean valid;

        public String getMessage() {
            return message;
        }

       public void setMessage(String message) {
            this.message = message;
        }

        public List<Data> getData() {
            return data;
        }

        public void setData(List<Data> data) {
            this.data = data;
        }

        public Boolean getValid() {
            return valid;
        }

        public void setValid(Boolean valid) {
            this.valid = valid;
        }
}
