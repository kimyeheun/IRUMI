package com.example.irumi.ui.auth.register

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.irumi.ui.auth.AuthMockRepository
import com.example.irumi.ui.auth.SignUpRequest
import com.example.irumi.ui.auth.SignUpResult
import com.example.irumi.ui.component.button.PrimaryButton
import com.example.irumi.ui.theme.BrandGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterRoute(
    onDone: () -> Unit,
    onGoHome: () -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val brand = BrandGreen
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    fun isEnabled() = name.isNotBlank() && email.isNotBlank() &&
            password.isNotBlank() && nickname.isNotBlank()

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
            modifier = Modifier.fillMaxSize().padding(inner),
            color = Color.White
        ) {
            RegisterScreen(
                name = name,
                email = email,
                password = password,
                nickname = nickname,
                loading = loading,
                brand = brand,
                onNameChange = { name = it },
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onNicknameChange = { nickname = it },
                onSubmit = {
                    if (!isEnabled() || loading) return@RegisterScreen
                    loading = true
                    scope.launch {
                        val req = SignUpRequest(name, email, password, nickname)
                        when (val res = AuthMockRepository.signUp(req)) {
                            is SignUpResult.Success -> {
                                loading = false
                                Toast.makeText(context, "회원가입이 완료되었습니다 (Mock)", Toast.LENGTH_SHORT).show()
                                onGoHome()
                            }
                            is SignUpResult.Error -> {
                                loading = false
                                Toast.makeText(context, "${res.status} ${res.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
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
    nickname: String,
    loading: Boolean,
    brand: androidx.compose.ui.graphics.Color,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNicknameChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onGoLogin: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp),
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
            value = nickname, onValueChange = onNicknameChange,
            label = { Text("닉네임") }, singleLine = true,
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 56.dp)
        )

        PrimaryButton(
            text = if (loading) "처리 중..." else "회원가입",
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && email.isNotBlank() &&
                    password.isNotBlank() && nickname.isNotBlank(),
            loading = loading
        )

        TextButton(onClick = onGoLogin) {
            Text("이미 계정이 있으신가요? 로그인", color = brand)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterPreview() {
    RegisterScreen(
        name = "", email = "", password = "", nickname = "",
        loading = false, brand = BrandGreen,
        onNameChange = {}, onEmailChange = {}, onPasswordChange = {}, onNicknameChange = {},
        onSubmit = {}, onGoLogin = {}
    )
}
