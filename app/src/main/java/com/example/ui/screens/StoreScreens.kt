package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CatalogRepository
import com.example.model.OrderStatus
import com.example.model.PortalRoute
import com.example.ui.components.CelestialGlassCard
import com.example.ui.components.CelestialTextField
import com.example.ui.components.GoldActionButton
import com.example.ui.components.NoticeBanner
import com.example.ui.components.SecondaryGlassButton
import com.example.ui.theme.CelestialColors
import com.example.viewmodel.CelestialUiState
import com.example.viewmodel.CelestialViewModel
import java.text.NumberFormat
import java.util.Locale

private val stepLabels = listOf("Home", "Lineup", "Cart", "Extras", "Service", "Checkout", "Payment", "Status")

private fun formatPhp(amount: Int): String =
    "₱" + NumberFormat.getNumberInstance(Locale.US).format(amount)

@Composable
fun StoreFlowScreen(
    state: CelestialUiState,
    viewModel: CelestialViewModel
) {
    val step = state.storeState.currentStep.coerceIn(1, 8)
    BackHandler {
        if (step > 1) viewModel.navigateStoreStep(step - 1)
        else viewModel.navigate(PortalRoute.LANDING)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 640.dp)) {
            // Store Workflow Progress Bar
            CelestialGlassCard(borderColor = CelestialColors.BorderGlass) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("STORE JOURNEY", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldDeep)
                        Text(
                            "Step $step of 8 · ${stepLabels[step - 1]}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        stepLabels.forEachIndexed { idx, _ ->
                            Box(
                                modifier = Modifier
                                    .size(if (idx + 1 == step) 10.dp else 7.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            idx + 1 == step -> CelestialColors.GoldLight
                                            idx + 1 < step -> CelestialColors.Emerald
                                            else -> CelestialColors.VioletMuted.copy(alpha = 0.3f)
                                        }
                                    )
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SecondaryGlassButton(
                        text = "Reset selections",
                        onClick = viewModel::resetStoreSelections,
                        modifier = Modifier.weight(1f),
                        testTag = "store_reset_button"
                    )
                    SecondaryGlassButton(
                        text = "Cart · ${state.totalItemsCount}",
                        onClick = { viewModel.navigateStoreStep(3) },
                        modifier = Modifier.weight(1f),
                        testTag = "store_cart_shortcut"
                    )
                }
            }

            NoticeBanner(state.flowNotice)
            Spacer(Modifier.height(14.dp))

            when (step) {
                1 -> StoreStepHome(viewModel)
                2 -> StoreStepLineup(state, viewModel)
                3 -> StoreStepCart(state, viewModel)
                4 -> StoreStepExtras(state, viewModel)
                5 -> StoreStepService(state, viewModel)
                6 -> StoreStepCheckout(state, viewModel)
                7 -> StoreStepPaymentPreview(state, viewModel)
                8 -> StoreStepStatus(state, viewModel)
            }
        }
    }
}

