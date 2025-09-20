package com.example.irumi.ui.payments.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.irumi.core.navigation.Route
import com.example.irumi.ui.payments.PaymentDetailRoute
import com.example.irumi.ui.payments.PaymentRoute
import kotlinx.serialization.Serializable

fun NavGraphBuilder.paymentsNavGraph(
    paddingValues: PaddingValues,
    navigateToPaymentDetail: () -> Unit
) {
    composable(route = "payments"){
        PaymentRoute(
            paddingValues = paddingValues,
            navigateToPaymentDetail = navigateToPaymentDetail
        )
    }
}

fun NavController.navigateToPaymentDetail(
    navOptions: NavOptions? = null
) {
    navigate("paymentDetail", navOptions)
}

fun NavGraphBuilder.paymentDetailNavGraph(
    paddingValues: PaddingValues
) {
    composable(route = "paymentDetail"){
        PaymentDetailRoute(
            paddingValues = paddingValues
        )
    }
}

@Serializable
data object PaymentDetail : Route