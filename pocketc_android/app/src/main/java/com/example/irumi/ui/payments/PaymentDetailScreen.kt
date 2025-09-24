package com.example.irumi.ui.payments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.irumi.core.mapper.CategoryMapper
import com.example.irumi.core.state.UiState
import com.example.irumi.domain.entity.payments.PaymentEntity
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

    // 1. 대분류 관련 상태 변수
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
        is UiState.Loading -> {}
        is UiState.Failure -> {}
        is UiState.Success -> {
            PaymentDetailScreen(
                modifier = Modifier.padding(paddingValues),
                paymentDetail = (state as UiState.Success<PaymentEntity>).data,
                majorCategoryNames = viewModel.majorCategoryNames, // ViewModel에서 대분류 이름 목록 가져오기
                minorCategoryNameOptions = minorCategoryNameOptions,
                selectedMajorCategoryName = selectedMajorCategoryName,
                selectedMinorCategoryName = selectedMinorCategoryName,
                onMajorCategoryNameSelected = viewModel::onMajorCategoryNameSelected,
                onMinorCategoryNameSelected = viewModel::onMinorCategoryNameSelected,
                onEditClick = { updatedAmount ->
                    Timber.d("!!! onEditClick UI ${updatedAmount}")
                    viewModel.onEditClick(updatedAmount)
                    navigateUp()
                }
            )
        }
    }
}

@Composable
fun PaymentDetailScreen(
    modifier: Modifier = Modifier, // Scaffold 패딩 등을 적용하기 위한 Modifier
    paymentDetail: PaymentEntity,
    majorCategoryNames: List<String>,
    minorCategoryNameOptions: List<String>,
    selectedMajorCategoryName: String,
    selectedMinorCategoryName: String,
    onMajorCategoryNameSelected: (String) -> Unit,
    onMinorCategoryNameSelected: (String) -> Unit,
    onEditClick: (updatedAmount: Int) -> Unit
) {
    var currentAmountForEditButton by remember(paymentDetail.amount) { mutableStateOf(paymentDetail.amount) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        // 상단 사용처, 금액 및 수정 버튼
        HeaderSection(
            merchantName = paymentDetail.merchantName
        )
        EditableAmountText(
            initialAmount = paymentDetail.amount,
            onAmountChange = { newAmount ->
                currentAmountForEditButton = newAmount
            }
        )

        Spacer(Modifier.height(32.dp))

        // 카테고리 설정
        CategorySection(
            majorCategoryNames = majorCategoryNames,
            minorCategoryNameOptions = minorCategoryNameOptions,
            selectedMajorCategoryName = selectedMajorCategoryName,
            selectedMinorCategoryName = selectedMinorCategoryName,
            onMajorCategoryNameSelected = onMajorCategoryNameSelected,
            onMinorCategoryNameSelected = onMinorCategoryNameSelected
        )

        Spacer(Modifier.height(32.dp))

        // 기타 결제 정보
        InfoSection(
            paymentDetail = paymentDetail
        )

        Spacer(Modifier.weight(1f)) // 버튼을 하단에 위치시키기 위한 Spacer

        Button(onClick = { onEditClick(currentAmountForEditButton) }, Modifier.fillMaxWidth()) {
            Text("수정 완료")
        }
    }
}

@Composable
fun HeaderSection(merchantName: String) {
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
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "${numberFormatter.format(amount)} 원",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(onClick = { showBottomSheet = true }) {
            Icon(Icons.Default.Edit, contentDescription = "금액 수정")
        }
    }

    if (showBottomSheet) {
        var inputText by remember { mutableStateOf(amount.toString()) }

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("금액 수정", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it.filter { ch -> ch.isDigit() } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(onClick = { showBottomSheet = false }) {
                        Text("취소")
                    }
                    Button(onClick = {
                        val newAmount = inputText.toIntOrNull()
                        if (newAmount != null) {
                            amount = newAmount
                            onAmountChange(newAmount)
                        }
                        showBottomSheet = false
                    }) {
                        Text("완료")
                    }
                }
            }
        }
    }
}

@Composable
fun CategorySection(
    majorCategoryNames: List<String>,
    minorCategoryNameOptions: List<String>,
    selectedMajorCategoryName: String,
    selectedMinorCategoryName: String,
    onMajorCategoryNameSelected: (String) -> Unit,
    onMinorCategoryNameSelected: (String) -> Unit,
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
            selectedCategoryName = selectedMajorCategoryName,
            onCategoryNameSelected = onMajorCategoryNameSelected,
            categoryNameList = majorCategoryNames
        )
        CategoryRow(
            label = "소분류",
            selectedCategoryName = selectedMinorCategoryName,
            onCategoryNameSelected = onMinorCategoryNameSelected,
            categoryNameList = minorCategoryNameOptions, // ViewModel에서 전달된 소분류 옵션 사용
            enabled = minorCategoryNameOptions.isNotEmpty() // 소분류 옵션이 있을 때만 활성화
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
    selectedCategoryName: String,
    onCategoryNameSelected: (String) -> Unit,
    categoryNameList: List<String>,
    enabled: Boolean = true // 드롭다운 활성화 여부
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
            color = if (enabled) Color.Gray else Color.LightGray
        )

        // 드롭다운 메뉴 박스 (식비, 아래 화살표)
        ExposedDropdownMenuBox(
            expanded = isExpanded && enabled, // 활성화 상태일 때만 확장 가능
            onExpandedChange = { if (enabled) isExpanded = !isExpanded },
            modifier = Modifier.weight(0.7f)
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                value = selectedCategoryName,
                onValueChange = {},
                placeholder = { if (categoryNameList.isEmpty()) Text("항목 없음") else Text("") },
                enabled = enabled, // TextField 자체도 활성화 상태에 따라 변경
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    // 포커스/언포커스 시 하단 라인 투명하게 (선택적)
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    // 비활성화 시 텍스트 및 아이콘 색상 (선택적)
                    disabledTextColor = Color.LightGray,
                    disabledTrailingIconColor = Color.LightGray
                ),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded && enabled) },
            )

            if (enabled) { // 활성화 상태일 때만 드롭다운 메뉴 표시
                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }
                ) {
                    if (categoryNameList.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text(text = "선택 가능한 항목이 없습니다.") },
                            onClick = { isExpanded = false },
                            enabled = false
                        )
                    } else {
                        categoryNameList.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item) },
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
    // CategoryMapper에서 실제 이름 가져오기
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
        onEditClick = {}
    )
}


