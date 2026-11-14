package com.example.data

import com.example.model.AddonItem
import com.example.model.DwellerEntry
import com.example.model.MasterCatalogEntry
import com.example.model.OnboardingStepContent
import com.example.model.StardustItem
import com.example.model.UserRole

data class DemoCredential(
    val apg: String,
    val role: UserRole
)

data class EmployeeRecoveryInfo(
    val question: String,
    val answer: String
)

data class DemoRecoveryOrder(
    val orderId: String,
    val startTime: Long,
    val totalPaid: Int,
    val paymentSender: String
)

object CatalogRepository {
    const val ONBOARDING_TIMEOUT_MS = 30L * 60L * 1000L
    const val RESTRICTION_MS = 24L * 60L * 60L * 1000L
    const val PURCHASE_WINDOW_MS = 60L * 60L * 1000L
    const val ACCESS_LOCK_MS = 60L * 60L * 1000L
    const val OWNER_SESSION_MS = 24L * 60L * 60L * 1000L

    val stardustLineup: List<StardustItem> = listOf(
        StardustItem("cloud9", "Cloud9", "5u", 700),
        StardustItem("mars", "Mars", "10u", 1200),
        StardustItem("venus", "Venus", "15u", 1700),
        StardustItem("saturn", "Saturn", "20u", 2150),
        StardustItem("nibiru", "Nibiru", "25u", 2600),
        StardustItem("nemik", "Nemik", "30u", 3100),
        StardustItem("vaelithar", "Vaelithar", "40u", 3500),
        StardustItem("half_g", "0.5 / Half G", "60u", 4450),
        StardustItem("one_g", "1.0 / One G", "110u", 6950)
    )

    val addons: List<AddonItem> = listOf(
        AddonItem("lubricant", "Lubricant (Small)", 150),
        AddonItem("accessory", "Accessory (Extra)", 50)
    )

    val dracoBulks: List<MasterCatalogEntry> = listOf(
        MasterCatalogEntry(
            id = "vhaerion-05",
            name = "VHAERION 0.5",
            category = "DRACO BULKS",
            quantity = "60u",
            pricePhp = 4450,
            includedFreebies = listOf("Lu Be", "5 Dagom")
        ),
        MasterCatalogEntry(
            id = "zhaevoryn-10",
            name = "ZHAEVORYN 1.0",
            category = "DRACO BULKS",
            quantity = "110u",
            pricePhp = 6950,
            includedFreebies = listOf("Lu Be", "10 Dagom")
        )
    )

    val specialServices: List<MasterCatalogEntry> = listOf(
        MasterCatalogEntry("priority", "Priority", "SPECIAL SERVICES", 150, description = "Priority daytime queue option, subject to availability and applicable service terms."),
        MasterCatalogEntry("gorush30", "GoRush30", "SPECIAL SERVICES", 500, description = "Requested after-hours expedited service window of 30 minutes, subject to availability."),
        MasterCatalogEntry("gorush60", "GoRush60", "SPECIAL SERVICES", 350, description = "Requested after-hours expedited service window of 60 minutes, subject to availability."),
        MasterCatalogEntry("gorush90", "GoRush90", "SPECIAL SERVICES", 200, description = "Requested after-hours expedited service window of 90 minutes, subject to availability."),
        MasterCatalogEntry("splitting", "Splitting", "SPECIAL SERVICES", 100, description = "Manual separation into individually labeled, appropriately packaged units where lawful and suitable."),
        MasterCatalogEntry("relocation", "Relocation", "SPECIAL SERVICES", 100, description = "Delivery destination change requested and confirmed before dispatch; applicable fare is additional."),
        MasterCatalogEntry("packaging-service", "Packaging", "SPECIAL SERVICES", null, description = "Custom protective packaging priced at material cost plus 30%."),
        MasterCatalogEntry("staging", "Staging", "SPECIAL SERVICES", 100, description = "Order preparation scheduled for an agreed future fulfillment window."),
        MasterCatalogEntry("delay", "Delay", "SPECIAL SERVICES", 100, description = "Additional waiting-time charge where disclosed and applicable under the service terms.")
    )

