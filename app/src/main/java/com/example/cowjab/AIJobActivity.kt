package com.example.cowjab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun AIJobPage(core: CoreViewModel) {
    val currentAIJob = core.getCurrentAIJob()

    val openEditTech = remember { mutableStateOf(false)}

    Text(
        text = "Inseminations for Visit on date: ${core.currentAIJobDateString}",
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
    )
    if (currentAIJob != null) {
        val technician = core.getTechnician(technicianID = currentAIJob.mainTechnicianID )
        if (technician != null) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Main Technician: ",
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
                Text(
                    text = technician.name,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = technician.technicianID.toString(),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
                Spacer(Modifier.weight(1f))
                Button(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    onClick = {
                        openEditTech.value = true
                    }) {
                    Text(text = "Edit Tech")
                }
            }
        }
    }

    if (openEditTech.value) {
        ChangeTechnician(
            core = core,
            uiToggle = {dummy: Boolean -> openEditTech.value = dummy}
        )
    }

    val currentBullName = remember { mutableStateOf("")}
    val currentBullID = remember { mutableStateOf(0)}

    val openSetBull = remember { mutableStateOf(false)}

    val cowSelectionPhase = remember { mutableStateOf(1) }

    val savingInsemination = remember { mutableStateOf(false) }

    Column {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 3.dp, vertical = 3.dp),
            elevation = 4.dp,
            border = BorderStroke(width = 1.dp, color = Color.LightGray),
            backgroundColor = Color(red = 0.8f, green = 1f, blue = 1f),
        ) {

            Row (
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
            ){
                Text(
                    text = "Bull: ",
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
                Text(
                    text = currentBullName.value,
                    fontSize = 20.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
                Spacer(Modifier.weight(1f))
                Button(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    onClick = {
                        openSetBull.value = true
                    }) {
                    Text(text = "Set Bull")
                }
            }
        }

        if (openSetBull.value) {
            SetBull(
                core = core,
                uiToggle = {dummy: Boolean -> openSetBull.value = dummy},
                assignBull = {
                        dummy: BullModel -> currentBullID.value = dummy.bullID
                                            currentBullName.value = dummy.bullName
                }
            )
        }

        CowSelection(
            core = core,
            currentBullID = currentBullID.value,
            currentBullName = currentBullName.value,
            cowSelectionPhase = cowSelectionPhase.value,
            setCowSelectionPhase = { x: Int -> cowSelectionPhase.value = x },
        )

        Text(
            text = "Proposed Inseminations",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )

        core.getProposedInseminationsForCurrentAIJob().forEach {
                proposedInsemination -> ProposedInseminationItem(
            core = core,
            proposedInsemination = proposedInsemination,
            addInseminationFromProposed = {
                    proposedInsem: ProposedInseminationModel -> core.addInseminationFromProposed(proposedInsem)
            },
            savingInsemination = savingInsemination,
        )
        }

        if (savingInsemination.value) {
            Text("Saving Insemination")
            savingInsemination.value = false
        }

        Spacer(modifier = Modifier.height(10.dp))
        Divider()

        Text(
            text = "Completed Inseminations",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )

        core.getInseminationsForCurrentAIJob().forEach {
                insemination -> InseminationItem(insemination)
        }
    }
}

@Composable
fun ChangeTechnician(
    core: CoreViewModel,
    uiToggle: (Boolean) -> Unit,
) {
    val expandTechnicianDropDown = remember { mutableStateOf(true)}

    Box(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.TopStart)
    ) {

        DropdownMenu(
            expanded = expandTechnicianDropDown.value,
            onDismissRequest = { uiToggle(false) },
        ) {

            core.technicians.forEach() {
                DropdownMenuItem(
                    onClick = {
                        core.setMainTechnician(it.technicianID)
                        expandTechnicianDropDown.value = false
                        uiToggle(false)
                    }) {
                    Text(text = it.name)
                }
            }
        }
    }
}

@Composable
fun SetBull(
    core: CoreViewModel,
    uiToggle: (Boolean) -> Unit,
    assignBull: (BullModel) -> Unit,
) {
    val expandBullDropDown = remember { mutableStateOf(true)}

    Box(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.TopStart)
    ) {

        DropdownMenu(
            expanded = expandBullDropDown.value,
            onDismissRequest = { uiToggle(false) },
        ) {

            core.bulls.forEach() {
                DropdownMenuItem(
                    onClick = {
                        assignBull(it)
                        expandBullDropDown.value = false
                        uiToggle(false)
                    }) {
                    Text(text = it.bullName)
                }
            }
        }
    }
}

