package io.github.yeahfo.mry.learn.core.common.exception;

import lombok.Getter;

//400：请求验证错误，请求中资源所属关系错误等
//401：认证错误
//403：权限不够
//404：资源未找到
//409：业务异常
//426：套餐检查失败
//500：系统错误
@Getter
public enum ErrorCode {
    //400
    USER_NOT_CURRENT_MEMBER( 400 ),
    QR_NOT_BELONG_TO_APP( 400 ),
    PASSWORD_CONFIRM_NOT_MATCH( 400 ),
    APPROVAL_PERMISSION_NOT_ALLOWED( 400 ),
    MODIFY_PERMISSION_NOT_ALLOWED( 400 ),
    CONTROL_PERMISSION_NOT_ALLOWED( 400 ),
    OPERATION_PERMISSION_NOT_ALLOWED( 400 ),
    POINT_CHECK_ANSWER_NOT_MATCH_TO_CONTROL( 400 ),
    NOT_ALL_POINT_CHECK_ANSWERED( 400 ),
    ONLY_PARTIAL_POINT_CHECK_ANSWERED( 400 ),
    MAX_INPUT_NUMBER_REACHED( 400 ),
    MAX_RANK_REACHED( 400 ),
    MIN_INPUT_NUMBER_REACHED( 400 ),
    INCORRECT_NUMBER_INPUT_PRECISION( 400 ),
    INCORRECT_INTEGER_PRECISION( 400 ),
    SINGLE_MEMBER_ONLY_ALLOW_SINGLE_ANSWER( 400 ),
    MAX_FILE_NUMBER_REACHED( 400 ),
    MAX_IMAGE_NUMBER_REACHED( 400 ),
    MAX_ITEM_NUMBER_REACHED( 400 ),
    ITEM_ANSWER_OPTION_DUPLICATED( 400 ),
    MAX_ITEM_COUNT_REACHED( 400 ),
    UPLOAD_FILE_ID_DUPLICATED( 400 ),
    UPLOAD_IMAGE_ID_DUPLICATED( 400 ),
    COUNTED_ITEM_ID_DUPLICATED( 400 ),
    SINGLE_DROPDOWN_ONLY_ALLOW_SINGLE_ANSWER( 400 ),
    NOT_ALL_ANSWERS_IN_CHECKBOX_OPTIONS( 400 ),
    CHECKBOX_MAX_SELECTION_REACHED( 400 ),
    CHECKBOX_MIN_SELECTION_NOT_REACHED( 400 ),
    ITEM_STATUS_ANSWER_NOT_IN_CONTROL( 400 ),
    NOT_ALL_ANSWERS_IN_RADIO_OPTIONS( 400 ),
    NOT_ALL_ANSWERS_IN_DROPDOWN_OPTIONS( 400 ),
    MEMBER_MAX_SELECTION_REACHED( 400 ),
    MEMBER_MIN_SELECTION_NOT_REACHED( 400 ),
    DROPDOWN_MAX_SELECTION_REACHED( 400 ),
    DROPDOWN_MIN_SELECTION_NOT_REACHED( 400 ),
    SINGLE_LINE_MAX_CONTENT_REACHED( 400 ),
    SINGLE_LINE_MIN_CONTENT_NOT_REACHED( 400 ),
    IDENTIFIER_MAX_CONTENT_REACHED( 400 ),
    IDENTIFIER_MIN_CONTENT_NOT_REACHED( 400 ),
    MULTI_LINE_MAX_CONTENT_REACHED( 400 ),
    MULTI_LINE_MIN_CONTENT_NOT_REACHED( 400 ),
    RICH_TEXT_MAX_CONTENT_REACHED( 400 ),
    RICH_TEXT_MIN_CONTENT_NOT_REACHED( 400 ),
    NOT_ALL_ANSWERS_IN_OPTIONS( 400 ),
    MULTI_SELECTION_LEVEL1_NOT_PROVIDED( 400 ),
    MULTI_SELECTION_LEVEL2_NOT_PROVIDED( 400 ),
    MULTI_SELECTION_LEVEL3_NOT_PROVIDED( 400 ),
    PROVINCE_NOT_PROVIDED( 400 ),
    CITY_NOT_PROVIDED( 400 ),
    DISTRICT_NOT_PROVIDED( 400 ),
    DETAIL_ADDRESS_NOT_PROVIDED( 400 ),
    MANDATORY_ANSWER_REQUIRED( 400 ),
    REQUEST_VALIDATION_FAILED( 400 ),
    ANSWERS_DUPLICATED( 400 ),
    MENU_LINK_ID_DUPLICATED( 400 ),
    PLATE_CONTROL_ID_DUPLICATED( 400 ),
    NOT_SUPPORTED_TARGET_CONTROL_FOR_TREND( 400 ),
    TREND_ITEM_ID_DUPLICATED( 400 ),
    TIME_SEGMENT_ID_DUPLICATED( 400 ),
    NOT_SUPPORTED_BASED_CONTROL_FOR_PIE( 400 ),
    NOT_SUPPORTED_BASED_CONTROL_FOR_DOUGHNUT( 400 ),
    CONTROL_NOT_SUPPORT_NUMBER_RANGE_SEGMENT( 400 ),
    PAGE_LINK_ID_DUPLICATED( 400 ),
    MAX_OVERFLOW( 400 ),
    MIN_OVERFLOW( 400 ),
    INITIAL_ITEM_STATUS_NOT_VALID( 400 ),
    VALIDATION_STATUS_OPTION_NOT_EXISTS( 400 ),
    CONTROL_NOT_NUMERICAL_VALUED( 400 ),
    CONTROL_SHOULD_NOT_SELF( 400 ),
    IMAGE_ID_DUPLICATED( 400 ),
    ALIAS_ID_DUPLICATED( 400 ),
    RECORD_ID_DUPLICATED( 400 ),
    NOT_SUPPORTED_BASED_CONTROL_FOR_TIME_SEGMENT( 400 ),
    NOT_SUPPORTED_TARGET_CONTROL_FOR_TIME_SEGMENT( 400 ),
    TEXT_OPTION_ID_DUPLICATED( 400 ),
    TEXT_OPTION_NOT_ENOUGH( 400 ),
    TEXT_OPTION_OVERFLOW( 400 ),
    VALIDATION_PAGE_NOT_EXIST( 400 ),
    VALIDATION_OPERATION_MENU_REF_PAGE_NOT_EXIST( 400 ),
    VALIDATION_CONTROL_NOT_EXIST( 400 ),
    NOT_SUPPORTED_BASED_CONTROL_BAR( 400 ),
    CONTROL_NOT_NUMBERED( 400 ),
    CONTROL_NOT_DATE( 400 ),
    CONTROL_NOT_CATEGORIZED( 400 ),
    NOT_SUPPORTED_TARGET_CONTROL_FOR_BAR( 400 ),
    NOT_SUPPORTED_TARGET_CONTROL_FOR_DOUGHNUT( 400 ),
    NOT_SUPPORTED_TARGET_CONTROL_FOR_PIE( 400 ),
    CONTROL_NOT_NUMBERED_FOR_NUMBER_RANGE_SEGMENT( 400 ),
    CONTROL_NOT_SUPPORT_REFERENCE( 400 ),
    VALIDATION_ATTRIBUTE_NOT_EXIST( 400 ),
    ATTACHMENT_ID_DUPLICATED( 400 ),
    EMPTY_FILLABLE_SETTING( 400 ),
    EMPTY_ATTRIBUTE_REF_PAGE_ID( 400 ),
    VALIDATION_ATTRIBUTE_REF_PAGE_NOT_EXIST( 400 ),
    EMPTY_ATTRIBUTE_REF_CONTROL_ID( 400 ),
    VALIDATION_ATTRIBUTE_REF_CONTROL_NOT_EXIST( 400 ),
    WRONG_ATTRIBUTE_REF_CONTROL_TYPE( 400 ),
    EMPTY_ATTRIBUTE_FIXED_VALUE( 400 ),
    ATTRIBUTE_RANGE_SHOULD_NOT_NULL( 400 ),
    CONTROL_ID_DUPLICATED( 400 ),
    NO_APP_HOME_PAGE( 400 ),
    PAGE_ID_DUPLICATED( 400 ),
    OPERATION_MENU_ITEM_ID_DUPLICATED( 400 ),
    OPERATION_MENU_ITEM_NAME_DUPLICATED( 400 ),
    OPERATION_MENU_ITEM_DUPLICATED( 400 ),
    ATTRIBUTE_ID_DUPLICATED( 400 ),
    ATTRIBUTE_NAME_DUPLICATED( 400 ),
    VALIDATION_LINK_PAGE_NOT_EXIST( 400 ),
    MIN_GREATER_THAN_MAX( 400 ),
    BAD_REQUEST( 400 ),
    GROUP_QR_NOT_SAME_APP( 400 ),
    NOT_ALL_MEMBERS_EXIST( 400 ),
    NOT_ALL_GROUPS_EXIST( 400 ),
    NOT_ALL_DEPARTMENTS_EXITS( 400 ),
    GROUP_PLATE_NOT_IN_SAME_APP( 400 ),
    OUT_OF_OFF_SET_RADIUS( 400 ),
    QRS_SHOULD_IN_ONE_APP( 400 ),
    NUMBER_REPORT_ID_DUPLICATED( 400 ),
    CHART_REPORT_ID_DUPLICATED( 400 ),
    ATTRIBUTE_NOT_NUMBER_VALUED( 400 ),
    ATTRIBUTE_NOT_DATE_OR_TIMESTAMP( 400 ),
    ATTRIBUTE_NOT_CATEGORIZED( 400 ),
    CONTROL_NOT_NUMBER_VALUED( 400 ),
    GROUP_HIERARCHY_NOT_MATCH( 400 ),
    DEPARTMENT_HIERARCHY_NOT_MATCH( 400 ),
    CIRCULATION_AFTER_SUBMISSION_ID_DUPLICATED( 400 ),
    CIRCULATION_PERMISSION_ID_DUPLICATED( 400 ),
    CIRCULATION_OPTION_NOT_EXISTS( 400 ),

