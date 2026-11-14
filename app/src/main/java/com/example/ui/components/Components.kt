package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PortalRoute
import com.example.ui.theme.CelestialColors

@Composable
fun CelestialBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CelestialColors.DeepSpace)
            .drawBehind {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            CelestialColors.MidnightBg,
                            CelestialColors.DeepSpace,
                            Color(0xFF080A14)
                        )
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x366E44FF), Color.Transparent),
                        center = Offset(size.width * 0.5f, size.height * 0.08f),
                        radius = size.width * 0.85f
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x20C770FF), Color.Transparent),
                        center = Offset(size.width * 0.85f, size.height * 0.72f),
                        radius = size.width * 0.65f
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x262962FF), Color.Transparent),
                        center = Offset(size.width * 0.15f, size.height * 0.82f),
                        radius = size.width * 0.7f
                    )
                )
            }
    ) {
        content()
    }
}

@Composable
fun CelestialGlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = CelestialColors.BorderGlass,
    backgroundColor: Color = CelestialColors.GlassSurface,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}

@Composable
fun GoldActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = "gold_action_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 52.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CelestialColors.GoldLight,
            contentColor = CelestialColors.MidnightBg,
            disabledContainerColor = CelestialColors.GoldDeep.copy(alpha = 0.3f),
            disabledContentColor = CelestialColors.MidnightBg.copy(alpha = 0.6f)
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SecondaryGlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = "secondary_action_button"
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CelestialColors.BorderGold),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = CelestialColors.GlassSurface,
            contentColor = CelestialColors.GoldBright
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CelestialTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    minLines: Int = 1,
    testTag: String = "celestial_text_field"
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = CelestialColors.GoldBright,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(placeholder, color = CelestialColors.VioletMuted, fontSize = 13.sp)
                }
            },
            visualTransformation = visualTransformation,
            singleLine = singleLine,
            minLines = minLines,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CelestialColors.DeepSpace.copy(alpha = 0.85f),
                unfocusedContainerColor = CelestialColors.DeepSpace.copy(alpha = 0.7f),
                focusedBorderColor = CelestialColors.GoldLight,
                unfocusedBorderColor = CelestialColors.BorderGlass,
                focusedTextColor = CelestialColors.TextPrimary,
                unfocusedTextColor = CelestialColors.TextPrimary,
                cursorColor = CelestialColors.GoldLight
            )
        )
    }
}

@Composable
fun PortalTopBar(
    onBrandClick: () -> Unit,
    menuOpen: Boolean,
    onToggleMenu: () -> Unit,
    purchaseWindowRemainingMs: Long? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onBrandClick)
                    .defaultMinSize(minHeight = 48.dp)
                    .padding(horizontal = 6.dp)
                    .testTag("portal_brand_home")
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(CelestialColors.VioletPrimary.copy(alpha = 0.22f))
                        .border(1.dp, CelestialColors.GoldDeep, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✦", color = CelestialColors.GoldBright, fontSize = 16.sp)
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = "CELESTIAL LOBBY",
                        color = CelestialColors.GoldBright,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 2.4.sp
                    )
                    Text(
                        text = "PRIVATE CLIENT PORTAL",
                        color = CelestialColors.VioletMuted,
                        fontSize = 9.sp,
                        letterSpacing = 1.6.sp
                    )
                }
            }

            IconButton(
                onClick = onToggleMenu,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("lobby_menu_trigger")
            ) {
                Icon(
                    imageVector = if (menuOpen) Icons.Default.Close else Icons.Default.Menu,
                    contentDescription = if (menuOpen) "Close navigation menu" else "Open navigation menu",
                    tint = CelestialColors.GoldBright
                )
            }
        }
        if (purchaseWindowRemainingMs != null && purchaseWindowRemainingMs > 0L) {
            val totalSeconds = (purchaseWindowRemainingMs / 1000L).toInt()
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                color = CelestialColors.AmberBg,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, CelestialColors.BorderGold)
            ) {
                Text(
                    text = "PURCHASE WINDOW ACTIVE · %02d:%02d REMAINING".format(minutes, seconds),
                    color = CelestialColors.GoldLight,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp)
                )
            }
        }
        HorizontalDivider(color = CelestialColors.BorderGold.copy(alpha = 0.35f))
    }
}

@Composable
fun PortalTopline(
    backLabel: String?,
    onBack: (() -> Unit)?,
    sectionBadge: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (backLabel != null && onBack != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onBack)
                    .defaultMinSize(minHeight = 48.dp)
                    .padding(end = 8.dp)
                    .testTag("topline_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = backLabel,
                    tint = CelestialColors.GoldBright,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = backLabel,
                    color = CelestialColors.GoldBright,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            Spacer(Modifier.width(1.dp))
        }
        Text(
            text = sectionBadge,
            style = MaterialTheme.typography.labelSmall,
            color = CelestialColors.GoldDeep
        )
    }
}

@Composable
fun PortalFooter(
    onOpenRoute: (PortalRoute) -> Unit,
    onHomeClick: () -> Unit,
    onStoreClick: () -> Unit,
    onTrackerClick: () -> Unit,
    showNavigationRow: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(color = CelestialColors.BorderGlass, modifier = Modifier.padding(bottom = 12.dp))
        if (showNavigationRow) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                FooterTextLink("HOME", onHomeClick)
                Text("·", color = CelestialColors.VioletMuted, fontSize = 11.sp)
                FooterTextLink("ABOUT") { onOpenRoute(PortalRoute.ABOUT) }
                Text("·", color = CelestialColors.VioletMuted, fontSize = 11.sp)
                FooterTextLink("FAQS") { onOpenRoute(PortalRoute.FAQS) }
                Text("·", color = CelestialColors.VioletMuted, fontSize = 11.sp)
                FooterTextLink("STORE", onStoreClick)
                Text("·", color = CelestialColors.VioletMuted, fontSize = 11.sp)
                FooterTextLink("TRACKER", onTrackerClick)
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FooterTextLink("TERMS") { onOpenRoute(PortalRoute.TERMS) }
            Text("·", color = CelestialColors.VioletMuted, fontSize = 11.sp)
            FooterTextLink("PRIVACY") { onOpenRoute(PortalRoute.PRIVACY) }
            Text("·", color = CelestialColors.VioletMuted, fontSize = 11.sp)
            FooterTextLink("INFO SEC") { onOpenRoute(PortalRoute.INFO_SEC) }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "DEMO ONLY — BROWSER/DEVICE LOCAL PROTOTYPE. NOT SECURE FOR REAL CUSTOMER DATA.",
            fontSize = 9.sp,
            color = CelestialColors.VioletMuted.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FooterTextLink(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        color = CelestialColors.GoldBright.copy(alpha = 0.85f),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp)
    )
}

@Composable
fun NoticeBanner(text: String, isSuccess: Boolean = false) {
    if (text.isBlank()) return
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(10.dp),
        color = if (isSuccess) CelestialColors.EmeraldBg else CelestialColors.AmberBg,
        border = BorderStroke(
            1.dp,
            if (isSuccess) CelestialColors.Emerald.copy(alpha = 0.5f) else CelestialColors.BorderGold
        )
    ) {
        Text(
            text = text,
            color = if (isSuccess) Color(0xFFA7F3D0) else CelestialColors.GoldLight,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            modifier = Modifier.padding(12.dp)
        )
    }
}