@Composable
private fun StoreStepHome(viewModel: CelestialViewModel) {
    CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
        Image(
            painter = painterResource(id = R.drawable.celestial_hero),
            contentDescription = "Celestial cosmic nebula banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(Modifier.height(14.dp))
        Text(
            "CELESTIAL LOBBY",
            style = MaterialTheme.typography.headlineMedium,
            color = CelestialColors.GoldLight,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "ENTER THE ORBIT. CHOOSE YOUR FANTASY...",
            style = MaterialTheme.typography.labelSmall,
            color = CelestialColors.VioletSoft,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(14.dp))
        GoldActionButton(
            text = "Explore STARDUST ITEMS",
            onClick = { viewModel.navigateStoreStep(2) },
            testTag = "explore_stardust_button"
        )
    }

    Spacer(Modifier.height(12.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        CelestialGlassCard(
            modifier = Modifier
                .weight(1f)
                .clickable { viewModel.navigateStoreStep(2) }
        ) {
            Text("PRIMARY REALMS", style = MaterialTheme.typography.labelSmall, color = CelestialColors.VioletSoft)
            Spacer(Modifier.height(4.dp))
            Text("STARDUST", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CelestialColors.GoldBright)
            Text("Available ITEMS", fontSize = 12.sp, color = CelestialColors.TextSecondary)
            Spacer(Modifier.height(10.dp))
            Text("Browse →", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CelestialColors.GoldLight)
        }
        CelestialGlassCard(
            modifier = Modifier
                .weight(1f)
                .clickable { viewModel.navigateStoreStep(4) }
        ) {
            Text("ENHANCEMENTS", style = MaterialTheme.typography.labelSmall, color = CelestialColors.VioletSoft)
            Spacer(Modifier.height(4.dp))
            Text("EXTRAS & ADD-ONS", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CelestialColors.GoldBright)
            Text("Optional enhancements", fontSize = 12.sp, color = CelestialColors.TextSecondary)
            Spacer(Modifier.height(10.dp))
            Text("View Add-ons →", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CelestialColors.GoldLight)
        }
    }

    Spacer(Modifier.height(12.dp))
    CelestialGlassCard(modifier = Modifier.clickable { viewModel.navigateStoreStep(5) }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("CELESTIAL SERVICES", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                Text("Standard processing under the No Rush Orders policy", fontSize = 12.sp, color = CelestialColors.VioletSoft)
            }
            Text("Configure ›", color = CelestialColors.GoldLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
    Spacer(Modifier.height(12.dp))
    Text(
        "FANTASY • EUPHORIA • PLEASURE",
        fontSize = 10.sp,
        letterSpacing = 2.sp,
        color = CelestialColors.VioletMuted,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun StoreStepLineup(state: CelestialUiState, viewModel: CelestialViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("STARDUST", style = MaterialTheme.typography.headlineMedium, color = CelestialColors.GoldBright)
            Text("OUR SERVER LINEUP — Choose your realm.", fontSize = 12.sp, color = CelestialColors.VioletSoft)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("Current Cart", fontSize = 11.sp, color = CelestialColors.VioletSoft)
            Text(formatPhp(state.stardustSubtotal), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CelestialColors.GoldLight)
        }
    }
    Spacer(Modifier.height(12.dp))

    CatalogRepository.stardustLineup.chunked(2).forEach { pair ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            pair.forEach { item ->
                val qty = state.storeState.cart[item.id] ?: 0
                CelestialGlassCard(modifier = Modifier.weight(1f)) {
                    Text(item.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    Text(item.units, fontSize = 11.sp, color = CelestialColors.VioletSoft)
                    Spacer(Modifier.height(4.dp))
                    Text(formatPhp(item.price), fontWeight = FontWeight.Bold, color = CelestialColors.GoldLight, fontSize = 15.sp)
                    Spacer(Modifier.height(10.dp))
                    if (qty > 0) {
                        QuantityStepper(
                            qty = qty,
                            onDecrement = { viewModel.updateCartQuantity(item.id, -1) },
                            onIncrement = { viewModel.updateCartQuantity(item.id, 1) },
                            testTagPrefix = "cart_${item.id}"
                        )
                    } else {
                        SecondaryGlassButton(
                            text = "+ Add",
                            onClick = { viewModel.updateCartQuantity(item.id, 1) },
                            testTag = "add_${item.id}"
                        )
                    }
                }
            }
            if (pair.size == 1) Spacer(Modifier.weight(1f))
        }
    }

    Spacer(Modifier.height(10.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SecondaryGlassButton("Back", onClick = { viewModel.navigateStoreStep(1) }, modifier = Modifier.weight(1f))
        GoldActionButton(
            text = "Review (${state.totalItemsCount}) →",
            onClick = { viewModel.navigateStoreStep(3) },
            modifier = Modifier.weight(1.4f),
            testTag = "review_selection_button"
        )
    }
}

@Composable
private fun StoreStepCart(state: CelestialUiState, viewModel: CelestialViewModel) {
    Text("YOUR SELECTION", style = MaterialTheme.typography.headlineMedium, color = CelestialColors.GoldBright)
    Text("Adjust quantities for your selected ITEMS.", fontSize = 12.sp, color = CelestialColors.VioletSoft)
    Spacer(Modifier.height(12.dp))

    val selected = CatalogRepository.stardustLineup.filter { (state.storeState.cart[it.id] ?: 0) > 0 }
    if (selected.isEmpty()) {
        CelestialGlassCard {
            Text("Your realm selection is empty.", fontSize = 13.sp, color = CelestialColors.TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            GoldActionButton("Explore Lineup", onClick = { viewModel.navigateStoreStep(2) })
        }
    } else {
        selected.forEach { item ->
            val qty = state.storeState.cart[item.id] ?: 0
            CelestialGlassCard(modifier = Modifier.padding(bottom = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text(item.units, fontSize = 11.sp, color = CelestialColors.VioletSoft)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        QuantityStepper(
                            qty = qty,
                            onDecrement = { viewModel.updateCartQuantity(item.id, -1) },
                            onIncrement = { viewModel.updateCartQuantity(item.id, 1) },
                            testTagPrefix = "review_${item.id}"
                        )
                        Text(formatPhp(item.price * qty), fontWeight = FontWeight.Bold, color = CelestialColors.GoldBright, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    Spacer(Modifier.height(10.dp))
    CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Subtotal (ITEMS)", fontWeight = FontWeight.SemiBold, color = CelestialColors.TextSecondary)
            Text(formatPhp(state.stardustSubtotal), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = CelestialColors.GoldLight)
        }
    }
    Spacer(Modifier.height(12.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SecondaryGlassButton("Add More", onClick = { viewModel.navigateStoreStep(2) }, modifier = Modifier.weight(1f))
        GoldActionButton("Next: Add-Ons →", onClick = { viewModel.navigateStoreStep(4) }, modifier = Modifier.weight(1.4f), testTag = "next_addons_button")
    }
}

@Composable
private fun StoreStepExtras(state: CelestialUiState, viewModel: CelestialViewModel) {
    Text("ADD-ONS", style = MaterialTheme.typography.headlineMedium, color = CelestialColors.GoldBright)
    Text("Choose optional extras and add-ons for your order.", fontSize = 12.sp, color = CelestialColors.VioletSoft)
    Spacer(Modifier.height(12.dp))

    CatalogRepository.addons.forEach { addon ->
        val qty = state.storeState.addons[addon.id] ?: 0
        CelestialGlassCard(modifier = Modifier.padding(bottom = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(addon.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    Text(formatPhp(addon.price), fontWeight = FontWeight.Bold, color = CelestialColors.GoldLight, fontSize = 13.sp)
                }
                QuantityStepper(
                    qty = qty,
                    onDecrement = { viewModel.updateAddonQuantity(addon.id, -1) },
                    onIncrement = { viewModel.updateAddonQuantity(addon.id, 1) },
                    testTagPrefix = "addon_${addon.id}"
                )
            }
        }
    }

    CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Subtotal (Items + Add-ons)", fontSize = 12.sp, color = CelestialColors.VioletSoft)
                Text("Before handling & eligible benefits", fontSize = 10.sp, color = CelestialColors.VioletMuted)
            }
            Text(formatPhp(state.itemsTotal), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = CelestialColors.GoldLight)
        }
    }
    Spacer(Modifier.height(12.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SecondaryGlassButton("Back", onClick = { viewModel.navigateStoreStep(3) }, modifier = Modifier.weight(1f))
        GoldActionButton("Next: Handling →", onClick = { viewModel.navigateStoreStep(5) }, modifier = Modifier.weight(1.4f), testTag = "next_handling_button")
    }
}

@Composable
private fun StoreStepService(state: CelestialUiState, viewModel: CelestialViewModel) {
    Text("SPECIAL HANDLING & PROMOTIONS", style = MaterialTheme.typography.headlineSmall, color = CelestialColors.GoldBright)
    Text("Choose your preferred service level and review benefits.", fontSize = 12.sp, color = CelestialColors.VioletSoft)
    Spacer(Modifier.height(12.dp))

    CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Standard Processing", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                Text(
                    "Orders follow the regular verification and preparation queue. Rush handling is not available under the No Rush Orders policy.",
                    fontSize = 12.sp,
                    color = CelestialColors.TextSecondary
                )
            }
            Text("Included ✓", fontWeight = FontWeight.Bold, color = CelestialColors.GoldLight, fontSize = 13.sp)
        }
    }
    Spacer(Modifier.height(10.dp))
    CelestialGlassCard {
        Text("ACCOUNT BENEFITS", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldBright)
        Spacer(Modifier.height(4.dp))
        Text(
            "Special checkout benefits must be attached to an eligible client account and approved under the applicable rules. Public discount codes are not accepted in this demo.",
            fontSize = 12.sp,
            color = CelestialColors.TextSecondary
        )
    }
    Spacer(Modifier.height(10.dp))
    CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Updated Subtotal", fontSize = 13.sp, color = CelestialColors.TextSecondary)
            Text(formatPhp(state.grandTotal), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = CelestialColors.GoldLight)
        }
    }
    Spacer(Modifier.height(12.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SecondaryGlassButton("Back", onClick = { viewModel.navigateStoreStep(4) }, modifier = Modifier.weight(1f))
        GoldActionButton("Next: Checkout →", onClick = { viewModel.navigateStoreStep(6) }, modifier = Modifier.weight(1.4f), testTag = "next_checkout_button")
    }
}

@Composable
private fun StoreStepCheckout(state: CelestialUiState, viewModel: CelestialViewModel) {
    Text("CHECKOUT", style = MaterialTheme.typography.headlineMedium, color = CelestialColors.GoldBright)
    Text("Confirm delivery details & delivery method.", fontSize = 12.sp, color = CelestialColors.VioletSoft)
    Spacer(Modifier.height(12.dp))

    CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
        Text("ORDER SUMMARY", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldLight)
        HorizontalDivider(color = CelestialColors.BorderGlass, modifier = Modifier.padding(vertical = 8.dp))
        SummaryRow("Items (${state.totalItemsCount})", formatPhp(state.itemsTotal))
        SummaryRow("Standard Processing", "₱0")
        HorizontalDivider(color = CelestialColors.BorderGlass, modifier = Modifier.padding(vertical = 8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total", fontWeight = FontWeight.ExtraBold, color = CelestialColors.GoldLight, fontSize = 15.sp)
            Text(formatPhp(state.grandTotal), fontWeight = FontWeight.ExtraBold, color = CelestialColors.GoldLight, fontSize = 16.sp)
        }
    }
    Spacer(Modifier.height(12.dp))

    CelestialGlassCard {
        Text("DELIVERY INFORMATION", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldLight)
        Spacer(Modifier.height(10.dp))
        CelestialTextField(
            value = state.storeState.deliveryDetails.name,
            onValueChange = { viewModel.updateDeliveryDetails(name = it) },
            label = "FULL NAME",
            placeholder = "e.g. Juan Dela Cruz",
            testTag = "checkout_name_input"
        )
        Spacer(Modifier.height(10.dp))
        CelestialTextField(
            value = state.storeState.deliveryDetails.phone,
            onValueChange = { viewModel.updateDeliveryDetails(phone = it) },
            label = "CONTACT NUMBER",
            placeholder = "e.g. 0917 123 4567",
            testTag = "checkout_phone_input"
        )
        Spacer(Modifier.height(10.dp))
        CelestialTextField(
            value = state.storeState.deliveryDetails.address,
            onValueChange = { viewModel.updateDeliveryDetails(address = it) },
            label = "DELIVERY ADDRESS / DESTINATION",
            placeholder = "e.g. Unit 4B Orbit Tower, Neo District",
            testTag = "checkout_address_input"
        )
    }
    Spacer(Modifier.height(10.dp))
    CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
        Text("FULFILLMENT METHOD", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldLight)
        Spacer(Modifier.height(6.dp))
        Text("Standard Rider Delivery", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
        Text("Designated delivery arrangement. No meetups or rush fulfillment.", fontSize = 11.sp, color = CelestialColors.TextSecondary)
    }
    Spacer(Modifier.height(14.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SecondaryGlassButton("Back", onClick = { viewModel.navigateStoreStep(5) }, modifier = Modifier.weight(1f))
        GoldActionButton("Place Order →", onClick = viewModel::placeStoreOrderPreview, modifier = Modifier.weight(1.4f), testTag = "place_order_button")
    }
}

@Composable
private fun StoreStepPaymentPreview(state: CelestialUiState, viewModel: CelestialViewModel) {
    CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(CelestialColors.SurfaceDark)
                .border(1.dp, CelestialColors.BorderGold, CircleShape)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Text("DEMO", color = CelestialColors.GoldLight, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "Checkout Preview · Not Submitted",
            fontWeight = FontWeight.Bold,
            color = CelestialColors.GoldLight,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "No order number has been generated. This is a visual preview only.",
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = CelestialColors.TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
    Spacer(Modifier.height(12.dp))
    CelestialGlassCard {
        Text("PAYMENT PREVIEW", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldBright)
        Spacer(Modifier.height(6.dp))
        Text("Estimated cart total: ${formatPhp(state.grandTotal)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(Modifier.height(6.dp))
        Text(
            "DEMO ONLY — PAYMENT PROOF COLLECTION IS DISABLED. Do not enter sender details or upload receipts.",
            fontSize = 12.sp,
            color = CelestialColors.GoldLight
        )
    }
    Spacer(Modifier.height(12.dp))
    GoldActionButton(
        text = "Payment Submission Disabled",
        onClick = viewModel::submitPaymentDisabledNotice,
        testTag = "payment_disabled_button"
    )
    Spacer(Modifier.height(8.dp))
    SecondaryGlassButton("View Order Status Preview", onClick = { viewModel.navigateStoreStep(8) })
}

@Composable
private fun StoreStepStatus(state: CelestialUiState, viewModel: CelestialViewModel) {
    val order = state.storeState.activeOrder
    CelestialGlassCard(borderColor = CelestialColors.BorderGold) {
        Text(
            if (order.status == OrderStatus.PAYMENT_EXPIRED) "Payment Window Expired" else "Order Received",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Order ${order.orderId.ifBlank { "Demo order" }}",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = CelestialColors.VioletSoft,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "DEMO ONLY — NO REAL ORDER WAS CREATED. This screen is a preview state and does not represent a submitted order or payment.",
            fontSize = 12.sp,
            color = CelestialColors.GoldLight,
            textAlign = TextAlign.Center
        )
    }
    Spacer(Modifier.height(12.dp))
    CelestialGlassCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("ORDER STATUS", style = MaterialTheme.typography.labelSmall, color = CelestialColors.GoldLight)
            Text(
                when (order.status) {
                    OrderStatus.PAYMENT_SUBMITTED -> "UNDER REVIEW"
                    OrderStatus.PAYMENT_EXPIRED -> "EXPIRED"
                    else -> "PAYMENT PENDING"
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CelestialColors.GoldBright
            )
        }
        HorizontalDivider(color = CelestialColors.BorderGlass, modifier = Modifier.padding(vertical = 8.dp))
        Text("Order Submitted: ${order.submittedTime ?: "Recorded in this device session"}", fontSize = 12.sp, color = CelestialColors.TextSecondary)
        Spacer(Modifier.height(4.dp))
        Text("Order Total: ${formatPhp(order.totalPaid ?: state.grandTotal)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CelestialColors.GoldLight)
    }
    Spacer(Modifier.height(12.dp))
    TextButton(onClick = { viewModel.navigateStoreStep(1) }, modifier = Modifier.fillMaxWidth()) {
        Text("Return to Store Homepage", color = CelestialColors.VioletSoft, fontSize = 12.sp)
    }
}

@Composable
private fun QuantityStepper(
    qty: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    testTagPrefix: String
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = CelestialColors.DeepSpace,
        border = BorderStroke(1.dp, CelestialColors.BorderGlass)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 36.dp, minHeight = 36.dp)
                    .clickable(onClick = onDecrement)
                    .testTag("${testTagPrefix}_minus"),
                contentAlignment = Alignment.Center
            ) {
                Text("−", fontWeight = FontWeight.Bold, color = CelestialColors.VioletSoft)
            }
            Text("$qty", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp))
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 36.dp, minHeight = 36.dp)
                    .clickable(onClick = onIncrement)
                    .testTag("${testTagPrefix}_plus"),
                contentAlignment = Alignment.Center
            ) {
                Text("+", fontWeight = FontWeight.Bold, color = CelestialColors.GoldLight)
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = CelestialColors.TextSecondary)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}
