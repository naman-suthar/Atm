package com.naman.ezobooksaym

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.naman.ezobooksaym.ui.theme.EzoBooksAYMTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EzoBooksAYMTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text("EzoBooks ATM")
                                },
                                navigationIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Menu,
                                        contentDescription = "EzoBooks Navigation drawer",
                                        modifier = Modifier.padding(horizontal = 12.dp)
                                    )
                                }
                            )
                        }
                    ) {padding->
                        ATMApp(viewModel, modifier = Modifier.padding(padding))
                    }

                }
            }
        }
    }
}


@Composable
fun ATMApp(viewModel: MainViewModel, modifier: Modifier) {
    val atmState = viewModel.atmState.collectAsState()
    var note200Input by remember {
        mutableStateOf("")
    }
    var note100Input by remember {
        mutableStateOf("")
    }
    var note500Input by remember {
        mutableStateOf("")
    }
    var note2000Input by remember {
        mutableStateOf("")
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DenominationItemUI(
            DenominationItem.Deno_2000,
            atmState.value.note2000.toString(),
            note2000Input
        ) {
            note2000Input = it
        }
        DenominationItemUI(
            DenominationItem.Deno_500,
            atmState.value.note500.toString(),
            note500Input
        ) {
            note500Input = it
        }
        DenominationItemUI(
            DenominationItem.Deno_200,
            atmState.value.note200.toString(),
            note200Input
        ) {
            note200Input = it
        }
        DenominationItemUI(
            DenominationItem.Deno_100,
            atmState.value.note100.toString(),
            note100Input
        ) {
            note100Input = it
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total",
                modifier = Modifier.weight(2f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge
            )
            Text(text = "${viewModel.getBalance()}", modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    viewModel.deposit(
                        x2000 = if (note2000Input.isNotBlank()) note2000Input.toInt() else 0,
                        x500 = if (note500Input.isNotBlank()) note500Input.toInt() else 0,
                        x200 = if (note200Input.isNotBlank()) note200Input.toInt() else 0,
                        x100 = if (note100Input.isNotBlank()) note100Input.toInt() else 0
                    )

                    note2000Input = ""
                    note500Input = ""
                    note200Input = ""
                    note100Input = ""
                },
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .widthIn(max = 100.dp)
            ) {
                Text(text = "Deposit")
            }
        }

        WithdrawAmount(onWithdraw = {
            viewModel.withdraw(it)
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawAmount(
    onWithdraw: (Int) -> Boolean
) {
    var withdrawAmount by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 24.dp, vertical = 30.dp
            )
    ) {
        TextField(
            placeholder = {
                Text(text = "Enter Amount")
            },
            value = withdrawAmount,
            onValueChange = {
                withdrawAmount = it
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (withdrawAmount.isBlank()) {
                    Toast.makeText(context, "Enter Amount please", Toast.LENGTH_SHORT).show()
                } else {
                    val result = onWithdraw(withdrawAmount.toInt())
                    if (result) {
                        Toast.makeText(context, "Withdraw SuccessFully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Request Failed", Toast.LENGTH_SHORT).show()
                    }
                    withdrawAmount = ""
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text(
                text = "Withdraw",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DenominationItemUI(
    denominationItem: DenominationItem,
    available: String,
    inputState: String,
    onValueChanged: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = when (denominationItem) {
                DenominationItem.Deno_2000 -> "2000"
                DenominationItem.Deno_500 -> "500"
                DenominationItem.Deno_200 -> "200"
                DenominationItem.Deno_100 -> "100"
            },
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        Text(text = available, modifier = Modifier.weight(1f))
        TextField(
            value = inputState, onValueChange = {
                if (it.length <= 3) {
                    onValueChanged(it)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 100.dp)
        )
    }


}


enum class DenominationItem {
    Deno_2000, Deno_500, Deno_200, Deno_100
}