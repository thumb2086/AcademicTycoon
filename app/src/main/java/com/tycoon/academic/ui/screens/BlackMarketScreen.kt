package com.tycoon.academic.ui.screens

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
// 修正：將 import 移到最上方，並改用基礎的 collectAsState
import androidx.compose.runtime.collectAsState
import com.tycoon.academic.R
import com.tycoon.academic.ui.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlackMarketScreen(financeViewModel: FinanceViewModel = hiltViewModel()) {
    // 改用 collectAsState(initial = null) 避免閃退
    val userProfile by financeViewModel.userProfile.collectAsState(initial = null)
    var repayAmount by remember { mutableStateOf("") }
    val profile = userProfile

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.black_market_bank), style = MaterialTheme.typography.headlineMedium)

        if (profile != null) {
            Text(stringResource(R.string.current_assets, profile.balance))
            Text(stringResource(R.string.current_debt, profile.debt))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.need_cash), style = MaterialTheme.typography.titleMedium)
                Button(onClick = { financeViewModel.borrow(1000) }) {
                    Text(stringResource(R.string.borrow_amount, 1000))
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.wanna_repay), style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = repayAmount,
                    onValueChange = { repayAmount = it },
                    label = { Text(stringResource(R.string.repay_amount)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        val amount = repayAmount.toLongOrNull() ?: 0
                        financeViewModel.repayDebt(amount)
                        repayAmount = ""
                    },
                    enabled = (repayAmount.toLongOrNull() ?: 0) > 0 && (profile?.debt ?: 0) > 0
                ) {
                    Text(stringResource(R.string.confirm_repayment))
                }
            }
        }
    }
}