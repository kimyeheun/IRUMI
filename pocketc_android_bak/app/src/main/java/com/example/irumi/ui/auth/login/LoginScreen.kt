package com.example.pocketc.ui.auth.login

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
import com.example.irumi.ui.component.button.PrimaryButton
import com.example.irumi.ui.theme.BrandGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginRoute(
    onSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val brand = BrandGreen
    var id by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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
            modifier = Modifier.fillMaxSize().padding(inner),
            color = Color.White
        ) {
            LoginScreen(
                id = id,
                pw = pw,
                loading = loading,
                onIdChange = { id = it },
                onPwChange = { pw = it },
                onSubmit = {
                    loading = true
                    scope.launch {
                        val ok = AuthMockRepository.login(id, pw)
                        loading = false
                        if (ok) {
                            Toast.makeText(context, "로그인 성공 (Mock)", Toast.LENGTH_SHORT).show()
                            onSuccess()
                        } else {
                            Toast.makeText(context, "아이디/비밀번호를 확인해 주세요", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun LoginScreen(
    id: String,
    pw: String,
    loading: Boolean,
    onIdChange: (String) -> Unit,
    onPwChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = id,
            onValueChange = onIdChange,
            label = { Text("아이디 (임시: 1234)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 56.dp)
        )
        OutlinedTextField(
            value = pw,
            onValueChange = onPwChange,
            label = { Text("비밀번호 (임시: 1234)") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 56.dp)
        )

        PrimaryButton(
            text = if (loading) "확인 중..." else "로그인",
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = id.isNotBlank() && pw.isNotBlank(),
            loading = loading
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        id = "",
        pw = "",
        loading = false,
        onIdChange = {},
        onPwChange = {},
        onSubmit = {}
    )
}
