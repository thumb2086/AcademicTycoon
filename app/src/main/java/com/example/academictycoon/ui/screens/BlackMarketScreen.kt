package com.example.academictycoon.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.academictycoon.R
import com.example.academictycoon.ui.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlackMarketScreen(financeViewModel: FinanceViewModel = hiltViewModel()) {
    val userProfile by financeViewModel.userProfile.collectAsStateWithLifecycle()
    var repayAmount by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.black_market_bank), style = MaterialTheme.typography.headlineMedium)

        userProfile?.let {
            Text(stringResource(R.string.current_assets, it.balance))
            Text(stringResource(R.string.current_debt, it.debt))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Borrow Section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.need_cash), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { financeViewModel.borrow(1000) }) {
                    Text(stringResource(R.string.borrow_amount, 1000))
                }
            }
        }

        // Repay Section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.wanna_repay), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = repayAmount,
                    onValueChange = { repayAmount = it },
                    label = { Text(stringResource(R.string.repay_amount)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val amount = repayAmount.toLongOrNull() ?: 0
                        financeViewModel.repayDebt(amount)
                        repayAmount = ""
                    },
                    enabled = (repayAmount.toLongOrNull() ?: 0) > 0 && userProfile?.debt ?: 0 > 0
                ) {
                    Text(stringResource(R.string.confirm_repayment))
                }
            }
        }
    }
}
