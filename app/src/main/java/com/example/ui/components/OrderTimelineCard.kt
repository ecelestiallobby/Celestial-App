package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActiveOrder
import com.example.model.DeliveryDetails
import com.example.model.OrderStatus
import com.example.ui.theme.CelestialColors

private data class TimelineStageUi(
    val key: OrderStatus,
    val label: String,
    val description: String,
    val subtext: String,
    val defaultTime: String,
    val icon: ImageVector
)

@Composable
fun OrderTimelineCard(
    order: ActiveOrder,
    deliveryDetails: DeliveryDetails,
    onUpdateStatus: ((OrderStatus) -> Unit)? = null,
    onEnterStore: (() -> Unit)? = null
) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    val currentIndex = when (order.status) {
        OrderStatus.SUBMITTED, OrderStatus.PAYMENT_SUBMITTED, OrderStatus.CONFIRMED -> 0
        OrderStatus.PROCESSING -> 1
        OrderStatus.IN_TRANSIT, OrderStatus.OUT_FOR_DELIVERY -> 2
        OrderStatus.DELIVERED -> 3
        else -> 0
    }

    val stages = listOf(
        TimelineStageUi(
            OrderStatus.CONFIRMED,
            "Confirmed",
            "Order registered & payment verified in realm ledger",
            "Credentials authenticated",
            order.paymentTime ?: order.submittedTime ?: "Nov 5, 2026 • 04:32 AM",
            Icons.Default.Check
        ),
        TimelineStageUi(
            OrderStatus.PROCESSING,
            "Processing",
            "Stardust server allocations assigned & packaged",
            "Allocating realm resources",
            "Est. +15 mins from confirmation",
            Icons.Default.Inventory2
        ),
        TimelineStageUi(
            OrderStatus.IN_TRANSIT,
            "In Transit",
            "Dispatched through secure celestial delivery route",
            "Courier en route",
            "Est. 30-45 mins arrival window",
            Icons.Default.LocalShipping
        ),
        TimelineStageUi(
            OrderStatus.DELIVERED,
            "Delivered",
            "Package received & transfer sealed safely at destination",
            "Transmission complete",
            "Completed at destination",
            Icons.Default.Verified
        )
    )

    val (badgeLabel, badgeColor) = when (currentIndex) {
        3 -> "DELIVERED" to CelestialColors.Emerald
        2 -> "IN TRANSIT" to Color(0xFF60A5FA)
        1 -> "PROCESSING" to CelestialColors.VioletSoft
        else -> "CONFIRMED" to CelestialColors.GoldLight
    }

    CelestialGlassCard(borderColor = CelestialColors.BorderGlass) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "ACTIVE ORDER TRACKING",
                        style = MaterialTheme.typography.labelSmall,
                        color = CelestialColors.VioletSoft
                    )
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = badgeColor.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = badgeLabel,
                            color = badgeColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = order.orderId.ifBlank { "DEMO ORDER" },
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    if (order.orderId.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CelestialColors.SurfaceDark,
                            border = BorderStroke(1.dp, CelestialColors.BorderGlass),
                            modifier = Modifier
                                .clickable {
                                    clipboard.setText(AnnotatedString(order.orderId))
                                    copied = true
                                }
                                .testTag("copy_order_id_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Order ID",
                                    tint = if (copied) CelestialColors.Emerald else CelestialColors.VioletSoft,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = if (copied) "Copied" else "Copy",
                                    fontSize = 10.sp,
                                    color = if (copied) CelestialColors.Emerald else CelestialColors.VioletSoft
                                )
                            }
                        }
                    }
                }
            }
            if (onEnterStore != null) {
                Text(
                    text = "Open Store ↗",
                    color = CelestialColors.GoldLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable(onClick = onEnterStore)
                        .padding(8.dp)
                )
            }
        }

        HorizontalDivider(color = CelestialColors.BorderGlass, modifier = Modifier.padding(vertical = 12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("FULFILLMENT JOURNEY", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldLight)
            Text("Stage ${currentIndex + 1} of 4", fontSize = 11.sp, color = CelestialColors.VioletSoft)
        }
        Spacer(Modifier.height(12.dp))

        stages.forEachIndexed { idx, step ->
            val isPast = idx < currentIndex
            val isCurrent = idx == currentIndex
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isPast -> CelestialColors.Emerald
                                isCurrent -> CelestialColors.GoldLight
                                else -> CelestialColors.SurfaceDark
                            }
                        )
                        .border(
                            1.dp,
                            when {
                                isPast -> CelestialColors.Emerald
                                isCurrent -> CelestialColors.GoldLight
                                else -> CelestialColors.BorderGlass
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPast) Icons.Default.Check else step.icon,
                        contentDescription = step.label,
                        tint = if (isPast || isCurrent) CelestialColors.MidnightBg else CelestialColors.VioletMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = step.label.uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isCurrent -> CelestialColors.GoldLight
                                isPast -> Color.White
                                else -> CelestialColors.VioletMuted
                            }
                        )
                        if (isCurrent) {
                            Text(
                                text = "ACTIVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CelestialColors.GoldLight
                            )
                        }
                    }
                    Text(step.description, fontSize = 11.sp, color = CelestialColors.TextSecondary)
                    Text(
                        step.defaultTime,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CelestialColors.VioletMuted
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DetailChip(
                label = "RECIPIENT",
                primary = deliveryDetails.name.ifBlank { "Authorized Client" },
                secondary = deliveryDetails.phone.ifBlank { "Encrypted Channel" },
                modifier = Modifier.weight(1f)
            )
            DetailChip(
                label = "METHOD",
                primary = deliveryDetails.address.ifBlank { "Designated Coordinates" },
                secondary = deliveryDetails.method,
                modifier = Modifier.weight(1f)
            )
        }

        if (onUpdateStatus != null) {
            HorizontalDivider(color = CelestialColors.BorderGlass, modifier = Modifier.padding(vertical = 12.dp))
            Text(
                text = "SIMULATE STATUS PROGRESSION (DEMO MODE)",
                style = MaterialTheme.typography.labelSmall,
                color = CelestialColors.VioletSoft
            )
            Spacer(Modifier.height(8.dp))
            val simOptions = listOf(
                OrderStatus.CONFIRMED to "1. Confirmed",
                OrderStatus.PROCESSING to "2. Processing",
                OrderStatus.IN_TRANSIT to "3. In Transit",
                OrderStatus.DELIVERED to "4. Delivered"
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                simOptions.forEach { (status, label) ->
                    val active = order.status == status ||
                        (status == OrderStatus.CONFIRMED && (order.status == OrderStatus.SUBMITTED || order.status == OrderStatus.PAYMENT_SUBMITTED)) ||
                        (status == OrderStatus.IN_TRANSIT && order.status == OrderStatus.OUT_FOR_DELIVERY)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (active) CelestialColors.GoldLight else CelestialColors.SurfaceDark,
                        border = BorderStroke(1.dp, if (active) CelestialColors.GoldLight else CelestialColors.BorderGlass),
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp)
                            .clickable { onUpdateStatus(status) }
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(4.dp)) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (active) CelestialColors.MidnightBg else CelestialColors.TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailChip(
    label: String,
    primary: String,
    secondary: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = CelestialColors.DeepSpace.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, CelestialColors.BorderGlass)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CelestialColors.VioletMuted)
            Spacer(Modifier.height(2.dp))
            Text(primary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White, maxLines = 1)
            Text(secondary, fontSize = 10.sp, color = CelestialColors.VioletSoft, maxLines = 1)
        }
    }
}
