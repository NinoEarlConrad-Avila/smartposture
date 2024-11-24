package com.example.smartposture.util;

import com.example.smartposture.data.response.LoginResponse;
import com.google.gson.Gson;

import retrofit2.Response;

public class ApiErrorUtil {
    public static String parseError(Response<?> response) {
        if (response.errorBody() != null) {
            try {
                Gson gson = new Gson();
                LoginResponse errorResponse = gson.fromJson(response.errorBody().string(), LoginResponse.class);
                return errorResponse.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                return "An unexpected error occurred.";
            }
        }
        return "Unknown error occurred.";
    }
}

