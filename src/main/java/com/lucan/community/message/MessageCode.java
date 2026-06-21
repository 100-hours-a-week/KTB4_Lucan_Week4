package com.lucan.community.message;

public enum MessageCode {

    REGISTER_SUCCESS("register_success"),
    LOGIN_SUCCESS("login_success"),

    USER_UPDATE_SUCCESS("user_update_success"),
    PASSWORD_UPDATE_SUCCESS("password_update_success"),
    USER_DELETE_SUCCESS("user_delete_success"),
    LOGOUT_SUCCESS("logout_success"),

    GET_POSTS_SUCCESS("get_posts_success"),
    GET_POST_SUCCESS("get_post_success"),
    CREATE_POST_SUCCESS("create_post_success"),
    POST_UPDATE_SUCCESS("post_update_success"),
    POST_DELETE_SUCCESS("post_delete_success"),

    CREATE_COMMENT_SUCCESS("create_comment_success"),
    COMMENT_UPDATE_SUCCESS("comment_update_success"),
    COMMENT_DELETE_SUCCESS("comment_delete_success"),

    LIKE_SUCCESS("like_success"),
    UNLIKE_SUCCESS("unlike_success"),

    EMAIL_ALREADY_EXISTS("email_already_exists"),
    NICKNAME_ALREADY_EXISTS("nickname_already_exists"),
    PASSWORD_NOT_MATCH("password_not_match"),

    LOGIN_FAILED("login_failed"),
    LOGIN_REQUIRED("login_required"),

    POST_NOT_FOUND("post_not_found"),
    COMMENT_NOT_FOUND("comment_not_found"),

    INVALID_REQUEST("invalid_request"),
    INTERNAL_SERVER_ERROR("internal_server_error");

    private final String message;

    MessageCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
