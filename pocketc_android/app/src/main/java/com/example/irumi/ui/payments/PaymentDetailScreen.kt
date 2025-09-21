package com.example.irumi.ui.payments

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.irumi.core.state.UiState
import com.example.irumi.domain.entity.PaymentEntity


@Composable
fun PaymentDetailRoute(
    paddingValues: PaddingValues,
    viewModel: PaymentsViewModel = hiltViewModel(),
    navigateUp: () -> Unit
    ) {
//    val coroutineScope = rememberCoroutineScope()
//    val snackBarHostState = remember { SnackbarHostState() }
//    var scoopDialogVisibility by remember { mutableStateOf(false) }
//    var deleteReviewDialogVisibility by remember { mutableStateOf(false) }

//    LaunchedEffect(viewModel.sideEffect, lifecycleOwner) {
//        viewModel.sideEffect.flowWithLifecycle(lifecycleOwner.lifecycle).collect { effect ->
//            when (effect) {
//                is PlaceDetailSideEffect.ShowSnackbar -> {
//                    onShowSnackBar(effect.message)
//                }
//                is PlaceDetailSideEffect.NavigateUp -> navigateUp()
//            }
//        }
//    }

//    val lifecycle = lifecycleOwner.lifecycle
//    LaunchedEffect(lifecycle) {
//        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//            viewModel.refresh()
//        }
//    }

    // 1. 대분류 관련 상태 변수
    val lifecycleOwner = LocalLifecycleOwner.current

    val state by viewModel.paymentDetailState.collectAsStateWithLifecycle(lifecycleOwner = lifecycleOwner)
    val selectedMajorCategory by viewModel.selectedMajorCategory.collectAsStateWithLifecycle(lifecycleOwner = lifecycleOwner)
    val selectedMinorCategory by viewModel.selectedMinorCategory.collectAsStateWithLifecycle(lifecycleOwner = lifecycleOwner)

    LaunchedEffect(Unit) {
        viewModel.getPaymentDetail()
    }

    when (state) {
        is UiState.Empty -> {}
        is UiState.Loading -> {}
        is UiState.Failure -> {}
        is UiState.Success -> {
            PaymentDetailScreen(
                paymentDetail = (state as UiState.Success<PaymentEntity>).data,
                majorCategories = viewModel.majorCategories,
                categoryMap = viewModel.categoryMap,
                selectedMajorCategory = selectedMajorCategory,
                selectedMinorCategory = selectedMinorCategory,
                onMajorCategorySelected = viewModel::onSelectedMajorCategorySelected,
                onMinorCategorySelected = viewModel::onSelectedMinorCategorySelected,
                onEditClick = {
                    viewModel.onEditClick()
                    navigateUp()
                }
            )
        }
    }
}

@Composable
fun PaymentDetailScreen(
    paymentDetail: PaymentEntity,
    majorCategories: List<String>,
    categoryMap: Map<String, List<String>>,
    selectedMajorCategory: String,
    selectedMinorCategory: String,
    onMajorCategorySelected: (String) -> Unit,
    onMinorCategorySelected: (String) -> Unit,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        // 상단 사용처, 금액 및 수정 버튼
        HeaderSection(
            merchantName = paymentDetail.merchantName,
            amount = paymentDetail.amount
        )

        Spacer(Modifier.height(32.dp))

        // 카테고리 설정
        CategorySection(
            majorCategories = majorCategories,
            categoryMap = categoryMap,
            selectedMajorCategory = selectedMajorCategory,
            selectedMinorCategory = selectedMinorCategory,
            onMajorCategorySelected = onMajorCategorySelected,
            onMinorCategorySelected = onMinorCategorySelected,
        )

        Spacer(Modifier.height(32.dp))

        // 기타 결제 정보
        InfoSection(
            paymentDetail = paymentDetail
        )

        Button(onClick = onEditClick, Modifier.fillMaxWidth()) {
            Text("수정")
        }
    }
}

@Composable
fun HeaderSection(merchantName: String, amount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 사용처 이름
        Text(
            text = merchantName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        // 금액
        Text(
            text = "${amount} 원", // amount.toFormattedString()
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.width(8.dp))
        // 수정 아이콘
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "수정",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun CategorySection(
    majorCategories: List<String>,
    categoryMap: Map<String, List<String>>,
    selectedMajorCategory: String,
    selectedMinorCategory: String,
    onMajorCategorySelected: (String) -> Unit,
    onMinorCategorySelected: (String) -> Unit,
) {
    Column {
        Text(
            text = "카테고리 설정",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        CategoryRow(
            label = "대분류",
            selectedCategory = selectedMajorCategory,
            onCategorySelected = onMajorCategorySelected,
            categoryList = majorCategories
        )
        CategoryRow(
            label = "소분류",
            selectedCategory = selectedMinorCategory,
            onCategorySelected = onMinorCategorySelected,
            categoryList = categoryMap[selectedMajorCategory] ?: emptyList()
        )
    }
}

@Composable
fun InfoSection(paymentDetail: PaymentEntity) {
    Column {
        // 결제일시
        DetailItem(
            label = "결제일시",
            value = paymentDetail.date, // toFormattedDateTime()
            showArrow = false
        )

        // 사용처
        DetailItem(
            label = "사용처",
            value = paymentDetail.merchantName,
            showArrow = false
        )
    }
}

@Composable
fun DetailItem(label: String, value: String, showArrow: Boolean = true) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 16.sp, color = Color.Gray)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = value, fontSize = 16.sp)
            if (showArrow) {
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryRow(
    label: String,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    categoryList: List<String>
) {
    var isExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 라벨 (대분류)
        Text(
            text = label,
            modifier = Modifier.weight(0.3f), // 30% 비율로 공간 차지
            fontSize = 16.sp,
            color = Color.Gray
        )

        // 드롭다운 메뉴 박스 (식비, 아래 화살표)
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded },
            modifier = Modifier.weight(0.7f) // 70% 비율로 공간 차지
        ) {
            TextField(
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                readOnly = true,
                value = selectedCategory,
                onValueChange = {},
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            )

            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                categoryList.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            onCategorySelected(item)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPaymentDetailScreen() {
    // 미리보기용 더미 데이터와 콜백 함수 전달
    val samplePaymentDetail = PaymentEntity(
        paymentId = 501,
        date = "2025-09-11T14:25:00Z",
        amount = 13500,
        majorCategory = 2,
        subCategory = 3,
        merchantName = "스타벅스",
        isApplied = true,
        isFixed = false,
        createdAt = "2025-09-11T14:25:30Z",
        updatedAt = "2025-09-11T14:25:30Z"
    )
    PaymentDetailScreen(
        paymentDetail = samplePaymentDetail,
        majorCategories = listOf("식비", "교통비", "생활"),
        categoryMap = mapOf(
            "식비" to listOf("점심", "저녁", "간식", "음료"),
    "교통비" to listOf("대중교통", "택시", "주유"),
    "생활" to listOf("마트/편의점", "쇼핑", "세탁")
    ),
        selectedMajorCategory = "식비",
        selectedMinorCategory = "간식",
        onMajorCategorySelected = {},
        onMinorCategorySelected = {},
        onEditClick = {}
    )
}


