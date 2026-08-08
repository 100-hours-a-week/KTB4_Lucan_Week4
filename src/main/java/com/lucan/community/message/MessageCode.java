package com.lucan.community.message;

public enum MessageCode {

    REGISTER_SUCCESS("register_success"),

    USER_UPDATE_SUCCESS("user_update_success"),
    SAME_NICKNAME("same_nickname"),
    CURRENT_PASSWORD_NOT_MATCH("current_password_not_match"),
    PASSWORD_UPDATE_SUCCESS("password_update_success"),
    SAME_AS_CURRENT_PASSWORD("same_as_current_password"),
    USER_DELETE_SUCCESS("user_delete_success"),

    GET_USER_SUCCESS("get_user_success"),

    GET_POSTS_SUCCESS("get_posts_success"),
    GET_POST_SUCCESS("get_post_success"),
    CREATE_POST_SUCCESS("create_post_success"),
    POST_UPDATE_SUCCESS("post_update_success"),
    POST_DELETE_SUCCESS("post_delete_success"),
    INCREASE_VIEW_COUNT_SUCCESS("increase_view_count_success"),

    CREATE_COMMENT_SUCCESS("create_comment_success"),
    GET_COMMENTS_SUCCESS("get_comments_success"),
    COMMENT_UPDATE_SUCCESS("comment_update_success"),
    COMMENT_DELETE_SUCCESS("comment_delete_success"),

    LIKE_SUCCESS("like_success"),
    UNLIKE_SUCCESS("unlike_success"),

    EMAIL_ALREADY_EXISTS("email_already_exists"),
    NICKNAME_ALREADY_EXISTS("nickname_already_exists"),
    PASSWORD_NOT_MATCH("password_not_match"),

    LOGIN_REQUIRED("login_required"),

    USER_NOT_FOUND("user_not_found"),
    POST_NOT_FOUND("post_not_found"),
    COMMENT_NOT_FOUND("comment_not_found"),

    INVALID_REQUEST("invalid_request"),
    INTERNAL_SERVER_ERROR("internal_server_error"),

    IMAGE_UPLOAD_SUCCESS("image_upload_success"),

    IMAGE_FILE_EMPTY("image_file_empty"),
    INVALID_IMAGE_FILE("invalid_image_file"),
    IMAGE_UPLOAD_FAILED("image_upload_failed"),
    IMAGE_DELETE_FAILED("image_delete_failed"),

    POST_UPDATE_FORBIDDEN("post_update_forbidden"),
    POST_DELETE_FORBIDDEN("post_delete_forbidden"),
    COMMENT_UPDATE_FORBIDDEN("comment_update_forbidden"),
    COMMENT_DELETE_FORBIDDEN("comment_delete_forbidden");


    private final String message;

    MessageCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