    //401
    AUTHENTICATION_FAILED( 401 ),

    //403
    ACCESS_DENIED( 403 ),
    WRONG_TENANT( 403 ),
    NO_VIEWABLE_GROUPS( 403 ),
    NO_VIEWABLE_PERMISSION_FOR_GROUP( 403 ),
    NO_VIEWABLE_PERMISSION_FOR_QR( 403 ),
    NO_VIEWABLE_PAGES( 403 ),
    NO_VIEWABLE_PERMISSION_FOR_PAGE( 403 ),
    NO_MANAGABLE_GROUPS( 403 ),
    NO_MANAGABLE_PERMISSION_FOR_GROUP( 403 ),
    NO_MANAGABLE_PERMISSION_FOR_QR( 403 ),
    NO_MANAGABLE_PAGES( 403 ),
    NO_MANAGABLE_PERMISSION_FOR_PAGE( 403 ),
    NO_APPROVABLE_GROUPS( 403 ),
    NO_APPROVABLE_PERMISSION_FOR_GROUP( 403 ),
    NO_APPROVABLE_PERMISSION_FOR_QR( 403 ),
    NO_APPROVABLE_PAGES( 403 ),
    NO_APPROVABLE_PERMISSION_FOR_PAGE( 403 ),

    //404
    PAGE_NOT_FOUND( 404 ),
    CONTROL_NOT_FOUND( 404 ),
    ATTRIBUTE_NOT_FOUND( 404 ),
    QR_NOT_FOUND( 404 ),
    GROUP_NOT_FOUND( 404 ),
    DEPARTMENT_NOT_FOUND( 404 ),
    GROUP_HIERARCHY_NOT_FOUND( 404 ),
    DEPARTMENT_HIERARCHY_NOT_FOUND( 404 ),
    MEMBER_NOT_FOUND( 404 ),
    AR_NOT_FOUND( 404 ),
    AR_NOT_FOUND_ALL( 404 ),
    TENANT_NOT_FOUND( 404 ),
    DOMAIN_EVENT_NOT_FOUND( 404 ),
    ORDER_NOT_FOUND( 404 ),