@Composable
fun CowSelection(
    core: CoreViewModel,
    currentBullID: Int,
    currentBullName: String,
    cowSelectionPhase: Int,
    setCowSelectionPhase: (Int) -> Unit,
) {
    val selectedCowID = remember { mutableStateOf(0) }
    val selectedCowTagID = remember { mutableStateOf("") }

    if (cowSelectionPhase == 1) {
        Button(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            onClick = {
                setCowSelectionPhase(2)
            }) {
            Text(text = "Select Cow to Inseminate")
        }
    }

    else if (cowSelectionPhase == 2) {
        SearchAndSelectCow(
            core = core,
            selectCow = { x: CowModel -> selectedCowID.value = x.cowID
                                         selectedCowTagID.value = x.cowTagID },
            setUIPhase = setCowSelectionPhase,
        )
    }

    else if (cowSelectionPhase == 3) {
        StageInsemination(
            core = core,
            cowID = selectedCowID.value,
            cowTagID = selectedCowTagID.value,
            currentBullID = currentBullID,
            currentBullName = currentBullName,
            setUIPhase = setCowSelectionPhase,
        )
    }
}


@Composable
fun SearchAndSelectCow(
    core: CoreViewModel,
    selectCow: (CowModel) -> Unit,
    setUIPhase: (Int) -> Unit,
) {

    var cowTagID by rememberSaveable{ mutableStateOf("") }
    val openDialog = remember { mutableStateOf(true) }

    val allCowsForFarm = core.getCowsForCurrentFarm()
    var searchCows: MutableList<CowModel>

    if (openDialog.value) {
        Dialog(
            onDismissRequest = {
                openDialog.value = false
                setUIPhase(1)
            },

        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp,
            ) {
                Column(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                ) {
                    Row {
                        Spacer(Modifier.weight(1f))
                        Button(
                            onClick = {
                                openDialog.value = false
                                setUIPhase(1)
                            }
                        ) {
                            Text(text = "Cancel")
                        }
                    }
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = cowTagID,
                        onValueChange = {
                            cowTagID = it
                        },
                        label = { Text("Search Cow Tag ID") }
                    )

                    searchCows = allCowsForFarm.filter {
                        it.cowTagID.startsWith(cowTagID, ignoreCase = true)
                    } as MutableList<CowModel>

                    LazyColumn() {
                        items(searchCows) {
                                searchCow -> CowSearchItem(
                            cow = searchCow,
                            selectCow = selectCow,
                            setOpenDialog = {x: Boolean -> openDialog.value = x},
                            setUIPhase = setUIPhase,
                        )
                        }
                    }

                    if (searchCows.isEmpty()) {
                        Button(
                            onClick = {
                                val newCow = core.addCow(cowTagID = cowTagID)
                                selectCow(newCow)
                                openDialog.value = false
                                setUIPhase(3)
                            }
                        ) {
                            Text(text = "Add New Cow")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CowSearchItem(
    cow: CowModel,
    selectCow: (CowModel) -> Unit,
    setOpenDialog: (Boolean) -> Unit,
    setUIPhase: (Int) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 3.dp),
        elevation = 4.dp,
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(text = cow.cowID.toString())
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = cow.cowTagID)

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    selectCow(cow)
                    setOpenDialog(false)
                    setUIPhase(3)
                }
            ) {
                Text(text = "Select Cow")
            }
        }
    }
}


