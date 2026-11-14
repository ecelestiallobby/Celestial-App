package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.CatalogRepository
import com.example.model.OwnerSignInMethod
import com.example.model.PortalRoute
import com.example.ui.components.CelestialGlassCard
import com.example.ui.components.CelestialTextField
import com.example.ui.components.GoldActionButton
import com.example.ui.components.NoticeBanner
import com.example.ui.components.SecondaryGlassButton
import com.example.ui.theme.CelestialColors
import com.example.viewmodel.CelestialUiState
import com.example.viewmodel.CelestialViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun AccessGateScreen(
    state: CelestialUiState,
    viewModel: CelestialViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.widthIn(max = 520.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(CelestialColors.SurfaceDark)
                    .border(1.5.dp, CelestialColors.GoldBright.copy(alpha = 0.75f), CircleShape)
                    .clickable { viewModel.tapEmblemForOwner() }
                    .testTag("celestial_emblem_trigger"),
                contentAlignment = Alignment.Center
            ) {
                Text("✦", fontSize = 42.sp, color = CelestialColors.GoldLight)
            }

            Spacer(Modifier.height(22.dp))
            Text(
                text = "CELESTIAL LOBBY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                color = CelestialColors.GoldBright
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "ACCESS GATE",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .width(96.dp)
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, CelestialColors.GoldBright, Color.Transparent)
                        )
                    )
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Enter your assigned credentials to access your environment.",
                style = MaterialTheme.typography.bodyMedium,
                color = CelestialColors.TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(22.dp))

            CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
                CelestialTextField(
                    value = state.accessIdInput,
                    onValueChange = viewModel::updateAccessId,
                    label = "ACCESS ID",
                    placeholder = "Enter your Access ID (e.g. CLI-01)",
                    testTag = "gate_access_id_input"
                )
                Spacer(Modifier.height(14.dp))
                CelestialTextField(
                    value = state.apgInput,
                    onValueChange = viewModel::updateApgInput,
                    label = "ACCESS CODE",
                    placeholder = "Enter your Access Code",
                    visualTransformation = PasswordVisualTransformation(),
                    testTag = "gate_access_code_input"
                )
                Spacer(Modifier.height(16.dp))
                GoldActionButton(
                    text = "VERIFY ACCESS",
                    onClick = viewModel::verifyAccess,
                    testTag = "verify_access_button"
                )

                state.gateMessage?.let {
                    Spacer(Modifier.height(8.dp))
                    NoticeBanner(text = it, isSuccess = state.gateMessageSuccess)
                }

                if (CatalogRepository.demoCredentials.containsKey(state.accessIdInput.trim().uppercase())) {
                    TextButton(
                        onClick = viewModel::demoQuickFill,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("demo_quick_fill_button")
                    ) {
                        Text(
                            "DEMO QUICK-FILL · FILL TEST CODE",
                            color = CelestialColors.GoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                TextButton(
                    onClick = viewModel::toggleRecovery,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("forgot_access_button")
                ) {
                    Text(
                        text = "FORGOT YOUR ACCESS?",
                        color = CelestialColors.VioletSoft,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }

                if (state.recoveryOpen) {
                    HorizontalDivider(color = CelestialColors.BorderGlass, modifier = Modifier.padding(vertical = 8.dp))
                    Text("CLIENT ACCESS RECOVERY", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldBright)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Enter your order number. Checks local record and sample record (DEMO-CL-2026-001 · ₱1,250 · NOVA).",
                        fontSize = 11.sp,
                        color = CelestialColors.TextSecondary
                    )
                    Spacer(Modifier.height(8.dp))
                    CelestialTextField(
                        value = state.recoveryOrderNumber,
                        onValueChange = { viewModel.updateRecoveryFields(orderNumber = it) },
                        label = "ORDER NUMBER",
                        placeholder = "Enter order number",
                        testTag = "recovery_order_input"
                    )
                    Spacer(Modifier.height(8.dp))
                    SecondaryGlassButton("LOOK UP ORDER", onClick = viewModel::lookupRecoveryOrder, testTag = "lookup_recovery_button")
                    if (state.recoveryOrderFound) {
                        Spacer(Modifier.height(8.dp))
                        CelestialTextField(
                            value = state.recoveryAmount,
                            onValueChange = { viewModel.updateRecoveryFields(amount = it) },
                            label = "EXACT AMOUNT PAID",
                            placeholder = "0",
                            testTag = "recovery_amount_input"
                        )
                        Spacer(Modifier.height(8.dp))
                        CelestialTextField(
                            value = state.recoverySender,
                            onValueChange = { viewModel.updateRecoveryFields(sender = it) },
                            label = "FIRST 4 LETTERS OF PAYMENT SENDER",
                            placeholder = "ABCD",
                            testTag = "recovery_sender_input"
                        )
                        Spacer(Modifier.height(8.dp))
                        GoldActionButton("VERIFY DETAILS", onClick = viewModel::verifyRecoveryDetails, testTag = "verify_recovery_button")
                    }
                    NoticeBanner(state.recoveryMessage)

                    HorizontalDivider(color = CelestialColors.BorderGlass, modifier = Modifier.padding(vertical = 10.dp))
                    Text("EMPLOYEE ACCESS RECOVERY", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldBright)
                    Spacer(Modifier.height(6.dp))
                    CelestialTextField(
                        value = state.employeeRecoveryId,
                        onValueChange = { viewModel.updateEmployeeRecoveryFields(id = it) },
                        label = "EMPLOYEE ID",
                        placeholder = "EMP-01 or EMP-02",
                        testTag = "employee_recovery_id_input"
                    )
                    val empInfo = CatalogRepository.employeeRecovery[state.employeeRecoveryId.trim().uppercase()]
                    if (empInfo != null) {
                        Spacer(Modifier.height(6.dp))
                        Text(empInfo.question, fontSize = 11.sp, color = CelestialColors.VioletSoft)
                        Spacer(Modifier.height(6.dp))
                        CelestialTextField(
                            value = state.employeeRecoveryAnswer,
                            onValueChange = { viewModel.updateEmployeeRecoveryFields(answer = it) },
                            label = "SECURITY ANSWER",
                            placeholder = "Enter assigned answer",
                            testTag = "employee_recovery_answer_input"
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    SecondaryGlassButton("VERIFY EMPLOYEE", onClick = viewModel::verifyEmployeeRecovery, testTag = "verify_employee_button")
                    if (state.employeeRecoveryVerified) {
                        Spacer(Modifier.height(8.dp))
                        CelestialTextField(
                            value = state.employeeNewApg,
                            onValueChange = { viewModel.updateEmployeeRecoveryFields(newApg = it) },
                            label = "NEW 8-CHAR ACCESS CODE",
                            placeholder = "8 alphanumeric chars",
                            visualTransformation = PasswordVisualTransformation(),
                            testTag = "employee_new_code_input"
                        )
                        Spacer(Modifier.height(8.dp))
                        GoldActionButton("RESET DEMO ACCESS CODE", onClick = viewModel::resetEmployeeApg, testTag = "reset_employee_code_button")
                    }
                    NoticeBanner(state.employeeRecoveryMessage)
                }

                if (state.gatewayLockUntil > state.nowMs) {
                    val lockStr = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(state.gatewayLockUntil))
                    Text(
                        text = "LOCKED UNTIL $lockStr",
                        color = CelestialColors.Rose,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            SecondaryGlassButton(
                text = "JOIN US / ENTER AS VISITOR",
                onClick = { viewModel.navigate(PortalRoute.GUEST) },
                testTag = "enter_as_visitor_button"
            )

            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextButton(
                    onClick = { viewModel.navigate(PortalRoute.ENVIRONMENT_GATE) },
                    modifier = Modifier.weight(1f).defaultMinSize(minHeight = 48.dp)
                ) {
                    Text("ENVIRONMENT GATE", fontSize = 10.sp, color = CelestialColors.VioletMuted)
                }
                TextButton(
                    onClick = { viewModel.navigate(PortalRoute.INVITATION_GATE) },
                    modifier = Modifier.weight(1f).defaultMinSize(minHeight = 48.dp)
                ) {
                    Text("BETA INVITATION", fontSize = 10.sp, color = CelestialColors.VioletMuted)
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = "DEMO ONLY — NOT SECURE FOR REAL CUSTOMER DATA",
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = CelestialColors.VioletMuted.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }

    if (state.showOwnerAccessDialog) {
        OwnerAccessDialog(state = state, viewModel = viewModel)
    }
}

@Composable
private fun OwnerAccessDialog(
    state: CelestialUiState,
    viewModel: CelestialViewModel
) {
    Dialog(onDismissRequest = viewModel::dismissOwnerAccessDialog) {
        CelestialGlassCard(
            borderColor = CelestialColors.BorderGold,
            backgroundColor = CelestialColors.MidnightBg
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "RESTRICTED OWNER ACCESS",
                        style = MaterialTheme.typography.labelSmall,
                        color = CelestialColors.GoldLight
                    )
                    TextButton(onClick = viewModel::dismissOwnerAccessDialog) {
                        Text("CLOSE", color = CelestialColors.GoldBright, fontSize = 11.sp)
                    }
                }
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val methods = listOf(
                        OwnerSignInMethod.PATTERN to "Pattern",
                        OwnerSignInMethod.PIN to "PIN",
                        OwnerSignInMethod.PASSWORD to "Password",
                        OwnerSignInMethod.VOICE to "Phrase"
                    )
                    methods.forEach { (method, label) ->
                        val active = state.ownerSignInMethod == method
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (active) CelestialColors.GoldLight else CelestialColors.SurfaceDark,
                            border = BorderStroke(1.dp, if (active) CelestialColors.GoldLight else CelestialColors.BorderGlass),
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 48.dp)
                                .clickable { viewModel.setOwnerSignInMethod(method) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
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
                Spacer(Modifier.height(12.dp))

                when (state.ownerSignInMethod) {
                    OwnerSignInMethod.PATTERN -> {
                        val prompt = when (state.ownerPatternSetupStep) {
                            0 -> "Create a pattern by tapping at least four dots."
                            1 -> "Confirm your new pattern."
                            else -> "Tap your saved Owner pattern."
                        }
                        Text(prompt, fontSize = 12.sp, color = CelestialColors.TextSecondary)
                        Spacer(Modifier.height(10.dp))
                        val selected = if (state.ownerPatternSetupStep == 1) state.ownerPatternConfirm else state.ownerPattern
                        Column(
                            modifier = Modifier
                                .width(200.dp)
                                .align(Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (row in 0..2) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    for (col in 0..2) {
                                        val idx = row * 3 + col
                                        val isPicked = selected.contains(idx)
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .clip(CircleShape)
                                                .background(if (isPicked) CelestialColors.GoldLight.copy(alpha = 0.25f) else CelestialColors.SurfaceDark)
                                                .border(2.dp, if (isPicked) CelestialColors.GoldLight else CelestialColors.VioletMuted, CircleShape)
                                                .clickable { viewModel.tapPatternDot(idx) }
                                                .testTag("owner_pattern_dot_$idx"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                if (isPicked) "${selected.indexOf(idx) + 1}" else "·",
                                                color = if (isPicked) CelestialColors.GoldLight else CelestialColors.TextSecondary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("${selected.size} dots selected", fontSize = 11.sp, color = CelestialColors.VioletSoft, modifier = Modifier.align(Alignment.CenterHorizontally))
                        Spacer(Modifier.height(10.dp))
                        GoldActionButton(
                            text = when (state.ownerPatternSetupStep) {
                                0 -> "Continue Pattern Setup"
                                1 -> "Confirm Pattern"
                                else -> "Unlock with Pattern"
                            },
                            onClick = viewModel::submitOwnerPattern,
                            testTag = "submit_owner_pattern_button"
                        )
                        Spacer(Modifier.height(6.dp))
                        SecondaryGlassButton("Clear Pattern", onClick = viewModel::clearOwnerPattern)
                    }

                    OwnerSignInMethod.PIN -> {
                        val prompt = when (state.ownerPinSetupStep) {
                            0 -> "Create a six-digit Owner PIN."
                            1 -> "Confirm your six-digit PIN."
                            else -> "Enter your saved six-digit PIN."
                        }
                        Text(prompt, fontSize = 12.sp, color = CelestialColors.TextSecondary)
                        Spacer(Modifier.height(8.dp))
                        CelestialTextField(
                            value = if (state.ownerPinSetupStep == 1) state.ownerPinConfirm else state.ownerPin,
                            onValueChange = { viewModel.updateOwnerPin(it, isConfirm = state.ownerPinSetupStep == 1) },
                            label = if (state.ownerPinSetupStep == 1) "CONFIRM 6-DIGIT PIN" else "6-DIGIT PIN",
                            placeholder = "••••••",
                            visualTransformation = PasswordVisualTransformation(),
                            testTag = "owner_pin_input"
                        )
                        Spacer(Modifier.height(10.dp))
                        GoldActionButton(
                            text = when (state.ownerPinSetupStep) {
                                0 -> "Continue PIN Setup"
                                1 -> "Confirm PIN"
                                else -> "Unlock with PIN"
                            },
                            onClick = viewModel::submitOwnerPin,
                            testTag = "submit_owner_pin_button"
                        )
                    }

                    OwnerSignInMethod.PASSWORD -> {
                        CelestialTextField(
                            value = state.ownerAccessId,
                            onValueChange = { viewModel.updateOwnerCredentials(id = it) },
                            label = "OWNER ACCESS ID",
                            placeholder = "CL-OWNER-001",
                            testTag = "owner_access_id_input"
                        )
                        Spacer(Modifier.height(10.dp))
                        CelestialTextField(
                            value = state.ownerAccessPhrase,
                            onValueChange = { viewModel.updateOwnerCredentials(phrase = it) },
                            label = "OWNER PASSWORD",
                            placeholder = "Enter Owner password",
                            visualTransformation = PasswordVisualTransformation(),
                            testTag = "owner_password_input"
                        )
                        Spacer(Modifier.height(12.dp))
                        GoldActionButton(
                            text = "Sign in to Owner Dashboard",
                            onClick = viewModel::submitOwnerPassword,
                            testTag = "submit_owner_password_button"
                        )
                    }

                    OwnerSignInMethod.VOICE, OwnerSignInMethod.GOOGLE -> {
                        CelestialTextField(
                            value = state.ownerVoicePhrase,
                            onValueChange = { viewModel.updateOwnerCredentials(voicePhrase = it) },
                            label = "OWNER PASSPHRASE",
                            placeholder = "AVISALA ESHMA",
                            testTag = "owner_phrase_input"
                        )
                        Spacer(Modifier.height(10.dp))
                        GoldActionButton(
                            text = "Submit Passphrase",
                            onClick = viewModel::submitOwnerVoicePhrase,
                            testTag = "submit_owner_phrase_button"
                        )
                        Spacer(Modifier.height(8.dp))
                        SecondaryGlassButton(
                            text = "Continue with Google",
                            onClick = viewModel::triggerOwnerGoogleSignIn
                        )
                        NoticeBanner(state.ownerGoogleMessage)
                    }
                }

                NoticeBanner(state.ownerAccessMessage)
            }
        }
    }
}

@Composable
fun RegularClientGateScreen(
    state: CelestialUiState,
    viewModel: CelestialViewModel
) {
    BackHandler { viewModel.navigate(PortalRoute.LANDING) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 520.dp)) {
            CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
                Text("CELESTIAL LOBBY", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldBright)
                Spacer(Modifier.height(4.dp))
                Text("ENTRANCE TO THE LOBBY", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Text("Enter your access credentials to continue.", style = MaterialTheme.typography.bodyMedium, color = CelestialColors.TextSecondary)
                Spacer(Modifier.height(16.dp))
                CelestialTextField(
                    value = state.accessIdInput,
                    onValueChange = viewModel::updateAccessId,
                    label = "ACCESS ID",
                    placeholder = "Enter your Access ID",
                    testTag = "regular_gate_id"
                )
                Spacer(Modifier.height(12.dp))
                CelestialTextField(
                    value = state.apgInput,
                    onValueChange = viewModel::updateApgInput,
                    label = "ACCESS CODE",
                    placeholder = "Enter your Access Code",
                    visualTransformation = PasswordVisualTransformation(),
                    testTag = "regular_gate_code"
                )
                Spacer(Modifier.height(16.dp))
                GoldActionButton("ENTER", onClick = viewModel::verifyAccess, testTag = "regular_gate_enter")
                state.gateMessage?.let { NoticeBanner(it, state.gateMessageSuccess) }
                HorizontalDivider(color = CelestialColors.BorderGlass, modifier = Modifier.padding(vertical = 16.dp))
                Text("New to CELESTIAL LOBBY?", fontSize = 12.sp, color = CelestialColors.TextSecondary)
                Spacer(Modifier.height(8.dp))
                SecondaryGlassButton("GUEST ENTRANCE", onClick = { viewModel.navigate(PortalRoute.GUEST) })
            }
        }
    }
}

@Composable
fun EnvironmentGateScreen(
    state: CelestialUiState,
    viewModel: CelestialViewModel
) {
    BackHandler { viewModel.navigate(PortalRoute.LANDING) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 520.dp)) {
            CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
                Text("CELESTIAL LOBBY · ACCESS GATE", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldBright)
                Spacer(Modifier.height(4.dp))
                Text("ENVIRONMENT VERIFICATION", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Text("Enter your complete Access Code. Its registered prefix identifies the environment you are trying to reach.", style = MaterialTheme.typography.bodyMedium, color = CelestialColors.TextSecondary)
                Spacer(Modifier.height(16.dp))
                CelestialTextField(
                    value = state.environmentAccessCode,
                    onValueChange = viewModel::updateEnvironmentCode,
                    label = "ACCESS CODE",
                    placeholder = "Enter your Access Code",
                    testTag = "env_access_code_input"
                )
                Spacer(Modifier.height(14.dp))
                GoldActionButton("IDENTIFY ENVIRONMENT", onClick = viewModel::identifyEnvironment, testTag = "identify_env_button")
                NoticeBanner(state.environmentRouteMessage)
                Spacer(Modifier.height(12.dp))
                SecondaryGlassButton("JOIN IN / ACCESS GUEST", onClick = { viewModel.navigate(PortalRoute.GUEST) })
                Spacer(Modifier.height(8.dp))
                SecondaryGlassButton("BETA INVITATION VERIFICATION", onClick = { viewModel.navigate(PortalRoute.INVITATION_GATE) })
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { viewModel.navigate(PortalRoute.LANDING) }, modifier = Modifier.fillMaxWidth()) {
                    Text("RETURN TO ACCESS GATE", color = CelestialColors.VioletSoft, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun EnvironmentRouteResultScreen(
    state: CelestialUiState,
    viewModel: CelestialViewModel
) {
    BackHandler { viewModel.navigate(PortalRoute.ENVIRONMENT_GATE) }
    val env = state.routedEnvironment
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 520.dp)) {
            CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
                Text("DESTINATION IDENTIFIED", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldBright)
                Spacer(Modifier.height(4.dp))
                Text(env?.displayLabel ?: "UNKNOWN ENVIRONMENT", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Text("Your Access Code prefix points to this environment.", style = MaterialTheme.typography.bodyMedium, color = CelestialColors.TextSecondary)
                Spacer(Modifier.height(12.dp))
                NoticeBanner(state.environmentRouteMessage, isSuccess = true)
                Spacer(Modifier.height(14.dp))
                SecondaryGlassButton("BACK TO ENVIRONMENT GATE", onClick = { viewModel.navigate(PortalRoute.ENVIRONMENT_GATE) })
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { viewModel.navigate(PortalRoute.LANDING) }, modifier = Modifier.fillMaxWidth()) {
                    Text("RETURN TO LOBBY", color = CelestialColors.VioletSoft, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun InvitationGateScreen(
    state: CelestialUiState,
    viewModel: CelestialViewModel
) {
    BackHandler { viewModel.navigate(PortalRoute.LANDING) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 520.dp)) {
            CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
                Text("CELESTIAL LOBBY · BETA ACCESS", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldBright)
                Spacer(Modifier.height(4.dp))
                Text("INVITATION VERIFICATION", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Text("Enter the invitation code assigned to your beta account.", style = MaterialTheme.typography.bodyMedium, color = CelestialColors.TextSecondary)
                Spacer(Modifier.height(14.dp))
                CelestialTextField(
                    value = state.invitationInput,
                    onValueChange = viewModel::updateInvitationInput,
                    label = "INVITATION CODE",
                    placeholder = "CL-XXXXXXXX",
                    testTag = "invitation_code_input"
                )
                Spacer(Modifier.height(14.dp))
                GoldActionButton(
                    text = "SUBMIT",
                    enabled = state.invitationAttempts < 3,
                    onClick = viewModel::verifyInvitation,
                    testTag = "submit_invitation_button"
                )
                NoticeBanner(state.invitationMessage)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (state.invitationAttempts >= 3) {
                        "This device has been locked after three unsuccessful attempts."
                    } else {
                        "${3 - state.invitationAttempts} attempts available."
                    },
                    fontSize = 11.sp,
                    color = CelestialColors.VioletMuted
                )
                Spacer(Modifier.height(12.dp))
                SecondaryGlassButton("Join In / Guest Entrance", onClick = { viewModel.navigate(PortalRoute.GUEST) })
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { viewModel.navigate(PortalRoute.LANDING) }, modifier = Modifier.fillMaxWidth()) {
                    Text("RETURN TO LOBBY", color = CelestialColors.VioletSoft, fontSize = 11.sp)
                }
            }
        }
    }
}
