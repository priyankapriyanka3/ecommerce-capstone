package com.booknest.app.presentation.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.booknest.app.presentation.components.BookCoverImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel,
    onNavigateBack: () -> Unit,
    onOrderPlaced: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Checkout",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (uiState.currentStep == CheckoutStep.SHIPPING_ADDRESS) {
                                onNavigateBack()
                            } else if (uiState.currentStep != CheckoutStep.ORDER_SUCCESS) {
                                viewModel.goToPreviousStep()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Step Progress Indicator
            CheckoutStepIndicator(
                currentStep = uiState.currentStep,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Step Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (uiState.currentStep) {
                    CheckoutStep.SHIPPING_ADDRESS -> ShippingAddressStep(
                        uiState = uiState,
                        onUpdateAddress = { name, street, city, state, zip, country, phone ->
                            viewModel.updateAddress(name, street, city, state, zip, country, phone)
                        },
                        onNext = { viewModel.goToNextStep() }
                    )

                    CheckoutStep.SHIPPING_METHOD -> ShippingMethodStep(
                        uiState = uiState,
                        onSelectMethod = { viewModel.selectShippingMethod(it) },
                        onNext = { viewModel.goToNextStep() }
                    )

                    CheckoutStep.PAYMENT_METHOD -> PaymentMethodStep(
                        uiState = uiState,
                        onSelectPaymentType = { viewModel.selectPaymentType(it) },
                        onUpdateCard = { num, holder, exp, cvv ->
                            viewModel.updateCardInfo(num, holder, exp, cvv)
                        },
                        onUpdateUpi = { viewModel.updateUpiId(it) },
                        onNext = { viewModel.goToNextStep() }
                    )

                    CheckoutStep.REVIEW_ORDER -> ReviewOrderStep(
                        uiState = uiState,
                        onPlaceOrder = {
                            viewModel.placeOrder { orderId ->
                                // Order placed, state will move to ORDER_SUCCESS
                            }
                        }
                    )

                    CheckoutStep.ORDER_SUCCESS -> OrderSuccessStep(
                        order = uiState.placedOrder,
                        onDone = {
                            uiState.placedOrder?.let { onOrderPlaced(it.id) } ?: onNavigateBack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CheckoutStepIndicator(
    currentStep: CheckoutStep,
    modifier: Modifier = Modifier
) {
    val steps = listOf("Address", "Delivery", "Payment", "Review")
    val currentIndex = when (currentStep) {
        CheckoutStep.SHIPPING_ADDRESS -> 0
        CheckoutStep.SHIPPING_METHOD -> 1
        CheckoutStep.PAYMENT_METHOD -> 2
        CheckoutStep.REVIEW_ORDER -> 3
        CheckoutStep.ORDER_SUCCESS -> 3
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, stepName ->
            val isCompleted = index < currentIndex
            val isCurrent = index == currentIndex

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> MaterialTheme.colorScheme.primary
                                isCurrent -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.outline
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stepName,
                    fontSize = 11.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrent) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun ShippingAddressStep(
    uiState: CheckoutUiState,
    onUpdateAddress: (String, String, String, String, String, String, String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Shipping Address",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        OutlinedTextField(
            value = uiState.fullName,
            onValueChange = { onUpdateAddress(it, uiState.streetAddress, uiState.city, uiState.state, uiState.zipCode, uiState.country, uiState.phone) },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.streetAddress,
            onValueChange = { onUpdateAddress(uiState.fullName, it, uiState.city, uiState.state, uiState.zipCode, uiState.country, uiState.phone) },
            label = { Text("Street Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = uiState.city,
                onValueChange = { onUpdateAddress(uiState.fullName, uiState.streetAddress, it, uiState.state, uiState.zipCode, uiState.country, uiState.phone) },
                label = { Text("City") },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = uiState.state,
                onValueChange = { onUpdateAddress(uiState.fullName, uiState.streetAddress, uiState.city, it, uiState.zipCode, uiState.country, uiState.phone) },
                label = { Text("State") },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = uiState.zipCode,
                onValueChange = { onUpdateAddress(uiState.fullName, uiState.streetAddress, uiState.city, uiState.state, it, uiState.country, uiState.phone) },
                label = { Text("Zip Code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = uiState.phone,
                onValueChange = { onUpdateAddress(uiState.fullName, uiState.streetAddress, uiState.city, uiState.state, uiState.zipCode, uiState.country, it) },
                label = { Text("Phone") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNext,
            enabled = uiState.fullName.isNotBlank() && uiState.streetAddress.isNotBlank() && uiState.city.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Continue to Delivery Method", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ShippingMethodStep(
    uiState: CheckoutUiState,
    onSelectMethod: (ShippingMethod) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Select Delivery Option",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        ShippingMethod.entries.forEach { method ->
            Card(
                onClick = { onSelectMethod(method) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.shippingMethod == method) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = uiState.shippingMethod == method,
                        onClick = { onSelectMethod(method) }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = method.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = method.duration,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Text(
                        text = if (method.price == 0.0) "FREE" else "$${String.format("%.2f", method.price)}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Continue to Payment", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PaymentMethodStep(
    uiState: CheckoutUiState,
    onSelectPaymentType: (PaymentType) -> Unit,
    onUpdateCard: (String, String, String, String) -> Unit,
    onUpdateUpi: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Payment Method",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        PaymentType.entries.forEach { pType ->
            Card(
                onClick = { onSelectPaymentType(pType) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.paymentType == pType) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = uiState.paymentType == pType,
                        onClick = { onSelectPaymentType(pType) }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = pType.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        if (uiState.paymentType == PaymentType.CREDIT_CARD) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.cardNumber,
                onValueChange = { onUpdateCard(it, uiState.cardHolder, uiState.expiryDate, uiState.cvv) },
                label = { Text("Card Number (16 Digits)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.cardHolder,
                onValueChange = { onUpdateCard(uiState.cardNumber, it, uiState.expiryDate, uiState.cvv) },
                label = { Text("Cardholder Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.expiryDate,
                    onValueChange = { onUpdateCard(uiState.cardNumber, uiState.cardHolder, it, uiState.cvv) },
                    label = { Text("MM/YY") },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = uiState.cvv,
                    onValueChange = { onUpdateCard(uiState.cardNumber, uiState.cardHolder, uiState.expiryDate, it) },
                    label = { Text("CVV") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
        } else if (uiState.paymentType == PaymentType.UPI) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.upiId,
                onValueChange = onUpdateUpi,
                label = { Text("UPI ID (e.g. name@okhdfcbank)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Review Order", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ReviewOrderStep(
    uiState: CheckoutUiState,
    onPlaceOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Review Order & Confirm",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Delivery Address Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Delivering To",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = uiState.fullName, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Text(text = "${uiState.streetAddress}, ${uiState.city}, ${uiState.state} ${uiState.zipCode}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Phone: ${uiState.phone}", style = MaterialTheme.typography.bodySmall)
            }
        }

        // Selected Delivery & Payment
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Shipping: ${uiState.shippingMethod.title} (${uiState.shippingMethod.duration})",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
                Text(
                    text = "Payment: ${uiState.paymentType.title}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
            }
        }

        // Cart items preview
        Text(
            text = "Items in Order (${uiState.cartItems.size})",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )

        uiState.cartItems.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BookCoverImage(
                    coverUrl = item.book.coverUrl,
                    title = item.book.title,
                    author = item.book.author,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.book.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1
                    )
                    Text(
                        text = "Qty: ${item.quantity} × $${String.format("%.2f", item.book.price)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Text(
                    text = "$${String.format("%.2f", item.book.price * item.quantity)}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // Total Summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Order Total",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "$${String.format("%.2f", uiState.total)}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onPlaceOrder,
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text("Place Order • $${String.format("%.2f", uiState.total)}", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun OrderSuccessStep(
    order: com.booknest.app.domain.model.Order?,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Thank You for Your Order!",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Order ID: ${order?.id ?: "BN-PROCESSED"}",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "We have received your order and will dispatch it right away. You can track its journey in your order history.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onDone,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("View Order History", fontWeight = FontWeight.Bold)
        }
    }
}
