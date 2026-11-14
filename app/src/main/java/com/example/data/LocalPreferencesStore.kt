package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AnnouncementDraft
import com.example.model.BetaEntry
import com.example.model.DwellerEntry
import com.example.model.EnvironmentRegistryEntry
import com.example.model.FeedbackItem
import com.example.model.GateThresholds
import com.example.model.GatewayFailureState
import com.example.model.GatewaySession
import com.example.model.InvitationSlot
import com.example.model.OnboardingRecord
import com.example.model.OwnerAccountRecord
import com.example.model.PlatformState
import com.example.model.StoreState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.security.MessageDigest

class LocalPreferencesStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("celestial_lobby_prefs_v1", Context.MODE_PRIVATE)

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun loadStoreState(): StoreState = readJson("CELESTIAL_LOBBY_STATE_v1") ?: StoreState()
    fun saveStoreState(state: StoreState) = writeJson("CELESTIAL_LOBBY_STATE_v1", state)

    fun loadOnboardingRecord(): OnboardingRecord? = readJson("CL_GUEST_ONBOARDING_V1")
    fun saveOnboardingRecord(record: OnboardingRecord?) {
        if (record == null) {
            prefs.edit().remove("CL_GUEST_ONBOARDING_V1").apply()
        } else {
            writeJson("CL_GUEST_ONBOARDING_V1", record)
        }
    }

    fun isOnboardingSessionActive(): Boolean = prefs.getBoolean("CL_ONBOARDING_ACTIVE_SESSION", false)
    fun setOnboardingSessionActive(active: Boolean) {
        prefs.edit().putBoolean("CL_ONBOARDING_ACTIVE_SESSION", active).apply()
    }

    fun loadOwnerSessionUntil(): Long = prefs.getLong("CL_OWNER_BROWSER_SESSION_V1", 0L)
    fun saveOwnerSessionUntil(expiresAt: Long) {
        if (expiresAt <= 0L) prefs.edit().remove("CL_OWNER_BROWSER_SESSION_V1").apply()
        else prefs.edit().putLong("CL_OWNER_BROWSER_SESSION_V1", expiresAt).apply()
    }

    fun loadGatewaySession(): GatewaySession? = readJson("CL_GATEWAY_SESSION_V1")
    fun saveGatewaySession(session: GatewaySession?) {
        if (session == null) prefs.edit().remove("CL_GATEWAY_SESSION_V1").apply()
        else writeJson("CL_GATEWAY_SESSION_V1", session)
    }

    fun loadGatewayFailureState(): GatewayFailureState =
        readJson("CL_GATEWAY_FAILURES_V1") ?: GatewayFailureState()
    fun saveGatewayFailureState(state: GatewayFailureState?) {
        if (state == null) prefs.edit().remove("CL_GATEWAY_FAILURES_V1").apply()
        else writeJson("CL_GATEWAY_FAILURES_V1", state)
    }

    fun loadEmployeeApgOverrides(): Map<String, String> =
        readJson("CL_EMPLOYEE_ACCESS_CODE_OVERRIDES_V1") ?: emptyMap()
    fun saveEmployeeApgOverrides(map: Map<String, String>) =
        writeJson("CL_EMPLOYEE_ACCESS_CODE_OVERRIDES_V1", map)

    fun loadOwnerPatternHash(): String? = prefs.getString("CL_OWNER_PATTERN_HASH_V1", null)
    fun saveOwnerPatternHash(hash: String) {
        prefs.edit().putString("CL_OWNER_PATTERN_HASH_V1", hash).apply()
    }

    fun loadOwnerPinHash(): String? = prefs.getString("CL_OWNER_PIN_HASH_V1", null)
    fun saveOwnerPinHash(hash: String) {
        prefs.edit().putString("CL_OWNER_PIN_HASH_V1", hash).apply()
    }

    fun loadOwnerNotes(): String =
        prefs.getString("CL_OWNER_NOTEPAD_V1", null)?.takeIf { it.isNotBlank() }
            ?: CatalogRepository.defaultOwnerNotes
    fun saveOwnerNotes(notes: String) {
        prefs.edit().putString("CL_OWNER_NOTEPAD_V1", notes).apply()
    }

    fun loadFeedbackItems(): List<FeedbackItem> = readJson("CL_OWNER_FEEDBACK_V1") ?: emptyList()
    fun saveFeedbackItems(items: List<FeedbackItem>) = writeJson("CL_OWNER_FEEDBACK_V1", items)

    fun loadInvitationSlots(): List<InvitationSlot> = readJson("CL_BETA_INVITATIONS_V1") ?: emptyList()
    fun saveInvitationSlots(slots: List<InvitationSlot>) = writeJson("CL_BETA_INVITATIONS_V1", slots)

    fun loadInvitationAttempts(): Int = prefs.getInt("CL_INVITATION_ATTEMPTS_V1", 0)
    fun saveInvitationAttempts(attempts: Int) {
        prefs.edit().putInt("CL_INVITATION_ATTEMPTS_V1", attempts).apply()
    }

    fun loadEnvironmentRegistry(): List<EnvironmentRegistryEntry> =
        readJson("CL_ENVIRONMENT_PREFIX_REGISTRY_V1") ?: emptyList()
    fun saveEnvironmentRegistry(entries: List<EnvironmentRegistryEntry>) =
        writeJson("CL_ENVIRONMENT_PREFIX_REGISTRY_V1", entries)

    fun loadOwnerAccounts(): List<OwnerAccountRecord> =
        readJson("CL_OWNER_ACCOUNT_REGISTRY_V1") ?: emptyList()
    fun saveOwnerAccounts(records: List<OwnerAccountRecord>) =
        writeJson("CL_OWNER_ACCOUNT_REGISTRY_V1", records)

    fun loadReferenceNotes(): String = prefs.getString("CL_OWNER_REFERENCE_CENTER_V1", "") ?: ""
    fun saveReferenceNotes(notes: String) {
        prefs.edit().putString("CL_OWNER_REFERENCE_CENTER_V1", notes).apply()
    }

    fun loadPlatformState(): PlatformState {
        val raw = prefs.getString("CL_OWNER_PLATFORM_STATE_V1", PlatformState.LIVE.name)
        return runCatching { PlatformState.valueOf(raw ?: PlatformState.LIVE.name) }
            .getOrDefault(PlatformState.LIVE)
    }
    fun savePlatformState(state: PlatformState) {
        prefs.edit().putString("CL_OWNER_PLATFORM_STATE_V1", state.name).apply()
    }

    fun loadVisibilityMap(): Map<String, Boolean> = readJson("CL_OWNER_VISIBILITY_V1") ?: emptyMap()
    fun saveVisibilityMap(map: Map<String, Boolean>) = writeJson("CL_OWNER_VISIBILITY_V1", map)

    fun loadBetaEntries(): List<BetaEntry> = readJson("CL_OWNER_BETA_KEYS_V1") ?: emptyList()
    fun saveBetaEntries(entries: List<BetaEntry>) = writeJson("CL_OWNER_BETA_KEYS_V1", entries)

    fun loadGateThresholds(): GateThresholds =
        readJson("CL_OWNER_GATE_THRESHOLDS_V1") ?: GateThresholds()
    fun saveGateThresholds(thresholds: GateThresholds) =
        writeJson("CL_OWNER_GATE_THRESHOLDS_V1", thresholds)

    fun loadDwellers(): List<DwellerEntry> =
        readJson("CL_OWNER_DWELLER_CMS_V1") ?: CatalogRepository.defaultDwellers
    fun saveDwellers(dwellers: List<DwellerEntry>) = writeJson("CL_OWNER_DWELLER_CMS_V1", dwellers)

    fun loadVaultNotes(): String = prefs.getString("CL_OWNER_VAULT_NOTES_V1", "") ?: ""
    fun saveVaultNotes(notes: String) {
        prefs.edit().putString("CL_OWNER_VAULT_NOTES_V1", notes).apply()
    }

    fun loadAnnouncementDraft(): AnnouncementDraft =
        readJson("CL_OWNER_ANNOUNCEMENT_DRAFT_V1") ?: AnnouncementDraft()
    fun saveAnnouncementDraft(draft: AnnouncementDraft) =
        writeJson("CL_OWNER_ANNOUNCEMENT_DRAFT_V1", draft)

    fun clearAllLocalSessions() {
        prefs.edit()
            .remove("CL_GATEWAY_SESSION_V1")
            .remove("CL_OWNER_BROWSER_SESSION_V1")
            .remove("CL_ONBOARDING_ACTIVE_SESSION")
            .apply()
    }

    fun sha256(value: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    private inline fun <reified T> readJson(key: String): T? {
        val raw = prefs.getString(key, null) ?: return null
        return runCatching { json.decodeFromString<T>(raw) }.getOrNull()
    }

    private inline fun <reified T> writeJson(key: String, value: T) {
        runCatching {
            prefs.edit().putString(key, json.encodeToString(value)).apply()
        }
    }
}
