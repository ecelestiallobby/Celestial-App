package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GuestPage
import com.example.model.PortalRoute
import com.example.ui.components.CelestialGlassCard
import com.example.ui.components.GoldActionButton
import com.example.ui.components.PortalTopline
import com.example.ui.components.SecondaryGlassButton
import com.example.ui.theme.CelestialColors
import com.example.viewmodel.CelestialUiState
import com.example.viewmodel.CelestialViewModel

@Composable
fun GuestEnvironmentScreen(
    state: CelestialUiState,
    viewModel: CelestialViewModel
) {
    BackHandler {
        if (state.guestPage != null) viewModel.setGuestPage(null)
        else viewModel.navigate(PortalRoute.LANDING)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 600.dp)) {
            PortalTopline(
                backLabel = if (state.guestPage != null) "Guest Environment" else "Access Gate",
                onBack = {
                    if (state.guestPage != null) viewModel.setGuestPage(null)
                    else viewModel.navigate(PortalRoute.LANDING)
                },
                sectionBadge = "GUEST ENVIRONMENT"
            )

            when (state.guestPage) {
                null -> GuestHomeContent(state, viewModel)
                GuestPage.CELESTIAL_OVERVIEW -> CelestialOverviewPage(viewModel)
                GuestPage.CELESTIAL_SECTORS -> CelestialSectorsPage(viewModel)
                GuestPage.LOWER_ORBIT -> LowerOrbitPage()
                GuestPage.CENTRAL_ORBIT -> CentralOrbitPage()
                GuestPage.OUTER_ORBIT -> OuterOrbitPage()
                GuestPage.DRACONIAN_OVERVIEW -> DraconianOverviewPage(viewModel)
                GuestPage.ANDROMEDA -> AndromedaPage()
                GuestPage.FOUNDERS -> FoundersPage()
            }
        }
    }
}

@Composable
private fun GuestHomeContent(state: CelestialUiState, viewModel: CelestialViewModel) {
    Text("A GATEWAY BEYOND THE KNOWN", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldDeep)
    Spacer(Modifier.height(6.dp))
    Text("Choose the archive that calls to you.", style = MaterialTheme.typography.headlineMedium, color = CelestialColors.TextPrimary)
    Spacer(Modifier.height(8.dp))
    Text(
        "Some realms reveal themselves to the curious. Others wait for those willing to ascend. Your journey begins with a choice.",
        style = MaterialTheme.typography.bodyMedium,
        color = CelestialColors.TextSecondary
    )

    val rec = state.onboardingRecord
    if (rec != null && rec.status != "completed") {
        Spacer(Modifier.height(16.dp))
        CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
            Text("YOUR JOURNEY IS WAITING", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldLight)
            Spacer(Modifier.height(6.dp))
            Text("Your onboarding progress is saved on this device.", fontSize = 13.sp, color = CelestialColors.TextSecondary)
            if (rec.status == "restricted" && (rec.restrictionUntil ?: 0L) > state.nowMs) {
                Spacer(Modifier.height(6.dp))
                Text(
                    "Viewing-only restriction is active. You may explore the public archives while Store access and onboarding continuation remain unavailable.",
                    fontSize = 12.sp,
                    color = CelestialColors.GoldBright
                )
            }
            Spacer(Modifier.height(12.dp))
            GoldActionButton("Continue Your Journey", onClick = viewModel::startOnboarding, testTag = "continue_journey_button")
        }
    } else if (rec?.status == "completed" && rec.termsAccepted) {
        Spacer(Modifier.height(16.dp))
        CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
            Text("ASCENSION RECORDED", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldLight)
            Spacer(Modifier.height(6.dp))
            Text("Your eight-step introduction is complete in this device demo.", fontSize = 13.sp, color = CelestialColors.TextSecondary)
            Spacer(Modifier.height(4.dp))
            Text("${rec.completedSteps.size} of 8 stages completed.", fontSize = 12.sp, color = CelestialColors.Emerald)
            Spacer(Modifier.height(12.dp))
            GoldActionButton("Enter the Store", onClick = viewModel::enterStore, testTag = "guest_enter_store_button")
        }
    }

    Spacer(Modifier.height(18.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        CelestialGlassCard(
            modifier = Modifier
                .weight(1f)
                .clickable { viewModel.setGuestPage(GuestPage.CELESTIAL_OVERVIEW) }
                .testTag("guest_card_celestial"),
            backgroundColor = CelestialColors.GlassCardAlt
        ) {
            Text("✧", fontSize = 24.sp, color = CelestialColors.GoldLight)
            Spacer(Modifier.height(8.dp))
            Text(
                "Across the orbital realms, every source of Stardust carries a different promise.",
                fontSize = 12.sp,
                color = CelestialColors.TextSecondary,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(14.dp))
            SecondaryGlassButton("CELESTIAL", onClick = { viewModel.setGuestPage(GuestPage.CELESTIAL_OVERVIEW) })
        }

        CelestialGlassCard(
            modifier = Modifier
                .weight(1f)
                .clickable { viewModel.setGuestPage(GuestPage.FOUNDERS) }
                .testTag("guest_card_founder"),
            backgroundColor = CelestialColors.GlassCardAlt
        ) {
            Text("♛", fontSize = 24.sp, color = CelestialColors.GoldLight)
            Spacer(Modifier.height(8.dp))
            Text(
                "Behind every realm is an origin. Step into the archive of those who began the story.",
                fontSize = 12.sp,
                color = CelestialColors.TextSecondary,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(14.dp))
            SecondaryGlassButton("FOUNDER", onClick = { viewModel.setGuestPage(GuestPage.FOUNDERS) })
        }
    }

    Spacer(Modifier.height(18.dp))
    CelestialGlassCard {
        Text("THE FIRST THRESHOLD", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldDeep)
        Spacer(Modifier.height(8.dp))
        Text(
            "Before the next passage opens, learn the ways of this environment and decide when you are ready to proceed.",
            style = MaterialTheme.typography.bodyMedium,
            color = CelestialColors.TextSecondary
        )
        Spacer(Modifier.height(14.dp))
        GoldActionButton(
            text = if (rec != null && rec.status != "completed") "Continue Your Journey" else "ONBOARD NOW",
            onClick = viewModel::startOnboarding,
            testTag = "onboard_now_button"
        )
    }
}

