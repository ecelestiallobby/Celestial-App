package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.OwnerOverlayPanel
import com.example.model.PortalRoute
import com.example.model.RegularHomePanel
import com.example.ui.components.CelestialBackdrop
import com.example.ui.components.PortalFooter
import com.example.ui.components.PortalTopBar
import com.example.ui.screens.AccessGateScreen
import com.example.ui.screens.ArmyDashboardScreen
import com.example.ui.screens.ClientDashboardScreen
import com.example.ui.screens.EnvironmentGateScreen
import com.example.ui.screens.EnvironmentRouteResultScreen
import com.example.ui.screens.GuestEnvironmentScreen
import com.example.ui.screens.InvitationGateScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.OnboardingSubmittedScreen
import com.example.ui.screens.OnboardingTermsScreen
import com.example.ui.screens.OwnerDashboardScreen
import com.example.ui.screens.OwnerOverlayDialogs
import com.example.ui.screens.PortalInfoPageScreen
import com.example.ui.screens.RegularClientGateScreen
import com.example.ui.screens.RestrictionScreen
import com.example.ui.screens.StoreFlowScreen
import com.example.ui.screens.StoreLockedScreen
import com.example.ui.theme.CelestialColors
import com.example.ui.theme.CelestialLobbyTheme
import com.example.viewmodel.CelestialUiState
import com.example.viewmodel.CelestialViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CelestialLobbyTheme {
                CelestialLobbyApp()
            }
        }
    }
}

