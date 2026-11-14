package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CatalogRepository
import com.example.data.LocalPreferencesStore
import com.example.model.ActiveOrder
import com.example.model.AnnouncementDraft
import com.example.model.BetaEntry
import com.example.model.DeliveryDetails
import com.example.model.DwellerEntry
import com.example.model.EnvironmentKey
import com.example.model.EnvironmentRegistryEntry
import com.example.model.FeedbackItem
import com.example.model.GateThresholds
import com.example.model.GatewayFailureState
import com.example.model.GatewaySession
import com.example.model.GuestPage
import com.example.model.InvitationSlot
import com.example.model.OnboardingRecord
import com.example.model.OrderStatus
import com.example.model.OwnerAccountRecord
import com.example.model.OwnerOverlayPanel
import com.example.model.OwnerSignInMethod
import com.example.model.PipelineStage
import com.example.model.PlatformState
import com.example.model.PortalRoute
import com.example.model.RegularHomePanel
import com.example.model.StoreState
import com.example.model.UserRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.text.DateFormat
import java.util.Date

data class CelestialUiState(
    val currentRoute: PortalRoute = PortalRoute.LANDING,
    val guestPage: GuestPage? = null,
    val activeEnvironment: String = "guest",
    val menuOpen: Boolean = false,
    val storeState: StoreState = StoreState(),
    val flowNotice: String = "",

    // Access Gate & Recovery
    val accessIdInput: String = "",
    val apgInput: String = "",
    val gateMessage: String? = null,
    val gateMessageSuccess: Boolean = false,
    val gatewaySession: GatewaySession? = null,
    val gatewayLockUntil: Long = 0L,
    val recoveryOpen: Boolean = false,
    val recoveryOrderNumber: String = "",
    val recoveryAmount: String = "",
    val recoverySender: String = "",
    val recoveryOrderFound: Boolean = false,
    val recoveryMessage: String = "",
    val employeeRecoveryId: String = "",
    val employeeRecoveryAnswer: String = "",
    val employeeRecoveryVerified: Boolean = false,
    val employeeRecoveryMessage: String = "",
    val employeeNewApg: String = "",

    // Environment Prefix & Invitation Gate
    val environmentAccessCode: String = "",
    val routedEnvironment: EnvironmentKey? = null,
    val environmentRouteMessage: String = "",
    val invitationInput: String = "",
    val invitationAttempts: Int = 0,
    val invitationSlots: List<InvitationSlot> = emptyList(),
    val invitationMessage: String = "",

    // Owner Override Access
    val ownerSessionUntil: Long = 0L,
    val ownerTriggerTaps: Int = 0,
    val showOwnerAccessDialog: Boolean = false,
    val ownerSignInMethod: OwnerSignInMethod = OwnerSignInMethod.PATTERN,
    val ownerAccessId: String = "",
    val ownerAccessPhrase: String = "",
    val ownerPattern: List<Int> = emptyList(),
    val ownerPatternConfirm: List<Int> = emptyList(),
    val ownerPatternSetupStep: Int = 0,
    val ownerPin: String = "",
    val ownerPinConfirm: String = "",
    val ownerPinSetupStep: Int = 0,
    val ownerVoicePhrase: String = "",
    val ownerGoogleMessage: String = "",
    val ownerAccessMessage: String = "",

    // Owner Workspace & Overlays
    val ownerOverlayPanel: OwnerOverlayPanel = OwnerOverlayPanel.NONE,
    val ownerNotes: String = "",
    val feedbackPage: String = "",
    val feedbackElement: String = "",
    val feedbackIssue: String = "",
    val feedbackChange: String = "",
    val feedbackItems: List<FeedbackItem> = emptyList(),

    // Owner Master Control
    val platformState: PlatformState = PlatformState.LIVE,
    val previewTier: String = "Owner",
    val editUiMode: Boolean = false,
    val visibilityMap: Map<String, Boolean> = emptyMap(),
    val bannerText: String = "",
    val bannerTier: String = "Guest",
    val pipelineStage: PipelineStage = PipelineStage.SUBMITTED,
    val sopChecklist: Map<String, Boolean> = emptyMap(),
    val betaEntries: List<BetaEntry> = emptyList(),
    val betaSearch: String = "",
    val gateThresholds: GateThresholds = GateThresholds(),
    val dwellers: List<DwellerEntry> = emptyList(),
    val vaultNotes: String = "",
    val quickCopySnippet: String = "CELESTIAL LOBBY · OWNER OPERATIONS",
    val ownerMasterNotice: String = "",

    // Account Generator & Prefixes
    val accountEnvironment: String = "regular-client",
    val accountAction: String = "generate",
    val accountIdLookup: String = "",
    val accountCodeReset: String = "",
    val accountMessage: String = "",
    val ownerAccounts: List<OwnerAccountRecord> = emptyList(),
    val ownerEnvironmentPrefix: String = "",
    val ownerPrefixMessage: String = "",
    val referenceNotes: String = "",

    // Onboarding
    val onboardingRecord: OnboardingRecord? = null,
    val onboardStep: Int = 0,
    val purchaseIntent: String = "now",
    val showExitWarning: Boolean = false,
    val termsAccepted: Boolean = false,
    val restrictionReason: String = "Your onboarding session was interrupted or declined.",
    val showRestrictionHelp: Boolean = false,
    val purchaseWindowRemainingMs: Long? = null,

    // Regular Client & Army Dashboards
    val regularCelestialsOpen: Boolean = false,
    val regularFoundersOpen: Boolean = false,
    val activeHomePanel: RegularHomePanel = RegularHomePanel.TRACKER,
    val trackerQuery: String = "",
    val trackerFound: Boolean? = null,
    val trackerMessage: String? = null,
    val activeArmyPanel: String? = null,

    // 3S Accordion
    val threeSSecurityOpen: Boolean = false,
    val threeSSecrecyOpen: Boolean = false,
    val threeSSafetyOpen: Boolean = false,

    val nowMs: Long = System.currentTimeMillis()
) {
    val isOwnerActive: Boolean get() = ownerSessionUntil > nowMs
    val stardustSubtotal: Int
        get() = CatalogRepository.stardustLineup.sumOf { it.price * (storeState.cart[it.id] ?: 0) }
    val addonsSubtotal: Int
        get() = CatalogRepository.addons.sumOf { it.price * (storeState.addons[it.id] ?: 0) }
    val itemsTotal: Int get() = stardustSubtotal + addonsSubtotal
    val totalItemsCount: Int get() = storeState.cart.values.sum()
    val grandTotal: Int get() = maxOf(0, itemsTotal - storeState.discount)

    fun canAccessStore(): Boolean {
        if (gatewaySession?.role == UserRole.CLIENT || gatewaySession?.role == UserRole.OWNER) return true
        if (ownerSessionUntil > nowMs) return true
        return onboardingRecord?.status == "completed" && onboardingRecord.termsAccepted
    }
}

class CelestialViewModel(application: Application) : AndroidViewModel(application) {
    private val store = LocalPreferencesStore(application.applicationContext)
    private val random = SecureRandom()

    private val _uiState = MutableStateFlow(buildInitialState())
    val uiState: StateFlow<CelestialUiState> = _uiState.asStateFlow()

    init {
        checkInterruptedOnboarding()
        startClockTicker()
    }

