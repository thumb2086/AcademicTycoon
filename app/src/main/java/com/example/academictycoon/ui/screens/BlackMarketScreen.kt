package com.example.academictycoon.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.academictycoon.ui.FinanceViewModel

@Composable
fun BlackMarketScreen(financeViewModel: FinanceViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Need some cash?", modifier = Modifier.padding(bottom = 16.dp))
        Button(onClick = { financeViewModel.borrow(1000) }) {
            Text("Borrow 1000")
        }
    }
}