@Composable
fun StageInsemination(
    core: CoreViewModel,
    cowID: Int,
    cowTagID: String,
    currentBullID: Int,
    currentBullName: String,
    setUIPhase: (Int) -> Unit,
) {

    val cowReturnInfo = core.getReturnInfoForCow(cowID = cowID, fromDate = core.currentAIJobDate)

    val statusColor = when (cowReturnInfo.inseminationReturnStatus) {
        InseminationReturnStatus.SHORT_RETURN -> Color.Red
        InseminationReturnStatus.NORMAL_RETURN -> Color.Green
        InseminationReturnStatus.LONG_RETURN -> Color.Magenta
        else -> Color.Black
    }

    val returnStatusText = when (cowReturnInfo.inseminationReturnStatus) {
        InseminationReturnStatus.SHORT_RETURN -> "Short Return"
        InseminationReturnStatus.NORMAL_RETURN -> "Normal Return"
        InseminationReturnStatus.LONG_RETURN -> "Long Return"
        else -> "Not inseminated this season"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 3.dp, vertical = 3.dp),
        backgroundColor = Color(red = 1f, green = 1f, blue = 0.8f),
    ) {

        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
        ) {
            Column {
                Row {
                    Text("Bull: ")
                    Text(
                        text = currentBullName,
                        fontWeight = FontWeight.Bold
                    )
                    Text("  Cow: ")
                    Text(
                        text = cowTagID,
                        fontWeight = FontWeight.Bold,
                    )
                }

                if (cowReturnInfo.daysSinceLastInsemination != null) {
                    Row {
                            Text(text = "Inseminated ")
                            Text(
                                text = cowReturnInfo.daysSinceLastInsemination.toString(),
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                            )
                            Text(text = " days ago")
                    }
                    Row {
                        Text(text = "by ")
                        Text(text = cowReturnInfo.lastInseminationBullName)
                    }
                }

                Row {
                    Text(text = returnStatusText, color = statusColor)
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    core.addProposedInsemination(
                        bullID = currentBullID,
                        bullName = currentBullName,
                        cowID = cowID,
                        cowTagID = cowTagID,
                    )
                    setUIPhase(1)
                }
            ) {
                Text(text = "OK")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { setUIPhase(1) }) {
                Text(text = "Cancel")
            }
        }
    }
}


