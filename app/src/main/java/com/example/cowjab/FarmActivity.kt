package com.example.cowjab

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate


@Composable
fun FarmPage(core: CoreViewModel) {
    val currentFarm = core.getCurrentFarm()
    val currentFarmName = currentFarm?.name
    val currentFarmAddress = currentFarm?.address

    var tabState by remember { mutableStateOf(0) }

    val onTabStateChange = { newTabState: Int -> tabState = newTabState}

    Column {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row() {
                Text(text = "Farm: ", modifier = Modifier.align(Alignment.CenterVertically))
                Text(text = "$currentFarmName", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Row() {
                Text(text = "Address: ")
                Text("$currentFarmAddress", color = Color.Blue)
            }
        }

        TabRow(selectedTabIndex = tabState) {
            Tab(selected = tabState == 0, onClick = { tabState = 0 },
                text = { Text("Cows") })
            Tab(selected = tabState == 1, onClick = { tabState = 1 },
                text = { Text("AI Visits") })
            Tab(selected = tabState == 2, onClick = { tabState = 2 },
                text = { Text(core.currentAIJobDateString) })
        }

        when (tabState) {
            0 -> if (currentFarm != null) {
                CowsPage(core = core)
            }
            1 -> currentFarm?.let { JobsPage(core = core, onTabStateChange = onTabStateChange) }
            2 -> currentFarm?.let { AIJobPage(core = core) }
        }
    }
}

@Composable
fun CowsPage(core: CoreViewModel) {
    val openAddCow = remember { mutableStateOf(false) }

    Column {

        if (openAddCow.value) {
            AddCow(
                openAddCow = openAddCow,
                addCow = {cowTagID: String -> core.addCow(cowTagID = cowTagID)}
            )
        }

        else {
            Button(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                onClick = {
                    openAddCow.value = true
                }) {
                Text(text = "Add Cow")
            }
        }

        core.getCowsForCurrentFarm().forEach { cow -> CowItem(cow) }
    }
}

@Composable
fun AddCow(openAddCow: MutableState<Boolean>, addCow: (String) -> Unit) {
    var cowTagID by rememberSaveable{ mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = cowTagID,
            onValueChange = { cowTagID = it },
            label = { Text("Cow Tag ID") }
        )

        Row {
            Button(
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = {
                    addCow(cowTagID)
                    cowTagID = ""
                    openAddCow.value = false
                }
            ) {
                Text("Save Cow")
            }
            Button(
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = {
                    openAddCow.value = false
                }
            ) {
                Text("Cancel")
            }
        }
    }
}

@Composable
fun CowItem(cow: CowModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 3.dp),
        elevation = 4.dp,
        border = BorderStroke(width = 1.dp, color = Color.LightGray),
        backgroundColor = Color(red = 1f, green = 1f, blue = 0.8f),
        ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(text = cow.cowID.toString())
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = cow.cowTagID)
        }
    }
}

@Composable
fun JobsPage(core: CoreViewModel, onTabStateChange: (Int) -> Unit) {

    val openAddJob = remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Artificial Insemination Visits to this farm, organized by date",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )

        Button(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            onClick = {
                openAddJob.value = true
            }) {
            Text(text = "Record AI Visit by picking Date")
        }

        if (openAddJob.value) {

            val today = LocalDate.now()

            val someContext = LocalContext.current
            val picker = DatePickerDialog(
                someContext,
                { _, year: Int, month: Int, day: Int ->
                    val aiJobID = core.addAIJob(
                        aiJobDate = LocalDate.of(year, month+1, day),
                    )
                    core.setCurrentAIJob(aiJobID = aiJobID)
                    openAddJob.value = false
                    onTabStateChange(2)
                },
                today.year,
                today.monthValue-1,
                today.dayOfMonth,
            )

            picker.show()

            // This runs when the DatePickerDialog is dismissed by clicking 'Cancel'.
            openAddJob.value = false
        }

        core.getAIJobsForCurrentFarm().forEach {
                aiJob -> AIJobItem(
            aiJob = aiJob, core = core, onTabStateChange = onTabStateChange
        ) }
    }
}

@Composable
fun AIJobItem(aiJob: AIJobModel, core: CoreViewModel, onTabStateChange: (Int) -> Unit) {
    val technician = core.getTechnician(technicianID = aiJob.mainTechnicianID)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 4.dp),
        elevation = 4.dp,
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Button(onClick = {
                core.setCurrentAIJob(aiJobID = aiJob.aiJobID)
                onTabStateChange(2)
            }) {
                Text(text = "Select")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Date:  ",
                fontStyle = FontStyle.Italic,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
            Text(
                text = aiJob.aiJobDate.toString(),
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
            if (technician != null) {
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Tech:  ",
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
                Text(
                    text = technician.name,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
        }
    }
}