    val onboardingSteps: List<OnboardingStepContent> = listOf(
        OnboardingStepContent(
            title = "About Us",
            copy = "Celestial Lobby is a compact client environment built around clear information, transparent processes and a considered customer experience. This introduction explains how the environment is intended to work.",
            extra = "The complete business story and location details will be published after the owner confirms the final copy."
        ),
        OnboardingStepContent(
            title = "Understanding the 3S",
            copy = "Our 3S standards are Security, Secrecy and Safety. They describe the principles intended to guide access, privacy-conscious handling and order quality.",
            extra = "Security: appropriate access controls. Secrecy: information limited to authorized purposes. Safety: documented checks and careful fulfillment. This local demo does not provide encryption or guaranteed privacy."
        ),
        OnboardingStepContent(
            title = "Understanding the No-Nos",
            copy = "Please review the official No-Nos information before continuing.",
            extra = "The official No-Nos content will be displayed here once confirmed by the owner. Please consult the Guest Environment No-Nos page for the currently approved guidance."
        ),
        OnboardingStepContent(
            title = "Simplified Process Flow",
            copy = "The intended customer journey is designed to make each stage clear.",
            extra = "Browse available items → review selections and add-ons → review disclosed charges → submit order details → submit payment proof → wait for manual verification and confirmation → fulfillment and status updates."
        ),
        OnboardingStepContent(
            title = "Store Overview",
            copy = "The store uses a restrained celestial-stardust presentation rather than an oversized catalogue.",
            extra = "The original store flow is retained behind client access. Current availability and prices shown there are prototype content and must be confirmed before any real use."
        ),
        OnboardingStepContent(
            title = "Understanding the Calculation",
            copy = "Review how the estimated total is formed before you place an order.",
            extra = "Core items + add-ons + disclosed additional services + applicable delivery or handling charges − eligible discounts = estimated total. No hidden charge should be added after commitment."
        ),
        OnboardingStepContent(
            title = "Final Acknowledgment",
            copy = "Confirm that you have reviewed the introduction, standards, process and demo limitations.",
            extra = "Completing onboarding does not generate an Access ID or APG, create a Regular Client account, or approve account access."
        ),
        OnboardingStepContent(
            title = "Purchasing Intent",
            copy = "Are you ready to explore the store, or would you prefer to consider your budget first?",
            extra = "Choose “I am ready to explore the store” to proceed to the final Terms and Policies review. The one-hour purchasing window begins when you press Continue to Store; place your order within that hour. Choosing “I am not ready to purchase right now” exits to the Guest Environment homepage and activates the 24-hour viewing-only restriction."
        )
    )

    val demoCredentials: Map<String, DemoCredential> = mapOf(
        "CLI-01" to DemoCredential("CLNT2026", UserRole.CLIENT),
        "EMP-01" to DemoCredential("CLNT2026", UserRole.EMP01),
        "EMP-02" to DemoCredential("CLNT2026", UserRole.EMP02),
        "OWN-01" to DemoCredential("OWNR2026", UserRole.OWNER)
    )

    val employeeRecovery: Map<String, EmployeeRecoveryInfo> = mapOf(
        "EMP-01" to EmployeeRecoveryInfo("What is the demo recovery word assigned to EMP-01?", "ORION"),
        "EMP-02" to EmployeeRecoveryInfo("What is the demo recovery word assigned to EMP-02?", "LYRA")
    )

    // Timestamp for 2026-11-12T00:00:00+08:00
    val demoRecoveryOrder = DemoRecoveryOrder(
        orderId = "DEMO-CL-2026-001",
        startTime = 1794412800000L,
        totalPaid = 1250,
        paymentSender = "NOVA"
    )

    val defaultDwellers: List<DwellerEntry> = listOf(
        "Aelunari",
        "Kaelthari",
        "Vaelith",
        "Seraphim",
        "Nhyxari",
        "Xyrem",
        "Veyrath",
        "The Dragornborns"
    ).map { DwellerEntry(name = it, status = "Available", lore = "") }

    val defaultOwnerNotes = """
CELESTIAL LOBBY — OWNER TESTING REFERENCE

OWNER OVERRIDE PASSWORD
AVISALA ESHMA

HOW TO OPEN OWNER ACCESS
1. Return to the public landing page.
2. Tap the CELESTIAL LOBBY emblem five times.
3. Enter the Owner Override credential, Pattern, PIN, or Passphrase.
4. Owner access is a device-local demo session lasting up to 24 hours.

OWNER OVERRIDE CONTROLS
• Return to Owner Workspace — reopen the owner workspace.
• Open Guest Environment — preview the visitor portal.
• Reset Onboarding & Open Guest Environment — clear the local guest onboarding record and active onboarding session only.

ACCESS CREDENTIAL STATUS
• Regular Client Access ID: CLI-01 (Demo).
• Employee Access IDs: EMP-01, EMP-02 (Demo).
• Public promo codes have been disabled; special benefits require account-based eligibility.

IMPORTANT
This is a local demo application. Owner access is not production-secure authentication. Do not store real customer, payment, or confidential information.
""".trimIndent()
}