@Composable
fun ProposedInseminationItem(
    core: CoreViewModel,
    proposedInsemination: ProposedInseminationModel,
    addInseminationFromProposed: (ProposedInseminationModel) -> Unit,
    savingInsemination: MutableState<Boolean>,
) {
    val openConfirmation = remember { mutableStateOf(false) }

    val openSelectBull = remember { mutableStateOf(false) }

    val openOptions = remember { mutableStateOf(false) }

    val openChangeTech = remember { mutableStateOf(false) }

    val statusColor = when (proposedInsemination.inseminationReturnStatus) {
        InseminationReturnStatus.SHORT_RETURN -> Color.Red
        InseminationReturnStatus.NORMAL_RETURN -> Color.Green
        InseminationReturnStatus.LONG_RETURN -> Color.Magenta
        else -> Color.Black
    }

    val returnStatusText = when (proposedInsemination.inseminationReturnStatus) {
        InseminationReturnStatus.SHORT_RETURN -> "Short Return"
        InseminationReturnStatus.NORMAL_RETURN -> "Normal Return"
        InseminationReturnStatus.LONG_RETURN -> "Long Return"
        else -> "Not inseminated this season"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 3.dp, vertical = 3.dp),
        elevation = 4.dp,
        border = BorderStroke(width = 1.dp, color = Color.LightGray),
        backgroundColor = Color(red = 1f, green = 1f, blue = 0.8f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
        ) {
            Column {
                Row {
                    Text("Bull: ")
                    Text(
                        text = proposedInsemination.bullName,
                        fontWeight = FontWeight.Bold
                    )
                    Text("  Cow: ")
                    Text(
                        text = proposedInsemination.cowTagID,
                        fontWeight = FontWeight.Bold,
                    )
                }
                if (proposedInsemination.daysSinceLastInsemination != null) {
                    Row {
                        Text(text = "Inseminated ")
                        Text(
                            text = proposedInsemination.daysSinceLastInsemination.toString(),
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                        )
                        Text(text = " days ago")
                    }
                    Row {
                        Text(text = "by ")
                        Text(text = proposedInsemination.lastInseminationBullName)
                    }
                }
                Row {
                    Text(text = returnStatusText, color = statusColor)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (openConfirmation.value) {
                DropdownMenu(
                    expanded = openConfirmation.value,
                    onDismissRequest = { openConfirmation.value = false },
                ) {
                    DropdownMenuItem(
                        onClick = {
                            savingInsemination.value = true
                            addInseminationFromProposed(proposedInsemination)
                            openConfirmation.value = false
                        }
                    ) {
                        Text("Confirm insemination was done")
                    }
                    DropdownMenuItem(
                        onClick = { openConfirmation.value = false }
                    ) {
                        Text("Cancel")
                    }
                }
            }

            else if (openSelectBull.value) {
                SetBull(
                    core = core,
                    uiToggle = { x: Boolean -> openSelectBull.value = x },
                    assignBull = {
                            x: BullModel -> core.addProposedInsemination(
                        bullID = x.bullID,
                        bullName = x.bullName,
                        cowID = proposedInsemination.cowID,
                        cowTagID = proposedInsemination.cowTagID,
                    )
                        core.removeProposedInsemination(proposedInsemination)
                    }
                )

                savingInsemination.value = true
            }

            else if (openOptions.value) {
                DropdownMenu(
                    expanded = openOptions.value,
                    onDismissRequest = { openOptions.value = false },
                ) {
                    DropdownMenuItem(
                        onClick = {
                            openOptions.value = false
                            openSelectBull.value = true
                        }
                    ) {
                        Text("Edit Bull")
                    }
                    DropdownMenuItem(
                        onClick = {
                            core.removeProposedInsemination(proposedInsemination)
                            savingInsemination.value = true
                        }
                    ) {
                        Text("Delete Proposed Insemination")
                    }
                    DropdownMenuItem(
                        onClick = {
                            openChangeTech.value = true
                            openOptions.value = false
                        }
                    ) {
                        Text("Change Technician")
                    }
                    DropdownMenuItem(
                        onClick = { openOptions.value = false }
                    ) {
                        Text("Cancel")
                    }
                }
            }

            else if (openChangeTech.value) {
                DropdownMenu(
                    expanded = openChangeTech.value,
                    onDismissRequest = { openChangeTech.value = false },
                ) {
                    core.technicians.forEach() {
                        DropdownMenuItem(
                            onClick = {
                                proposedInsemination.technicianID = it.technicianID
                                proposedInsemination.technicianName = it.name
                                openChangeTech.value = false
                            }) {
                            Text(text = it.name)
                        }
                    }
                    DropdownMenuItem(
                        onClick = { openChangeTech.value = false}
                    ) {
                        Text(text = "Cancel", color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Row {
                    if (proposedInsemination.bullID == 0) {
                        Button(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            onClick = { openSelectBull.value = true }) {
                            Text("Select Bull")
                        }

                    }
                    else {
                        Button(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            onClick = { openConfirmation.value = true }
                        ) {
                            Text("Done")
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = { openOptions.value = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Options")
                    }
                }
                Row {
                    Text(text = "Technician: ")
                    Text(text = proposedInsemination.technicianName, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = proposedInsemination.technicianID.toString(),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun InseminationItem(insemination: InseminationModel) {

    val statusColor = when (insemination.inseminationReturnStatus) {
        InseminationReturnStatus.SHORT_RETURN -> Color.Red
        InseminationReturnStatus.NORMAL_RETURN -> Color.Green
        InseminationReturnStatus.LONG_RETURN -> Color.Magenta
        else -> Color.Black
    }

    val returnStatusText = when (insemination.inseminationReturnStatus) {
        InseminationReturnStatus.SHORT_RETURN -> "Short Return"
        InseminationReturnStatus.NORMAL_RETURN -> "Normal Return"
        InseminationReturnStatus.LONG_RETURN -> "Long Return"
        else -> "Not inseminated this season"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 3.dp, vertical = 3.dp),
        elevation = 4.dp,
        border = BorderStroke(width = 1.dp, color = Color.LightGray),
        backgroundColor = Color(red = 1f, green = 0.8f, blue = 1f),
        ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
        ) {
            Row {
                Text("Bull: ")
                Text(
                    text = insemination.bullName,
                    fontWeight = FontWeight.Bold,
                )
                Text("  Cow: ")
                Text(
                    text = insemination.cowTagID,
                    fontWeight = FontWeight.Bold,
                )
            }
            if (insemination.daysSinceLastInsemination != null) {
                 Row {
                    Text(text = "Inseminated ")
                    Text(
                        text = insemination.daysSinceLastInsemination.toString(),
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                    )
                    Text(text = " days before by ")
                    Text(text = insemination.lastInseminationBullName)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            Row {
                Text(text = returnStatusText, color = statusColor)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Technician: ")
                Text(text = insemination.technicianName, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = insemination.technicianID.toString(),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
