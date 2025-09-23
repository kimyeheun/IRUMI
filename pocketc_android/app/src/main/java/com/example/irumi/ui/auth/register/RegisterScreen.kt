package com.example.irumi.ui.auth.register

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.irumi.ui.auth.AuthViewModel
import com.example.irumi.ui.component.button.PrimaryButton
import com.example.irumi.ui.theme.BrandGreen

private enum class RegisterStep { Name, Email, Password, Budget, Complete }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterRoute(
    onDone: () -> Unit,   // "이미 계정있음 → 로그인으로"
    onGoHome: () -> Unit, // 완료 화면에서 시작하기 → 홈 이동
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // 스텝 상태
    var step by remember { mutableStateOf(RegisterStep.Name) }

    // 입력값
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }

    // 예산 드롭다운
    val budgetOptions = remember {
        listOf(
            1_000_000 to "100만원 이하",
            2_000_000 to "200만원",
            3_000_000 to "300만원",
            4_000_000 to "400만원",
            5_000_000 to "500만원",
            6_000_000 to "600만원",
            7_000_000 to "700만원",
            8_000_000 to "800만원",
            9_000_000 to "900만원",
            10_000_000 to "1000만원 이상"
        )
    }
    var budgetValue by remember { mutableStateOf<Int?>(null) }
    var budgetExpanded by remember { mutableStateOf(false) }

    var rememberMe by remember { mutableStateOf(true) }

    val loading = viewModel.loading
    val error = viewModel.error
    val isLoggedIn = viewModel.isLoggedIn

    // 가입 성공 → 완료 스텝으로 이동 (바로 홈 이동 X)
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            Toast.makeText(context, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show()
            step = RegisterStep.Complete
        }
    }
    // 에러 토스트
    LaunchedEffect(error) {
        error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("회원가입") },
                navigationIcon = {
                    IconButton(onClick = {
                        when (step) {
                            RegisterStep.Name -> onBack()
                            RegisterStep.Email -> step = RegisterStep.Name
                            RegisterStep.Password -> step = RegisterStep.Email
                            RegisterStep.Budget -> step = RegisterStep.Password
                            RegisterStep.Complete -> onBack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    ) { inner ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
            color = Color.White
        ) {
            when (step) {
                RegisterStep.Name -> {
                    SingleFieldStep(
                        title = "이름을 입력해주세요",
                        placeholder = "이름",
                        value = name,
                        onValueChange = { name = it },
                        buttonText = "확인",
                        enabled = name.isNotBlank(),
                        onConfirm = { step = RegisterStep.Email }
                    )
//                    BottomLinks(
//                        rememberMe = rememberMe,
//                        onRememberChange = { rememberMe = it },
//                        onGoLogin = onDone
//                    )
                }

                RegisterStep.Email -> {
                    SingleFieldStep(
                        title = "이메일을 입력해주세요",
                        placeholder = "이메일",
                        value = email,
                        onValueChange = { email = it },
                        keyboardType = KeyboardType.Email,
                        buttonText = "확인",
                        enabled = email.isNotBlank(),
                        onConfirm = { step = RegisterStep.Password }
                    )
//                    BottomLinks(
//                        rememberMe = rememberMe,
//                        onRememberChange = { rememberMe = it },
//                        onGoLogin = onDone
//                    )
                }

                RegisterStep.Password -> {
                    PasswordStep(
                        title = "비밀번호를 입력해주세요",
                        password = password,
                        confirm = passwordConfirm,
                        onPasswordChange = { password = it },
                        onConfirmChange = { passwordConfirm = it },
                        onConfirmClick = {
                            if (password.isBlank() || passwordConfirm.isBlank()) {
                                Toast.makeText(context, "비밀번호를 모두 입력하세요.", Toast.LENGTH_SHORT).show()
                                return@PasswordStep
                            }
                            if (password != passwordConfirm) {
                                Toast.makeText(context, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                                return@PasswordStep
                            }
                            step = RegisterStep.Budget
                        }
                    )
//                    BottomLinks(
//                        rememberMe = rememberMe,
//                        onRememberChange = { rememberMe = it },
//                        onGoLogin = onDone
//                    )
                }

                RegisterStep.Budget -> {
                    BudgetDropdownStep(
                        title = "월 예산을 선택해주세요",
                        options = budgetOptions,
                        selected = budgetValue,
                        expanded = budgetExpanded,
                        onExpandedChange = { budgetExpanded = it },
                        onSelect = { v -> budgetValue = v; budgetExpanded = false },
                        onSubmit = {
                            val budget = budgetValue ?: run {
                                Toast.makeText(context, "월 예산을 선택해주세요.", Toast.LENGTH_SHORT).show()
                                return@BudgetDropdownStep
                            }
                            // 최종 회원가입
                            viewModel.signUp(
                                name = name,
                                email = email,
                                pw = password,
                                budget = budget,
                                remember = rememberMe
                            )
                        },
                        loading = loading
                    )
//                    BottomLinks(
//                        rememberMe = rememberMe,
//                        onRememberChange = { rememberMe = it },
//                        onGoLogin = onDone
//                    )
                }

                RegisterStep.Complete -> {
                    CompleteStep(onStart = onGoHome)
                }
            }
        }
    }
}

/* -------------------- 스텝 컴포저블들 -------------------- */

@Composable
private fun SingleFieldStep(
    title: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    buttonText: String,
    enabled: Boolean,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = BrandGreen)
        OutlinedTextField(
            value = value, onValueChange = onValueChange,
            label = { Text(placeholder) }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 56.dp)
        )
        PrimaryButton(
            text = buttonText,
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled
        )
    }
}

