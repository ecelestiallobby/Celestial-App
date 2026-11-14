package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.PortalRoute
import com.example.model.RegularHomePanel
import com.example.model.UserRole
import com.example.ui.components.CelestialGlassCard
import com.example.ui.components.CelestialTextField
import com.example.ui.components.GoldActionButton
import com.example.ui.components.NoticeBanner
import com.example.ui.components.OrderTimelineCard
import com.example.ui.components.PortalTopline
import com.example.ui.components.SecondaryGlassButton
import com.example.ui.theme.CelestialColors
import com.example.viewmodel.CelestialUiState
import com.example.viewmodel.CelestialViewModel

@Composable
fun ClientDashboardScreen(
    state: CelestialUiState,
    viewModel: CelestialViewModel
) {
    BackHandler { viewModel.lockSwitchAccess() }
    val activeOrder = state.storeState.activeOrder

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 600.dp)) {
            PortalTopline(
                backLabel = if (state.gatewaySession?.role == UserRole.CLIENT) "Lock / Switch Access" else "Lobby",
                onBack = {
                    if (state.gatewaySession?.role == UserRole.CLIENT) viewModel.lockSwitchAccess()
                    else viewModel.navigate(PortalRoute.LANDING)
                },
                sectionBadge = "REGULAR CLIENT ENVIRONMENT"
            )

            Text("Client Dashboard", style = MaterialTheme.typography.headlineMedium, color = CelestialColors.TextPrimary)
            Spacer(Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CelestialGlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = viewModel::toggleRegularCelestials)
                ) {
                    Text("✦ Celestials", fontWeight = FontWeight.Bold, color = CelestialColors.GoldBright, fontSize = 14.sp)
                    Text("Archive ${if (state.regularCelestialsOpen) "▲" else "▼"}", fontSize = 11.sp, color = CelestialColors.VioletSoft)
                }
                CelestialGlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = viewModel::toggleRegularFounders)
                ) {
                    Text("✧ Founders", fontWeight = FontWeight.Bold, color = CelestialColors.GoldBright, fontSize = 14.sp)
                    Text("Archive ${if (state.regularFoundersOpen) "▲" else "▼"}", fontSize = 11.sp, color = CelestialColors.VioletSoft)
                }
            }

            if (state.regularCelestialsOpen) {
                Spacer(Modifier.height(8.dp))
                CelestialGlassCard {
                    Text("Celestial Archive content will be added from the official lore supplied by the owner.", fontSize = 12.sp, color = CelestialColors.TextSecondary)
                }
            }
            if (state.regularFoundersOpen) {
                Spacer(Modifier.height(8.dp))
                CelestialGlassCard {
                    Text("Four Founder profiles are planned. Source biographies are pending.", fontSize = 12.sp, color = CelestialColors.TextSecondary)
                }
            }

            Spacer(Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DashboardTile("◈", "Store", "Browse & order", Modifier.weight(1f)) { viewModel.enterStore() }
                DashboardTile("⌁", "Order Tracker", "Find an order", Modifier.weight(1f)) {
                    viewModel.setRegularHomePanel(
                        if (state.activeHomePanel == RegularHomePanel.TRACKER) RegularHomePanel.NONE else RegularHomePanel.TRACKER
                    )
                }
                DashboardTile("▤", "Order History", "Saved on device", Modifier.weight(1f)) {
                    viewModel.setRegularHomePanel(
                        if (state.activeHomePanel == RegularHomePanel.HISTORY) RegularHomePanel.NONE else RegularHomePanel.HISTORY
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Quotation", "Canvass", "Personalize", "Promotions").forEach { tag ->
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        color = CelestialColors.SurfaceDark,
                        border = BorderStroke(1.dp, CelestialColors.BorderGlass)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(tag, fontSize = 10.sp, color = CelestialColors.VioletSoft, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            if (activeOrder.orderId.isNotBlank() && state.activeHomePanel == RegularHomePanel.NONE) {
                Spacer(Modifier.height(16.dp))
                OrderTimelineCard(
                    order = activeOrder,
                    deliveryDetails = state.storeState.deliveryDetails,
                    onUpdateStatus = viewModel::updateOrderStatus,
                    onEnterStore = viewModel::enterStore
                )
            }

            if (state.activeHomePanel == RegularHomePanel.TRACKER) {
                Spacer(Modifier.height(16.dp))
                CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⌁ Order Tracker", fontWeight = FontWeight.Bold, color = CelestialColors.GoldBright, fontSize = 15.sp)
                        TextButton(onClick = { viewModel.setRegularHomePanel(RegularHomePanel.NONE) }) {
                            Text("✕", color = CelestialColors.VioletSoft)
                        }
                    }
                    CelestialTextField(
                        value = state.trackerQuery,
                        onValueChange = viewModel::updateTrackerQuery,
                        label = "ORDER ID",
                        placeholder = "e.g. #CL20261104-001",
                        testTag = "client_tracker_input"
                    )
                    Spacer(Modifier.height(10.dp))
                    GoldActionButton("Track Order", onClick = viewModel::lookupOrderInTracker, testTag = "track_order_button")
                    state.trackerMessage?.let {
                        NoticeBanner(it, isSuccess = state.trackerFound == true)
                    }
                    if (state.trackerFound == true && activeOrder.orderId.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        OrderTimelineCard(
                            order = activeOrder,
                            deliveryDetails = state.storeState.deliveryDetails,
                            onUpdateStatus = viewModel::updateOrderStatus,
                            onEnterStore = viewModel::enterStore
                        )
                    }
                }
            }

            if (state.activeHomePanel == RegularHomePanel.HISTORY) {
                Spacer(Modifier.height(16.dp))
                CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("▤ Order History", fontWeight = FontWeight.Bold, color = CelestialColors.GoldBright, fontSize = 15.sp)
                        TextButton(onClick = { viewModel.setRegularHomePanel(RegularHomePanel.NONE) }) {
                            Text("✕", color = CelestialColors.VioletSoft)
                        }
                    }
                    if (activeOrder.orderId.isNotBlank()) {
                        OrderTimelineCard(
                            order = activeOrder,
                            deliveryDetails = state.storeState.deliveryDetails,
                            onUpdateStatus = viewModel::updateOrderStatus,
                            onEnterStore = viewModel::enterStore
                        )
                    } else {
                        Text(
                            "No local demo orders found.",
                            fontSize = 13.sp,
                            color = CelestialColors.VioletMuted,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardTile(
    icon: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    CelestialGlassCard(
        modifier = modifier
            .defaultMinSize(minHeight = 84.dp)
            .clickable(onClick = onClick),
        backgroundColor = CelestialColors.GlassCardAlt
    ) {
        Text(icon, fontSize = 18.sp, color = CelestialColors.GoldLight)
        Spacer(Modifier.height(4.dp))
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
        Text(subtitle, fontSize = 10.sp, color = CelestialColors.VioletSoft, maxLines = 1)
    }
}

@Composable
fun ArmyDashboardScreen(
    state: CelestialUiState,
    viewModel: CelestialViewModel
) {
    BackHandler { viewModel.lockSwitchAccess() }
    val role = state.gatewaySession?.role
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 600.dp)) {
            PortalTopline(backLabel = "Lock / Switch Access", onBack = viewModel::lockSwitchAccess, sectionBadge = "CELESTIAL ARMIES")
            Text("AUTHORIZED DEMO WORKSPACE", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldDeep)
            Spacer(Modifier.height(4.dp))
            Text("Celestial Armies Homepage", style = MaterialTheme.typography.headlineMedium, color = CelestialColors.TextPrimary)
            Spacer(Modifier.height(6.dp))
            Text(
                "Signed in as ${state.gatewaySession?.accessId.orEmpty()}. Both employee panels are visible; only your assigned panel is enabled.",
                style = MaterialTheme.typography.bodyMedium,
                color = CelestialColors.TextSecondary
            )
            Spacer(Modifier.height(14.dp))

            val isEmp1 = role == UserRole.EMP01 || state.isOwnerActive
            val isEmp2 = role == UserRole.EMP02 || state.isOwnerActive

            CelestialGlassCard(
                modifier = Modifier.clickable(enabled = isEmp1) { viewModel.setActiveArmyPanel("emp01") },
                borderColor = if (isEmp1) CelestialColors.BorderGold else CelestialColors.BorderGlass
            ) {
                Text("EMP-01 PANEL", fontWeight = FontWeight.Bold, color = CelestialColors.GoldBright)
                Text(
                    if (isEmp1) "Enabled for this session." else "Unavailable for this account.",
                    fontSize = 12.sp,
                    color = CelestialColors.TextSecondary
                )
            }
            Spacer(Modifier.height(10.dp))
            CelestialGlassCard(
                modifier = Modifier.clickable(enabled = isEmp2) { viewModel.setActiveArmyPanel("emp02") },
                borderColor = if (isEmp2) CelestialColors.BorderGold else CelestialColors.BorderGlass
            ) {
                Text("EMP-02 PANEL", fontWeight = FontWeight.Bold, color = CelestialColors.GoldBright)
                Text(
                    if (isEmp2) "Enabled for this session." else "Unavailable for this account.",
                    fontSize = 12.sp,
                    color = CelestialColors.TextSecondary
                )
            }

            state.activeArmyPanel?.let { activePanel ->
                Spacer(Modifier.height(14.dp))
                CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("ACTIVE WORKSPACE", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldLight)
                            Text("${activePanel.uppercase()} DASHBOARD", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        }
                        TextButton(onClick = { viewModel.setActiveArmyPanel(null) }) {
                            Text("CLOSE", color = CelestialColors.VioletSoft, fontSize = 11.sp)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("MY TASKS", "PENDING", "COMPLETED").forEach { label ->
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                color = CelestialColors.DeepSpace,
                                border = BorderStroke(1.dp, CelestialColors.BorderGlass)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("0", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text(label, fontSize = 9.sp, color = CelestialColors.VioletSoft)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("MY WORK QUEUE: No assigned tasks in this demo workspace.", fontSize = 12.sp, color = CelestialColors.TextSecondary)
                    Spacer(Modifier.height(4.dp))
                    Text("WORK RECORDS: No employee records created in this preview.", fontSize = 12.sp, color = CelestialColors.TextSecondary)
                    Spacer(Modifier.height(4.dp))
                    Text("TEAM NOTICES: No active team notices.", fontSize = 12.sp, color = CelestialColors.TextSecondary)
                }
            }
        }
    }
}

@Composable
fun PortalInfoPageScreen(
    route: PortalRoute,
    state: CelestialUiState,
    viewModel: CelestialViewModel
) {
    BackHandler { viewModel.navigateEnvironmentHome() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 600.dp)) {
            PortalTopline(backLabel = "Home", onBack = viewModel::navigateEnvironmentHome, sectionBadge = "CELESTIAL LOBBY")
            when (route) {
                PortalRoute.ABOUT -> {
                    Text("OUR STORY", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldDeep)
                    Text("About Us", style = MaterialTheme.typography.headlineMedium, color = CelestialColors.TextPrimary)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "CELESTIAL LOBBY is a private client environment designed to guide visitors through its archives, onboarding journey, and storefront experience.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CelestialColors.TextSecondary
                    )
                    Spacer(Modifier.height(12.dp))
                    CelestialGlassCard {
                        Text(
                            "Explore the Celestials, meet the Founders, and learn about the standards that shape the experience. This portal is currently a demonstration; operational features are introduced only when implemented and tested.",
                            fontSize = 13.sp,
                            color = CelestialColors.TextSecondary
                        )
                    }
                }
                PortalRoute.NO_NOS -> {
                    Text("FIVE NON-NEGOTIABLES", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldDeep)
                    Text("The No-Nos", style = MaterialTheme.typography.headlineMedium, color = CelestialColors.TextPrimary)
                    Spacer(Modifier.height(12.dp))
                    listOf("NO COD", "NO MEET UPS", "NO REFUNDS", "NO RUSH ORDERS", "NO SPAMMING").forEach { rule ->
                        CelestialGlassCard(modifier = Modifier.padding(bottom = 8.dp), borderColor = CelestialColors.BorderGold) {
                            Text(rule, fontWeight = FontWeight.Bold, color = CelestialColors.GoldBright, letterSpacing = 1.2.sp)
                        }
                    }
                    Text("Please review the full Store Policies and Service Guidelines before proceeding with an order.", fontSize = 12.sp, color = CelestialColors.VioletMuted)
                }
                PortalRoute.FAQS -> {
                    Text("HELP ARCHIVE", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldDeep)
                    Text("Frequently Asked Questions", style = MaterialTheme.typography.headlineMedium, color = CelestialColors.TextPrimary)
                    Spacer(Modifier.height(12.dp))
                    listOf(
                        "About Us", "Orders & Processing", "Products & Availability",
                        "Pricing & Payments", "Packaging", "Delivery & Fulfillment", "Customer Support"
                    ).forEach { topic ->
                        CelestialGlassCard(modifier = Modifier.padding(bottom = 8.dp)) {
                            Text(topic, fontWeight = FontWeight.Bold, color = CelestialColors.GoldBright)
                            Spacer(Modifier.height(4.dp))
                            Text("Detailed answers for this category are being prepared for publication.", fontSize = 12.sp, color = CelestialColors.TextSecondary)
                        }
                    }
                }
                PortalRoute.THREE_S -> ThreeSContent(state, viewModel)
                PortalRoute.TERMS -> SimplePendingPolicyPage("Terms and Conditions", "The official Terms and Conditions page. Its governing terms and full content will be published here.", "Terms and Conditions content is pending publication.")
                PortalRoute.PRIVACY -> SimplePendingPolicyPage("Data and Privacy", "Information about data handling, privacy practices, and client information will be provided on this page.", "Data and Privacy content is pending publication.")
                PortalRoute.INFO_SEC -> SimplePendingPolicyPage("Info Sec", "Information Security guidance and relevant security notices will be published on this page.", "Information Security content is pending publication.")
                PortalRoute.CONTACT -> SimplePendingPolicyPage("Contact Us", "Official contact channels and customer assistance information will be listed here.", "Contact details are pending publication.")
                else -> {}
            }
        }
    }
}

