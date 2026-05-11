package com.totem.ia.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val BgDeep = Color(0xFF020205)
private val PurpleNeon = Color(0xFF8B5CF6)
private val CyanNeon = Color(0xFF06B6D4)

data class OnboardingSlide(
    val title: String,
    val description: String,
    val color: Color
)

private val slides = listOf(
    OnboardingSlide(
        "Escolha uma jornada",
        "Explore trilhas de conhecimento em Filosofia, Psicologia e Grandes Livros.",
        PurpleNeon
    ),
    OnboardingSlide(
        "Ouça no seu Totem",
        "Conecte sua estátua via Bluetooth e ouça as reflexões do seu mentor.",
        CyanNeon
    ),
    OnboardingSlide(
        "Evolua diariamente",
        "Responda às perguntas da IA e acompanhe seu progresso na jornada.",
        Color(0xFF10B981)
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { index ->
                SlideContent(slides[index])
            }

            // Pager Indicator
            Row(
                Modifier.padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(slides.size) { i ->
                    val color = if (pagerState.currentPage == i) slides[i].color else Color.White.copy(0.2f)
                    Box(
                        Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            // Button Action
            Button(
                onClick = {
                    if (pagerState.currentPage < slides.size - 1) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onComplete()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 40.dp)
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = slides[pagerState.currentPage].color),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    if (pagerState.currentPage == slides.size - 1) "Começar" else "Próximo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                if (pagerState.currentPage < slides.size - 1) {
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.ChevronRight, null)
                }
            }
        }
    }
}

@Composable
private fun SlideContent(slide: OnboardingSlide) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Decorative Element (Orb)
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    Brush.radialGradient(
                        listOf(slide.color.copy(0.2f), Color.Transparent)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(slide.color, slide.color.copy(0.4f))))
            )
        }

        Spacer(Modifier.height(48.dp))

        Text(
            slide.title,
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        
        Spacer(Modifier.height(16.dp))

        Text(
            slide.description,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(0.6f),
            textAlign = TextAlign.Center,
            lineHeight = 28.sp
        )
    }
}