@Composable
private fun CelestialOverviewPage(viewModel: CelestialViewModel) {
    Text("THE CELESTIAL ARCHIVE", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldDeep)
    Spacer(Modifier.height(4.dp))
    Text("The Celestials", style = MaterialTheme.typography.headlineMedium, color = CelestialColors.TextPrimary)
    Spacer(Modifier.height(8.dp))
    Text(
        "Across the dimensional network, the Celestials endure through Stardust—the essence that sustains their existence, their gifts, and the powers woven into their being. Yet no two realms shape that essence alike.",
        style = MaterialTheme.typography.bodyMedium,
        color = CelestialColors.TextSecondary
    )
    Spacer(Modifier.height(14.dp))
    CelestialGlassCard {
        Text("THE ESSENCE OF EXISTENCE", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldLight)
        Spacer(Modifier.height(6.dp))
        Text(
            "Without Stardust, a Celestial may lose strength, skills, magic, and the abilities that define them. The origin of that Stardust matters: each realm bears its own nature.",
            fontSize = 13.sp,
            color = CelestialColors.TextSecondary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "And when a being draws near to a realm and synchronizes with its dwellers, a different kind of awakening may begin.",
            fontSize = 12.sp,
            fontStyle = FontStyle.Italic,
            color = CelestialColors.VioletSoft
        )
    }
    Spacer(Modifier.height(18.dp))
    Text("Two paths beyond the threshold", style = MaterialTheme.typography.titleMedium, color = CelestialColors.TextPrimary)
    Spacer(Modifier.height(10.dp))
    ArchiveRouteCard(
        icon = "☄",
        title = "Celestial Sectors",
        desc = "Follow the orbital paths. Discover the dwellers, their essence, and the ascensions held within each realm.",
        action = "Explore the sectors ↗",
        onClick = { viewModel.setGuestPage(GuestPage.CELESTIAL_SECTORS) }
    )
    Spacer(Modifier.height(10.dp))
    ArchiveRouteCard(
        icon = "♜",
        title = "Draconian Archive",
        desc = "Beyond the recognized territories lies a domain of private influence, ancient bloodlines, and sovereign privilege.",
        action = "Approach the restricted archive ↗",
        onClick = { viewModel.setGuestPage(GuestPage.DRACONIAN_OVERVIEW) }
    )
}

