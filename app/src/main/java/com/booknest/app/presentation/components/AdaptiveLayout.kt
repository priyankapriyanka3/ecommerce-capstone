package com.booknest.app.presentation.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class WindowWidthSizeClass {
    Compact, Medium, Expanded
}

@Composable
fun AppAdaptiveScaffold(
    modifier: Modifier = Modifier,
    navigationRail: (@Composable () -> Unit)? = null,
    bottomBar: (@Composable () -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val width = maxWidth
        val isTablet = width >= 600.dp

        if (isTablet && navigationRail != null) {
            Row(modifier = Modifier.fillMaxSize()) {
                navigationRail()
                Scaffold(
                    modifier = Modifier.weight(1f),
                    content = content
                )
            }
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = { bottomBar?.invoke() },
                content = content
            )
        }
    }
}