    //409
    PLATE_QR_NOT_MATCH( 409 ),
    MEMBER_ALREADY_LOCKED( 409 ),
    MEMBER_ALREADY_DEACTIVATED( 409 ),
    TENANT_ALREADY_DEACTIVATED( 409 ),
    SUBMISSION_REQUIRE_MEMBER( 409 ),
    CONTROL_NOT_COMPLETE( 409 ),
    APP_ALREADY_UPDATED( 409 ),
    APP_ALREADY_LOCKED( 409 ),
    UPDATE_PERIOD_EXPIRED( 409 ),
    PAGE_NOT_ALLOW_CHANGE_BY_SUBMITTER( 409 ),
    CANNOT_UPDATE_APPROVED_SUBMISSION( 409 ),
    SUBMISSION_ALREADY_EXISTS_FOR_INSTANCE( 409 ),
    SUBMISSION_ALREADY_EXISTS_FOR_MEMBER( 409 ),
    SUBMISSION_NOT_ALLOWED_BY_CIRCULATION( 409 ),
    APP_NOT_ACTIVE( 409 ),
    QR_NOT_ACTIVE( 409 ),
    GROUP_NOT_ACTIVE( 409 ),
    MUST_SIGN_AGREEMENT( 409 ),
    APPROVAL_NOT_ENABLED( 409 ),
    PAGE_NOT_FILLABLE( 409 ),
    CONTROL_NOT_EXIST_FOR_ANSWER( 409 ),
    PLATE_COUNT_LIMIT_REACHED( 409 ),
    NO_ACTIVE_TENANT_ADMIN_LEFT( 409 ),
    MAX_TENANT_ADMIN_REACHED( 409 ),
    SUBMISSION_ALREADY_APPROVED( 409 ),
    NO_MORE_THAN_ONE_VISIBLE_GROUP_LEFT( 409 ),
    CONTROL_TYPE_NOT_MATCH( 409 ),
    ATTRIBUTE_SCHEMA_CANNOT_MODIFIED( 409 ),
    PASSWORD_NOT_MATCH( 409 ),
    NEW_PASSWORD_SAME_WITH_OLD( 409 ),
    APP_WITH_NAME_ALREADY_EXISTS( 409 ),
    QR_WITH_NAME_ALREADY_EXISTS( 409 ),
    QR_WITH_CUSTOM_ID_ALREADY_EXISTS( 409 ),
    PLATE_BATCH_WITH_NAME_ALREADY_EXISTS( 409 ),
    GROUP_WITH_NAME_ALREADY_EXISTS( 409 ),
    GROUP_NAME_DUPLICATES( 409 ),
    GROUP_WITH_CUSTOM_ID_ALREADY_EXISTS( 409 ),
    MEMBER_NOT_FOUND_FOR_FINDBACK_PASSWORD( 409 ),
    MAX_GROUP_MANAGER_REACHED( 409 ),
    MEMBER_WITH_MOBILE_ALREADY_EXISTS( 409 ),
    MEMBER_WITH_MOBILE_OR_EMAIL_ALREADY_EXISTS( 409 ),
    MEMBER_WITH_EMAIL_ALREADY_EXISTS( 409 ),
    MEMBER_WITH_CUSTOM_ID_ALREADY_EXISTS( 409 ),
    FORBIDDEN_SUBDOMAIN_PREFIX( 409 ),
    SUBDOMAIN_UPDATED_TOO_OFTEN( 409 ),
    ILLEGAL_PLAN( 409 ),
    PLATE_NOT_BOUND( 409 ),
    PLATE_ALREADY_BOUND( 409 ),
    PLATE_NOT_FOR_APP( 409 ),
    PLATE_NOT_EXIT_FOR_BOUND( 409 ),
    TENANT_WITH_SUBDOMAIN_PREFIX_ALREADY_EXISTS( 409 ),
    VERIFICATION_CODE_COUNT_OVERFLOW( 409 ),
    VERIFICATION_CODE_CHECK_FAILED( 409 ),
    VERIFICATION_CODE_ALREADY_SENT( 409 ),
    TOO_MANY_VERIFICATION_CODE_FOR_TODAY( 409 ),
    ORDER_NOT_FROM_PAID_PRE_ORDER( 409 ),
    INSTANCE_ALIAS_TOO_SHORT( 409 ),
    GROUP_ALIAS_TOO_SHORT( 409 ),
    CUSTOM_ID_ALIAS_TOO_SHORT( 409 ),
    GROUP_ALIAS_NOT_ALLOWED( 409 ),
    INSTANCE_ALIAS_NOT_ALLOWED( 409 ),
    MOBILE_EMAIL_CANNOT_BOTH_EMPTY( 409 ),
    ANSWER_NOT_UNIQUE_PER_APP( 409 ),
    ANSWER_NOT_UNIQUE_PER_INSTANCE( 409 ),
    PLATE_SETTING_HAS_ATTRIBUTES( 409 ),
    PLATE_SETTING_NOT_COMPLETE( 409 ),
    APP_TEMPLATE_NOT_PUBLISHED( 409 ),
    DOWNGRADE_PLAN_NOT_ALLOWED( 409 ),
    UPGRADE_TO_SAME_PLAN_NOT_ALLOWED( 409 ),
    UPGRADE_FREE_PLAN_NOT_ALLOWED( 409 ),
    PURCHASE_FREE_PLAN_NOT_ALLOWED( 409 ),
    PACKAGE_DURATION_TOO_LONG( 409 ),
    MAX_TENANT_MEMBER_SIZE_REACHED( 409 ),
    MAX_EXTRA_STORAGE_REACHED( 409 ),
    MAX_VIDEO_TRAFFIC_REACHED( 409 ),
    ORDER_REQUIRE_NON_FREE_PLAN( 409 ),
    INVOICE_ALREADY_REQUESTED( 409 ),
    INVOICE_NOT_REQUESTED( 409 ),
    NO_INVOICE_TITLE( 409 ),
    ORDER_NOT_PAID( 409 ),
    MAX_CONSIGNEE_REACHED( 409 ),
    CONSIGNEE_ID_DUPLICATED( 409 ),
    INVALID_QR_EXCEL( 409 ),
    QR_IMPORT_OVERFLOW( 409 ),
    QR_IMPORT_DUPLICATED_CUSTOM_ID( 409 ),
    NO_RECORDS_FOR_QR_IMPORT( 409 ),
    INVALID_MEMBER_EXCEL( 409 ),
    MEMBER_IMPORT_OVERFLOW( 409 ),
    NO_RECORDS_FOR_MEMBER_IMPORT( 409 ),
    ASSIGNMENT_PLAN_WITH_NAME_ALREADY_EXISTS( 409 ),
    APP_ASSIGNMENT_NOT_ENABLED( 409 ),
    ASSIGNMENT_START_TIME_AFTER_END_TIME( 409 ),
    ASSIGNMENT_DURATION_EXCEED_FREQUENCY( 409 ),
    ASSIGNMENT_NOTIFY_TIME_OVERFLOW( 409 ),
    MAX_ASSIGNMENT_PLAN_REACHED( 409 ),
    ASSIGNMENT_CLOSED( 409 ),
    ASSIGNMENT_ALREADY_EXISTS( 409 ),
    IDENTIFY_MOBILE_NOT_THE_SAME( 409 ),
    DEPARTMENT_COUNT_LIMIT_REACHED( 409 ),
    DEPARTMENT_WITH_NAME_ALREADY_EXISTS( 409 ),
    MAX_DEPARTMENT_MANAGER_REACHED( 409 ),
    GROUP_HIERARCHY_TOO_DEEP( 409 ),
    GROUP_NOT_VISIBLE( 409 ),
    NOT_DEPARTMENT_MEMBER( 409 ),
    DEPARTMENT_HIERARCHY_TOO_DEEP( 409 ),
    DEPARTMENT_NAME_DUPLICATES( 409 ),
    DEPARTMENT_WITH_CUSTOM_ID_ALREADY_EXISTS( 409 ),
    GROUP_SYNCED( 409 ),