@Composable
private fun CelestialSectorsPage(viewModel: CelestialViewModel) {
    Text("CELESTIAL ARCHIVE · ORBITAL INDEX", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldDeep)
    Spacer(Modifier.height(4.dp))
    Text("Where will you ascend?", style = MaterialTheme.typography.headlineMedium, color = CelestialColors.TextPrimary)
    Spacer(Modifier.height(8.dp))
    Text("Every passage leads to a different nature. Choose the orbit that draws you closer.", style = MaterialTheme.typography.bodyMedium, color = CelestialColors.TextSecondary)
    Spacer(Modifier.height(14.dp))
    SectorCard("LOWER ORBIT SECTOR", "Cloud9 · Mars", "A gentler threshold, where restoration and momentum take their first form.") {
        viewModel.setGuestPage(GuestPage.LOWER_ORBIT)
    }
    Spacer(Modifier.height(10.dp))
    SectorCard("CENTRAL ORBIT SECTOR", "Venus · Saturn", "Where refinement, synergy, and the shaping of greater builds begin to reveal themselves.") {
        viewModel.setGuestPage(GuestPage.CENTRAL_ORBIT)
    }
    Spacer(Modifier.height(10.dp))
    SectorCard("OUTER ORBIT SECTOR", "Nibiru · Nemik", "Beyond familiar limits, where endurance is tested and the unknown draws near.") {
        viewModel.setGuestPage(GuestPage.OUTER_ORBIT)
    }
}

@Composable
private fun LowerOrbitPage() {
    OrbitRealmPage(
        eyebrow = "LOWER ORBIT SECTOR",
        title = "The first awakening.",
        lead = "Some arrive seeking restoration. Others come to feel their energy return. Here, the first ascent is only the beginning.",
        realms = listOf(
            RealmLore("☁️ CLOUD9 · NEW SPAWN'S AREA", "Aelunari — The Dreamborn", "The Aelunari inhabit Cloud9, a tranquil pocket-realm suspended above the dimensional network. Their bodies naturally regenerate Stardust energy, making their server the preferred recovery zone.", "Trait: Low-Latency • Recovery • Respawn\nDifficulty: Easy • Recommended: Beginners\nSystem: Rest. Recharge. Respawn.", "Let your strength return. Feel the weight of the world fall away, and remember what it is to rise."),
            RealmLore("🔴 MARS · MID-GAME RUSH", "Kaelthari — The Redborn", "The Kaelthari evolved beneath the crimson skies of Mars, where every encounter is built around speed, intensity, and rapid energy cycling. Famous for Rush Instances.", "Trait: Speed • Rush • High Output\nDifficulty: Easy • Recommended: Regular Players\nSystem: Queue fast. Burn bright. Breed faster.", "When stillness no longer satisfies, answer the call of the crimson rush.")
        )
    )
}

@Composable
private fun CentralOrbitPage() {
    OrbitRealmPage(
        eyebrow = "CENTRAL ORBIT SECTOR",
        title = "Refinement has its price.",
        lead = "Here, presence becomes influence. Synergy deepens, and every enhancement brings you closer to the build you were meant to command.",
        realms = listOf(
            RealmLore("🟣 VENUS · INCUBUS' FAVORITE", "Vaelith — The Velvetborn", "Venusian species renowned throughout the Galactic Federation for their exceptional Charm, Synergy, and Focus stats. Operates like a permanent high-tier guild instance.", "Trait: Charm • Synergy • Elite Progression\nDifficulty: Mid-High • Recommended: Advanced Players\nSystem: Velvet Aura. Elite Guild.", "Some entrances are remembered. Others leave an impression that refuses to fade."),
            RealmLore("🪐 SATURN · THE EMPRESS BUILD", "Seraphrin — The Ringborn", "The Seraphrin dwell within Saturn's colossal ring system, constructing their civilization around ancient Empress Cores capable of dramatically enhancing player builds.", "Trait: Build Enhancement • Augmentation • Raid Preparation\nDifficulty: High-Advance • Recommended: B ranks\nSystem: Solid built. Hard Core. Indulgence.", "A stronger form is not simply found. It is forged, refined, and claimed.")
        )
    )
}

