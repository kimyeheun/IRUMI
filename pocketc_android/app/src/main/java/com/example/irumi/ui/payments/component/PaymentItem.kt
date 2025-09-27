package com.example.irumi.ui.payments.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.irumi.core.designsystem.component.dialog.TwoButtonDialog
import com.example.irumi.ui.payments.TossColors
import com.example.irumi.ui.payments.model.PaymentDetailUiModel
import kotlinx.coroutines.delay

@Composable
fun PaymentItem(
    payment: PaymentDetailUiModel,
    onClick: () -> Unit,
    onPaymentCheckClick: (paymentId: Int, onFailure: () -> Unit) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var locallyApplied by remember(payment.paymentId, payment.isApplied) {
        mutableStateOf(payment.isApplied)
    }

    var btnPressed by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        targetValue = if (btnPressed) Color.Gray.copy(alpha = 0.1f) else Color.Transparent,
        animationSpec = tween(150),
        label = "backgroundColor"
    )

    var cardPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (cardPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        cardPressed = true
                        val released = tryAwaitRelease()
                        cardPressed = false
                        if (released) {
                            kotlinx.coroutines.delay(50)
                            onClick()
                        }
                    }
                )
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 결제 정보
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = payment.merchantName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TossColors.OnSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${payment.majorCategoryName} • ${payment.subCategoryName}",
                        fontSize = 14.sp,
                        color = TossColors.OnSurfaceVariant.copy(alpha = 0.8f)
                    )
                }

                // 금액
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${String.format("%,d", payment.amount)}원",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TossColors.OnSurface
                    )
                    if (locallyApplied) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "추가 완료",
                                fontSize = 12.sp,
                                color = Color.LightGray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(
                                    color = backgroundColor,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            showDialog = true
                                        },
                                        onPress = {
                                            btnPressed = true
                                            tryAwaitRelease()
                                            btnPressed = false
                                        }
                                    )
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF7B7B))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "미션에 추가하기",
                                fontSize = 12.sp,
                                color = Color(0xFFFF7B7B),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 화살표 아이콘
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "상세보기",
                    tint = TossColors.OnSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    // 확인 다이얼로그
    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 아이콘
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(TossColors.Primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🎯",
                            fontSize = 28.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 제목
                    Text(
                        text = "미션에 반영할까요?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 설명
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${payment.majorCategoryName} : ${payment.amount}원",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TossColors.Primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "결제 내역이 미션 진행도에 반영됩니다",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // 버튼들
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 확인 버튼
                        Button(
                            onClick = {
                                locallyApplied = true
                                showDialog = false
                                onPaymentCheckClick(payment.paymentId) { locallyApplied = false }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TossColors.Primary
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp
                            )
                        ) {
                            Text(
                                text = "반영하기",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                        // 취소 버튼
                        OutlinedButton(
                            onClick = { showDialog = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Gray
                            ),
                            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "취소",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}