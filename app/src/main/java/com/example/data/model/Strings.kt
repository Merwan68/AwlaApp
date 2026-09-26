package com.example.data.model

object AppStrings {
    fun get(key: String, lang: AppLanguage): String {
        return translations[key]?.get(lang) ?: translations[key]?.get(AppLanguage.ENGLISH) ?: key
    }

    private val translations: Map<String, Map<AppLanguage, String>> = mapOf(
        "app_title" to mapOf(
            AppLanguage.ENGLISH to "Al-Awla Tour & Travel",
            AppLanguage.AMHARIC to "አል-አውላ አስጎብኚ እና የጉዞ ወኪል",
            AppLanguage.ARABIC to "الأولى للسياحة والسفر"
        ),
        "tagline" to mapOf(
            AppLanguage.ENGLISH to "Your Sacred Journey, Our Sacred Trust",
            AppLanguage.AMHARIC to "የተቀደሰው ጉዞዎ፣ የእኛ ታማኝ አደራ",
            AppLanguage.ARABIC to "رحلتكم المباركة، أمانتنا المقدسة"
        ),
        "home" to mapOf(
            AppLanguage.ENGLISH to "Home",
            AppLanguage.AMHARIC to "ዋና ገጽ",
            AppLanguage.ARABIC to "الرئيسية"
        ),
        "umrah" to mapOf(
            AppLanguage.ENGLISH to "Umrah",
            AppLanguage.AMHARIC to "ዑምራ",
            AppLanguage.ARABIC to "عمرة"
        ),
        "tours" to mapOf(
            AppLanguage.ENGLISH to "Tours",
            AppLanguage.AMHARIC to "አለም አቀፍ ጉዞዎች",
            AppLanguage.ARABIC to "رحلات دولية"
        ),
        "bookings" to mapOf(
            AppLanguage.ENGLISH to "Bookings",
            AppLanguage.AMHARIC to "የተያዙ ጉዞዎች",
            AppLanguage.ARABIC to "حجوزاتي"
        ),
        "support" to mapOf(
            AppLanguage.ENGLISH to "Support",
            AppLanguage.AMHARIC to "ድጋፍ / ቻት",
            AppLanguage.ARABIC to "الدعم الفوري"
        ),
        "contact" to mapOf(
            AppLanguage.ENGLISH to "Contact",
            AppLanguage.AMHARIC to "አግኙን",
            AppLanguage.ARABIC to "اتصل بنا"
        ),
        "profile" to mapOf(
            AppLanguage.ENGLISH to "Profile",
            AppLanguage.AMHARIC to "መገለጫ",
            AppLanguage.ARABIC to "الملف الشخصي"
        ),
        "admin" to mapOf(
            AppLanguage.ENGLISH to "Admin Desk",
            AppLanguage.AMHARIC to "አስተዳዳሪ",
            AppLanguage.ARABIC to "لوحة الإدارة"
        ),
        "quick_book" to mapOf(
            AppLanguage.ENGLISH to "Book Now",
            AppLanguage.AMHARIC to "አሁን ይያዙ",
            AppLanguage.ARABIC to "احجز الآن"
        ),
        "view_details" to mapOf(
            AppLanguage.ENGLISH to "View Details",
            AppLanguage.AMHARIC to "ዝርዝሮችን ይመልከቱ",
            AppLanguage.ARABIC to "عرض التفاصيل"
        ),
        "featured_umrah" to mapOf(
            AppLanguage.ENGLISH to "Featured Umrah Packages",
            AppLanguage.AMHARIC to "ተመራጭ የዑምራ ፓኬጆች",
            AppLanguage.ARABIC to "باقات العمرة المميزة"
        ),
        "global_destinations" to mapOf(
            AppLanguage.ENGLISH to "International Destinations",
            AppLanguage.AMHARIC to "ዓለም አቀፍ መዳረሻዎች",
            AppLanguage.ARABIC to "وجهات سياحية دولية"
        ),
        "why_alawla" to mapOf(
            AppLanguage.ENGLISH to "Why Choose Al-Awla?",
            AppLanguage.AMHARIC to "ለምን አል-አውላን ይመርጣሉ?",
            AppLanguage.ARABIC to "لماذا تختار الأولى؟"
        ),
        "coming_soon" to mapOf(
            AppLanguage.ENGLISH to "Coming Soon",
            AppLanguage.AMHARIC to "በቅርብ ቀን",
            AppLanguage.ARABIC to "قريباً"
        ),
        "track_booking" to mapOf(
            AppLanguage.ENGLISH to "Track Status",
            AppLanguage.AMHARIC to "ሁኔታን ተከታተል",
            AppLanguage.ARABIC to "تتبع الحجز"
        ),
        "live_chat_title" to mapOf(
            AppLanguage.ENGLISH to "Al-Awla Live Concierge",
            AppLanguage.AMHARIC to "አል-አውላ የቀጥታ ውይይት ድጋፍ",
            AppLanguage.ARABIC to "المحادثة المباشرة مع خدمة العملاء"
        ),
        "chat_placeholder" to mapOf(
            AppLanguage.ENGLISH to "Ask about packages, visas, or your booking...",
            AppLanguage.AMHARIC to "ስለ ፓኬጆች፣ ቪዛ ወይም ቦታ ማስያዝዎ ይጠይቁ...",
            AppLanguage.ARABIC to "اسأل عن الباقات أو التأشيرات أو حجزك..."
        ),
        "send" to mapOf(
            AppLanguage.ENGLISH to "Send",
            AppLanguage.AMHARIC to "ላክ",
            AppLanguage.ARABIC to "إرسال"
        ),
        "whatsapp_chat" to mapOf(
            AppLanguage.ENGLISH to "WhatsApp Us",
            AppLanguage.AMHARIC to "በዋትስአፕ ያግኙን",
            AppLanguage.ARABIC to "تواصل عبر واتساب"
        ),
        "call_hotline" to mapOf(
            AppLanguage.ENGLISH to "Call Hotline",
            AppLanguage.AMHARIC to "በስልክ ይደውሉ",
            AppLanguage.ARABIC to "اتصال هاتفي"
        ),
        "diaspora_sponsorship" to mapOf(
            AppLanguage.ENGLISH to "Diaspora Family Sponsorship",
            AppLanguage.AMHARIC to "የዲያስፖራ ቤተሰብ ድጋፍ",
            AppLanguage.ARABIC to "رعاية عائلات المغتربين"
        ),
        "guaranteed_services" to mapOf(
            AppLanguage.ENGLISH to "100% Guaranteed Flights & Luxury Haram Hotels",
            AppLanguage.AMHARIC to "100% አስተማማኝ በረራዎችና ምርጥ የሀረም ሆቴሎች",
            AppLanguage.ARABIC to "رحلات مؤكدة 100% وفنادق فاخرة مطلة على الحرم"
        ),
        "booking_receipt" to mapOf(
            AppLanguage.ENGLISH to "Booking Voucher & QR Reference",
            AppLanguage.AMHARIC to "የቦታ ማስያዣ ደረሰኝ እና የQR ኮድ",
            AppLanguage.ARABIC to "إيصال الحجز ورمز QR"
        ),
        "upload_documents" to mapOf(
            AppLanguage.ENGLISH to "Upload Travel Documents",
            AppLanguage.AMHARIC to "የጉዞ ሰነዶችን ይጫኑ",
            AppLanguage.ARABIC to "رفع مستندات السفر"
        )
    )
}