@Composable
private fun OuterOrbitPage() {
    OrbitRealmPage(
        eyebrow = "OUTER ORBIT SECTOR",
        title = "Beyond the familiar.",
        lead = "The farther you travel, the less the old rules can promise you. Only those willing to face the unknown discover what waits beyond the edge.",
        realms = listOf(
            RealmLore("🌑 NIBIRU · CELESTIAL INVERTED REALM", "Nhyxari — The Starborn", "The Nhyxari are ancient entities who migrated through the void long before conventional star maps existed. Players who synchronize unlock powerful Vitality Buffs.", "Traits: Vitality • Endurance • Raid Boss\nDifficulty: Boss • Recommended: A Rank\nSystem: Void Migration | Dense Cores | Dark Fantasy", "The void does not call everyone. Those who answer may return changed."),
            RealmLore("⚫ NEMIK · THE ABYSSAL ZONE", "Xyrem — The Abyssals", "Nobody knows where the Xyrem originated. Only fearless high-level players enter willingly. Those who survive gain access to unusual Endurance, Focus, and Limit-Break mechanics.", "Traits: Abyss • Endurance • Limit Break\nDifficulty: ABYSS • Recommended: S rank\nSystem: Unstable Rift. Zero Rules. Overwhelming Playstyle.", "At the edge of every limit lies a question: how far are you willing to go?")
        )
    )
}

@Composable
private fun DraconianOverviewPage(viewModel: CelestialViewModel) {
    Text("RESTRICTED ARCHIVE · ANDROMEDA GALAXY", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldDeep)
    Spacer(Modifier.height(4.dp))
    Text("The Draconians", style = MaterialTheme.typography.headlineMedium, color = CelestialColors.TextPrimary)
    Spacer(Modifier.height(8.dp))
    Text(
        "Some worlds invite exploration. Others are entered quietly, through circles where influence is inherited and access is never assumed.",
        style = MaterialTheme.typography.bodyMedium,
        color = CelestialColors.TextSecondary
    )
    Spacer(Modifier.height(14.dp))
    CelestialGlassCard {
        Text("A PRIVATE DOMAIN", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldLight)
        Spacer(Modifier.height(6.dp))
        Text(
            "The Draconian territories are a separate archive from the Celestial orbital realms. Here, discretion carries weight, lineage opens doors, and every invitation implies a world few are permitted to see.",
            fontSize = 13.sp,
            color = CelestialColors.TextSecondary
        )
    }
    Spacer(Modifier.height(14.dp))
    ArchiveRouteCard(
        icon = "♛",
        title = "Restricted Sector of Andromeda Galaxy",
        desc = "Approach the houses of the Draconian Empire and discover the bloodlines whose names command a room before they enter it.",
        action = "Request passage ↗",
        onClick = { viewModel.setGuestPage(GuestPage.ANDROMEDA) }
    )
}

@Composable
private fun AndromedaPage() {
    OrbitRealmPage(
        eyebrow = "DRACONIAN ARCHIVE · RESTRICTED TERRITORY",
        title = "Restricted Sector of Andromeda Galaxy",
        lead = "Beyond the public map, the old houses remain. Their power is not announced; it is understood.",
        realms = listOf(
            RealmLore("🌍 HIGHYRTS · THE DRAGONBORN", "Vhaerion — Reptalian Valkyries", "The Vhaerion belong to the bureaucratic houses of the Draconian Empire. They specialize in Bonding Protocols, allowing compatible players to maintain unusually high energy and synergy.", "Server Trait: Rapid • Elated • Ecstatic\nDifficulty: SS • Recommended: S rank players\nSystem: Neverland Euphoria", "An alliance can be an advantage. A bond, in the right hands, can become an empire."),
            RealmLore("GOD-TIER DRACONIANS", "Zhaevoryn", "The ruling bloodlines of the Draconian Empire. They possess extraordinary energy density, and their ancient Sovereign Protocols are among the most powerful abilities in the Galactic Federation.", "Server Trait: Supremacy • Sovereignty\nDifficulty: SSS • Recommended: SS Rank players\nSystem: Ibiza Rave Ragnarok", "There is wealth, there is influence, and then there is the authority to define the rules.")
        )
    )
}

