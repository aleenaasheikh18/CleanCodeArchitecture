package com.chat.myapplication.utility

object NetworkConstants {

    const val HEADER_KEY_CONTENT_TYPE = "Content-Type"
    const val HEADER_KEY_CONTENT_TYPE_VALUE = "text/plain"
    const val HEADER_KEY_CONTENT_AUTHORIZATION = "Authorization"
    const val HEADER_AUTHORIZATION_TYPE = "Bearer "

    const val HEADER_KEY_DEVICE_ID = "device-id"
    const val HEADER_KEY_DEVICE_TYPE = "device-type"
    const val HEADER_KEY_BRAND = "brand"
    const val HEADER_KEY_MODEL = "model"
    const val HEADER_KEY_OS = "os"
    const val HEADER_KEY_APP_VERSION = "app-version"
    const val HEADER_KEY_LANGUAGE = "Accept-Language"
    const val CLIENT_ID = "client-id"
    const val REQUEST_SIGNATURE = "x-request-signature"

    const val CONNECT_TIME_OUT = 90L
    const val READ_TIME_OUT = 90L


    /**
     * Error Messages
     * */
    const val NETWORK_ERROR = "No internet connection. Please try again later."
    const val SOMETHING_WENT_WRONG = "Something went wrong , please try again later"
    const val SERVER_NOT_AVAILABLE = "Server not available , please try again later"
    const val HTTP_NOT_FOUND = "Http not found"

}
