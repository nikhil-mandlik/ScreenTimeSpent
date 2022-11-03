package com.swameal.screentimespent.ui

import android.widget.ImageView
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView


@Composable
fun HomeScreen(
    navigateToDetails: () -> Unit,
    navigateToMiniInfo: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Home")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = navigateToDetails) {
            Text(text = "Navigate to Details")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = navigateToMiniInfo) {
            Text(text = "Navigate to Bottom Sheet")
        }
    }
}