@Composable
private fun FoundersPage() {
    Text("ORIGINS · FOUNDERS ARCHIVE", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldDeep)
    Spacer(Modifier.height(4.dp))
    Text("Those who began the story.", style = MaterialTheme.typography.headlineMedium, color = CelestialColors.TextPrimary)
    Spacer(Modifier.height(8.dp))
    Text(
        "Every world has an origin. Every legacy begins with those who dared to imagine what did not yet exist.",
        style = MaterialTheme.typography.bodyMedium,
        color = CelestialColors.TextSecondary
    )
    Spacer(Modifier.height(16.dp))
    val founders = listOf(
        Triple("Erson", "Founder", "✦"),
        Triple("Spencer", "Founder", "✧"),
        Triple("Eunice", "Founder", "♛"),
        Triple("Hono", "Founder", "◇")
    )
    founders.chunked(2).forEach { pair ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            pair.forEach { (name, role, symbol) ->
                CelestialGlassCard(modifier = Modifier.weight(1f)) {
                    Text(symbol, fontSize = 26.sp, color = CelestialColors.GoldBright)
                    Spacer(Modifier.height(8.dp))
                    Text(name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CelestialColors.TextPrimary)
                    Text(role, fontSize = 12.sp, color = CelestialColors.VioletSoft)
                }
            }
        }
    }
    Text(
        "Founder biographies and individual profile details will be added when their official introductions are supplied.",
        fontSize = 12.sp,
        color = CelestialColors.VioletMuted
    )
}

private data class RealmLore(
    val badge: String,
    val title: String,
    val body: String,
    val specs: String,
    val quote: String
)

@Composable
private fun OrbitRealmPage(
    eyebrow: String,
    title: String,
    lead: String,
    realms: List<RealmLore>
) {
    Text(eyebrow, style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldDeep)
    Spacer(Modifier.height(4.dp))
    Text(title, style = MaterialTheme.typography.headlineMedium, color = CelestialColors.TextPrimary)
    Spacer(Modifier.height(8.dp))
    Text(lead, style = MaterialTheme.typography.bodyMedium, color = CelestialColors.TextSecondary)
    Spacer(Modifier.height(14.dp))
    realms.forEach { realm ->
        CelestialGlassCard(modifier = Modifier.padding(bottom = 12.dp)) {
            Text(realm.badge, style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldLight)
            Spacer(Modifier.height(6.dp))
            Text(realm.title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = CelestialColors.TextPrimary)
            Spacer(Modifier.height(6.dp))
            Text(realm.body, fontSize = 13.sp, color = CelestialColors.TextSecondary, lineHeight = 19.sp)
            Spacer(Modifier.height(10.dp))
            Text(realm.specs, fontSize = 11.sp, color = CelestialColors.VioletSoft, lineHeight = 17.sp)
            Spacer(Modifier.height(10.dp))
            Text(realm.quote, fontSize = 12.sp, fontStyle = FontStyle.Italic, color = CelestialColors.GoldBright)
        }
    }
}

@Composable
private fun ArchiveRouteCard(
    icon: String,
    title: String,
    desc: String,
    action: String,
    onClick: () -> Unit
) {
    CelestialGlassCard(
        modifier = Modifier
            .defaultMinSize(minHeight = 48.dp)
            .clickable(onClick = onClick),
        backgroundColor = CelestialColors.GlassCardAlt
    ) {
        Text(icon, fontSize = 22.sp, color = CelestialColors.GoldLight)
        Spacer(Modifier.height(6.dp))
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(Modifier.height(4.dp))
        Text(desc, fontSize = 12.sp, color = CelestialColors.TextSecondary, lineHeight = 18.sp)
        Spacer(Modifier.height(10.dp))
        Text(action, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CelestialColors.GoldLight)
    }
}

@Composable
private fun SectorCard(kicker: String, title: String, desc: String, onClick: () -> Unit) {
    CelestialGlassCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(kicker, style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldDeep)
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Text("↗", fontSize = 18.sp, color = CelestialColors.GoldLight)
        }
        Spacer(Modifier.height(6.dp))
        Text(desc, fontSize = 12.sp, color = CelestialColors.TextSecondary)
    }
}
