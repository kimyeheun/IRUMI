// ui/auth/register/RegisterScreen.kt
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterRoute(
    onDone: () -> Unit,   // "이미 계정있음 → 로그인으로"
    onGoHome: () -> Unit, // 회원가입 성공 후 홈 이동
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var budgetText by remember { mutableStateOf("") } // 숫자 입력
    var rememberMe by remember { mutableStateOf(true) }

    val loading = viewModel.loading
    val error = viewModel.error
    val isLoggedIn = viewModel.isLoggedIn

    // 가입 성공(토큰 저장 완료)이면 홈으로
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            Toast.makeText(context, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show()
            onGoHome()
        }
    }
    // 에러 토스트
    LaunchedEffect(error) {
        error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }

    fun isEnabled(): Boolean =
        name.isNotBlank() && email.isNotBlank() &&
                password.isNotBlank() && budgetText.toIntOrNull()?.let { it > 0 } == true &&
                !loading

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("회원가입") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
            RegisterScreen(
                name = name,
                email = email,
                password = password,
                budgetText = budgetText,
                rememberMe = rememberMe,
                loading = loading,
                onNameChange = { name = it },
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onBudgetChange = { budgetText = it.filter { ch -> ch.isDigit() } }, // 숫자만
                onRememberChange = { rememberMe = it },
                onSubmit = {
                    if (!isEnabled()) return@RegisterScreen
                    val budget = budgetText.toIntOrNull() ?: return@RegisterScreen
                    viewModel.signUp(name, email, password, budget, remember = rememberMe)
                },
                onGoLogin = onDone
            )
        }
    }
}

@Composable
fun RegisterScreen(
    name: String,
    email: String,
    password: String,
    budgetText: String,
    rememberMe: Boolean,
    loading: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onBudgetChange: (String) -> Unit,
    onRememberChange: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    onGoLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = name, onValueChange = onNameChange,
            label = { Text("이름") }, singleLine = true,
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 56.dp)
        )
        OutlinedTextField(
            value = email, onValueChange = onEmailChange,
            label = { Text("이메일") }, singleLine = true,
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 56.dp)
        )
        OutlinedTextField(
            value = password, onValueChange = onPasswordChange,
            label = { Text("비밀번호") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 56.dp)
        )
        OutlinedTextField(
            value = budgetText, onValueChange = onBudgetChange,
            label = { Text("월 예산 (숫자)") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 56.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = rememberMe, onCheckedChange = onRememberChange)
            Text("자동 로그인")
        }

        PrimaryButton(
            text = if (loading) "처리 중..." else "회원가입",
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && email.isNotBlank() &&
                    password.isNotBlank() && budgetText.isNotBlank() && !loading,
            loading = loading
        )

        TextButton(onClick = onGoLogin) {
            Text("이미 계정이 있으신가요? 로그인", color = BrandGreen)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterPreview() {
    RegisterScreen(
        name = "",
        email = "",
        password = "",
        budgetText = "",
        rememberMe = true,
        loading = false,
        onNameChange = {},
        onEmailChange = {},
        onPasswordChange = {},
        onBudgetChange = {},
        onRememberChange = {},
        onSubmit = {},
        onGoLogin = {}
    )
}
