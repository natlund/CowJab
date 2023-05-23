package com.example.cowjab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BullsPage(core: CoreViewModel) {
    val bulls = core.bulls

    val openAddBull = remember { mutableStateOf(false) }

    Column {
        if  (openAddBull.value) {
            AddBull(
                openAddBull = openAddBull,
                addBull = { bull: String -> core.addBull(bull) }
            )
        }

        else {
            Button(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                onClick = {openAddBull.value = true}
            ) {
                Text(text = "Add Bull")
            }
        }

        bulls.forEach { bull -> BullItem(bull) }

    }
}

@Composable
fun AddBull(openAddBull: MutableState<Boolean>, addBull: (String) -> Unit) {
    var bullName by rememberSaveable{ mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = bullName,
            onValueChange = { bullName = it },
            label = { Text("New Bull Name") }
        )

        Row {
            Button(
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = {
                    addBull(bullName)
                    bullName = ""
                    openAddBull.value = false
                }
            ) {
                Text("Save Bull")
            }
            Button(
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = {
                    openAddBull.value = false
                }
            ) {
                Text("Cancel")
            }
        }
    }

}

@Composable
fun BullItem(bull: BullModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 3.dp),
        elevation = 4.dp,
        border = BorderStroke(width = 1.dp, color = Color.LightGray),
        backgroundColor = Color(red = 0.8f, green = 1f, blue = 1f),
        ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(text = bull.bullID.toString())
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = bull.bullName)
        }
    }
}
