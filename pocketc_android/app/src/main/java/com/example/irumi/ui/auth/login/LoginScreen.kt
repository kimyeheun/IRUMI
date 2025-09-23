package com.example.irumi.ui.auth.login

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.irumi.ui.auth.AuthViewModel
import com.example.irumi.ui.component.button.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginRoute(
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }

    val loading = viewModel.loading
    val error = viewModel.error
    val isLoggedIn = viewModel.isLoggedIn

    // 로그인 성공 시 이동
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
            onSuccess()
        }
    }
    // 에러 토스트
    LaunchedEffect(error) {
        error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("로그인") },
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
            LoginScreen(
                email = email,
                pw = pw,
                rememberMe = rememberMe,
                loading = loading,
                onEmailChange = { email = it },
                onPwChange = { pw = it },
                onSubmit = { viewModel.login(email, pw, remember = rememberMe) }
            )
        }
    }
}

@Composable
fun LoginScreen(
    email: String,
    pw: String,
    rememberMe: Boolean,
    loading: Boolean,
    onEmailChange: (String) -> Unit,
    onPwChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("이메일") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 56.dp)
        )
        OutlinedTextField(
            value = pw,
            onValueChange = onPwChange,
            label = { Text("비밀번호") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 56.dp)
        )

        PrimaryButton(
            text = if (loading) "확인 중..." else "로그인",
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && pw.isNotBlank() && !loading,
            loading = loading
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        email = "",
        pw = "",
        rememberMe = true,
        loading = false,
        onEmailChange = {},
        onPwChange = {},
        onSubmit = {}
    )
}