    private fun buildInitialState(): CelestialUiState {
        val now = System.currentTimeMillis()
        val rawOwnerUntil = store.loadOwnerSessionUntil()
        val ownerUntil = if (rawOwnerUntil > now) rawOwnerUntil else 0L
        if (rawOwnerUntil > 0L && ownerUntil == 0L) store.saveOwnerSessionUntil(0L)

        val savedStore = store.loadStoreState().let {
            if (it.activeOrder.orderId == "#CL20261104-001") {
                StoreState(currentStep = 1)
            } else {
                it.copy(promoCode = "", discount = 0)
            }
        }
        val savedOnboarding = store.loadOnboardingRecord()
        val savedRegistry = store.loadEnvironmentRegistry()
        val ownerPrefix = savedRegistry.firstOrNull { it.environment == "owner" }?.prefix.orEmpty()
        val savedFailure = store.loadGatewayFailureState()
        val lockUntil = if (savedFailure.lockUntil > now) savedFailure.lockUntil else 0L
        val announcement = store.loadAnnouncementDraft()
        val mappedStage = when (savedStore.activeOrder.status) {
            OrderStatus.IN_TRANSIT, OrderStatus.OUT_FOR_DELIVERY, OrderStatus.DELIVERED -> PipelineStage.DISPATCHED
            OrderStatus.PROCESSING -> PipelineStage.PACKAGING
            else -> PipelineStage.SUBMITTED
        }

        return CelestialUiState(
            storeState = savedStore,
            gatewaySession = store.loadGatewaySession(),
            gatewayLockUntil = lockUntil,
            invitationAttempts = store.loadInvitationAttempts(),
            invitationSlots = store.loadInvitationSlots(),
            ownerSessionUntil = ownerUntil,
            ownerPatternSetupStep = if (store.loadOwnerPatternHash() != null) 2 else 0,
            ownerPinSetupStep = if (store.loadOwnerPinHash() != null) 2 else 0,
            ownerNotes = store.loadOwnerNotes(),
            feedbackItems = store.loadFeedbackItems(),
            platformState = store.loadPlatformState(),
            visibilityMap = store.loadVisibilityMap(),
            bannerText = announcement.text,
            bannerTier = announcement.tier,
            pipelineStage = mappedStage,
            betaEntries = store.loadBetaEntries(),
            gateThresholds = store.loadGateThresholds(),
            dwellers = store.loadDwellers(),
            vaultNotes = store.loadVaultNotes(),
            ownerAccounts = store.loadOwnerAccounts(),
            ownerEnvironmentPrefix = ownerPrefix,
            referenceNotes = store.loadReferenceNotes(),
            onboardingRecord = savedOnboarding,
            trackerQuery = savedStore.activeOrder.orderId,
            trackerFound = if (savedStore.activeOrder.orderId.isNotBlank()) true else null,
            trackerMessage = if (savedStore.activeOrder.orderId.isNotBlank()) {
                "Active order ${savedStore.activeOrder.orderId} verified in celestial ledger."
            } else null,
            nowMs = now
        )
    }

    private fun checkInterruptedOnboarding() {
        val saved = store.loadOnboardingRecord()
        val now = System.currentTimeMillis()
        if (saved != null && saved.status != "restricted" &&
            saved.purchaseWindowStartedAt != null &&
            saved.purchaseWindowOrderPlacedAt == null &&
            now - saved.purchaseWindowStartedAt >= CatalogRepository.PURCHASE_WINDOW_MS
        ) {
            applyOnboardingRestriction("The one-hour purchasing window expired before an order was placed.")
            return
        }
        if (saved?.status == "in-progress" && store.isOnboardingSessionActive()) {
            val restricted = saved.copy(
                status = "restricted",
                restrictionUntil = now + CatalogRepository.RESTRICTION_MS,
                restrictionReason = "The previous app session ended unexpectedly during onboarding.",
                termsAccepted = false
            )
            store.setOnboardingSessionActive(false)
            store.saveOnboardingRecord(restricted)
            _uiState.update {
                it.copy(onboardingRecord = restricted, restrictionReason = restricted.restrictionReason)
            }
        }
    }

