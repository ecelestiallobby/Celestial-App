package com.example.model

import kotlinx.serialization.Serializable

@Serializable
enum class OrderStatus(val wireValue: String, val label: String) {
    SUBMITTED("submitted", "Submitted"),
    PAYMENT_SUBMITTED("payment_submitted", "Payment Submitted"),
    PAYMENT_EXPIRED("payment_expired", "Payment Expired"),
    CONFIRMED("confirmed", "Confirmed"),
    PROCESSING("processing", "Processing"),
    IN_TRANSIT("in_transit", "In Transit"),
    OUT_FOR_DELIVERY("out_for_delivery", "Out for Delivery"),
    DELIVERED("delivered", "Delivered")
}

@Serializable
data class StardustItem(
    val id: String,
    val name: String,
    val units: String,
    val price: Int
)

@Serializable
data class AddonItem(
    val id: String,
    val name: String,
    val price: Int
)

@Serializable
data class MasterCatalogEntry(
    val id: String,
    val name: String,
    val category: String,
    val pricePhp: Int? = null,
    val quantity: String? = null,
    val description: String? = null,
    val includedFreebies: List<String> = emptyList()
)

@Serializable
data class OnboardingStepContent(
    val title: String,
    val copy: String,
    val extra: String
)

@Serializable
data class DeliveryDetails(
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val method: String = "Standard Rider Delivery"
)

@Serializable
data class ActiveOrder(
    val orderId: String = "",
    val startTime: Long? = null,
    val paymentSender: String = "",
    val paymentScreenshot: String? = null,
    val status: OrderStatus = OrderStatus.SUBMITTED,
    val submittedTime: String? = null,
    val paymentTime: String? = null,
    val totalPaid: Int? = null
)

@Serializable
data class StoreState(
    val currentStep: Int = 1,
    val cart: Map<String, Int> = emptyMap(),
    val addons: Map<String, Int> = emptyMap(),
    val promoCode: String = "",
    val discount: Int = 0,
    val deliveryDetails: DeliveryDetails = DeliveryDetails(),
    val activeOrder: ActiveOrder = ActiveOrder()
)

@Serializable
enum class PortalRoute {
    LANDING,
    ENVIRONMENT_GATE,
    ENVIRONMENT_ROUTE,
    INVITATION_GATE,
    OWNER_PREVIEW,
    ARMY_HOME,
    GUEST,
    ONBOARDING,
    ONBOARDING_TERMS,
    RESTRICTION,
    STORE_LOCKED,
    ONBOARDING_SUBMITTED,
    REGULAR_GATE,
    REGULAR_HOME,
    ABOUT,
    NO_NOS,
    FAQS,
    TERMS,
    PRIVACY,
    INFO_SEC,
    THREE_S,
    CONTACT,
    STORE
}

enum class GuestPage {
    CELESTIAL_OVERVIEW,
    CELESTIAL_SECTORS,
    LOWER_ORBIT,
    CENTRAL_ORBIT,
    OUTER_ORBIT,
    DRACONIAN_OVERVIEW,
    ANDROMEDA,
    FOUNDERS
}

enum class UserRole {
    CLIENT,
    EMP01,
    EMP02,
    OWNER
}

@Serializable
data class GatewaySession(
    val accessId: String,
    val role: UserRole,
    val startedAt: Long
)

@Serializable
data class GatewayFailureState(
    val count: Int = 0,
    val lockUntil: Long = 0L
)

@Serializable
data class OnboardingRecord(
    val status: String = "in-progress",
    val step: Int = 0,
    val startedAt: Long = 0L,
    val restrictionUntil: Long? = null,
    val restrictionReason: String = "",
    val termsAccepted: Boolean = false,
    val helpUsed: Boolean = false,
    val completedSteps: List<Int> = emptyList(),
    val purchaseWindowStartedAt: Long? = null,
    val purchaseWindowOrderPlacedAt: Long? = null
)

enum class OwnerSignInMethod {
    PATTERN,
    PIN,
    PASSWORD,
    VOICE,
    GOOGLE
}

enum class EnvironmentKey(val wireValue: String, val displayLabel: String) {
    OWNER("owner", "CELESTIAL LOBBY OWNER"),
    ARMY_OWNER("army-owner", "CELESTIAL ARMY OWNER"),
    ARMY_MEMBER("army-member", "CELESTIAL ARMY MEMBER"),
    REGULAR_CLIENT("regular-client", "REGULAR CLIENT")
}

@Serializable
data class EnvironmentRegistryEntry(
    val environment: String,
    val prefix: String
)

@Serializable
data class InvitationSlot(
    val slot: Int,
    val invitationCode: String,
    val accessId: String = ""
)

@Serializable
data class OwnerAccountRecord(
    val accessId: String,
    val accessCode: String,
    val environment: String,
    val status: String = "Active",
    val createdAt: Long
)

@Serializable
data class BetaEntry(
    val slot: Int,
    val code: String,
    val accessId: String = "",
    val status: String = "Unassigned"
)

@Serializable
data class DwellerEntry(
    val name: String,
    val status: String = "Available",
    val lore: String = ""
)

@Serializable
data class FeedbackItem(
    val id: String,
    val date: String,
    val page: String,
    val element: String = "",
    val issue: String,
    val change: String = ""
)

@Serializable
data class GateThresholds(
    val warningAttempts: Int = 2,
    val lockoutMinutes: Int = 60
)

@Serializable
data class AnnouncementDraft(
    val text: String = "",
    val tier: String = "Guest"
)

enum class PlatformState(val label: String) {
    LIVE("Live"),
    MAINTENANCE("Maintenance"),
    LOCKDOWN("Lockdown")
}

enum class PipelineStage(val id: String, val label: String, val mappedStatus: OrderStatus) {
    SUBMITTED("submitted", "Submitted", OrderStatus.SUBMITTED),
    PACKAGING("packaging", "In Packaging", OrderStatus.PROCESSING),
    WEIGHT_VERIFIED("weight-verified", "Weight Verified", OrderStatus.PROCESSING),
    DISPATCHED("dispatched", "Dispatched", OrderStatus.IN_TRANSIT)
}

enum class RegularHomePanel {
    NONE,
    TRACKER,
    HISTORY
}

enum class OwnerOverlayPanel {
    NONE,
    NOTEPAD,
    TOOLS
}
