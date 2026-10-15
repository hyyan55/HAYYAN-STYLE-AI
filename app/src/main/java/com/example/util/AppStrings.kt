package com.example.util

object AppStrings {
    const val APP_NAME = "HAYYAN STYLE AI"
    const val DEVELOPER_NAME_EN = "Hayyan Mohamed"
    const val DEVELOPER_NAME_AR = "حيان محمد"
    const val CREDIT_EN = "Developed by Hayyan Mohamed"
    const val CREDIT_AR = "المطور حيان محمد"
    const val TAGLINE_EN = "Your Wardrobe. Your Style."
    const val TAGLINE_AR = "خزانتك، أناقتك، اختيارك."
    const val WHATSAPP_PHONE = "+249927309195"
    const val WHATSAPP_URL = "https://wa.me/249927309195"
    const val WHATSAPP_PREFILLED_MSG = "السلام عليكم، لدي استفسار بخصوص تطبيق HAYYAN STYLE AI."
    const val APP_VERSION = "v1.0.0"

    // Helper functions for dynamic language
    fun getAppName(): String = APP_NAME

    fun getDeveloperCredit(isArabic: Boolean): String =
        if (isArabic) CREDIT_AR else CREDIT_EN

    fun getTagline(isArabic: Boolean): String =
        if (isArabic) TAGLINE_AR else TAGLINE_EN

    fun getDeveloperName(isArabic: Boolean): String =
        if (isArabic) DEVELOPER_NAME_AR else DEVELOPER_NAME_EN
}