    private fun startClockTicker() {
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                val now = System.currentTimeMillis()
                val current = _uiState.value

                // Check owner session expiration
                if (current.ownerSessionUntil in 1L..now) {
                    store.saveOwnerSessionUntil(0L)
                    _uiState.update {
                        it.copy(
                            ownerSessionUntil = 0L,
                            ownerOverlayPanel = OwnerOverlayPanel.NONE,
                            currentRoute = if (it.currentRoute == PortalRoute.OWNER_PREVIEW) PortalRoute.LANDING else it.currentRoute
                        )
                    }
                }

                // Check onboarding 30-min timeout
                val rec = current.onboardingRecord
                if ((current.currentRoute == PortalRoute.ONBOARDING || current.currentRoute == PortalRoute.ONBOARDING_TERMS) &&
                    rec?.status == "in-progress" &&
                    now - rec.startedAt >= CatalogRepository.ONBOARDING_TIMEOUT_MS
                ) {
                    applyOnboardingRestriction("The 30-minute onboarding time limit expired.")
                }

                // Check 1-hour purchasing window
                val updatedRec = _uiState.value.onboardingRecord
                var remainingWindow: Long? = null
                if (updatedRec != null && updatedRec.status != "restricted" &&
                    updatedRec.purchaseWindowStartedAt != null &&
                    updatedRec.purchaseWindowOrderPlacedAt == null
                ) {
                    val elapsed = now - updatedRec.purchaseWindowStartedAt
                    if (elapsed >= CatalogRepository.PURCHASE_WINDOW_MS) {
                        applyOnboardingRestriction("The one-hour purchasing window expired before an order was placed.")
                    } else {
                        remainingWindow = CatalogRepository.PURCHASE_WINDOW_MS - elapsed
                    }
                }

                _uiState.update { it.copy(nowMs = now, purchaseWindowRemainingMs = remainingWindow) }
            }
        }
    }

    // --- Navigation ---
    fun navigate(route: PortalRoute, guestSubPage: GuestPage? = null) {
        val current = _uiState.value
        val onboardingActive = current.currentRoute == PortalRoute.ONBOARDING ||
            current.currentRoute == PortalRoute.ONBOARDING_TERMS
        if (onboardingActive && route != PortalRoute.ONBOARDING && route != PortalRoute.ONBOARDING_TERMS) {
            _uiState.update { it.copy(showExitWarning = true, menuOpen = false) }
            return
        }
        if (route == PortalRoute.STORE && !current.canAccessStore()) {
            _uiState.update { it.copy(currentRoute = PortalRoute.STORE_LOCKED, menuOpen = false) }
            return
        }
        _uiState.update {
            it.copy(
                currentRoute = route,
                guestPage = guestSubPage ?: if (route == PortalRoute.GUEST) null else it.guestPage,
                menuOpen = false
            )
        }
    }

    fun navigateEnvironmentHome() {
        val current = _uiState.value
        when {
            current.isOwnerActive -> navigate(PortalRoute.OWNER_PREVIEW)
            current.activeEnvironment == "regular" -> navigate(PortalRoute.REGULAR_HOME)
            else -> {
                _uiState.update { it.copy(guestPage = null) }
                navigate(PortalRoute.GUEST)
            }
        }
    }

    fun toggleMenu(open: Boolean? = null) {
        _uiState.update { it.copy(menuOpen = open ?: !it.menuOpen) }
    }

    fun setGuestPage(page: GuestPage?) {
        _uiState.update { it.copy(guestPage = page) }
    }

    fun enterStore() {
        val current = _uiState.value
        if (!current.canAccessStore()) {
            val saved = current.onboardingRecord
            if (saved?.status == "restricted" && (saved.restrictionUntil ?: 0L) > System.currentTimeMillis()) {
                _uiState.update { it.copy(currentRoute = PortalRoute.RESTRICTION, menuOpen = false) }
            } else {
                _uiState.update { it.copy(currentRoute = PortalRoute.STORE_LOCKED, menuOpen = false) }
            }
            return
        }
        _uiState.update { it.copy(currentRoute = PortalRoute.STORE, menuOpen = false) }
    }

    // --- Access Gate & Recovery ---
    fun updateAccessId(value: String) = _uiState.update { it.copy(accessIdInput = value.uppercase()) }
    fun updateApgInput(value: String) = _uiState.update { it.copy(apgInput = value.uppercase()) }

    fun demoQuickFill() {
        val id = _uiState.value.accessIdInput.trim().uppercase()
        val cred = CatalogRepository.demoCredentials[id] ?: return
        _uiState.update {
            it.copy(
                apgInput = cred.apg,
                gateMessage = "DEMO QUICK-FILL populated the matching test credential.",
                gateMessageSuccess = true
            )
        }
    }

    fun verifyAccess() {
        val a = _uiState.value.accessIdInput.trim().uppercase()
        val p = _uiState.value.apgInput.trim().uppercase()
        val now = System.currentTimeMillis()
        var failureState = store.loadGatewayFailureState()
        if (failureState.lockUntil > now) {
            val formatted = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(failureState.lockUntil))
            _uiState.update {
                it.copy(
                    gatewayLockUntil = failureState.lockUntil,
                    gateMessage = "ACCESS LOCKED. Try again after $formatted.",
                    gateMessageSuccess = false
                )
            }
            return
        }
        if (failureState.lockUntil in 1L..now) {
            failureState = GatewayFailureState()
            store.saveGatewayFailureState(null)
        }

        val account = CatalogRepository.demoCredentials[a]
        val overrides = store.loadEmployeeApgOverrides()
        val expectedApg = account?.let { overrides[a] ?: it.apg }.orEmpty()

        if (account == null || expectedApg != p) {
            val count = failureState.count + 1
            val lockUntil = if (count >= 3) now + CatalogRepository.ACCESS_LOCK_MS else 0L
            store.saveGatewayFailureState(GatewayFailureState(count, lockUntil))
            val msg = if (lockUntil > 0L) {
                "THREE UNSUCCESSFUL ATTEMPTS. ACCESS LOCKED FOR ONE HOUR."
            } else {
                val rem = 3 - count
                "ACCESS NOT RECOGNIZED. $rem ATTEMPT${if (rem == 1) "" else "S"} REMAINING."
            }
            _uiState.update {
                it.copy(gatewayLockUntil = lockUntil, gateMessage = msg, gateMessageSuccess = false)
            }
            return
        }

        store.saveGatewayFailureState(null)
        val session = GatewaySession(accessId = a, role = account.role, startedAt = now)
        store.saveGatewaySession(session)

        when (account.role) {
            UserRole.CLIENT -> _uiState.update {
                it.copy(
                    gatewaySession = session,
                    gatewayLockUntil = 0L,
                    gateMessage = "ACCESS VERIFIED. OPENING YOUR ENVIRONMENT…",
                    gateMessageSuccess = true,
                    recoveryOpen = false,
                    activeArmyPanel = null,
                    activeEnvironment = "regular",
                    activeHomePanel = RegularHomePanel.TRACKER,
                    currentRoute = PortalRoute.REGULAR_HOME
                )
            }
            UserRole.EMP01, UserRole.EMP02 -> _uiState.update {
                it.copy(
                    gatewaySession = session,
                    gatewayLockUntil = 0L,
                    gateMessage = "ACCESS VERIFIED. OPENING YOUR ENVIRONMENT…",
                    gateMessageSuccess = true,
                    recoveryOpen = false,
                    activeArmyPanel = null,
                    currentRoute = PortalRoute.ARMY_HOME
                )
            }
            UserRole.OWNER -> {
                val expiresAt = now + CatalogRepository.OWNER_SESSION_MS
                store.saveOwnerSessionUntil(expiresAt)
                _uiState.update {
                    it.copy(
                        gatewaySession = session,
                        gatewayLockUntil = 0L,
                        ownerSessionUntil = expiresAt,
                        gateMessage = "ACCESS VERIFIED. OPENING YOUR ENVIRONMENT…",
                        gateMessageSuccess = true,
                        recoveryOpen = false,
                        activeArmyPanel = null,
                        currentRoute = PortalRoute.OWNER_PREVIEW
                    )
                }
            }
        }
    }

    fun lockSwitchAccess() {
        store.saveGatewaySession(null)
        store.saveOwnerSessionUntil(0L)
        _uiState.update {
            it.copy(
                gatewaySession = null,
                ownerSessionUntil = 0L,
                ownerOverlayPanel = OwnerOverlayPanel.NONE,
                activeEnvironment = "guest",
                guestPage = null,
                activeArmyPanel = null,
                gateMessage = null,
                recoveryOpen = false,
                currentRoute = PortalRoute.LANDING
            )
        }
    }

    fun toggleRecovery() {
        _uiState.update {
            it.copy(
                recoveryOpen = !it.recoveryOpen,
                recoveryMessage = "",
                recoveryOrderFound = false,
                employeeRecoveryMessage = ""
            )
        }
    }

    fun updateRecoveryFields(orderNumber: String? = null, amount: String? = null, sender: String? = null) {
        _uiState.update {
            it.copy(
                recoveryOrderNumber = orderNumber ?: it.recoveryOrderNumber,
                recoveryOrderFound = if (orderNumber != null) false else it.recoveryOrderFound,
                recoveryAmount = amount ?: it.recoveryAmount,
                recoverySender = sender?.uppercase()?.take(4) ?: it.recoverySender
            )
        }
    }

    fun lookupRecoveryOrder() {
        val number = _uiState.value.recoveryOrderNumber.trim().uppercase()
        if (number.isEmpty()) {
            _uiState.update { it.copy(recoveryMessage = "ENTER YOUR ORDER NUMBER.", recoveryOrderFound = false) }
            return
        }
        val now = System.currentTimeMillis()
        val active = _uiState.value.storeState.activeOrder
        val demo = CatalogRepository.demoRecoveryOrder
        val activeMatch = active.orderId.isNotBlank() &&
            active.orderId.uppercase() == number &&
            (active.startTime ?: 0L) > 0L &&
            now - (active.startTime ?: 0L) in 0L..(90L * 24L * 60L * 60L * 1000L)
        val demoMatch = demo.orderId.uppercase() == number &&
            now - demo.startTime in 0L..(90L * 24L * 60L * 60L * 1000L)
        val found = activeMatch || demoMatch
        _uiState.update {
            it.copy(
                recoveryOrderFound = found,
                recoveryMessage = if (found) {
                    "ORDER NUMBER FOUND. Continue with the exact amount paid and first four letters of the payment sender."
                } else {
                    "ORDER NUMBER NOT FOUND. Please check your entry and try again."
                }
            )
        }
    }

    fun verifyRecoveryDetails() {
        val state = _uiState.value
        val number = state.recoveryOrderNumber.trim().uppercase()
        val demo = CatalogRepository.demoRecoveryOrder
        val isDemo = number == demo.orderId
        val targetTotal = if (isDemo) demo.totalPaid else state.storeState.activeOrder.totalPaid
        val targetSender = if (isDemo) demo.paymentSender else state.storeState.activeOrder.paymentSender
        val senderPrefix = targetSender.trim().take(4).uppercase()
        val enteredAmount = state.recoveryAmount.toIntOrNull()
        val enteredSender = state.recoverySender.trim().take(4).uppercase()
        val ok = state.recoveryOrderFound && targetTotal != null && enteredAmount == targetTotal &&
            senderPrefix.length == 4 && enteredSender == senderPrefix
        _uiState.update {
            it.copy(
                recoveryMessage = if (ok) {
                    "DEMO VERIFICATION MATCHED. This confirms the sample record only. Contact celestiallobby@gmail.com for real account recovery."
                } else {
                    "DETAILS COULD NOT BE VERIFIED. Check the information or contact celestiallobby@gmail.com."
                }
            )
        }
    }

    fun updateEmployeeRecoveryFields(id: String? = null, answer: String? = null, newApg: String? = null) {
        _uiState.update {
            it.copy(
                employeeRecoveryId = id?.uppercase() ?: it.employeeRecoveryId,
                employeeRecoveryVerified = if (id != null) false else it.employeeRecoveryVerified,
                employeeRecoveryMessage = if (id != null) "" else it.employeeRecoveryMessage,
                employeeRecoveryAnswer = answer ?: it.employeeRecoveryAnswer,
                employeeNewApg = newApg?.uppercase()?.take(8) ?: it.employeeNewApg
            )
        }
    }

    fun verifyEmployeeRecovery() {
        val id = _uiState.value.employeeRecoveryId.trim().uppercase()
        val info = CatalogRepository.employeeRecovery[id]
        if (info == null || _uiState.value.employeeRecoveryAnswer.trim().uppercase() != info.answer) {
            _uiState.update {
                it.copy(
                    employeeRecoveryVerified = false,
                    employeeRecoveryMessage = "RECOVERY LOCKED. No credentials were revealed. Contact celestiallobby@gmail.com for Owner verification."
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    employeeRecoveryVerified = true,
                    employeeRecoveryMessage = "IDENTITY CHECK PASSED IN DEMO. Confirmed Access ID: $id. Set a new demo Access Code below."
                )
            }
        }
    }

    fun resetEmployeeApg() {
        val state = _uiState.value
        val id = state.employeeRecoveryId.trim().uppercase()
        val code = state.employeeNewApg.trim().uppercase()
        if (!state.employeeRecoveryVerified || !Regex("^[A-Z0-9]{8}$").matches(code)) {
            _uiState.update { it.copy(employeeRecoveryMessage = "ENTER A NEW 8-CHARACTER LETTER/NUMBER ACCESS CODE.") }
            return
        }
        val overrides = store.loadEmployeeApgOverrides().toMutableMap()
        overrides[id] = code
        store.saveEmployeeApgOverrides(overrides)
        _uiState.update {
            it.copy(
                employeeRecoveryMessage = "DEMO ACCESS CODE RESET FOR $id. Use the confirmed Access ID and your new code. Owner oversight is still required for real access restoration.",
                employeeRecoveryVerified = false,
                employeeRecoveryAnswer = "",
                employeeNewApg = ""
            )
        }
    }

    // --- Hidden Owner Access (5-Tap Trigger) ---
    fun tapEmblemForOwner() {
        val current = _uiState.value
        val next = current.ownerTriggerTaps + 1
        if (next >= 5) {
            _uiState.update { it.copy(ownerTriggerTaps = 0) }
            activateOwnerTrigger()
        } else {
            _uiState.update { it.copy(ownerTriggerTaps = next) }
        }
    }

    fun activateOwnerTrigger() {
        val now = System.currentTimeMillis()
        if (_uiState.value.ownerSessionUntil > now) {
            _uiState.update { it.copy(currentRoute = PortalRoute.OWNER_PREVIEW, showOwnerAccessDialog = false) }
            return
        }
        _uiState.update {
            it.copy(
                ownerAccessId = "",
                ownerAccessPhrase = "",
                ownerSignInMethod = OwnerSignInMethod.PATTERN,
                ownerPattern = emptyList(),
                ownerPatternConfirm = emptyList(),
                ownerPatternSetupStep = if (store.loadOwnerPatternHash() != null) 2 else 0,
                ownerPin = "",
                ownerPinConfirm = "",
                ownerPinSetupStep = if (store.loadOwnerPinHash() != null) 2 else 0,
                ownerVoicePhrase = "",
                ownerAccessMessage = "",
                ownerGoogleMessage = "",
                showOwnerAccessDialog = true
            )
        }
    }

    fun dismissOwnerAccessDialog() {
        _uiState.update {
            it.copy(
                showOwnerAccessDialog = false,
                ownerAccessId = "",
                ownerAccessPhrase = "",
                ownerAccessMessage = ""
            )
        }
    }

    fun setOwnerSignInMethod(method: OwnerSignInMethod) {
        _uiState.update {
            it.copy(
                ownerSignInMethod = method,
                ownerPattern = emptyList(),
                ownerPatternConfirm = emptyList(),
                ownerPatternSetupStep = if (store.loadOwnerPatternHash() != null) 2 else 0,
                ownerPinSetupStep = if (store.loadOwnerPinHash() != null) 2 else 0,
                ownerAccessMessage = "",
                ownerGoogleMessage = ""
            )
        }
    }

    fun tapPatternDot(index: Int) {
        _uiState.update { state ->
            if (state.ownerPatternSetupStep == 1) {
                if (state.ownerPatternConfirm.contains(index)) state
                else state.copy(ownerPatternConfirm = state.ownerPatternConfirm + index)
            } else {
                if (state.ownerPattern.contains(index)) state
                else state.copy(ownerPattern = state.ownerPattern + index)
            }
        }
    }

    fun clearOwnerPattern() {
        _uiState.update {
            it.copy(
                ownerPattern = emptyList(),
                ownerPatternConfirm = emptyList(),
                ownerAccessMessage = "Pattern cleared."
            )
        }
    }

    fun submitOwnerPattern() {
        val state = _uiState.value
        val activeSeq = if (state.ownerPatternSetupStep == 1) state.ownerPatternConfirm else state.ownerPattern
        if (activeSeq.size < 4) {
            _uiState.update { it.copy(ownerAccessMessage = "Connect at least four dots to create a pattern.") }
            return
        }
        when (state.ownerPatternSetupStep) {
            0 -> _uiState.update {
                it.copy(
                    ownerPatternConfirm = emptyList(),
                    ownerPatternSetupStep = 1,
                    ownerAccessMessage = "Pattern recorded. Draw the same pattern again to confirm."
                )
            }
            1 -> {
                if (state.ownerPattern.joinToString("-") != state.ownerPatternConfirm.joinToString("-")) {
                    _uiState.update {
                        it.copy(
                            ownerPattern = emptyList(),
                            ownerPatternConfirm = emptyList(),
                            ownerPatternSetupStep = 0,
                            ownerAccessMessage = "The patterns do not match. Start again."
                        )
                    }
                    return
                }
                store.saveOwnerPatternHash(store.sha256(state.ownerPattern.joinToString("-")))
                _uiState.update {
                    it.copy(
                        ownerPatternSetupStep = 2,
                        ownerPattern = emptyList(),
                        ownerPatternConfirm = emptyList(),
                        ownerAccessMessage = "Pattern created for this device. Draw it once more to sign in."
                    )
                }
            }
            else -> {
                val saved = store.loadOwnerPatternHash()
                val attempt = store.sha256(state.ownerPattern.joinToString("-"))
                if (saved != null && saved == attempt) {
                    enterOwnerOverride()
                } else {
                    _uiState.update { it.copy(ownerAccessMessage = "Pattern not recognized. Try again.") }
                }
            }
        }
    }

    fun updateOwnerPin(value: String, isConfirm: Boolean = false) {
        val clean = value.filter { it.isDigit() }.take(6)
        _uiState.update {
            if (isConfirm) it.copy(ownerPinConfirm = clean) else it.copy(ownerPin = clean)
        }
    }

    fun submitOwnerPin() {
        val state = _uiState.value
        val activePin = if (state.ownerPinSetupStep == 1) state.ownerPinConfirm else state.ownerPin
        if (!Regex("^\\d{6}$").matches(activePin)) {
            _uiState.update { it.copy(ownerAccessMessage = "Enter exactly six digits.") }
            return
        }
        when (state.ownerPinSetupStep) {
            0 -> _uiState.update {
                it.copy(ownerPinSetupStep = 1, ownerAccessMessage = "PIN entered. Re-enter the same six digits to confirm.")
            }
            1 -> {
                if (state.ownerPin != state.ownerPinConfirm) {
                    _uiState.update {
                        it.copy(
                            ownerPin = "",
                            ownerPinConfirm = "",
                            ownerPinSetupStep = 0,
                            ownerAccessMessage = "The PIN entries do not match. Start again."
                        )
                    }
                    return
                }
                store.saveOwnerPinHash(store.sha256(state.ownerPin))
                _uiState.update {
                    it.copy(
                        ownerPinSetupStep = 2,
                        ownerPin = "",
                        ownerPinConfirm = "",
                        ownerAccessMessage = "Six-digit PIN created for this device. Enter it again to sign in."
                    )
                }
            }
            else -> {
                val saved = store.loadOwnerPinHash()
                if (saved != null && saved == store.sha256(state.ownerPin)) {
                    enterOwnerOverride()
                } else {
                    _uiState.update { it.copy(ownerAccessMessage = "PIN not recognized. Try again.") }
                }
            }
        }
    }

    fun updateOwnerCredentials(id: String? = null, phrase: String? = null, voicePhrase: String? = null) {
        _uiState.update {
            it.copy(
                ownerAccessId = id ?: it.ownerAccessId,
                ownerAccessPhrase = phrase ?: it.ownerAccessPhrase,
                ownerVoicePhrase = voicePhrase ?: it.ownerVoicePhrase
            )
        }
    }

    fun submitOwnerPassword() {
        val state = _uiState.value
        if (state.ownerAccessId.trim() != "CL-OWNER-001" || state.ownerAccessPhrase != "V7!Orion#92Lx@Q4") {
            _uiState.update { it.copy(ownerAccessMessage = "Access ID or password not recognized. Please try again.") }
            return
        }
        enterOwnerOverride()
    }

    fun submitOwnerVoicePhrase() {
        val normalized = _uiState.value.ownerVoicePhrase.uppercase().replace(Regex("[^A-Z0-9]"), "")
        if (normalized != "AVISALAESHMA") {
            _uiState.update { it.copy(ownerAccessMessage = "Passphrase not recognized. Enter AVISALA ESHMA exactly.") }
            return
        }
        enterOwnerOverride()
    }

    fun triggerOwnerGoogleSignIn() {
        _uiState.update {
            it.copy(
                ownerGoogleMessage = "Google Sign-In is approved, but the private Apps Script verifier has not been deployed and connected yet. No Google account has been authenticated."
            )
        }
    }

    private fun enterOwnerOverride() {
        val expiresAt = System.currentTimeMillis() + CatalogRepository.OWNER_SESSION_MS
        store.saveOwnerSessionUntil(expiresAt)
        _uiState.update {
            it.copy(
                ownerSessionUntil = expiresAt,
                showOwnerAccessDialog = false,
                ownerAccessId = "",
                ownerAccessPhrase = "",
                ownerPattern = emptyList(),
                ownerPatternConfirm = emptyList(),
                ownerPin = "",
                ownerPinConfirm = "",
                ownerVoicePhrase = "",
                ownerAccessMessage = "",
                currentRoute = PortalRoute.OWNER_PREVIEW
            )
        }
    }

    fun exitOwnerOverride() {
        store.saveOwnerSessionUntil(0L)
        _uiState.update {
            it.copy(
                ownerSessionUntil = 0L,
                ownerOverlayPanel = OwnerOverlayPanel.NONE,
                currentRoute = PortalRoute.LANDING
            )
        }
    }

    // --- Guest Onboarding ---
    fun startOnboarding() {
        val saved = store.loadOnboardingRecord()
        val now = System.currentTimeMillis()
        if (saved?.status == "restricted" && (saved.restrictionUntil ?: 0L) > now) {
            _uiState.update {
                it.copy(
                    onboardingRecord = saved,
                    restrictionReason = saved.restrictionReason,
                    currentRoute = PortalRoute.RESTRICTION
                )
            }
            return
        }
        val step = if (saved != null && saved.status != "completed") saved.step.coerceIn(0, 7) else 0
        val record = if (saved != null && saved.status != "completed") {
            saved.copy(
                status = "in-progress",
                startedAt = now,
                restrictionUntil = null,
                purchaseWindowStartedAt = null,
                purchaseWindowOrderPlacedAt = null
            )
        } else {
            OnboardingRecord(status = "in-progress", step = 0, startedAt = now)
        }
        store.saveOnboardingRecord(record)
        store.setOnboardingSessionActive(true)
        _uiState.update {
            it.copy(
                onboardingRecord = record,
                onboardStep = step,
                purchaseIntent = "now",
                showExitWarning = false,
                currentRoute = PortalRoute.ONBOARDING
            )
        }
    }

    fun onboardingBack() {
        val prev = maxOf(0, _uiState.value.onboardStep - 1)
        val saved = store.loadOnboardingRecord()?.copy(step = prev)
        if (saved != null) store.saveOnboardingRecord(saved)
        _uiState.update { it.copy(onboardStep = prev, onboardingRecord = saved ?: it.onboardingRecord) }
    }

    fun onboardingContinue() {
        val state = _uiState.value
        val step = state.onboardStep
        val now = System.currentTimeMillis()
        if (step == 7) {
            if (state.purchaseIntent == "later") {
                applyOnboardingRestriction("You chose not to proceed with purchasing intent at this time.")
                return
            }
            val saved = store.loadOnboardingRecord() ?: OnboardingRecord(startedAt = now)
            val updated = saved.copy(
                status = "in-progress",
                step = 7,
                completedSteps = (saved.completedSteps + 7).distinct().sorted(),
                purchaseWindowStartedAt = saved.purchaseWindowStartedAt ?: now,
                purchaseWindowOrderPlacedAt = null,
                restrictionUntil = null,
                restrictionReason = ""
            )
            store.saveOnboardingRecord(updated)
            _uiState.update {
                it.copy(onboardingRecord = updated, currentRoute = PortalRoute.ONBOARDING_TERMS)
            }
            return
        }
        val nextStep = minOf(7, step + 1)
        val saved = store.loadOnboardingRecord() ?: OnboardingRecord(startedAt = now)
        val updated = saved.copy(
            step = nextStep,
            completedSteps = (saved.completedSteps + step).distinct().sorted()
        )
        store.saveOnboardingRecord(updated)
        _uiState.update {
            it.copy(onboardStep = nextStep, onboardingRecord = updated)
        }
    }

    fun setPurchaseIntent(intent: String) = _uiState.update { it.copy(purchaseIntent = intent) }
    fun requestOnboardingExit() = _uiState.update { it.copy(showExitWarning = true) }
    fun dismissOnboardingExitWarning() = _uiState.update { it.copy(showExitWarning = false) }
    fun confirmOnboardingExit() = applyOnboardingRestriction("You chose to leave or navigate away while onboarding was active.")

    fun applyOnboardingRestriction(reason: String) {
        val prev = store.loadOnboardingRecord() ?: _uiState.value.onboardingRecord
        val record = OnboardingRecord(
            status = "restricted",
            step = prev?.step ?: _uiState.value.onboardStep,
            startedAt = prev?.startedAt ?: System.currentTimeMillis(),
            restrictionUntil = System.currentTimeMillis() + CatalogRepository.RESTRICTION_MS,
            restrictionReason = reason,
            termsAccepted = false,
            helpUsed = prev?.helpUsed ?: false,
            completedSteps = prev?.completedSteps ?: emptyList(),
            purchaseWindowStartedAt = prev?.purchaseWindowStartedAt,
            purchaseWindowOrderPlacedAt = prev?.purchaseWindowOrderPlacedAt
        )
        store.setOnboardingSessionActive(false)
        store.saveOnboardingRecord(record)
        _uiState.update {
            it.copy(
                onboardingRecord = record,
                restrictionReason = reason,
                showExitWarning = false,
                showRestrictionHelp = false,
                guestPage = null,
                currentRoute = PortalRoute.GUEST
            )
        }
    }

    fun setTermsAccepted(accepted: Boolean) = _uiState.update { it.copy(termsAccepted = accepted) }

    fun acceptTermsAndEnterStore() {
        val saved = store.loadOnboardingRecord() ?: return
        store.setOnboardingSessionActive(false)
        val updated = saved.copy(
            status = "completed",
            termsAccepted = true,
            completedSteps = (0..7).toList(),
            restrictionUntil = null
        )
        store.saveOnboardingRecord(updated)
        _uiState.update {
            it.copy(
                onboardingRecord = updated,
                termsAccepted = true,
                currentRoute = PortalRoute.STORE
            )
        }
    }

    fun revealOneTimeRestrictionHelp() {
        val saved = store.loadOnboardingRecord() ?: return
        if (saved.helpUsed) return
        val updated = saved.copy(helpUsed = true)
        store.saveOnboardingRecord(updated)
        _uiState.update { it.copy(onboardingRecord = updated, showRestrictionHelp = true) }
    }

    fun resetOnboardingForOwner() {
        store.saveOnboardingRecord(null)
        store.setOnboardingSessionActive(false)
        _uiState.update {
            it.copy(
                onboardingRecord = null,
                onboardStep = 0,
                purchaseIntent = "now",
                showExitWarning = false,
                showRestrictionHelp = false,
                restrictionReason = "Your onboarding session was interrupted or declined.",
                guestPage = null,
                ownerOverlayPanel = OwnerOverlayPanel.NONE,
                currentRoute = PortalRoute.GUEST
            )
        }
    }

    // --- Store Workflow ---
    fun navigateStoreStep(step: Int) {
        val state = _uiState.value
        if (step > state.storeState.currentStep + 1) {
            _uiState.update { it.copy(flowNotice = "Please complete the current step before moving ahead.") }
            return
        }
        if (step >= 4 && state.totalItemsCount == 0) {
            val corrected = if (state.storeState.currentStep > 2) {
                state.storeState.copy(currentStep = 2)
            } else state.storeState
            store.saveStoreState(corrected)
            _uiState.update {
                it.copy(
                    storeState = corrected,
                    flowNotice = "Add at least one ITEM before continuing."
                )
            }
            return
        }
        val next = state.storeState.copy(currentStep = step.coerceIn(1, 8))
        store.saveStoreState(next)
        _uiState.update { it.copy(storeState = next, flowNotice = "") }
    }

    fun updateCartQuantity(itemId: String, change: Int) {
        _uiState.update { state ->
            val currentQty = state.storeState.cart[itemId] ?: 0
            val nextQty = maxOf(0, currentQty + change)
            val nextCart = state.storeState.cart.toMutableMap()
            if (nextQty == 0) nextCart.remove(itemId) else nextCart[itemId] = nextQty
            val nextStore = state.storeState.copy(cart = nextCart)
            store.saveStoreState(nextStore)
            state.copy(storeState = nextStore)
        }
    }

    fun updateAddonQuantity(addonId: String, change: Int) {
        _uiState.update { state ->
            val currentQty = state.storeState.addons[addonId] ?: 0
            val nextQty = maxOf(0, currentQty + change)
            val nextAddons = state.storeState.addons.toMutableMap()
            nextAddons[addonId] = nextQty
            val nextStore = state.storeState.copy(addons = nextAddons)
            store.saveStoreState(nextStore)
            state.copy(storeState = nextStore)
        }
    }

    fun updateDeliveryDetails(name: String? = null, phone: String? = null, address: String? = null) {
        _uiState.update { state ->
            val details = state.storeState.deliveryDetails.copy(
                name = name ?: state.storeState.deliveryDetails.name,
                phone = phone ?: state.storeState.deliveryDetails.phone,
                address = address ?: state.storeState.deliveryDetails.address
            )
            val nextStore = state.storeState.copy(deliveryDetails = details)
            store.saveStoreState(nextStore)
            state.copy(storeState = nextStore)
        }
    }

    fun placeStoreOrderPreview() {
        val state = _uiState.value
        if (state.totalItemsCount < 1) {
            val next = state.storeState.copy(currentStep = 2)
            store.saveStoreState(next)
            _uiState.update { it.copy(storeState = next, flowNotice = "Your cart is empty. Add at least one ITEM first.") }
            return
        }
        val d = state.storeState.deliveryDetails
        if (d.name.isBlank() || d.phone.isBlank() || d.address.isBlank()) {
            _uiState.update { it.copy(flowNotice = "Complete the required delivery preview details before opening the next demo screen.") }
            return
        }
        if (d.method != "Standard Rider Delivery") {
            _uiState.update { it.copy(flowNotice = "Please select the permitted standard delivery method.") }
            return
        }
        val next = state.storeState.copy(currentStep = 7)
        store.saveStoreState(next)
        _uiState.update {
            it.copy(
                storeState = next,
                flowNotice = "DEMO ONLY — NO REAL ORDER WILL BE CREATED OR PROCESSED. This opens a payment-screen preview only."
            )
        }
    }

    fun submitPaymentDisabledNotice() {
        _uiState.update {
            it.copy(flowNotice = "DEMO ONLY — PAYMENT SUBMISSION IS DISABLED. No payment proof is collected, stored, transmitted, or processed.")
        }
    }

    fun resetStoreSelections() {
        val next = StoreState(currentStep = 1)
        store.saveStoreState(next)
        _uiState.update { it.copy(storeState = next, flowNotice = "") }
    }

    // --- Regular Client & Order Timeline ---
    fun toggleRegularCelestials() = _uiState.update { it.copy(regularCelestialsOpen = !it.regularCelestialsOpen) }
    fun toggleRegularFounders() = _uiState.update { it.copy(regularFoundersOpen = !it.regularFoundersOpen) }
    fun setRegularHomePanel(panel: RegularHomePanel) = _uiState.update { it.copy(activeHomePanel = panel) }
    fun updateTrackerQuery(query: String) = _uiState.update { it.copy(trackerQuery = query) }

    fun lookupOrderInTracker() {
        val query = _uiState.value.trackerQuery.trim()
        if (query.isEmpty()) {
            _uiState.update { it.copy(trackerFound = false, trackerMessage = "Please enter an Order ID.") }
            return
        }
        val order = _uiState.value.storeState.activeOrder
        if (order.orderId.isNotBlank() && query.equals(order.orderId, ignoreCase = true)) {
            _uiState.update {
                it.copy(
                    trackerFound = true,
                    trackerMessage = "Order ${order.orderId} verified in celestial ledger. Current status: ${order.status.label}."
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    trackerFound = false,
                    trackerMessage = "NO MATCHING ORDER FOUND. Check the Order ID or contact Customer Assistance."
                )
            }
        }
    }

    fun updateOrderStatus(newStatus: OrderStatus) {
        _uiState.update { state ->
            val updatedOrder = state.storeState.activeOrder.copy(status = newStatus)
            val nextStore = state.storeState.copy(activeOrder = updatedOrder)
            store.saveStoreState(nextStore)
            val stage = when (newStatus) {
                OrderStatus.IN_TRANSIT, OrderStatus.OUT_FOR_DELIVERY, OrderStatus.DELIVERED -> PipelineStage.DISPATCHED
                OrderStatus.PROCESSING -> PipelineStage.PACKAGING
                else -> PipelineStage.SUBMITTED
            }
            state.copy(storeState = nextStore, pipelineStage = stage)
        }
    }

    fun setActiveArmyPanel(panel: String?) = _uiState.update { it.copy(activeArmyPanel = panel) }

    // --- 3S Accordions ---
    fun toggleThreeS(which: String) {
        _uiState.update {
            when (which) {
                "security" -> it.copy(threeSSecurityOpen = !it.threeSSecurityOpen)
                "secrecy" -> it.copy(threeSSecrecyOpen = !it.threeSSecrecyOpen)
                else -> it.copy(threeSSafetyOpen = !it.threeSSafetyOpen)
            }
        }
    }

    fun toggleAllThreeS() {
        _uiState.update {
            val allOpen = it.threeSSecurityOpen && it.threeSSecrecyOpen && it.threeSSafetyOpen
            val next = !allOpen
            it.copy(threeSSecurityOpen = next, threeSSecrecyOpen = next, threeSSafetyOpen = next)
        }
    }

    // --- Environment Gate & Beta Invitations ---
    fun updateEnvironmentCode(code: String) = _uiState.update {
        it.copy(environmentAccessCode = code.uppercase(), environmentRouteMessage = "")
    }

    fun identifyEnvironment() {
        val code = _uiState.value.environmentAccessCode.trim().uppercase().replace(Regex("\\s+"), "")
        if (code.length < 3) {
            _uiState.update { it.copy(environmentRouteMessage = "Enter your complete Access Code.") }
            return
        }
        val registry = store.loadEnvironmentRegistry()
            .filter { Regex("^[A-Z0-9]{2,3}$").matches(it.prefix.uppercase()) }
        val matches = registry
            .filter { code.startsWith(it.prefix.uppercase()) }
            .sortedByDescending { it.prefix.length }
        if (matches.isEmpty()) {
            _uiState.update {
                it.copy(
                    routedEnvironment = null,
                    environmentRouteMessage = "This Access Code does not match a registered environment prefix. Check the code or contact the Owner."
                )
            }
            return
        }
        if (matches.size > 1 && matches[0].prefix.length == matches[1].prefix.length) {
            _uiState.update {
                it.copy(
                    routedEnvironment = null,
                    environmentRouteMessage = "The environment registry contains an ambiguous prefix. Contact the Owner."
                )
            }
            return
        }
        if (code.length <= matches[0].prefix.length) {
            _uiState.update {
                it.copy(environmentRouteMessage = "Enter the complete Access Code, including the characters after its environment prefix.")
            }
            return
        }
        val env = EnvironmentKey.entries.firstOrNull { it.wireValue == matches[0].environment }
        if (env == null) {
            _uiState.update {
                it.copy(environmentRouteMessage = "This prefix is registered to an unsupported environment. Contact the Owner.")
            }
            return
        }
        _uiState.update {
            it.copy(
                routedEnvironment = env,
                environmentRouteMessage = "Environment identified. The prefix selects a destination only; it does not authenticate your identity or grant access.",
                currentRoute = PortalRoute.ENVIRONMENT_ROUTE
            )
        }
    }

    fun updateInvitationInput(code: String) = _uiState.update { it.copy(invitationInput = code.uppercase()) }

    fun verifyInvitation() {
        val state = _uiState.value
        if (state.invitationAttempts >= 3) {
            _uiState.update { it.copy(invitationMessage = "This device demo is locked after three unsuccessful attempts.") }
            return
        }
        val target = state.invitationInput.trim().uppercase()
        val match = state.invitationSlots.firstOrNull { it.invitationCode == target }
        if (match != null) {
            if (match.accessId.isBlank()) {
                _uiState.update { it.copy(invitationMessage = "This invitation has not yet been assigned to a beta user. Please contact the Owner.") }
                return
            }
            _uiState.update {
                it.copy(
                    invitationMessage = "Invitation verified. Continue to your beta sign-in.",
                    gateMessage = null,
                    currentRoute = PortalRoute.REGULAR_GATE
                )
            }
            return
        }
        val nextAttempts = state.invitationAttempts + 1
        store.saveInvitationAttempts(nextAttempts)
        val remaining = 3 - nextAttempts
        _uiState.update {
            it.copy(
                invitationAttempts = nextAttempts,
                invitationMessage = if (nextAttempts >= 3) {
                    "Access permanently locked in this device demo after three unsuccessful attempts."
                } else {
                    "Invitation code not recognized. $remaining attempt${if (remaining == 1) "" else "s"} remaining."
                }
            )
        }
    }

    // --- Owner Master Control & Account Management ---
    fun setOwnerOverlayPanel(panel: OwnerOverlayPanel) = _uiState.update { it.copy(ownerOverlayPanel = panel) }

    fun updateOwnerNotes(notes: String) {
        store.saveOwnerNotes(notes)
        _uiState.update { it.copy(ownerNotes = notes) }
    }

    fun updateFeedbackFields(page: String? = null, element: String? = null, issue: String? = null, change: String? = null) {
        _uiState.update {
            it.copy(
                feedbackPage = page ?: it.feedbackPage,
                feedbackElement = element ?: it.feedbackElement,
                feedbackIssue = issue ?: it.feedbackIssue,
                feedbackChange = change ?: it.feedbackChange
            )
        }
    }

    fun saveFeedbackItem() {
        val state = _uiState.value
        if (state.feedbackPage.isBlank() || state.feedbackIssue.isBlank()) return
        val item = FeedbackItem(
            id = System.currentTimeMillis().toString(),
            date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date()),
            page = state.feedbackPage.trim(),
            element = state.feedbackElement.trim(),
            issue = state.feedbackIssue.trim(),
            change = state.feedbackChange.trim()
        )
        val next = listOf(item) + state.feedbackItems
        store.saveFeedbackItems(next)
        _uiState.update {
            it.copy(
                feedbackItems = next,
                feedbackPage = "",
                feedbackElement = "",
                feedbackIssue = "",
                feedbackChange = ""
            )
        }
    }

    fun deleteFeedbackItem(id: String) {
        val next = _uiState.value.feedbackItems.filterNot { it.id == id }
        store.saveFeedbackItems(next)
        _uiState.update { it.copy(feedbackItems = next) }
    }

    fun setPreviewTier(tier: String) = _uiState.update { it.copy(previewTier = tier) }
    fun setPlatformState(state: PlatformState) {
        store.savePlatformState(state)
        _uiState.update { it.copy(platformState = state) }
    }
    fun toggleEditUiMode() = _uiState.update { it.copy(editUiMode = !it.editUiMode) }
    fun toggleVisibilityTarget(id: String) {
        val state = _uiState.value
        val key = "${state.previewTier}:$id"
        val next = state.visibilityMap.toMutableMap()
        next[key] = !(next[key] ?: false)
        store.saveVisibilityMap(next)
        _uiState.update { it.copy(visibilityMap = next) }
    }

    fun updateBanner(text: String? = null, tier: String? = null) {
        val state = _uiState.value
        val nextText = text ?: state.bannerText
        val nextTier = tier ?: state.bannerTier
        store.saveAnnouncementDraft(AnnouncementDraft(nextText, nextTier))
        _uiState.update { it.copy(bannerText = nextText, bannerTier = nextTier) }
    }

    fun updatePipelineStage(stage: PipelineStage) {
        _uiState.update { it.copy(pipelineStage = stage) }
        if (_uiState.value.storeState.activeOrder.orderId.isNotBlank()) {
            updateOrderStatus(stage.mappedStatus)
        }
    }

    fun toggleSopChecklist(item: String) {
        _uiState.update {
            val next = it.sopChecklist.toMutableMap()
            next[item] = !(next[item] ?: false)
            it.copy(sopChecklist = next)
        }
    }

    fun generateBetaBatch() {
        val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        fun makeCode(): String = buildString {
            repeat(8) { append(alphabet[random.nextInt(alphabet.length)]) }
        }
        val existing = _uiState.value.betaEntries.map { it.code }.toMutableSet()
        val next = (1..20).map { slot ->
            var code = makeCode()
            while (existing.contains(code)) code = makeCode()
            existing.add(code)
            BetaEntry(slot = slot, code = code, accessId = "", status = "Unassigned")
        }
        store.saveBetaEntries(next)
        val slots = next.map { InvitationSlot(slot = it.slot, invitationCode = "CL-${it.code}", accessId = "") }
        store.saveInvitationSlots(slots)
        _uiState.update {
            it.copy(
                betaEntries = next,
                invitationSlots = slots,
                ownerMasterNotice = "20 unique invitation codes generated on this device. These are invitation keys, not individual Access Codes."
            )
        }
    }

    fun updateBetaEntry(slot: Int, accessId: String? = null, status: String? = null) {
        val next = _uiState.value.betaEntries.map { entry ->
            if (entry.slot == slot) {
                val id = accessId?.uppercase() ?: entry.accessId
                val st = status ?: if (accessId != null) {
                    if (id.isNotBlank()) "Active" else "Unassigned"
                } else entry.status
                entry.copy(accessId = id, status = st)
            } else entry
        }
        store.saveBetaEntries(next)
        _uiState.update { it.copy(betaEntries = next) }
    }

    fun updateBetaSearch(q: String) = _uiState.update { it.copy(betaSearch = q) }

    fun updateGateThresholds(warningAttempts: Int? = null, lockoutMinutes: Int? = null) {
        val current = _uiState.value.gateThresholds
        val next = current.copy(
            warningAttempts = (warningAttempts ?: current.warningAttempts).coerceIn(1, 10),
            lockoutMinutes = (lockoutMinutes ?: current.lockoutMinutes).coerceIn(1, 1440)
        )
        store.saveGateThresholds(next)
        _uiState.update { it.copy(gateThresholds = next) }
    }

    fun updateDweller(index: Int, status: String? = null, lore: String? = null) {
        val next = _uiState.value.dwellers.mapIndexed { idx, item ->
            if (idx == index) item.copy(status = status ?: item.status, lore = lore ?: item.lore) else item
        }
        store.saveDwellers(next)
        _uiState.update { it.copy(dwellers = next) }
    }

    fun updateVaultNotes(notes: String) {
        store.saveVaultNotes(notes)
        _uiState.update { it.copy(vaultNotes = notes) }
    }

    fun updateQuickCopy(snippet: String) = _uiState.update { it.copy(quickCopySnippet = snippet) }
    fun setOwnerMasterNotice(notice: String) = _uiState.update { it.copy(ownerMasterNotice = notice) }

    fun endAllLocalSessions() {
        store.clearAllLocalSessions()
        _uiState.update {
            it.copy(
                gatewaySession = null,
                ownerSessionUntil = 0L,
                ownerOverlayPanel = OwnerOverlayPanel.NONE,
                currentRoute = PortalRoute.LANDING
            )
        }
    }

    // Account Generator
    fun updateAccountGeneratorFields(
        env: String? = null,
        action: String? = null,
        lookupId: String? = null,
        resetCode: String? = null
    ) {
        _uiState.update {
            it.copy(
                accountEnvironment = env ?: it.accountEnvironment,
                accountAction = action ?: it.accountAction,
                accountIdLookup = lookupId?.uppercase() ?: it.accountIdLookup,
                accountCodeReset = resetCode?.uppercase()?.replace(Regex("[^A-Z0-9]"), "")?.take(8) ?: it.accountCodeReset
            )
        }
    }

    fun executeAccountAction() {
        val state = _uiState.value
        val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        fun makeCode(): String = buildString {
            repeat(8) { append(alphabet[random.nextInt(alphabet.length)]) }
        }
        val prefixes = mapOf(
            "celestial-army" to "ARMY",
            "regular-client" to "CL",
            "beta-tester" to "BETA",
            "guest-visitor" to "GUEST"
        )
        val labels = mapOf(
            "celestial-army" to "Celestial Army",
            "regular-client" to "Regular Client / Member",
            "beta-tester" to "Beta Tester",
            "guest-visitor" to "Guest / Visitor"
        )
        val prefix = prefixes[state.accountEnvironment] ?: "CL"
        val label = labels[state.accountEnvironment] ?: "Regular Client / Member"

        if (state.accountAction == "generate") {
            val suffix = System.currentTimeMillis().toString(36).uppercase().takeLast(6)
            val accessId = "$prefix-$suffix"
            val record = OwnerAccountRecord(
                accessId = accessId,
                accessCode = makeCode(),
                environment = label,
                status = "Active",
                createdAt = System.currentTimeMillis()
            )
            val next = state.ownerAccounts + record
            store.saveOwnerAccounts(next)
            _uiState.update {
                it.copy(
                    ownerAccounts = next,
                    accountMessage = "Generated demo credentials: $accessId / ${record.accessCode}. Environment: ${record.environment}.",
                    accountIdLookup = "",
                    accountCodeReset = ""
                )
            }
        } else {
            val lookup = state.accountIdLookup.trim().uppercase()
            val idx = state.ownerAccounts.indexOfFirst { it.accessId == lookup }
            if (idx < 0) {
                _uiState.update { it.copy(accountMessage = "Access ID not found in this device registry.") }
                return
            }
            if (state.ownerAccounts[idx].environment != label) {
                _uiState.update { it.copy(accountMessage = "Environment mismatch. Select the account's assigned environment.") }
                return
            }
            val next = state.ownerAccounts.toMutableList()
            val msg = if (state.accountAction == "reset-code") {
                if (state.accountCodeReset.isNotEmpty() && !Regex("^[A-Z0-9]{8}$").matches(state.accountCodeReset)) {
                    _uiState.update { it.copy(accountMessage = "Access Code must contain exactly 8 letters and/or numbers.") }
                    return
                }
                val newCode = state.accountCodeReset.ifEmpty { makeCode() }
                next[idx] = next[idx].copy(accessCode = newCode)
                "Access Code reset for ${next[idx].accessId}: $newCode"
            } else {
                val suffix = System.currentTimeMillis().toString(36).uppercase().takeLast(6)
                next[idx] = next[idx].copy(accessId = "$prefix-$suffix", accessCode = makeCode())
                "Credentials regenerated. New Access ID: ${next[idx].accessId} / Access Code: ${next[idx].accessCode}"
            }
            store.saveOwnerAccounts(next)
            _uiState.update {
                it.copy(ownerAccounts = next, accountMessage = msg, accountIdLookup = "", accountCodeReset = "")
            }
        }
    }

    fun updateOwnerAccountStatus(accessId: String, status: String) {
        val next = _uiState.value.ownerAccounts.map {
            if (it.accessId == accessId) it.copy(status = status) else it
        }
        store.saveOwnerAccounts(next)
        _uiState.update { it.copy(ownerAccounts = next) }
    }

    fun updateOwnerPrefix(prefix: String) {
        val clean = prefix.uppercase().replace(Regex("[^A-Z0-9]"), "").take(3)
        _uiState.update { it.copy(ownerEnvironmentPrefix = clean, ownerPrefixMessage = "") }
    }

    fun saveOwnerEnvironmentPrefix() {
        val prefix = _uiState.value.ownerEnvironmentPrefix.trim().uppercase()
        if (!Regex("^[A-Z0-9]{2,3}$").matches(prefix)) {
            _uiState.update { it.copy(ownerPrefixMessage = "Use exactly 2 or 3 letters and/or digits for the Owner prefix.") }
            return
        }
        val others = store.loadEnvironmentRegistry().filterNot { it.environment == "owner" }
        if (others.any { it.prefix.equals(prefix, ignoreCase = true) }) {
            _uiState.update { it.copy(ownerPrefixMessage = "That prefix is already assigned to another environment.") }
            return
        }
        store.saveEnvironmentRegistry(others + EnvironmentRegistryEntry("owner", prefix))
        _uiState.update {
            it.copy(
                ownerEnvironmentPrefix = prefix,
                ownerPrefixMessage = "Owner prefix saved in this device registry. Environment Gate can now identify this destination; full credential verification remains a separate sign-in step."
            )
        }
    }

    fun updateReferenceNotes(notes: String) {
        store.saveReferenceNotes(notes)
        _uiState.update { it.copy(referenceNotes = notes) }
    }
}