    //426
    MEMBER_COUNT_LIMIT_REACHED( 426 ),
    CONTROL_TYPES_NOT_ALLOWED( 426 ),
    CONTROL_TYPE_NOT_ALLOWED( 426 ),
    UPDATE_LOGO_NOT_ALLOWED( 426 ),
    ASSIGNMENT_NOT_ALLOWED( 426 ),
    UPDATE_SUBDOMAIN_NOT_ALLOWED( 426 ),
    REFRESH_API_SECRET_NOT_ALLOWED( 426 ),
    UPDATE_WEBHOOK_SETTING_NOT_ALLOWED( 426 ),
    APP_COUNT_LIMIT_REACHED( 426 ),
    LOW_PLAN_FOR_APP_TEMPLATE( 426 ),
    QR_COUNT_LIMIT_REACHED( 426 ),
    SUBMISSION_COUNT_LIMIT_REACHED( 426 ),
    GROUP_COUNT_LIMIT_REACHED( 426 ),
    REPORTING_NOT_ALLOWED( 426 ),
    KANBAN_NOT_ALLOWED( 426 ),
    COPY_APP_NOT_ALLOWED( 426 ),
    MAX_STORAGE_REACHED( 426 ),
    BATCH_QR_IMPORT_NOT_ALLOWED( 426 ),
    BATCH_MEMBER_IMPORT_NOT_ALLOWED( 426 ),
    SUBMISSION_APPROVE_NOT_ALLOWED( 426 ),

    //429
    TOO_MANY_REQUEST( 429 ),

    //500
    SYSTEM_ERROR( 500 );

    private final int status;

    ErrorCode( int status ) {
        this.status = status;
    }
}
