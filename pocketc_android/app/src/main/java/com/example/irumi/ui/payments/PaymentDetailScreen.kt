package com.example.irumi.ui.payments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.irumi.core.mapper.CategoryMapper
import com.example.irumi.core.state.UiState
import com.example.irumi.domain.entity.payments.PaymentEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PaymentDetailRoute(
    paddingValues: PaddingValues,
    paymentId: Int?,
    viewModel: PaymentsViewModel = hiltViewModel(),
    navigateUp: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.paymentDetailState.collectAsStateWithLifecycle(lifecycleOwner = lifecycleOwner)
    val selectedMajorCategoryName by viewModel.selectedMajorCategoryName.collectAsStateWithLifecycle()
    val minorCategoryNameOptions by viewModel.minorCategoryNameOptions.collectAsStateWithLifecycle()
    val selectedMinorCategoryName by viewModel.selectedMinorCategoryName.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getPaymentDetail(paymentId)
    }

    when (state) {
        is UiState.Empty -> {}
        is UiState.Loading -> LoadingScreen()
        is UiState.Failure -> ErrorScreen()
        is UiState.Success -> {
            PaymentDetailScreen(
                modifier = Modifier.padding(paddingValues),
                paymentDetail = (state as UiState.Success<PaymentEntity>).data,
                majorCategoryNames = viewModel.majorCategoryNames,
                minorCategoryNameOptions = minorCategoryNameOptions,
                selectedMajorCategoryName = selectedMajorCategoryName,
                selectedMinorCategoryName = selectedMinorCategoryName,
                onMajorCategoryNameSelected = viewModel::onMajorCategoryNameSelected,
                onMinorCategoryNameSelected = viewModel::onMinorCategoryNameSelected,
                onEditClick = { updatedAmount ->
                    Timber.d("!!! onEditClick UI ${updatedAmount}")
                    viewModel.onEditClick(updatedAmount)
                    navigateUp()
                },
                onBackClick = navigateUp
            )
        }
    }
}

// 재사용 가능한 뒤로가기 상단바
@Composable
fun BackNavigationBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = TossColors.OnSurface,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TossColors.OnSurface
            )
        }
}