@Composable
fun CelestialLobbyApp(viewModel: CelestialViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.menuOpen) {
        if (state.menuOpen && drawerState.isClosed) drawerState.open()
        else if (!state.menuOpen && drawerState.isOpen) drawerState.close()
    }

    LaunchedEffect(drawerState.currentValue) {
        val isOpen = drawerState.currentValue == DrawerValue.Open
        if (isOpen != state.menuOpen) viewModel.toggleMenu(isOpen)
    }

    val showChrome = state.currentRoute !in setOf(
        PortalRoute.LANDING,
        PortalRoute.REGULAR_GATE,
        PortalRoute.INVITATION_GATE,
        PortalRoute.ENVIRONMENT_GATE,
        PortalRoute.ENVIRONMENT_ROUTE
    )

    val showOwnerSwitcher = state.isOwnerActive && state.currentRoute !in setOf(
        PortalRoute.LANDING,
        PortalRoute.REGULAR_GATE,
        PortalRoute.INVITATION_GATE,
        PortalRoute.ENVIRONMENT_GATE,
        PortalRoute.ENVIRONMENT_ROUTE,
        PortalRoute.ONBOARDING,
        PortalRoute.ONBOARDING_TERMS
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showChrome,
        drawerContent = {
            NavigationDrawerContent(
                state = state,
                viewModel = viewModel,
                onClose = {
                    scope.launch { drawerState.close() }
                    viewModel.toggleMenu(false)
                }
            )
        }
    ) {
        CelestialBackdrop {
            Scaffold(
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets.safeDrawing,
                topBar = {
                    if (showChrome) {
                        Column {
                            PortalTopBar(
                                onBrandClick = {
                                    if (state.currentRoute == PortalRoute.STORE) {
                                        viewModel.navigate(PortalRoute.LANDING)
                                    } else {
                                        viewModel.navigateEnvironmentHome()
                                    }
                                },
                                menuOpen = state.menuOpen,
                                onToggleMenu = { viewModel.toggleMenu() },
                                purchaseWindowRemainingMs = state.purchaseWindowRemainingMs
                            )
                            if (showOwnerSwitcher) {
                                OwnerEnvironmentSwitcherBar(state = state, viewModel = viewModel)
                            }
                        }
                    }
                },
                bottomBar = {
                    if (showChrome) {
                        Box(modifier = Modifier.navigationBarsPadding()) {
                            PortalFooter(
                                onOpenRoute = { viewModel.navigate(it) },
                                onHomeClick = { viewModel.navigateEnvironmentHome() },
                                onStoreClick = { viewModel.enterStore() },
                                onTrackerClick = {
                                    viewModel.setRegularHomePanel(RegularHomePanel.TRACKER)
                                    viewModel.navigate(PortalRoute.REGULAR_HOME)
                                },
                                showNavigationRow = state.currentRoute != PortalRoute.STORE
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (state.currentRoute) {
                        PortalRoute.LANDING -> AccessGateScreen(state, viewModel)
                        PortalRoute.REGULAR_GATE -> RegularClientGateScreen(state, viewModel)
                        PortalRoute.ENVIRONMENT_GATE -> EnvironmentGateScreen(state, viewModel)
                        PortalRoute.ENVIRONMENT_ROUTE -> EnvironmentRouteResultScreen(state, viewModel)
                        PortalRoute.INVITATION_GATE -> InvitationGateScreen(state, viewModel)
                        PortalRoute.GUEST -> GuestEnvironmentScreen(state, viewModel)
                        PortalRoute.ONBOARDING -> OnboardingScreen(state, viewModel)
                        PortalRoute.ONBOARDING_TERMS -> OnboardingTermsScreen(state, viewModel)
                        PortalRoute.RESTRICTION -> RestrictionScreen(state, viewModel)
                        PortalRoute.STORE_LOCKED -> StoreLockedScreen(viewModel)
                        PortalRoute.ONBOARDING_SUBMITTED -> OnboardingSubmittedScreen(viewModel)
                        PortalRoute.REGULAR_HOME -> ClientDashboardScreen(state, viewModel)
                        PortalRoute.ARMY_HOME -> ArmyDashboardScreen(state, viewModel)
                        PortalRoute.OWNER_PREVIEW -> OwnerDashboardScreen(state, viewModel)
                        PortalRoute.STORE -> StoreFlowScreen(state, viewModel)
                        PortalRoute.ABOUT,
                        PortalRoute.NO_NOS,
                        PortalRoute.FAQS,
                        PortalRoute.TERMS,
                        PortalRoute.PRIVACY,
                        PortalRoute.INFO_SEC,
                        PortalRoute.THREE_S,
                        PortalRoute.CONTACT -> PortalInfoPageScreen(state.currentRoute, state, viewModel)
                    }
                }
            }
        }
    }

    if (state.isOwnerActive) {
        OwnerOverlayDialogs(state = state, viewModel = viewModel)
    }
}

@Composable
private fun OwnerEnvironmentSwitcherBar(
    state: CelestialUiState,
    viewModel: CelestialViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CelestialColors.SurfaceDark.copy(alpha = 0.85f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val items = listOf(
            "Owner" to (state.currentRoute == PortalRoute.OWNER_PREVIEW),
            "Guest" to (state.currentRoute == PortalRoute.GUEST),
            "Member" to (state.currentRoute == PortalRoute.REGULAR_HOME),
            "Army" to (state.currentRoute == PortalRoute.ARMY_HOME)
        )
        items.forEach { (label, active) ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (active) CelestialColors.GoldLight else CelestialColors.MidnightBg,
                border = BorderStroke(1.dp, if (active) CelestialColors.GoldLight else CelestialColors.BorderGlass),
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 36.dp)
                    .clickable {
                        when (label) {
                            "Owner" -> viewModel.navigate(PortalRoute.OWNER_PREVIEW)
                            "Guest" -> viewModel.navigate(PortalRoute.GUEST)
                            "Member" -> viewModel.navigate(PortalRoute.REGULAR_HOME)
                            else -> {
                                viewModel.setActiveArmyPanel("emp01")
                                viewModel.navigate(PortalRoute.ARMY_HOME)
                            }
                        }
                    }
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 6.dp)) {
                    Text(
                        label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (active) CelestialColors.MidnightBg else CelestialColors.TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationDrawerContent(
    state: CelestialUiState,
    viewModel: CelestialViewModel,
    onClose: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = CelestialColors.MidnightBg.copy(alpha = 0.96f),
        drawerContentColor = CelestialColors.TextPrimary,
        modifier = Modifier
            .width(280.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("CELESTIAL LOBBY", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldBright)
            Spacer(Modifier.height(12.dp))
            Text("SITE NAVIGATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CelestialColors.VioletSoft)
            Spacer(Modifier.height(6.dp))

            DrawerNavItem("HOME") { viewModel.navigateEnvironmentHome(); onClose() }
            DrawerNavItem("ABOUT US") { viewModel.navigate(PortalRoute.ABOUT); onClose() }
            DrawerNavItem("3-S STANDARDS") { viewModel.navigate(PortalRoute.THREE_S); onClose() }
            DrawerNavItem("NO-NOS") { viewModel.navigate(PortalRoute.NO_NOS); onClose() }
            DrawerNavItem("FAQS") { viewModel.navigate(PortalRoute.FAQS); onClose() }
            DrawerNavItem("STORE") { viewModel.enterStore(); onClose() }
            DrawerNavItem("ORDER TRACKER") {
                viewModel.setRegularHomePanel(RegularHomePanel.TRACKER)
                viewModel.navigate(PortalRoute.REGULAR_HOME)
                onClose()
            }

            if (state.isOwnerActive) {
                HorizontalDivider(color = CelestialColors.BorderGlass, modifier = Modifier.padding(vertical = 10.dp))
                Text("OWNER CONTROLS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CelestialColors.GoldLight)
                Spacer(Modifier.height(6.dp))
                DrawerNavItem("Owner Notepad") {
                    viewModel.setOwnerOverlayPanel(OwnerOverlayPanel.NOTEPAD)
                    onClose()
                }
                DrawerNavItem("Override Controls") {
                    viewModel.setOwnerOverlayPanel(OwnerOverlayPanel.TOOLS)
                    onClose()
                }
                DrawerNavItem("Exit Owner Session") {
                    viewModel.exitOwnerOverride()
                    onClose()
                }
            }

            Spacer(Modifier.weight(1f))
            HorizontalDivider(color = CelestialColors.BorderGlass, modifier = Modifier.padding(vertical = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("TERMS", fontSize = 10.sp, color = CelestialColors.GoldBright, modifier = Modifier.clickable { viewModel.navigate(PortalRoute.TERMS); onClose() })
                Text("PRIVACY", fontSize = 10.sp, color = CelestialColors.GoldBright, modifier = Modifier.clickable { viewModel.navigate(PortalRoute.PRIVACY); onClose() })
                Text("INFO SEC", fontSize = 10.sp, color = CelestialColors.GoldBright, modifier = Modifier.clickable { viewModel.navigate(PortalRoute.INFO_SEC); onClose() })
            }
        }
    }
}

@Composable
private fun DrawerNavItem(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CelestialColors.TextPrimary)
    }
}
