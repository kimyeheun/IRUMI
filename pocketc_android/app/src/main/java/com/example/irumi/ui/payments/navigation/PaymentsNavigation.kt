package com.example.irumi.ui.payments.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.irumi.core.navigation.PaymentDetail
import com.example.irumi.core.navigation.Payments
import com.example.irumi.ui.payments.PaymentDetailRoute
import com.example.irumi.ui.payments.PaymentRoute

fun NavController.navigateToPayments(navOptions: NavOptions? = null) {
    navigate(Payments, navOptions)
}

fun NavGraphBuilder.paymentsNavGraph(
    paddingValues: PaddingValues,
    onNavigateToDetail: (Int) -> Unit
) {
    composable<Payments> {
        PaymentRoute(
            paddingValues = paddingValues,
            onNavigateToDetail = onNavigateToDetail
        )
    }
}

fun NavController.navigateToPaymentDetail(
    paymentId: Int,
    navOptions: NavOptions? = null
) {
    navigate(PaymentDetail(paymentId), navOptions)
}

fun NavGraphBuilder.paymentDetailNavGraph(
    paddingValues: PaddingValues,
    navigateUp: () -> Unit
) {
    composable<PaymentDetail> {
        PaymentDetailRoute(
            paddingValues = paddingValues,
            navigateUp = navigateUp
        )
    }
}