@Composable
private fun PasswordStep(
    title: String,
    password: String,
    confirm: String,
    onPasswordChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onConfirmClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = BrandGreen)

        OutlinedTextField(
            value = password, onValueChange = onPasswordChange,
            label = { Text("비밀번호") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 56.dp)
        )
        OutlinedTextField(
            value = confirm, onValueChange = onConfirmChange,
            label = { Text("비밀번호 확인") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 56.dp)
        )

        PrimaryButton(
            text = "확인",
            onClick = onConfirmClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = password.isNotBlank() && confirm.isNotBlank()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BudgetDropdownStep(
    title: String,
    options: List<Pair<Int, String>>,
    selected: Int?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (Int) -> Unit,
    onSubmit: () -> Unit,
    loading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = BrandGreen)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selected?.let { s -> options.first { it.first == s }.second } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("월 예산") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 56.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                options.forEach { (value, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = { onSelect(value) }
                    )
                }
            }
        }

        PrimaryButton(
            text = if (loading) "처리 중..." else "회원가입",
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = selected != null && !loading,
            loading = loading
        )
    }
}

//@Composable
//private fun BottomLinks(
//    rememberMe: Boolean,
//    onRememberChange: (Boolean) -> Unit,
//    onGoLogin: () -> Unit
//) {
//    Spacer(Modifier.height(8.dp))
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 20.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Checkbox(checked = rememberMe, onCheckedChange = onRememberChange)
//        Text("자동 로그인")
//        Spacer(Modifier.weight(1f))
//        TextButton(onClick = onGoLogin) {
//            Text("이미 계정이 있으신가요? 로그인", color = BrandGreen)
//        }
//    }
//}

@Composable
private fun CompleteStep(onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("회원가입이 완료됐어요 🎉", style = MaterialTheme.typography.titleLarge, color = BrandGreen)
        Text("이제 이룸이를 시작해볼까요?", color = Color(0xFF6B7280))
        PrimaryButton(
            text = "시작하기",
            onClick = onStart,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/* -------- 프리뷰 (간단) -------- */
@Preview(showBackground = true)
@Composable
private fun RegisterPreview() {
    SingleFieldStep(
        title = "이름을 입력해주세요",
        placeholder = "이름",
        value = "",
        onValueChange = {},
        buttonText = "확인",
        enabled = false,
        onConfirm = {}
    )
}