@Composable
fun PaymentDetailScreen(
    modifier: Modifier = Modifier,
    paymentDetail: PaymentEntity,
    majorCategoryNames: List<String>,
    minorCategoryNameOptions: List<String>,
    selectedMajorCategoryName: String,
    selectedMinorCategoryName: String,
    onMajorCategoryNameSelected: (String) -> Unit,
    onMinorCategoryNameSelected: (String) -> Unit,
    onEditClick: (updatedAmount: Int) -> Unit,
    onBackClick: () -> Unit
) {
    var currentAmountForEditButton by remember(paymentDetail.amount) {
        mutableStateOf(paymentDetail.amount)
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = TossColors.Surface
    ) {
        Column {
            // 상단바
            BackNavigationBar(
                title = "상세 내역",
                onBackClick = onBackClick
            )


                // 스크롤 가능한 컨텐츠
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .background(color = TossColors.Background)
                        .padding(horizontal = 20.dp)
                ) {

                        // 결제 정보 카드
                        PaymentInfoCard(
                            merchantName = paymentDetail.merchantName,
                            initialAmount = paymentDetail.amount,
                            onAmountChange = { newAmount ->
                                currentAmountForEditButton = newAmount
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 카테고리 설정 카드
                        CategoryCard(
                            majorCategoryNames = majorCategoryNames,
                            minorCategoryNameOptions = minorCategoryNameOptions,
                            selectedMajorCategoryName = selectedMajorCategoryName,
                            selectedMinorCategoryName = selectedMinorCategoryName,
                            onMajorCategoryNameSelected = onMajorCategoryNameSelected,
                            onMinorCategoryNameSelected = onMinorCategoryNameSelected
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 기타 정보 카드
                        PaymentDetailsCard(paymentDetail = paymentDetail)

                    Spacer(Modifier.weight(1f)) // 버튼을 하단에 위치시키기 위한 Spacer


                    // 수정 완료 버튼
                    Button(
                        onClick = { onEditClick(currentAmountForEditButton) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TossColors.Primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            "수정 완료",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }


@Composable
fun PaymentInfoCard(
    merchantName: String,
    initialAmount: Int,
    onAmountChange: (Int) -> Unit
) {
    Column(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // 상점명
            Text(
                text = merchantName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TossColors.OnSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 편집 가능한 금액
            EditableAmountText(
                initialAmount = initialAmount,
                onAmountChange = onAmountChange
            )
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableAmountText(
    initialAmount: Int,
    onAmountChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var amount by remember(initialAmount) { mutableStateOf(initialAmount) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val numberFormatter = remember { NumberFormat.getNumberInstance(Locale.KOREA) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TossColors.OutlineVariant)
            .clickable { showBottomSheet = true }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "${numberFormatter.format(amount)}원",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TossColors.OnSurface
            )
        }

        Icon(
            Icons.Default.Edit,
            contentDescription = "금액 수정",
            tint = TossColors.OnSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }

    if (showBottomSheet) {
        var inputText by remember { mutableStateOf(amount.toString()) }

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = TossColors.Background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "금액 수정",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TossColors.OnSurface
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { newValue ->
                        val filtered = newValue.filter { it.isDigit() }
                        val numericValue = filtered.toLongOrNull() ?: 0L
                        if (numericValue <= 10_000_000) { // 1000만원 제한
                            inputText = filtered
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("금액을 입력하세요 (최대 1,000만원)") },
                    colors = TextFieldDefaults.colors(
                        TossColors.Primary
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showBottomSheet = false },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("취소", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            val newAmount = inputText.toIntOrNull()
                            if (newAmount != null) {
                                amount = newAmount
                                onAmountChange(newAmount)
                            }
                            showBottomSheet = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TossColors.Primary
                        )
                    ) {
                        Text("완료", fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CategoryCard(
    paymentViewModel: PaymentsViewModel = hiltViewModel(),
    majorCategoryNames: List<String>,
    minorCategoryNameOptions: List<String>,
    selectedMajorCategoryName: String,
    selectedMinorCategoryName: String,
    onMajorCategoryNameSelected: (String) -> Unit,
    onMinorCategoryNameSelected: (String) -> Unit,
) {
    val paymentDetail = paymentViewModel.paymentDetailState.collectAsState()
    // paymentDetail에서 기존 카테고리 정보를 가져와서 초기값으로 사용
    Column(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        CategoryRow(
            label = "대분류",
            selectedCategoryName = selectedMajorCategoryName,
            onCategoryNameSelected = onMajorCategoryNameSelected,
            categoryNameList = majorCategoryNames
        )

        Spacer(modifier = Modifier.height(16.dp))

        CategoryRow(
            label = "소분류",
            selectedCategoryName = selectedMinorCategoryName,
            onCategoryNameSelected = onMinorCategoryNameSelected,
            categoryNameList = minorCategoryNameOptions,
            enabled = minorCategoryNameOptions.isNotEmpty()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryRow(
    label: String,
    selectedCategoryName: String,
    onCategoryNameSelected: (String) -> Unit,
    categoryNameList: List<String>,
    enabled: Boolean = true
) {
    var isExpanded by remember { mutableStateOf(false) }

    Row {
        Text(
            text = label,
            fontSize = 16.sp,
            color = TossColors.OnSurface,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                        .weight(0.3f)
                .align(Alignment.CenterVertically)
        )

        ExposedDropdownMenuBox(
            modifier = Modifier.weight(0.7f),
            expanded = isExpanded, // && enabled,
            onExpandedChange = {isExpanded = !isExpanded}//{ if (enabled) isExpanded = !isExpanded }
        ) {
            Card(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
//                    .clickable(enabled = enabled) {
//                        if (enabled) isExpanded = !isExpanded
//                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (enabled) TossColors.OutlineVariant
                    else TossColors.OutlineVariant.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedCategoryName.ifEmpty {
                            if (categoryNameList.isEmpty()) "항목 없음" else "선택하세요"
                        },
                        fontSize = 16.sp,
                        color = if (enabled && selectedCategoryName.isNotEmpty())
                            TossColors.OnSurface
                        else TossColors.OnSurfaceVariant,
                        fontWeight = if (selectedCategoryName.isNotEmpty())
                            FontWeight.Medium
                        else FontWeight.Normal
                    )

                    if (enabled) {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TossColors.OnSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Timber.d("!!! enalbed = ${enabled} isExpanded = ${isExpanded}")
            if (enabled) {
                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false },
                    modifier = Modifier.background(TossColors.Background)
                ) {
                    if (categoryNameList.isEmpty()) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "선택 가능한 항목이 없습니다.",
                                    color = TossColors.OnSurfaceVariant
                                )
                            },
                            onClick = { isExpanded = false },
                            enabled = false
                        )
                    } else {
                        categoryNameList.forEach { item ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = item,
                                        color = TossColors.OnSurface,
                                        fontWeight = if (item == selectedCategoryName)
                                            FontWeight.SemiBold
                                        else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    onCategoryNameSelected(item)
                                    isExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentDetailsCard(paymentDetail: PaymentEntity) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            DetailItem(
                label = "결제일시",
                value = paymentDetail.date
            )

            Spacer(modifier = Modifier.height(16.dp))

            DetailItem(
                label = "사용처",
                value = paymentDetail.merchantName
            )
        }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = TossColors.OnSurface,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.3f)
        )

        Text(
            text = value,
            fontSize = 16.sp,
            color = TossColors.OnSurface,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.7f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun LoadingScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = TossColors.Surface
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "로딩 중...",
                fontSize = 16.sp,
                color = TossColors.OnSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = TossColors.Surface
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "오류가 발생했습니다",
                fontSize = 16.sp,
                color = TossColors.Error
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPaymentDetailScreen() {
    val samplePaymentDetail = PaymentEntity(
        paymentId = 501,
        date = "2025-09-11T14:25:00Z",
        amount = 13500,
        majorCategory = 2,
        subCategory = 3,
        merchantName = "스타벅스 강남점",
        isApplied = true,
        isFixed = false,
        createdAt = "2025-09-11T14:25:30Z",
        updatedAt = "2025-09-11T14:25:30Z"
    )

    val majorName = CategoryMapper.getMajorName(samplePaymentDetail.majorCategory) ?: "식비"
    val subName = CategoryMapper.getSubName(samplePaymentDetail.subCategory) ?: "간식"
    val subOptions = CategoryMapper.getSubListByMajorId(samplePaymentDetail.majorCategory)

    PaymentDetailScreen(
        paymentDetail = samplePaymentDetail,
        majorCategoryNames = CategoryMapper.majorNameToId.keys.toList(),
        minorCategoryNameOptions = subOptions,
        selectedMajorCategoryName = majorName,
        selectedMinorCategoryName = subName,
        onMajorCategoryNameSelected = {},
        onMinorCategoryNameSelected = {},
        onEditClick = {},
        onBackClick = {}
    )
}