@Composable
private fun ThreeSContent(state: CelestialUiState, viewModel: CelestialViewModel) {
    Text("CELESTIAL LOBBY · CLIENT COMMITMENT", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldDeep)
    Text("The 3S Framework", style = MaterialTheme.typography.headlineMedium, color = CelestialColors.TextPrimary)
    Spacer(Modifier.height(4.dp))
    Text("Three principles. One standard of care.", fontWeight = FontWeight.SemiBold, color = CelestialColors.GoldBright, fontSize = 14.sp)
    Spacer(Modifier.height(6.dp))
    Text(
        "Security, Secrecy and Safety guide how we approach information, account access and order handling across the Celestial Lobby experience.",
        style = MaterialTheme.typography.bodyMedium,
        color = CelestialColors.TextSecondary
    )
    Spacer(Modifier.height(14.dp))
    CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
        Image(
            painter = painterResource(id = R.drawable.hoeno_3s),
            contentDescription = "Hoeno founder portrait with the words Secretive. Secured. Safe.",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(Modifier.height(10.dp))
        Text("HOENO · FOUNDER", style = MaterialTheme.typography.labelSmall, color = CelestialColors.VioletSoft)
        Text("SECRETIVE. SECURED. SAFE.", fontWeight = FontWeight.Bold, color = CelestialColors.GoldLight)
    }

    Spacer(Modifier.height(14.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("◈" to "SECURITY", "◉" to "SECRECY", "⬡" to "SAFETY").forEach { (sym, name) ->
            CelestialGlassCard(modifier = Modifier.weight(1f)) {
                Text(sym, fontSize = 18.sp, color = CelestialColors.GoldLight)
                Text(name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }

    Spacer(Modifier.height(14.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Explore the standards", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
        TextButton(onClick = viewModel::toggleAllThreeS) {
            val allOpen = state.threeSSecurityOpen && state.threeSSecrecyOpen && state.threeSSafetyOpen
            Text(if (allOpen) "Hide all" else "Expand all", color = CelestialColors.GoldLight, fontSize = 12.sp)
        }
    }

    ThreeSAccordion(
        kicker = "01 · SYSTEM & IDENTITY PROTECTION",
        title = "SECURITY",
        subtitle = "Protecting accounts, identifiers and access.",
        expanded = state.threeSSecurityOpen,
        onToggle = { viewModel.toggleThreeS("security") },
        bullets = listOf(
            "Unique Identification: Assigned client identifiers reduce displaying personal details.",
            "Role-Based Access: Access is limited according to each team member's authorization.",
            "Communication Protection: End-to-end encryption is claimed only where actually verified."
        )
    )
    ThreeSAccordion(
        kicker = "02 · DISCRETION & INFORMATION PRIVACY",
        title = "SECRECY",
        subtitle = "Minimizing collection and limiting exposure.",
        expanded = state.threeSSecrecyOpen,
        onToggle = { viewModel.toggleThreeS("secrecy") },
        bullets = listOf(
            "Data Minimization: Avoid requesting unnecessary personal details.",
            "Need-to-Know: Operational info is visible only to authorized tasks.",
            "Retention & Deletion: Subject to documented retention and expiry policies."
        )
    )
    ThreeSAccordion(
        kicker = "03 · FULFILLMENT & HANDLING INTEGRITY",
        title = "SAFETY",
        subtitle = "Verification, packaging and careful handoff.",
        expanded = state.threeSSafetyOpen,
        onToggle = { viewModel.toggleThreeS("safety") },
        bullets = listOf(
            "Verification Standards: Checked for structural integrity and weight accuracy.",
            "Discreet Packaging: Unbranded, tamper-evident packaging where appropriate.",
            "Verified Handoff: Defined verification steps appropriate to the fulfillment method."
        )
    )
}

@Composable
private fun ThreeSAccordion(
    kicker: String,
    title: String,
    subtitle: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    bullets: List<String>
) {
    CelestialGlassCard(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .clickable(onClick = onToggle)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(kicker, style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldDeep)
                Text(title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                Text(subtitle, fontSize = 12.sp, color = CelestialColors.TextSecondary)
            }
            Text(if (expanded) "−" else "+", fontSize = 22.sp, color = CelestialColors.GoldLight)
        }
        if (expanded) {
            HorizontalDivider(color = CelestialColors.BorderGlass, modifier = Modifier.padding(vertical = 10.dp))
            bullets.forEach { bullet ->
                Text("• $bullet", fontSize = 12.sp, color = CelestialColors.TextSecondary, modifier = Modifier.padding(bottom = 4.dp))
            }
        }
    }
}

@Composable
private fun SimplePendingPolicyPage(title: String, subtitle: String, body: String) {
    Text(title, style = MaterialTheme.typography.headlineMedium, color = CelestialColors.TextPrimary)
    Spacer(Modifier.height(6.dp))
    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = CelestialColors.TextSecondary)
    Spacer(Modifier.height(12.dp))
    CelestialGlassCard {
        Text(body, fontSize = 13.sp, color = CelestialColors.TextSecondary)
    }
}
