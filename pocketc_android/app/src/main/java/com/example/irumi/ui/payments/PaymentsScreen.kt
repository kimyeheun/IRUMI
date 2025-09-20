package com.example.irumi.ui.payments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.irumi.model.payments.Transaction

@Composable
fun PaymentRoute(
//    onBackButtonClick: () -> Unit,
    paddingValues: PaddingValues,
    navigateToPaymentDetail: () -> Unit,
//    viewModel: FollowViewModel = hiltViewModel()
) {
//    val followers by viewModel.followers.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

//    LaunchedEffect(viewModel.sideEffect, lifecycleOwner) {
//        viewModel.sideEffect.flowWithLifecycle(lifecycleOwner.lifecycle).collect { effect ->
//            when (effect) {
//                is FollowPageSideEffect.ShowSnackbar -> {
//                    showSnackBar(effect.message)
//                }
//                is FollowPageSideEffect.ShowError -> {
//                    showSnackBar(effect.errorType.description)
//                }
//            }
//        }
//    }

//    FollowScreen(
//        followers = followers,
//        following = following,
//        followType = followType,
//        paddingValues = paddingValues,
//        onUserClick = navigateToUserProfile,
//        onMyClick = navigateToMyPage,
//        onBackButtonClick = onBackButtonClick,
//        onRefresh = viewModel::refresh,
//        onFollowButtonClick = viewModel::toggleFollow
//    )
    PaymentsScreen(
        onPaymentItemClick = navigateToPaymentDetail
    )
}

@Composable
fun PaymentsScreen(viewModel: PaymentsViewModel = hiltViewModel(),
                   onPaymentItemClick: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current // screen이 속한 mainActivity의 생명주기

//    val state by viewModel.state.collectAsStateWithLifecycle(lifecycleOwner = lifecycleOwner) // 백그라운드 리소스 절약

    // LaunchedEffect -> 컴포저블이 처음 나타날 때 또는 key 값 변경 시 코루틴 시작
    // key1 = true -> 컴포저블이 처음 실행 될 때 단 한 번만 실행되도록 보장
//    LaunchedEffect(key1 = true) {
//        Log.d("getDummy", "호출")
//        viewModel.getDummy(2)
//    }
//
//    when (state) {
//        is UiState.Empty -> {}
//        is UiState.Loading -> {
//            Log.d("getDummy", "Loading")
//        }
//
//        is UiState.Failure -> {
//            Log.d("getDummy", "실패")
//        }
//
//        is UiState.Success -> {
//            Log.d("서버 테스트 ", (state as UiState.Success<DummyEntity>).data.userId.toString())
//        }
//    }


    // todo -> ViewModel에서 관리할 상태 (예시)
    val currentMonth = "2025년 9월"
    val groupedPayments by viewModel.groupedTransactions.collectAsState()
    val monthlyTotal by viewModel.monthlyTotal.collectAsState()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 상단 네비게이션
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "이전달",
                        modifier = Modifier.size(24.dp)
                            .clickable {
                                // 여기에 클릭 시 실행할 동작을 작성하세요.
                                // 예: 다음 달로 이동하는 함수 호출
                            }
                    )
                    //Button(onClick = { /* 이전 달 */ }) { Text("<") }
                    Text(
                        text = "$currentMonth 결제내역",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "다음달",
                        modifier = Modifier.size(24.dp)
                            .clickable {
                                // 여기에 클릭 시 실행할 동작을 작성하세요.
                                // 예: 다음 달로 이동하는 함수 호출
                            }
                    )
                }
                // 당월 지출
                Text("당월 지출 : $monthlyTotal", modifier = Modifier.padding(top = 8.dp))
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            groupedPayments.forEach { paymentByDay ->
                // 일별 헤더 아이템
                item {
                    DayHeader(date = paymentByDay.date)
                }
                // 일별 결제 내역 아이템들
                items(items = paymentByDay.payments) { payment ->
                    PaymentItem(payment = payment,
                        onClick = onPaymentItemClick)
                }
            }
        }
    }
}

@Composable
fun DayHeader(date: String) {
    Text(
        text = date,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
fun PaymentItem(
    payment: Transaction,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = payment.merchantName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${payment.majorCategory} | ${payment.subCategory}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // Right Section: Amount & Action Button
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "${payment.amount} 원",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (payment.amount < 0) Color.Black else Color.Blue // Assuming negative for expenses
            )
            if (!payment.isApplied) {
                Button(onClick = { /* 반영 버튼 클릭 */ }) {
                    Text("반영", fontSize = 12.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPaymentsScreen() {
    PaymentsScreen(onPaymentItemClick = {})
}